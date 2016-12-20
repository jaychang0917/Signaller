package com.jaychang.signaller.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jaychang.npp.NPhotoPicker;
import com.jaychang.nrv.NRecyclerView;
import com.jaychang.nrv.OnLoadMorePageListener;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.signaller.core.SignallerDataManager;
import com.jaychang.signaller.core.SignallerDbManager;
import com.jaychang.signaller.core.SignallerEvents;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.SocketManager;
import com.jaychang.signaller.core.UserData;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.core.model.Image;
import com.jaychang.signaller.core.model.ImageAttribute;
import com.jaychang.signaller.core.model.Payload;
import com.jaychang.signaller.core.model.SocketChatMessage;
import com.jaychang.signaller.ui.config.ChatMessageCellProvider;
import com.jaychang.signaller.ui.config.ChatRoomControlViewProvider;
import com.jaychang.signaller.ui.config.ChatRoomToolbarProvider;
import com.jaychang.signaller.ui.config.CustomChatMessageCellProvider;
import com.jaychang.signaller.ui.config.UIConfig;
import com.jaychang.signaller.ui.part.ChatMessageCell;
import com.jaychang.signaller.ui.part.DefaultChatMessageDateSeparatorCell;
import com.jaychang.signaller.util.LogUtils;
import com.jaychang.signaller.util.NetworkStateMonitor;
import com.jaychang.utils.AppUtils;
import com.jaychang.utils.ImageDimension;
import com.jaychang.utils.ImageUtils;
import com.jaychang.utils.SimpleTextChangedListener;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.jaychang.signaller.ui.config.ChatMessageType.IMAGE;
import static com.jaychang.signaller.ui.config.ChatMessageType.TEXT;

public class ChatRoomActivity extends RxAppCompatActivity {

  @BindView(R2.id.toolbarHolder)
  FrameLayout toolbarHolder;
  @BindView(R2.id.controlViewHolder)
  FrameLayout controlViewHolder;
  @BindView(R2.id.recyclerView)
  NRecyclerView recyclerView;
  @BindView(R2.id.rootView)
  RelativeLayout rootView;
  ImageView photoIconView;
  ImageView emojiIconView;
  EmojiEditText inputEditText;
  View sendMsgView;

  public static final String EXTRA_CHATROOM_ID = "EXTRA_CHATROOM_ID";

  private static final int OFF_SCREEN_CELLS_THRESHOLD = 24;

  private String chatRoomId;
  private ChatRoom chatRoom;
  private String cursor;
  private boolean hasMoreData;
  private boolean questScrollToBottom = true;
  private EmojiPopup emojiPopup;
  private ChatMessageCellProvider chatMessageCellProvider;
  private ChatRoomToolbarProvider chatRoomToolbarProvider;
  private ChatRoomControlViewProvider chatRoomControlViewProvider;
  private CustomChatMessageCellProvider customChatMessageCellProvider;
  private UIConfig uiConfig;

  public static void start(Context context, String chatRoomId) {
    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.putExtra(EXTRA_CHATROOM_ID, chatRoomId);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chatroom);
    ButterKnife.bind(this);
    init();
    loadChatMessages();
  }

  @Override
  protected void onResume() {
    super.onResume();
    UserData.getInstance().setInChatRoomPage(true);
  }

  @Override
  protected void onPause() {
    super.onPause();
    UserData.getInstance().setInChatRoomPage(false);
  }

  public void init() {
    initData();
    initUIConfig();
    initToolbar();
    setStatusBarColor();
    initRecyclerView();
    initControlView();
    initEmojiKeyboard();
    monitorNetworkState();
    monitorInput();
  }

  private void initUIConfig() {
    uiConfig = Signaller.getInstance().getUiConfig();
    chatMessageCellProvider = uiConfig.getChatMessageCellProvider();
    chatRoomToolbarProvider = uiConfig.getChatRoomToolbarProvider();
    chatRoomControlViewProvider = uiConfig.getChatRoomControlViewProvider();
    customChatMessageCellProvider = uiConfig.getCustomChatMessageCellProvider();
  }

  private void initData() {
    chatRoomId = getIntent().getStringExtra(EXTRA_CHATROOM_ID);
    chatRoom = SignallerDbManager.getInstance().getChatRoom(chatRoomId);

    UserData.getInstance().setCurrentChatRoomId(chatRoomId);
  }

  private void initToolbar() {
    View toolbar = chatRoomToolbarProvider.getToolbar(this, chatRoom);
    toolbarHolder.addView(toolbar);
  }

  private void setStatusBarColor() {
    AppUtils.setStatusBarColor(this, uiConfig.getToolbarBackgroundColor());
  }

  private void initRecyclerView() {
    recyclerView.useVerticalLinearMode();
    recyclerView.setCellSpacing(8);
    recyclerView.setOnLoadMoreListener(true, new OnLoadMorePageListener() {
      @Override
      public void onLoadMore(int page) {
        if (hasMoreData) {
          loadChatMessages();
        }
      }

      @Override
      public int getThreshold() {
        return OFF_SCREEN_CELLS_THRESHOLD;
      }
    });
  }

  private void initControlView() {
    View controlView = LayoutInflater.from(this).inflate(chatRoomControlViewProvider.getLayoutRes(), null);
    inputEditText = (EmojiEditText) controlView.findViewById(chatRoomControlViewProvider.getInputEditTextId());
    emojiIconView = (ImageView) controlView.findViewById(chatRoomControlViewProvider.getEmojiIconViewId());
    photoIconView = (ImageView) controlView.findViewById(chatRoomControlViewProvider.getPhotoIconViewId());
    sendMsgView = controlView.findViewById(chatRoomControlViewProvider.getSendViewId());

    emojiIconView.setOnClickListener(view -> {
      showEmojiKeyboard();
    });

    photoIconView.setOnClickListener(view -> {
      showPhotoPicker();
    });

    sendMsgView.setOnClickListener(view -> {
      addTextMessage();
    });

    controlViewHolder.addView(controlView);
  }

  private void initEmojiKeyboard() {
    emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
      .setOnEmojiPopupShownListener(() -> emojiIconView.setImageResource(R.drawable.ic_keyboard))
      .setOnEmojiPopupDismissListener(() -> emojiIconView.setImageResource(R.drawable.ic_emoji))
      .setOnSoftKeyboardCloseListener(() -> emojiPopup.dismiss())
      .build(inputEditText);
  }

  private void monitorNetworkState() {
    NetworkStateMonitor.getInstance()
      .monitor(this)
      .subscribe(networkState -> {
        handleInput();
      });
  }

  private void monitorInput() {
    inputEditText.addTextChangedListener(new SimpleTextChangedListener() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        handleInput();
      }
    });
  }

  private void handleInput() {
    boolean hasEnterText = !TextUtils.isEmpty(inputEditText.getText());
    boolean isSocketConnected = SocketManager.getInstance().isConnected();
    boolean isNetworkConnected = NetworkStateMonitor.getInstance().isConnected(this);

    if (hasEnterText && isSocketConnected && isNetworkConnected) {
      sendMsgView.setEnabled(true);
    } else {
      sendMsgView.setEnabled(false);
    }

    if (isSocketConnected && isNetworkConnected) {
      photoIconView.setEnabled(true);
      photoIconView.setAlpha(1f);
    } else {
      photoIconView.setEnabled(false);
      photoIconView.setAlpha(0.7f);
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onMsgReceived(SignallerEvents.OnMsgReceivedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    ChatMessage chatMessage = SignallerDbManager.getInstance().getChatMessage(event.msgId);
    handleChatMessage(chatMessage);
  }

  private void handleChatMessage(ChatMessage message) {
    if (message.isImage()) {
      if (!message.isOwnMessage()) {
        addOtherImageMessageCell(message);
      }
    } else if (message.isText()) {
      if (!message.isOwnMessage()) {
        addOtherTextMessageCell(message);
      }
    } else {
      if (customChatMessageCellProvider != null) {
        addCustomMessageCell(message);
      }
    }
  }

  private void loadChatMessages() {
    SignallerDataManager.getInstance().getChatMessages(chatRoom.getReceiver().getUserId(), cursor)
      .subscribe(response -> {
          cursor = response.cursor;
          hasMoreData = response.hasMore;
          bindChatMessages(response.chatMessages);
          scrollToBottomOnce();
        },
        error -> {
//          LogUtils.d(error.getMessage());
        });
  }

  private void bindChatMessages(List<ChatMessage> chatMessages) {
    List<ChatMessage> reversedMessages = new ArrayList<>(chatMessages);
    Collections.reverse(reversedMessages);

    int totalCountBefore = recyclerView.getCellsCount();

    for (int i = 0; i < reversedMessages.size(); i++) {
      ChatMessage message = reversedMessages.get(i);

      int nextPos = i + 1;
      boolean isSameSender = false;
      if (nextPos > reversedMessages.size() - 1) {
        isSameSender = false;
      } else if (message.isSameSender(reversedMessages.get(nextPos))) {
        isSameSender = true;
      }

      ChatMessageCell cell = null;
      if (message.isCustomType() && customChatMessageCellProvider != null) {
        cell = customChatMessageCellProvider.getCustomChatMessageCells(message);
      } else if (message.isOwnMessage() && message.isImage() && isSameSender) {
        cell = chatMessageCellProvider.getOwnChatMessageCell(IMAGE, message);
      } else if (message.isOwnMessage() && message.isImage() && !isSameSender) {
        cell = chatMessageCellProvider.getOwnChatMessageCell(IMAGE, message);
      } else if (message.isOwnMessage() && !message.isImage() && isSameSender) {
        cell = chatMessageCellProvider.getOwnChatMessageCell(TEXT, message);
      } else if (message.isOwnMessage() && !message.isImage() && !isSameSender) {
        cell = chatMessageCellProvider.getOwnChatMessageCell(TEXT, message);
      } else if (!message.isOwnMessage() && message.isImage() && isSameSender) {
        cell = chatMessageCellProvider.getOtherChatMessageCell(IMAGE, message);
      } else if (!message.isOwnMessage() && message.isImage() && !isSameSender) {
        cell = chatMessageCellProvider.getOtherChatMessageCell(IMAGE, message);
      } else if (!message.isOwnMessage() && !message.isImage() && isSameSender) {
        cell = chatMessageCellProvider.getOtherChatMessageCell(TEXT, message);
      } else if (!message.isOwnMessage() && !message.isImage() && !isSameSender) {
        cell = chatMessageCellProvider.getOtherChatMessageCell(TEXT, message);
      }

      if (cell == null) {
        return;
      }

      cell.setCallback(msg -> {
        goToPhotoPage(msg.getImage().getUrl());
      });

      recyclerView.addCell(cell, 0);

      // date cell
      boolean isSameDate = false;
      if (nextPos > reversedMessages.size() - 1) {
        isSameDate = true;
      } else if (message.isSameDate(reversedMessages.get(nextPos))) {
        isSameDate = true;
      }

      if (!isSameDate) {
        recyclerView.addCell(new DefaultChatMessageDateSeparatorCell(message.getMtime()), 0);
      }
    }

    recyclerView.getAdapter().notifyItemRangeInserted(0, recyclerView.getCellsCount() - totalCountBefore);
  }

  private void showEmojiKeyboard() {
    emojiPopup.toggle();
  }

  private void showPhotoPicker() {
    int colorPrimary = uiConfig.getToolbarBackgroundColor();

    NPhotoPicker.with(this)
      .toolbarColor(colorPrimary)
      .statusBarColor(colorPrimary)
      .selectedBorderColor(colorPrimary)
      .pickSinglePhotoFromAlbum()
      .subscribe(
        uri -> {
          addImageMessage(uri);
        },
        error -> {
          LogUtils.d(error.getMessage());
        }
      );
  }

  private void addTextMessage() {
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setMtime(System.currentTimeMillis());
    chatMessage.setType("text");
    chatMessage.setContent(inputEditText.getText().toString());
    addOwnTextMessageCell(chatMessage);
    sendTextMessage();
    clearInput();
  }

  private void sendTextMessage() {
    SocketChatMessage socketChatMessage = new SocketChatMessage();
    socketChatMessage.setRoomId(chatRoomId);

    long curTimestamp = System.currentTimeMillis();

    ChatMessage message = new ChatMessage();
    message.setTimestamp(curTimestamp);
    message.setType("text");
    message.setContent(inputEditText.getText().toString());
    socketChatMessage.setMessage(message);

    Payload payload = new Payload();
    payload.setTimestamp(curTimestamp);
    socketChatMessage.setPayload(payload);

    SignallerDbManager.getInstance().addPendingChatMessageAsync(socketChatMessage, () -> {
      SocketManager.getInstance().send(socketChatMessage);
    });
  }

  private void clearInput() {
    inputEditText.setText("");
  }

  private void uploadPhotoAndSendImageMsg(Uri uri) {
    SignallerDataManager.getInstance().uploadPhoto(uri)
      .subscribe(image -> {
          LogUtils.d("photo uploaded: " + uri.toString());
          sendImageMessage(image.getResourceId());
        },
        error -> {
          LogUtils.d(error.getMessage());
        });
  }

  private void addImageMessage(Uri uri) {
    ChatMessage message = new ChatMessage();
    message.setMtime(System.currentTimeMillis());
    Image image = new Image();
    ImageDimension dimension = ImageUtils.getImageDimensionFromUri(uri);
    image.setAttributes(new ImageAttribute(dimension.getWidth(), dimension.getHeight()));
    image.setUrl(uri.toString());
    message.setImage(image);
    addOwnImageMessageCell(message);
    uploadPhotoAndSendImageMsg(uri);
  }

  private void sendImageMessage(String imageResourceId) {
    SocketChatMessage socketChatMessage = new SocketChatMessage();
    socketChatMessage.setRoomId(chatRoomId);

    long curTimestamp = System.currentTimeMillis();

    ChatMessage message = new ChatMessage();
    message.setTimestamp(curTimestamp);
    message.setType("image");
    message.setContent(imageResourceId);
    socketChatMessage.setMessage(message);

    Payload payload = new Payload();
    payload.setTimestamp(curTimestamp);
    socketChatMessage.setPayload(payload);

    SignallerDbManager.getInstance().addPendingChatMessageAsync(socketChatMessage, () -> {
      LogUtils.d("send image to server.");
      SocketManager.getInstance().send(socketChatMessage);
    });
  }

  private void addOwnTextMessageCell(ChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOwnChatMessageCell(TEXT, message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addOwnImageMessageCell(ChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOwnChatMessageCell(IMAGE, message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addOtherTextMessageCell(ChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOtherChatMessageCell(TEXT, message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addOtherImageMessageCell(ChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOtherChatMessageCell(IMAGE, message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addCustomMessageCell(ChatMessage message) {
    ChatMessageCell cell = customChatMessageCellProvider.getCustomChatMessageCells(message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void scrollToBottom() {
    recyclerView.getLayoutManager().scrollToPosition(recyclerView.getCellsCount() - 1);
  }

  private void scrollToBottomOnce() {
    if (recyclerView.getCellsCount() > 0 && questScrollToBottom) {
      questScrollToBottom = false;
      scrollToBottom();
    }
  }

  private void goToPhotoPage(String url) {

  }

}
