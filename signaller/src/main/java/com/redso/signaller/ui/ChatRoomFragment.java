package com.redso.signaller.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jaychang.npp.NPhotoPicker;
import com.jaychang.srv.SimpleRecyclerView;
import com.jaychang.srv.decoration.SectionHeaderProviderAdapter;
import com.jaychang.utils.ImageDimension;
import com.jaychang.utils.ImageUtils;
import com.jaychang.utils.SimpleTextChangedListener;
import com.redso.signaller.R;
import com.redso.signaller.core.NetworkStateMonitor;
import com.redso.signaller.core.Signaller;
import com.redso.signaller.core.SignallerDataManager;
import com.redso.signaller.core.SignallerDbManager;
import com.redso.signaller.core.SignallerEvents;
import com.redso.signaller.core.SocketManager;
import com.redso.signaller.core.UserData;
import com.redso.signaller.core.model.SignallerChatMessage;
import com.redso.signaller.core.model.SignallerImage;
import com.redso.signaller.core.model.SignallerImageAttribute;
import com.redso.signaller.core.model.SignallerPayload;
import com.redso.signaller.core.model.SignallerSocketChatMessage;
import com.redso.signaller.core.push.SignallerPushNotificationManager;
import com.redso.signaller.util.GsonUtils;
import com.redso.signaller.util.LogUtils;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static com.redso.signaller.ui.ChatMessageType.IMAGE;
import static com.redso.signaller.ui.ChatMessageType.TEXT;


public class ChatRoomFragment extends RxFragment {

  interface MessageCallback {
    void onMessageSavedToDb(SignallerSocketChatMessage message);
  }

  public static final String EXTRA_CHAT_ID = "EXTRA_CHAT_ID";
  public static final String EXTRA_CHAT_ROOM_ID = "EXTRA_CHAT_ROOM_ID";
  private static final int OFF_SCREEN_CELLS_THRESHOLD = 24;

  private SimpleRecyclerView messageRecyclerView;
  private FrameLayout controlViewPlaceHolder;
  private ImageView photoIconView;
  private ImageView emojiIconView;
  private EmojiEditText inputEditText;
  private View sendMsgView;
  private EmojiPopup emojiPopup;

  private ChatMessageCellProvider chatMessageCellProvider;
  private ChatRoomControlViewProvider chatRoomControlViewProvider;
  private ChatRoomDateSectionViewProvider chatRoomDateSectionViewProvider;
  private ChatRoomThemeProvider chatRoomThemeProvider;

  private String chatId;
  private String chatRoomId;
  private String cursor;
  private boolean hasMoreData;
  private boolean questScrollToBottom = true;
  private boolean needClearUnreadCount;

  public static ChatRoomFragment newInstance(String chatId, String chatRoomId) {
    ChatRoomFragment fragment = new ChatRoomFragment();
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_CHAT_ID, chatId);
    bundle.putString(EXTRA_CHAT_ROOM_ID, chatRoomId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.sig_fragment_chatroom, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init();
    loadChatMessages();
  }

  public void init() {
    initViews(getView());
    initData();
    initUIConfig();
    initRecyclerView();
    initControlView();
    monitorNetworkState();
    monitorInput();
    cancelNotificationIfNeed();
  }

  private void initViews(View rootView) {
    messageRecyclerView = (SimpleRecyclerView) rootView.findViewById(R.id.messageRecyclerView);
    controlViewPlaceHolder = (FrameLayout) rootView.findViewById(R.id.controlViewPlaceHolder);
  }

  private void initData() {
    chatId = getArguments().getString(EXTRA_CHAT_ID);
    chatRoomId = getArguments().getString(EXTRA_CHAT_ROOM_ID);

    UserData.getInstance().setCurrentChatRoomId(chatRoomId);
  }

  private void initUIConfig() {
    UIConfig uiConfig = Signaller.getInstance().getUiConfig();
    chatMessageCellProvider = uiConfig.getChatMessageCellProvider();
    chatRoomControlViewProvider = uiConfig.getChatRoomControlViewProvider();
    chatRoomDateSectionViewProvider = uiConfig.getChatRoomDateSectionViewProvider();
    chatRoomThemeProvider = uiConfig.getChatRoomThemeProvider();
  }

  private void initRecyclerView() {
    if (chatRoomDateSectionViewProvider != null) {
      messageRecyclerView.setSectionHeader(new SectionHeaderProviderAdapter<SignallerChatMessage>() {
        @NonNull
        @Override
        public View getSectionHeaderView(SignallerChatMessage chatMessage, int position) {
          return chatRoomDateSectionViewProvider.getChatRoomDateSectionView(chatMessage);
        }

        @Override
        public boolean isSameSection(SignallerChatMessage item, SignallerChatMessage nextItem) {
          return chatRoomDateSectionViewProvider.isSameSection(item, nextItem);
        }
      });
    }

    messageRecyclerView.setLoadMoreToTop(true);
    messageRecyclerView.setAutoLoadMoreThreshold(OFF_SCREEN_CELLS_THRESHOLD);
    messageRecyclerView.setOnLoadMoreListener(simpleRecyclerView -> {
      if (hasMoreData) {
        loadChatMessages();
      }
    });
  }

  private void initControlView() {
    View controlView = LayoutInflater.from(getContext()).inflate(chatRoomControlViewProvider.getLayoutRes(), null);
    inputEditText = (EmojiEditText) controlView.findViewById(chatRoomControlViewProvider.getInputEditTextId());
    photoIconView = (ImageView) controlView.findViewById(chatRoomControlViewProvider.getPhotoIconViewId());
    sendMsgView = controlView.findViewById(chatRoomControlViewProvider.getSendMessageViewId());

    EmojiKeyboardViewInfo emojiKeyboardViewInfo = chatRoomControlViewProvider.getEmojiKeyboardViewInfo();
    if (emojiKeyboardViewInfo != null) {
      emojiIconView = (ImageView) controlView.findViewById(emojiKeyboardViewInfo.getEmojiIconViewId());
      emojiIconView.setOnClickListener(view -> {
        showEmojiKeyboard();
      });
      initEmojiKeyboard(emojiKeyboardViewInfo);
    }

    photoIconView.setOnClickListener(view -> {
      showPhotoPicker();
    });

    sendMsgView.setOnClickListener(view -> {
      addTextMessage();
    });

    controlViewPlaceHolder.addView(controlView);
  }

  private void initEmojiKeyboard(EmojiKeyboardViewInfo info) {
    emojiPopup = EmojiPopup.Builder.fromRootView(getView())
      .setOnEmojiPopupShownListener(() -> emojiIconView.setImageResource(info.getKeyboardIconResId()))
      .setOnEmojiPopupDismissListener(() -> emojiIconView.setImageResource(info.getEmojiIconResId()))
      .setOnSoftKeyboardCloseListener(() -> emojiPopup.dismiss())
      .build(inputEditText);
  }

  private void monitorNetworkState() {
    NetworkStateMonitor.getInstance()
      .monitor(getContext())
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
    boolean isNetworkConnected = NetworkStateMonitor.getInstance().isConnected(getContext());

    if (hasEnterText && isSocketConnected && isNetworkConnected) {
      sendMsgView.setEnabled(true);
    } else {
      sendMsgView.setEnabled(false);
    }
  }

  private void cancelNotificationIfNeed() {
    SignallerPushNotificationManager.cancelNotification(chatId);
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

    if (needClearUnreadCount) {
      EventBus.getDefault().postSticky(new SignallerEvents.ClearUnreadCountEvent(chatRoomId));
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onMsgReceived(SignallerEvents.OnMsgReceivedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    SignallerChatMessage chatMessage = SignallerDbManager.getInstance().getChatMessage(event.chatRoomId, event.msgId);
    handleChatMessage(chatMessage);

    needClearUnreadCount = true;
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
    if (message == null) {
      reload();
      SignallerPushNotificationManager.cancelNotification(chatId);
      return;
    }

    if (message.isImage()) {
      if (!message.isOwnMessage()) {
        addOtherImageMessageCell(message);
      }
    } else if (message.isText()) {
      if (!message.isOwnMessage()) {
        addOtherTextMessageCell(message);
      }
    }
  }

  private void reload() {
    cursor = null;
    messageRecyclerView.removeAllCells(false);
    questScrollToBottom = true;
    loadChatMessages();
  }

  private void loadChatMessages() {
    messageRecyclerView.setLoadingMore(true);
    SignallerDataManager.getInstance().getChatMessages(chatId, cursor)
      .compose(bindUntilEvent(FragmentEvent.DESTROY))
      .subscribe(response -> {
          cursor = response.cursor;
          hasMoreData = response.hasMore;
          bindChatMessages(response.chatMessages);
          LogUtils.d(String.format("Load %1$s messages", response.chatMessages.size()));
          scrollToBottomOnce();
          messageRecyclerView.setLoadingMore(false);
        },
        error -> {
          messageRecyclerView.setLoadingMore(false);
          LogUtils.e("Fail to load messages:" + error.getMessage());
        });
  }

  private void bindChatMessages(List<SignallerChatMessage> chatMessages) {
    List<ChatMessageCell> cells = new ArrayList<>();
    for (int i = 0; i < chatMessages.size(); i++) {
      SignallerChatMessage message = chatMessages.get(i);

      int nextPos = i + 1;
      boolean isSameSender = false;
      if (nextPos > chatMessages.size() - 1) {
        isSameSender = false;
      } else if (message.isSameSender(chatMessages.get(nextPos))) {
        isSameSender = true;
      }

      ChatMessageCell cell = null;
      if (message.isOwnMessage() && message.isImage() && isSameSender) {
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

      cells.add(cell);
    }

    messageRecyclerView.addCells(0, cells);
  }

  private void showEmojiKeyboard() {
    emojiPopup.toggle();
  }

  private void showPhotoPicker() {
    Subscriber<Uri> subscriber = new Subscriber<Uri>() {
      @Override
      public void onCompleted() {
      }

      @Override
      public void onError(Throwable error) {
        LogUtils.e("Fail to show photo picker:" + error.getMessage());
      }

      @Override
      public void onNext(Uri uri) {
        addImageMessage(uri);
      }
    };

    if (chatRoomThemeProvider != null) {
      NPhotoPicker.with(getContext())
        .toolbarColor(chatRoomThemeProvider.getPhotoPickerToolbarBackgroundColor())
        .statusBarColor(chatRoomThemeProvider.getStatusBarColor())
        .selectedBorderColor(chatRoomThemeProvider.getStatusBarColor())
        .pickSinglePhotoFromAlbum()
        .subscribe(subscriber);
    } else {
      NPhotoPicker.with(getContext())
        .pickSinglePhotoFromAlbum()
        .subscribe(subscriber);
    }
  }

  private void addTextMessage() {
    SignallerChatMessage chatMessage = new SignallerChatMessage();
    chatMessage.setTimestamp(System.currentTimeMillis());
    chatMessage.setType("text");
    chatMessage.setSent(false);
    chatMessage.setContent(inputEditText.getText().toString());

    addOwnTextMessageCell(chatMessage);
    addChatMessageToDb(chatMessage, socketChatMessage -> {
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
      LogUtils.d("Saved chat msg to db and queue.");
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
          LogUtils.d("Photo uploaded: " + uri.toString());
          socketChatMessage.getMessage().setContent(image.getResourceId());
          SocketManager.getInstance().send(socketChatMessage);
          LogUtils.d("Sent image msg to server.");
        },
        error -> {
          LogUtils.d("Photo upload fail.");
        });
  }

  private void addOwnTextMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOwnChatMessageCell(TEXT, message);
    messageRecyclerView.addCell(cell);
    scrollToBottom();
  }

  private void addOwnImageMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOwnChatMessageCell(IMAGE, message);
    messageRecyclerView.addCell(cell);
    scrollToBottom();
  }

  private void addOtherTextMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOtherChatMessageCell(TEXT, message);
    messageRecyclerView.addCell(cell);
    scrollToBottom();
  }

  private void addOtherImageMessageCell(SignallerChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOtherChatMessageCell(IMAGE, message);
    messageRecyclerView.addCell(cell);
    scrollToBottom();
  }

  private void scrollToBottom() {
    ((LinearLayoutManager) messageRecyclerView.getLayoutManager()).scrollToPositionWithOffset(messageRecyclerView.getItemCount() - 1, 0);
  }

  private void scrollToBottomOnce() {
    if (messageRecyclerView.getItemCount() > 0 && questScrollToBottom) {
      questScrollToBottom = false;
      scrollToBottom();
    }
  }

//  @Override
//  public void onBackPressed() {
//    super.onBackPressed();
//    if (isTaskRoot()) {
//      TaskStackBuilder.create(this)
//        .addNextIntentWithParentStack(new Intent(this, Signaller.getInstance().getAppConfig().getPushNotificationParentStack()))
//        .startActivities();
//    }
//  }

}
