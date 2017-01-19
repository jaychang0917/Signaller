package com.jaychang.signaller.core;

import com.jaychang.utils.PreferenceUtils;

public class UserData {

  private static final UserData INSTANCE = new UserData();
  private static final String KEY_PREFIX = UserData.class.getName();
  private static final String KEY_IS_IN_CHAT_ROOM_PAGE = KEY_PREFIX + "KEY_IS_IN_CHAT_ROOM_PAGE";
  private static final String KEY_CUR_CHAT_ROOM_ID = KEY_PREFIX + "KEY_CUR_CHAT_ROOM_ID";
  private static final String KEY_ACCESS_TOKEN = KEY_PREFIX + "KEY_ACCESS_TOKEN";
  private static final String KEY_USER_ID = KEY_PREFIX + "KEY_USER_ID";

  private UserData() {
  }

  public static UserData getInstance() {
    return INSTANCE;
  }

  public boolean isInChatRoomPage() {
    return PreferenceUtils.getBoolean(Signaller.getInstance().getAppContext(), KEY_IS_IN_CHAT_ROOM_PAGE);
  }

  public void setInChatRoomPage(boolean inChatRoomPage) {
    PreferenceUtils.saveBoolean(Signaller.getInstance().getAppContext(), KEY_IS_IN_CHAT_ROOM_PAGE, inChatRoomPage);
  }

  public String getCurrentChatRoomId() {
    return PreferenceUtils.getString(Signaller.getInstance().getAppContext(), KEY_CUR_CHAT_ROOM_ID);
  }

  public void setCurrentChatRoomId(String currentChatRoomId) {
    PreferenceUtils.saveString(Signaller.getInstance().getAppContext(), KEY_CUR_CHAT_ROOM_ID, currentChatRoomId);
  }

  public String getAccessToken() {
    return PreferenceUtils.getString(Signaller.getInstance().getAppContext(), KEY_ACCESS_TOKEN);
  }

  public void setAccessToken(String accessToken) {
    PreferenceUtils.saveString(Signaller.getInstance().getAppContext(), KEY_ACCESS_TOKEN, accessToken);
  }

  public String getUserId() {
    return PreferenceUtils.getString(Signaller.getInstance().getAppContext(), KEY_USER_ID);
  }

  public void setUserId(String userId) {
    PreferenceUtils.saveString(Signaller.getInstance().getAppContext(), KEY_USER_ID, userId);
  }

  public void clear() {
    PreferenceUtils.remove(Signaller.getInstance().getAppContext(), KEY_IS_IN_CHAT_ROOM_PAGE);
    PreferenceUtils.remove(Signaller.getInstance().getAppContext(), KEY_CUR_CHAT_ROOM_ID);
    PreferenceUtils.remove(Signaller.getInstance().getAppContext(), KEY_ACCESS_TOKEN);
    PreferenceUtils.remove(Signaller.getInstance().getAppContext(), KEY_USER_ID);
  }
}
