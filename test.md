# Signaller

## Features
- [x] Text, image messaging
- [x] UI customization
- [x] Push notification

## Todo
- [ ] Prevent duplicate message request

## Table of Contents
- [Basic Usage](#basic_usage)
- [Advance Usage](#adv_usage)
    - [Connect / Disconnect Socket](#socket)
    - [Join / Leave Chat Room](#join_leave_room)
    - [Send Message](#send_msg)
    - [Go to chat page](#go_chat_page)
    - [Get Unread Message Count](#unread)
    - [Push Notification Handling](#push)
    - [Customize chatroom page](#cus_chatroom) 

## Sample project
<img src="https://github.com/jaychang0917/SimpleRecyclerView/blob/master/art/qr_code_1_1_9.png" width="100" height="100">

[Sample apk](https://github.com/jaychang0917/SimpleRecyclerView/blob/master/art/SimpleRecyclerView_1_1_9.apk)

## Installation
In your app level build.gradle :
```java
dependencies {
    compile 'com.github.jaychang0917:SimpleRecyclerView:1.1.10'
}
```

## <a name=basic_usage>Basic Usage</a>
### Step 1 - Configurate the signaller on demand
```java
public class App extends MultiDexApplication {

  @Override
  public void onCreate() {
    super.onCreate();

    initSignaller();
  }

  private void initSignaller() {
    AppConfig appConfig = AppConfig.newBuilder(Constant.SOCKET_URL, Constant.SERVER_DOMAIN)
      // set it if you need push notification
      .enablePushNotification(
        R.string.app_name,
        R.mipmap.ic_launcher,
        Constant.PUSH_SENDER_ID,
        // optional, only need if you use your own chat room activity
        CustomChatRoomActivity.class,
        // parent activity of CustomChatRoomActivity
        MainActivity.class)
      .build();

    UIConfig uiConfig = UIConfig.newBuilder()
      // optional, set it if you need the chat room list
      .setChatRoomCellProvider(new ChatRoomCellProvider() {
        @NonNull
        @Override
        public ChatRoomCell getChatRoomCell(ChatRoom chatRoom) {
          return new CustomChatRoomCell(chatRoom);
        }
      })
      .setChatMessageCellProvider(new ChatMessageCellProvider() {
        @NonNull
        @Override
        public ChatMessageCell getOwnChatMessageCell(ChatMessageType type, ChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new CustomOwnTextMessageCell(message);
          } else if (type.equals(ChatMessageType.IMAGE)) {
            return new CustomOwnImageMessageCell(message);
          }
          throw new RuntimeException("Unsupported chat message type");
        }

        @NonNull
        @Override
        public ChatMessageCell getOtherChatMessageCell(ChatMessageType type, ChatMessage message) {
          if (type.equals(ChatMessageType.TEXT)) {
            return new CustomOtherTextMessageCell(message);
          } else if (type.equals(ChatMessageType.IMAGE)) {
            return new CustomOtherImageMessageCell(message);
          }
          throw new RuntimeException("Unsupported chat message type");
        }
      })
      .setChatRoomMessageInputViewProvider(new SimpleChatRoomMessageInputViewProvider() {
        // if you enable the default emoji keyboard, your MUST use SignallerEditText as the input view
        @Override
        public int getLayoutRes() {
          return R.layout.view_message_input;
        }

        @Override
        public int getInputEditTextId() {
          return R.id.inputEditText;
        }

        // optional
        @Override
        public EmojiKeyboardViewInfo getEmojiKeyboardViewInfo() {
          return new EmojiKeyboardViewInfo() {
            @Override
            public int getEmojiIconViewId() {
              return R.id.emojiIconView;
            }

            @Override
            public int getEmojiIconResId() {
              return R.drawable.ic_emoji;
            }

            @Override
            public int getKeyboardIconResId() {
              return R.drawable.ic_keyboard;
            }
          };
        }

        @Override
        public int getPhotoPickerIconViewId() {
          return R.id.photoPickerIconView;
        }

        @Override
        public int getSendMessageViewId() {
          return R.id.sendMessageView;
        }
      })
      // the following options are optional
      .setChatRoomDateSectionViewProvider(new ChatRoomDateSectionViewProvider() {
        @NonNull
        @Override
        public View getChatRoomDateSectionView(ChatMessage item) {
          CustomChatRoomDateSectionView view = new CustomChatRoomDateSectionView(getApplicationContext());
          view.bind(item);
          return view;
        }

        @Override
        public boolean isSameSection(ChatMessage item, ChatMessage nextItem) {
          return item.isSameDate(nextItem);
        }
      })
      .setChatRoomToolbarProvider(new ChatRoomToolbarProvider() {
        @NonNull
        @Override
        public View getToolbar(Activity activity, String username) {
          return CustomChatRoomToolbar.create(activity, username);
        }
      })
      .setChatRoomPhotoPickerThemeColor(R.color.colorPrimaryDark)
      .setChatRoomEmptyStateViewRes(R.layout.view_empty_state)
      .setChatRoomBackgroundRes(R.color.colorBackground)
      .setChatRoomListEmptyStateViewRes(R.layout.view_empty_state)
      .setChatRoomListDividerColorRes(R.color.colorGrey)
      .setChatRoomListDividerPaddingDp(64, 0, 0, 0)
      .build();

    // init Signaller with provided configs
    Signaller.init(this, appConfig, uiConfig);

    // Enable debug log and Stetho
    Signaller.getInstance().setDebugEnabled(true);
  }

}
```

### Step 2 - Connect signaller after login
```java
Signaller.getInstance().connect(accessToken, userId);
```

### Step 3 - Disconnect signaller after logout
```java
Signaller.getInstance().disconnect();
```

### Step 4 - Create custom `ChatRoomCell` / `ChatMessageCell`
- Custom `ChatRoomCell`: [Example](https://github.com/jaychang0917/Signaller/blob/master/app/src/main/java/com/redso/signaller/demo/chat/CustomChatRoomCell.java)
- Custom `ChatMessageCell`: [Example](https://github.com/jaychang0917/Signaller/blob/master/app/src/main/java/com/redso/signaller/demo/chat/CustomOwnTextMessageCell.java)

### Step 5 - Add chat room list fragment
```java
getChildFragmentManager().beginTransaction()
      .replace(R.id.chatRoomListFragment, ChatRoomListFragment.newInstance())
      .commitNow();
```

#### That's it!

---

## <a name=adv_usage>Advanced Usage</a>

### <a name=socket>Connect / Disconnect Socket</a>
```java
Signaller.getInstance().connectSocket(accessToken); 
// or
Signaller.getInstance().connectSocket(accessToken, callback); 
```

### <a name=join_leave_room>Join / Leave Chat Room</a>
```java
Signaller.getInstance().joinIndividualChatRoom(targetUserId); 
// or
Signaller.getInstance().joinIndividualChatRoom(targetUserId, callback); 
```
```java
Signaller.getInstance().joinGroupChatRoom(groupChatId); 
// or
Signaller.getInstance().joinGroupChatRoom(groupChatId, callback); 
```
```java
Signaller.getInstance().leaveIndividualChatRoom(targetUserId); 
// or
Signaller.getInstance().leaveIndividualChatRoom(targetUserId, callback); 
```
```java
Signaller.getInstance().leaveGroupChatRoom(groupChatId); 
// or
Signaller.getInstance().leaveGroupChatRoom(groupChatId, callback); 
```

### <a name=send_msg>Send Message</a>
```java
Signaller.getInstance().sendTextMessage(message); 
// or
Signaller.getInstance().sendImageMessage(uri); 
```

### <a name=go_chat_page>Go to chat room page</a>
```java
Signaller.getInstance().goToIndividualChatRoomPage(context, targetUserId, toolbarTitle); 
// or
Signaller.getInstance().goToGroupChatRoomPage(context, groupChatId, toolbarTitle); 
```

### <a name=unread>Get Unread Message Count</a>
```java
Signaller.getInstance().getUnreadMessageCount(callback)
```

### <a name=push>Push Notification Handling</a>
If you need *BOTH* signaller and app specific push notification, what you need to do is just create a service which extends 
`SignallerPushNotificationService`, and handle your app push notification logic in `onMessageReceived()`, that is it, siganller will do the rest for you.
```java
public class PushNotificationService extends SignallerPushNotificationService {

  @Override
  public void onMessageReceived(String from, Bundle data) {
    super.onMessageReceived(from, data);

    // app push notification handling
  }

}
```
And register it in `AndroidManifest.xml`
```xml
<service
    android:name=".chat.PushNotificationService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    </intent-filter>
</service>
```

If you *ONLY* need siganller push notification, signaller do all the tedious works for you!

### <a name=cus_chatroom>Customize chatroom page</a>




