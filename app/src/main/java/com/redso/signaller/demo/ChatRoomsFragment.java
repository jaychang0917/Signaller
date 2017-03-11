package com.redso.signaller.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redso.signaller.demo.widget.BottomTabManager;
import com.redso.signaller.ui.ChatRoomListFragment;

public class ChatRoomsFragment extends Fragment implements BottomTabManager.OnTabSelectListener {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_chat, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init();
  }

  public void init() {
    ChatRoomListFragment fragment = ChatRoomListFragment.newInstance();
    getChildFragmentManager().beginTransaction()
      .replace(R.id.chatRoomListFragment, fragment)
      .commitNow();
  }

  @Override
  public void onTabSelected(int pos) {
    init();
  }

}
