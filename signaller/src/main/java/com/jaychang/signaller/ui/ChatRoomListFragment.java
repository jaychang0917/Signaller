package com.jaychang.signaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaychang.nrv.NRecyclerView;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.signaller.core.DataManager;
import com.jaychang.signaller.core.UserData;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.util.LogUtils;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoomListFragment extends RxFragment {

  @BindView(R2.id.recyclerView)
  NRecyclerView recyclerView;

  private String cursor;
  private boolean hasMoreData;

  public static ChatRoomListFragment newInstance() {
    return new ChatRoomListFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chatroom_list, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    init();
    loadChatRooms();
  }

  public void init() {
    recyclerView.useVerticalLinearMode();
    recyclerView.showDivider();
  }

  private void loadChatRooms() {
    DataManager.getInstance().getChatRooms(cursor)
      .compose(bindToLifecycle())
      .subscribe(
        response -> {
          hasMoreData = response.hasMore;
          bindChatRooms(response.chatRooms);
        }, error -> {
          LogUtils.e("loadChatRooms:" + error.getMessage());
        });
  }

  private void bindChatRooms(List<ChatRoom> chatRooms) {
    for (ChatRoom chatRoom : chatRooms) {
      ChatRoomCell cell = new ChatRoomCell(chatRoom);
      cell.setCallback(room -> {
        goToChatRoomPage(room);
      });
      recyclerView.addCell(cell);
    }
    recyclerView.getAdapter().notifyDataSetChanged();
  }

  private void goToChatRoomPage(ChatRoom room) {
    Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
    intent.putExtra(ChatRoomActivity.EXTRA_USER_ID, room.receiver.userId);
    intent.putExtra(ChatRoomActivity.EXTRA_USERNAME, room.receiver.name);
    intent.putExtra(ChatRoomActivity.EXTRA_CHATROOM_ID, room.info.chatroomId);
    startActivity(intent);
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    UserData.getInstance().setInChatRoomListPage(isVisibleToUser);
  }

}
