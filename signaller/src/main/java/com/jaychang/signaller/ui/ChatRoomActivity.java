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
import com.jaychang.signaller.core.NetworkStateMonitor;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.SignallerDataManager;
import com.jaychang.signaller.core.SignallerDbManager;
import com.jaychang.signaller.core.SignallerEvents;
import com.jaychang.signaller.core.SocketManager;
import com.jaychang.signaller.core.UserData;
import com.jaychang.signaller.core.model.SignallerChatMessage;
import com.jaychang.signaller.core.model.SignallerImage;
import com.jaychang.signaller.core.model.SignallerImageAttribute;
import com.jaychang.signaller.core.model.SignallerPayload;
import com.jaychang.signaller.core.model.SignallerSocketChatMessage;
import com.jaychang.signaller.core.push.SignallerNotificationManager;
import com.jaychang.signaller.ui.config.ChatMessageCellProvider;
import com.jaychang.signaller.ui.config.ChatRoomControlViewProvider;
import com.jaychang.signaller.ui.config.ChatRoomToolbarProvider;
import com.jaychang.signaller.ui.config.CustomChatMessageCellProvider;
import com.jaychang.signaller.ui.config.DateSeparatorViewProvider;
import com.jaychang.signaller.ui.config.UIConfig;
import com.jaychang.signaller.ui.part.ChatMessageCell;
import com.jaychang.signaller.ui.part.DateSeparatorItemDecoration;
import com.jaychang.signaller.util.GsonUtils;
import com.jaychang.signaller.util.LogUtils;
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
  NRecyclerView messageList;
  @BindView(R2.id.rootView)
  RelativeLayout rootView;
  ImageView photoIconView;
  ImageView emojiIconView;
  EmojiEditText inputEditText;
  View sendMsgView;

  interface MessageCallback {
    void onMessageSavedToDb(SignallerSocketChatMessage message);
  }

  public static final String EXTRA_CHAT_ROOM_ID = "EXTRA_CHAT_ROOM_ID";
  public static final String EXTRA_TITLE = "EXTRA_TITLE";
  public static final String EXTRA_USER_ID = "EXTRA_USER_ID";

  private static final int OFF_SCREEN_CELLS_THRESHOLD = 24;

  private String chatRoomId;
  private String title;
  private String userId;
  private String cursor;
  private boolean hasMoreData;
  private boolean questScrollToBottom = true;
  private EmojiPopup emojiPopup;
  private ChatMessageCellProvider chatMessageCellProvider;
  private ChatRoomToolbarProvider chatRoomToolbarProvider;
  private ChatRoomControlViewProvider chatRoomControlViewProvider;
  private CustomChatMessageCellProvider customChatMessageCellProvider;
  private DateSeparatorViewProvider dateSeparatorViewProvider;
  private boolean isShowChatMessageDateSeparator;
  private UIConfig uiConfig;

  public static void start(Context context, String chatRoomId, String userId, String username) {
    Intent intent = new Intent(context, ChatRoomActivity.class);
    intent.putExtra(EXTRA_CHAT_ROOM_ID, chatRoomId);
    intent.putExtra(EXTRA_USER_ID, userId);
    intent.putExtra(EXTRA_TITLE, username);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.sig_activity_chatroom);
    ButterKnife.bind(this);
    init();
    loadChatMessages();
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
    cancelNotificationIfNeed();
  }

  private void initUIConfig() {
    uiConfig = Signaller.getInstance().getUiConfig();
    chatRoomToolbarProvider = uiConfig.getChatRoomToolbarProvider();
    chatRoomControlViewProvider = uiConfig.getChatRoomControlViewProvider();
    chatMessageCellProvider = uiConfig.getChatMessageCellProvider();
    customChatMessageCellProvider = uiConfig.getCustomChatMessageCellProvider();
    dateSeparatorViewProvider = uiConfig.getDateSeparatorViewProvider();
    isShowChatMessageDateSeparator = uiConfig.isShowDateSeparatorView();
  }

  private void initData() {
    chatRoomId = getIntent().getStringExtra(EXTRA_CHAT_ROOM_ID);
    userId = getIntent().getStringExtra(EXTRA_USER_ID);
    title = getIntent().getStringExtra(EXTRA_TITLE);

    UserData.getInstance().setCurrentChatRoomId(chatRoomId);
  }

  private void initToolbar() {
    View toolbar = chatRoomToolbarProvider.getToolbar(this, title);
    toolbarHolder.addView(toolbar);
  }

  private void setStatusBarColor() {
    int statusBarColor = Signaller.getInstance().getUiConfig().getChatRoomStatusBarBackgroundColor();
    if (statusBarColor != 0) {
      AppUtils.setStatusBarColor(this, statusBarColor);
    }
  }

  private void initRecyclerView() {
    messageList.useVerticalLinearMode();
    messageList.setCellSpacing(8);
    if (isShowChatMessageDateSeparator) {
      messageList.addItemDecoration(new DateSeparatorItemDecoration(dateSeparatorViewProvider));
    }
    messageList.setOnLoadMoreListener(true, new OnLoadMorePageListener() {
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
  }

  private void cancelNotificationIfNeed() {
    SignallerNotificationManager.cancelNotification(userId);
  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
    UserData.getInstance().setInChatRoomPage(true);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
    UserData.getInstance().setInChatRoomPage(false);
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onMsgReceived(SignallerEvents.OnMsgReceivedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    SignallerChatMessage chatMessage = SignallerDbManager.getInstance().getChatMessage(event.msgId);
    handleChatMessage(chatMessage);
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onSocketConnected(SignallerEvents.OnSocketConnectedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    handleInput();
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onSocketDisconnected(SignallerEvents.OnSocketDisconnectedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    handleInput();
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onSocketConnecting(SignallerEvents.OnSocketConnectingEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    handleInput();
  }

  private void handleChatMessage(SignallerChatMessage message) {
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
    SignallerDataManager.getInstance().getChatMessages(userId, cursor)
      .subscribe(response -> {
          cursor = response.cursor;
          hasMoreData = response.hasMore;
          bindChatMessages(response.chatMessages);
          scrollToBottomOnce();
        },
        error -> {
        });
  }

  private void bindChatMessages(List<SignallerChatMessage> chatMessages) {
    List<SignallerChatMessage> reversedMessages = new ArrayList<>(chatMessages);
    Collections.reverse(reversedMessages);

    int totalCountBefore = messageList.getCellsCount();

    for (int i = 0; i < reversedMessages.size(); i++) {
      SignallerChatMessage message = reversedMessages.get(i);

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

      messageList.addCell(cell, 0);
    }

    messageList.getAdapter().notifyItemRangeInserted(0, messageList.getCellsCount() - totalCountBefore);
  }

  private void showEmojiKeyboard() {
    emojiPopup.toggle();
  }

  private void showPhotoPicker() {
      NPhotoPicker.with(this)
        .toolbarColor(uiConfig.getChatRoomStatusBarBackgroundColor())
        .statusBarColor(uiConfig.getChatRoomStatusBarBackgroundColor())
        .selectedBorderColor(uiConfig.getChatRoomStatusBarBackgroundColor())
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
    SignallerChatMessage chatMessage = new SignallerChatMessage();
    chatMessage.setTimestamp(System.currentTimeMillis());
    chatMessage.setType("text");
    chatMessage.setSent(false);
    chatMessage.setContent(inputEditText.getText().toString());

    addOwnTextMessageCell(chatMessage);
    addChatMessageToDb(chatMessage, socketChatMessage -> {
      LogUtils.d("sent text msg to server.");
      SocketManager.getInstance().send(socketChatMessage);
    });
    clearInput();
  }

  private void addChatMessageToDb(SignallerChatMessage chatMessage, MessageCallback callback) {
    // save temp msg to db
    SignallerDbManager.getInstance().saveChatMessageAsync(chatMessage);
    // save to pending queue
    SignallerSocketChatMessage socketChatMessage = new SignallerSocketChatMessage();
    socketChatMessage.setRoomId(chatRoomId);
    SignallerPayload payload = new SignallerPayload();
    payload.setTimestamp(chatMessage.getTimestamp());
    socketChatMessage.setPayloadJson(GsonUtils.getGson().toJson(payload));
    socketChatMessage.setPayloadModel(payload);
    socketChatMessage.setMessage(chatMessage);
    SignallerDbManager.getInstance().addPendingChatMessageAsync(socketChatMessage, () -> {
      LogUtils.d("saved chat msg to db and queue.");
      callback.onMessageSavedToDb(socketChatMessage);
    });
  }

  private void clearInput() {
    inputEditText.setText("");
  }

  private void addImageMessage(Uri uri) {
    SignallerChatMessage message = new SignallerChatMessage();
    long time = System.currentTimeMillis();
    message.setMsgTime(time);
    SignallerImage image = new SignallerImage();
    ImageDimension dimension = ImageUtils.getImageDimensionFromUri(uri);
    image.setAttributes(new SignallerImageAttribute(dimension.getWidth(), dimension.getHeight()));
    image.setUrl(uri.toString());
    message.setImage(image);
    message.setTimestamp(time);
    message.setType("image");
    message.setSent(false);

    addOwnImageMessageCell(message);
    addChatMessageToDb(message, socketChatMessage -> {
      uploadPhotoAndSendImageMsg(uri, socketChatMessage);
    });
  }

  private void uploadPhotoAndSendImageMsg(Uri uri, SignallerSocketChatMessage socketChatMessage) {
    SignallerDataManager.getInstance().uploadPhoto(uri)
      .subscribe(image -> {
          LogUtils.d("photo uploaded: " + uri.toString());
          socketChatMessage.getMessage().setContent(image.getResourceId());
          SocketManager.getInstance().send(socketChatMessage);
          LogUtils.d("sent image msg to server.");
        },
        error -> {
          LogUtils.d("photo upload fail.");
        });
  }

  private void addOwnTextMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOwnChatMessageCell(TEXT, message);
    messageList.addCell(cell);
    messageList.getAdapter().notifyItemInserted(messageList.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addOwnImageMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOwnChatMessageCell(IMAGE, message);
    messageList.addCell(cell);
    messageList.getAdapter().notifyItemInserted(messageList.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addOtherTextMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOtherChatMessageCell(TEXT, message);
    messageList.addCell(cell);
    messageList.getAdapter().notifyItemInserted(messageList.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addOtherImageMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOtherChatMessageCell(IMAGE, message);
    messageList.addCell(cell);
    messageList.getAdapter().notifyItemInserted(messageList.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addCustomMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = customChatMessageCellProvider.getCustomChatMessageCells(message);
    messageList.addCell(cell);
    messageList.getAdapter().notifyItemInserted(messageList.getCellsCount() - 1);
    scrollToBottom();
  }

  private void scrollToBottom() {
    messageList.getLayoutManager().scrollToPosition(messageList.getCellsCount() - 1);
  }

  private void scrollToBottomOnce() {
    if (messageList.getCellsCount() > 0 && questScrollToBottom) {
      questScrollToBottom = false;
      scrollToBottom();
    }
  }

  private void goToPhotoPage(String url) {

  }

}
