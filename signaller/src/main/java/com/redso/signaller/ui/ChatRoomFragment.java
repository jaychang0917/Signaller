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
import com.redso.signaller.core.DataManager;
import com.redso.signaller.core.DatabaseManager;
import com.redso.signaller.core.Events;
import com.redso.signaller.core.NetworkStateMonitor;
import com.redso.signaller.core.ProxyManager;
import com.redso.signaller.core.Signaller;
import com.redso.signaller.core.SocketManager;
import com.redso.signaller.core.UserData;
import com.redso.signaller.core.model.ChatMessage;
import com.redso.signaller.core.model.Image;
import com.redso.signaller.core.model.ImageAttribute;
import com.redso.signaller.core.model.Payload;
import com.redso.signaller.core.model.SocketChatMessage;
import com.redso.signaller.core.push.SignallerPushNotificationManager;
import com.redso.signaller.util.ChatUtils;
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

import static com.redso.signaller.ui.ChatMessageType.IMAGE;
import static com.redso.signaller.ui.ChatMessageType.TEXT;


public class ChatRoomFragment extends RxFragment implements ChatRoomOperations {

  interface MessageCallback {
    void onMessageSavedToDb(SocketChatMessage message);
  }

  public interface PickPhotoCallback {
    void onPickPhoto();
  }

  public static final String EXTRA_CHAT_ID = "EXTRA_CHAT_ID";
  public static final String EXTRA_CHAT_ROOM_ID = "EXTRA_CHAT_ROOM_ID";
  private static final int OFF_SCREEN_CELLS_THRESHOLD = 24;

  private SimpleRecyclerView messageRecyclerView;
  private FrameLayout messageInputViewPlaceholder;
  private ImageView photoIconView;
  private ImageView emojiIconView;
  private EmojiEditText inputEditText;
  private View sendMsgView;
  private EmojiPopup emojiPopup;

  private ChatMessageCellProvider chatMessageCellProvider;
  private ChatRoomMessageInputViewProvider chatRoomMessageInputViewProvider;
  private ChatRoomDateSectionViewProvider chatRoomDateSectionViewProvider;
  private int chatRoomPhotoPickerThemeColor;
  private int chatRoomEmptyStateViewRes;
  private View chatRoomEmptyStateView;
  private int chatRoomBackgroundRes;
  private PickPhotoCallback pickPhotoCallback;

  private String chatRoomId;
  private String chatId;
  private String cursor;
  private boolean hasMoreData;
  private boolean questScrollToBottom = true;

  static ChatRoomFragment newInstance(String chatRoomId, String userId) {
    ChatRoomFragment fragment = new ChatRoomFragment();
    Bundle bundle = new Bundle();
    bundle.putString(EXTRA_CHAT_ROOM_ID, chatRoomId);
    bundle.putString(EXTRA_CHAT_ID, userId);
    fragment.setArguments(bundle);
    return fragment;
  }

  public static ChatRoomFragment fromUserId(String userId) {
    String chatRoomId = ChatUtils.createChatRoomId(UserData.getInstance().getUserId(), userId);
    return newInstance(chatRoomId, userId);
  }

  public static ChatRoomFragment fromGroupId(String groupId) {
    return newInstance(groupId, groupId);
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
    ProxyManager.getInstance().setChatRoomFragmentProxy(new ChatRoomFragmentProxy(this));
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    ProxyManager.getInstance().setChatRoomFragmentProxy(null);
  }

  public void init() {
    initViews(getView());
    initData();
    initUIConfig();
    initRecyclerView();
    initMessageInputView();
    monitorNetworkState();
    monitorInput();
    cancelNotificationIfNeed();
    clearUnreadCount();
    clearEvents();
  }

  private void initViews(View rootView) {
    messageRecyclerView = (SimpleRecyclerView) rootView.findViewById(R.id.messageRecyclerView);
    messageInputViewPlaceholder = (FrameLayout) rootView.findViewById(R.id.controlViewPlaceHolder);
  }

  private void initData() {
    chatId = getArguments().getString(EXTRA_CHAT_ID);
    chatRoomId = getArguments().getString(EXTRA_CHAT_ROOM_ID);

    UserData.getInstance().setCurrentChatRoomId(chatRoomId);
  }

  private void initUIConfig() {
    UIConfig uiConfig = Signaller.getInstance().getUiConfig();
    chatMessageCellProvider = uiConfig.getChatMessageCellProvider();
    chatRoomMessageInputViewProvider = uiConfig.getChatRoomMessageInputViewProvider();
    chatRoomDateSectionViewProvider = uiConfig.getChatRoomDateSectionViewProvider();
    chatRoomPhotoPickerThemeColor = uiConfig.getChatRoomPhotoPickerThemeColor();
    chatRoomEmptyStateViewRes = uiConfig.getChatRoomEmptyStateViewRes();
    chatRoomEmptyStateView = uiConfig.getChatRoomEmptyStateView();
    chatRoomBackgroundRes = uiConfig.getChatRoomBackgroundRes();
  }

  private void initRecyclerView() {
    // load more
    messageRecyclerView.setLoadMoreToTop(true);
    messageRecyclerView.setAutoLoadMoreThreshold(OFF_SCREEN_CELLS_THRESHOLD);
    messageRecyclerView.setOnLoadMoreListener(simpleRecyclerView -> {
      if (hasMoreData) {
        loadChatMessages();
      }
    });

    // date section
    if (chatRoomDateSectionViewProvider != null) {
      messageRecyclerView.setSectionHeader(new SectionHeaderProviderAdapter<ChatMessage>() {
        @NonNull
        @Override
        public View getSectionHeaderView(ChatMessage chatMessage, int position) {
          return chatRoomDateSectionViewProvider.getChatRoomDateSectionView(chatMessage);
        }

        @Override
        public boolean isSameSection(ChatMessage item, ChatMessage nextItem) {
          return chatRoomDateSectionViewProvider.isSameSection(item, nextItem);
        }
      });
    }

    // empty state view
    if (chatRoomEmptyStateViewRes != 0) {
      messageRecyclerView.setEmptyStateView(chatRoomEmptyStateViewRes);
    } else if (chatRoomEmptyStateView != null) {
      messageRecyclerView.setEmptyStateView(chatRoomEmptyStateView);
    }

    // background
    if (chatRoomBackgroundRes != 0) {
      messageRecyclerView.setBackgroundResource(chatRoomBackgroundRes);
    }
  }

  private void initMessageInputView() {
    View messageInputView = LayoutInflater.from(getContext()).inflate(chatRoomMessageInputViewProvider.getLayoutRes(), null);
    inputEditText = (EmojiEditText) messageInputView.findViewById(chatRoomMessageInputViewProvider.getInputEditTextId());
    photoIconView = (ImageView) messageInputView.findViewById(chatRoomMessageInputViewProvider.getPhotoPickerIconViewId());
    sendMsgView = messageInputView.findViewById(chatRoomMessageInputViewProvider.getSendMessageViewId());

    EmojiKeyboardViewInfo emojiKeyboardViewInfo = chatRoomMessageInputViewProvider.getEmojiKeyboardViewInfo();
    if (emojiKeyboardViewInfo != null) {
      emojiIconView = (ImageView) messageInputView.findViewById(emojiKeyboardViewInfo.getEmojiIconImageViewId());
      emojiIconView.setOnClickListener(view -> {
        showEmojiKeyboard();
      });
      initEmojiKeyboard(emojiKeyboardViewInfo);
    }

    photoIconView.setOnClickListener(view -> {
      if (pickPhotoCallback != null) {
        pickPhotoCallback.onPickPhoto();
      } else {
        showDefaultPhotoPicker();
      }
    });

    sendMsgView.setOnClickListener(view -> {
      sendTextMessage(inputEditText.getText().toString());
    });

    messageInputViewPlaceholder.addView(messageInputView);
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
      .compose(bindToLifecycle())
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

  private void clearUnreadCount() {
    DatabaseManager.getInstance().clearUnreadMessageCount(chatRoomId);
    DataManager.getInstance().clearUnreadCount(chatRoomId);
  }

  // in init state, clear events (OnMsgReceivedEvent, OnMsgSentEvent) to
  // prevent duplicate calls like loadChatMessages().
  private void clearEvents() {
    EventBus.getDefault().removeStickyEvent(Events.OnMsgReceivedEvent.class);
    EventBus.getDefault().removeStickyEvent(Events.OnMsgSentEvent.class);
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

    // clear that unread count for this chat room
    EventBus.getDefault().postSticky(new Events.ClearUnreadCountEvent(chatRoomId));
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onMsgReceived(Events.OnMsgReceivedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    ChatMessage chatMessage = DatabaseManager.getInstance().getChatMessage(event.chatRoomId, event.msgId);
    handleChatMessage(chatMessage);
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onMsgSent(Events.OnMsgSentEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    updateChatMessage(event.messageCellIndex, event.chatMessage);
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onSocketConnected(Events.OnSocketConnectedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    handleInput();
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onSocketDisconnected(Events.OnSocketDisconnectedEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    handleInput();
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void onSocketConnecting(Events.OnSocketConnectingEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    handleInput();
  }

  private void handleChatMessage(ChatMessage message) {
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

  private void updateChatMessage(int messageCellIndex, ChatMessage message) {
    // if the OnMsgSentEvent come back when re-enter chat room (send photo -> back -> come back),
    // since the data is not sync with server,
    // the messageCellIndex may lager than data's size, then we ignore updating it.
    if (messageCellIndex >= messageRecyclerView.getItemCount()) {
      return;
    }

    ChatMessageCell cell = (ChatMessageCell) messageRecyclerView.getCell(messageCellIndex);
    if (cell != null) {
      cell.setChatMessage(message);
      messageRecyclerView.getAdapter().notifyItemChanged(messageCellIndex);
      LogUtils.d(String.format("Chat message cell (%1$s) is updated.", message));
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
    DataManager.getInstance().getChatMessages(chatId, cursor)
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

  private void bindChatMessages(List<ChatMessage> chatMessages) {
    List<ChatMessageCell> cells = new ArrayList<>();
    for (int i = 0; i < chatMessages.size(); i++) {
      ChatMessage message = chatMessages.get(i);

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

  private void showDefaultPhotoPicker() {
    int themeColor = chatRoomPhotoPickerThemeColor != 0 ?
      chatRoomPhotoPickerThemeColor : android.R.color.background_dark;

    NPhotoPicker.with(getContext())
      .toolbarColor(themeColor)
      .statusBarColor(themeColor)
      .selectedBorderColor(themeColor)
      .pickSinglePhoto()
      .subscribe(uri -> {
        sendImageMessage(uri);
      }, error -> {
        LogUtils.e("Fail to show photo picker:" + error.getMessage());
      });
  }

  @Override
  public void sendTextMessage(String msg) {
    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setTimestamp(System.currentTimeMillis());
    chatMessage.setType("text");
    chatMessage.setSent(false);
    chatMessage.setContent(msg);

    addOwnTextMessageCell(chatMessage);
    addChatMessageToDb(chatMessage, socketChatMessage -> {
      SocketManager.getInstance().send(socketChatMessage);
      LogUtils.d(String.format("Try to sent text msg (%1$s) to server.", socketChatMessage.getMessage().getMsgId()));
    });
    clearInput();
  }

  private void addChatMessageToDb(ChatMessage chatMessage, MessageCallback callback) {
    // save temp msg to db
    DatabaseManager.getInstance().saveChatMessageAsync(chatMessage);
    // save to pending queue
    SocketChatMessage socketChatMessage = new SocketChatMessage();
    socketChatMessage.setRoomId(chatRoomId);
    Payload payload = new Payload();
    payload.setTimestamp(chatMessage.getTimestamp());
    payload.setMessageCellIndex(messageRecyclerView.getItemCount() - 1);
    socketChatMessage.setPayloadJson(GsonUtils.getGson().toJson(payload));
    socketChatMessage.setPayloadModel(payload);
    socketChatMessage.setMessage(chatMessage);
    DatabaseManager.getInstance().addPendingChatMessageAsync(socketChatMessage, () -> {
      LogUtils.d("Saved chat msg to db and queue.");
      callback.onMessageSavedToDb(socketChatMessage);
    });
  }

  private void clearInput() {
    inputEditText.setText("");
  }

  @Override
  public void sendImageMessage(Uri uri) {
    ChatMessage message = new ChatMessage();
    long time = System.currentTimeMillis();
    message.setMsgTime(time);
    Image image = new Image();
    ImageDimension dimension = ImageUtils.getImageDimensionFromUri(uri);
    image.setAttributes(new ImageAttribute(dimension.getWidth(), dimension.getHeight()));
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

  private void uploadPhotoAndSendImageMsg(Uri uri, SocketChatMessage socketChatMessage) {
    LogUtils.d("Trying to upload photo: " + uri.toString());
    DataManager.getInstance().uploadPhoto(uri)
      .subscribe(image -> {
          LogUtils.d("Photo uploaded: " + uri.toString());
          socketChatMessage.getMessage().setContent(image.getResourceId());
          SocketManager.getInstance().send(socketChatMessage);
          LogUtils.d(String.format("Try to sent image msg (%1$s) to server.", socketChatMessage.getMessage().getMsgId()));
        },
        error -> {
          LogUtils.d("Photo upload fail.");
        });
  }

  private void addOwnTextMessageCell(ChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOwnChatMessageCell(TEXT, message);
    messageRecyclerView.addCell(cell);
    scrollToBottom();
  }

  private void addOwnImageMessageCell(ChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOwnChatMessageCell(IMAGE, message);
    messageRecyclerView.addCell(cell);
    scrollToBottom();
  }

  private void addOtherTextMessageCell(ChatMessage message) {
    ChatMessageCell cell = chatMessageCellProvider.getOtherChatMessageCell(TEXT, message);
    messageRecyclerView.addCell(cell);
    scrollToBottom();
  }

  private void addOtherImageMessageCell(ChatMessage message) {
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

  public void setPickPhotoCallback(PickPhotoCallback pickPhotoCallback) {
    this.pickPhotoCallback = pickPhotoCallback;
  }

}
