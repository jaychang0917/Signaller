package com.redso.signaller.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redso.signaller.demo.widget.BottomTabManager;
import com.redso.signaller.ui.ChatRoomListFragment;

public class ChatsFragment extends Fragment implements BottomTabManager.OnTabSelectListener {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_chat, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
//    ChatRoomActivity.start(getContext(), "5714163003293696_5730827476402176", "5730827476402176", "xx");
    init();
  }

  public void init() {
    getChildFragmentManager().beginTransaction()
      .replace(R.id.chatRoomListFragment, ChatRoomListFragment.newInstance())
      .commitNow();
  }

  @Override
  public void onTabSelected(int pos) {
    init();
  }

}
