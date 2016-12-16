package com.jaychang.signaller.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jaychang.npp.NPhotoPicker;
import com.jaychang.nrv.NRecyclerView;
import com.jaychang.nrv.OnLoadMorePageListener;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.signaller.core.DataManager;
import com.jaychang.signaller.core.Events;
import com.jaychang.signaller.core.SocketManager;
import com.jaychang.signaller.core.UserData;
import com.jaychang.signaller.core.model.ChatMessage;
import com.jaychang.signaller.core.model.Image;
import com.jaychang.signaller.core.model.SocketChatMessage;
import com.jaychang.signaller.util.LogUtils;
import com.jaychang.signaller.util.NetworkStateMonitor;
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
import butterknife.OnClick;

public class ChatRoomActivity extends RxAppCompatActivity {

  @BindView(R2.id.toolbar)
  Toolbar toolbar;
  @BindView(R2.id.recyclerView)
  NRecyclerView recyclerView;
  @BindView(R2.id.photoView)
  ImageView photoView;
  @BindView(R2.id.emojiView)
  ImageView emojiView;
  @BindView(R2.id.inputMessageView)
  EmojiEditText inputMessageView;
  @BindView(R2.id.sendMsgView)
  Button sendMsgView;
  @BindView(R2.id.rootView)
  RelativeLayout rootView;

  public static final String EXTRA_USER_ID = "EXTRA_USER_ID";
  public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
  public static final String EXTRA_CHATROOM_ID = "EXTRA_CHATROOM_ID";

  private String userId;
  private String username;
  private String chatRoomId;
  private String cursor;
  private boolean hasMoreData;
  private EmojiPopup emojiPopup;

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
    initToolbar();
    initRecyclerView();
    initEmojiKeyboard();
    monitorNetworkState();
    handleInput();
    disableInput();
  }

  private void initData() {
    userId = getIntent().getStringExtra(EXTRA_USER_ID);
    username = getIntent().getStringExtra(EXTRA_USERNAME);
    chatRoomId = getIntent().getStringExtra(EXTRA_CHATROOM_ID);

    UserData.getInstance().setCurrentChatRoomId(chatRoomId);
  }

  private void initToolbar() {
    toolbar.setNavigationOnClickListener(view -> {
      finish();
    });
    toolbar.setTitle(username);
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
        return 5;
      }
    });
  }

  private void initEmojiKeyboard() {
    emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
      .setOnEmojiPopupShownListener(() -> emojiView.setImageResource(android.R.drawable.star_on))
      .setOnEmojiPopupDismissListener(() -> emojiView.setImageResource(android.R.drawable.star_off))
      .setOnSoftKeyboardCloseListener(() -> emojiPopup.dismiss())
      .build(inputMessageView);
  }

  private void monitorNetworkState() {
    NetworkStateMonitor.getInstance()
      .monitor(this)
      .subscribe(networkState -> {
        boolean isNetworkConnected = networkState != NetworkStateMonitor.NetworkState.DISCONNECTED;
        if (isNetworkConnected) {
          enableInput();
        } else {
          disableInput();
        }
      });
  }

  private void handleInput() {
    inputMessageView.addTextChangedListener(new SimpleTextChangedListener() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!TextUtils.isEmpty(inputMessageView.getText()) && SocketManager.getInstance().isConnected()) {
          enableInput();
        } else {
          disableInput();
        }
      }
    });
  }

  private void disableInput() {
    photoView.setEnabled(false);
    sendMsgView.setEnabled(false);
  }

  private void enableInput() {
    photoView.setEnabled(true);
    sendMsgView.setEnabled(true);
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
  public void onMsgReceived(Events.OnMsgReceivedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    handleChatMessage(event.chatMessage);
  }

  private void handleChatMessage(ChatMessage message) {
    if (message.isImage()) {
      if (!message.isOwnMessage()) {
        addAnotherImageMessageCell(message);
      }
    } else if (message.isEvent()) {

    } else {
      if (!message.isOwnMessage()) {
        addAnotherTextMessageCell(message);
      }
    }
  }

  private void loadChatMessages() {
    DataManager.getInstance().getChatMessages(userId, cursor)
      .subscribe(response -> {
          cursor = response.cursor;
          hasMoreData = response.hasMore;
          bindChatMessages(response.chatMessages);
        },
        error -> {
          LogUtils.d(error.getMessage());
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
      if (message.isOwnMessage() && message.isImage() && isSameSender) {
        cell = new OwnImageMessageCell(message);
        ((OwnImageMessageCell) cell).setCallback(msg -> {
          goToPhotoPage(msg.image.url);
        });
      } else if (message.isOwnMessage() && message.isImage() && !isSameSender) {
        cell = new OwnImageMessageCell(message);
        ((OwnImageMessageCell) cell).setCallback(msg -> {
          goToPhotoPage(msg.image.url);
        });
      } else if (message.isOwnMessage() && !message.isImage() && isSameSender) {
        cell = new OwnTextMessageCell(message);
      } else if (message.isOwnMessage() && !message.isImage() && !isSameSender) {
        cell = new OwnTextMessageCell(message);
      } else if (!message.isOwnMessage() && message.isImage() && isSameSender) {
        cell = new AnotherImageMessageCell(message);
        ((AnotherImageMessageCell) cell).setCallback(msg -> {
          goToPhotoPage(msg.image.url);
        });
      } else if (!message.isOwnMessage() && message.isImage() && !isSameSender) {
        cell = new AnotherImageMessageCell(message);
        ((AnotherImageMessageCell) cell).setCallback(msg -> {
          goToPhotoPage(msg.image.url);
        });
      } else if (!message.isOwnMessage() && !message.isImage() && isSameSender) {
        cell = new AnotherTextMessageCell(message);
      } else if (!message.isOwnMessage() && !message.isImage() && !isSameSender) {
        cell = new AnotherTextMessageCell(message);
      }

      recyclerView.addCell(cell, 0);

      // date cell
      boolean isSameDate = false;
      if (nextPos > reversedMessages.size() - 1) {
        isSameDate = true;
      } else if (message.isSameDate(reversedMessages.get(nextPos))) {
        isSameDate = true;
      }

      if (!isSameDate) {
        recyclerView.addCell(new ChatMessageDateCell(message.mtime), 0);
      }
    }

    recyclerView.getAdapter().notifyItemRangeInserted(0, recyclerView.getCellsCount() - totalCountBefore);
  }

  @OnClick(R2.id.photoView)
  void showPhotoPickerDialog() {
    NPhotoPicker.with(this)
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

  @OnClick(R2.id.emojiView)
  void showEmojiKeyboard() {
    emojiPopup.toggle();
  }

  @OnClick(R2.id.sendMsgView)
  void addTextMessage() {
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.type = "text";
    chatMessage.content = inputMessageView.getText().toString();
    addOwnTextMessageCell(chatMessage);
    sendTextMessage();
  }

  private void sendTextMessage() {
    SocketChatMessage socketChatMessage = new SocketChatMessage();
    socketChatMessage.roomId = chatRoomId;
    ChatMessage message = new ChatMessage();
    message.type = "text";
    message.content = inputMessageView.getText().toString();
    socketChatMessage.message = message;

    SocketManager.getInstance().send(socketChatMessage);

    inputMessageView.setText("");
  }

  private void uploadPhotoAndSendImageMsg(Uri uri) {
    DataManager.getInstance().uploadPhoto(uri)
      .subscribe(image -> {
          sendImageMessage(image.resourceId);
        },
        error -> {
          LogUtils.d(error.getMessage());
        });
  }

  private void addImageMessage(Uri uri) {
    ChatMessage message = new ChatMessage();
    Image image = new Image();
    image.url = uri.toString();
    message.image = image;

    addOwnImageMessageCell(message);
    uploadPhotoAndSendImageMsg(uri);

    inputMessageView.setText("");
  }

  private void sendImageMessage(String imageResourceId) {
    SocketChatMessage socketChatMessage = new SocketChatMessage();
    socketChatMessage.roomId = chatRoomId;
    ChatMessage message = new ChatMessage();
    message.type = "image";
    message.content = imageResourceId;
    socketChatMessage.message = message;
    SocketManager.getInstance().send(socketChatMessage);
  }

  private void addOwnTextMessageCell(ChatMessage message) {
    OwnTextMessageCell cell = new OwnTextMessageCell(message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addOwnImageMessageCell(ChatMessage message) {
    OwnImageMessageCell cell = new OwnImageMessageCell(message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addAnotherTextMessageCell(ChatMessage message) {
    AnotherTextMessageCell cell = new AnotherTextMessageCell(message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void addAnotherImageMessageCell(ChatMessage message) {
    AnotherImageMessageCell cell = new AnotherImageMessageCell(message);
    recyclerView.addCell(cell);
    recyclerView.getAdapter().notifyItemInserted(recyclerView.getCellsCount() - 1);
    scrollToBottom();
  }

  private void scrollToBottom() {
    recyclerView.getLayoutManager().scrollToPosition(recyclerView.getCellsCount() - 1);
  }

  private void goToPhotoPage(String url) {

  }

}