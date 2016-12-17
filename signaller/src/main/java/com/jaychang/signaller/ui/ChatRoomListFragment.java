package com.jaychang.signaller.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaychang.nrv.BaseCell;
import com.jaychang.nrv.NRecyclerView;
import com.jaychang.nrv.OnLoadMorePageListener;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;
import com.jaychang.signaller.core.DataManager;
import com.jaychang.signaller.core.DatabaseManager;
import com.jaychang.signaller.core.Events;
import com.jaychang.signaller.core.model.ChatRoom;
import com.jaychang.signaller.util.LogUtils;
import com.jaychang.signaller.util.NetworkStateMonitor;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoomListFragment extends RxFragment {

  @BindView(R2.id.recyclerView)
  NRecyclerView recyclerView;

  private static final int OFF_SCREEN_CELLS_THRESHOLD = 10;
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
  public void updateChatRoomList(Events.UpdateChatRoomListEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    ChatRoom chatRoom = DatabaseManager.getInstance().getChatRoom(event.chatRoomId);
    insertOrUpdateChatRoom(chatRoom);
  }

  public void init() {
    initRecyclerView();
    removePendingEvents();
  }

  private void initRecyclerView() {
    recyclerView.useVerticalLinearMode();
    recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).colorResId(R.color.divider).build());
    recyclerView.setOnLoadMoreListener(new OnLoadMorePageListener() {
      @Override
      public void onLoadMore(int i) {
        if (hasMoreData) {
          loadChatRoomsFromNetwork();
        }
      }

      @Override
      public int getThreshold() {
        return OFF_SCREEN_CELLS_THRESHOLD;
      }
    });
  }

  private void removePendingEvents() {
    // remove all extra events that no subscribers, prevent duplicate chatroom list loading
    EventBus.getDefault().removeStickyEvent(Events.UpdateChatRoomListEvent.class);
  }

  private void loadChatRooms() {
    boolean isConnected = NetworkStateMonitor.getInstance().isConnected(getContext());

    if (isConnected) {
      loadChatRoomsFromNetwork();
    } else {
      loadChatRoomsFromDB();
    }
  }

  private void loadChatRoomsFromNetwork() {
    DataManager.getInstance().getChatRooms(cursor)
      .compose(bindToLifecycle())
      .subscribe(
        response -> {
          hasMoreData = response.hasMore;
          cursor = response.cursor;
          bindChatRooms(sort(response.chatRooms));
        }, error -> {
          LogUtils.e("loadChatRoomsFromNetwork:" + error.getMessage());
        });
  }

  private List<ChatRoom> sort(List<ChatRoom> rooms) {
    Collections.sort(rooms, (chatRoom, other) -> (int)(other.mtime - chatRoom.mtime));
    return rooms;
  }

  private void insertOrUpdateChatRoom(ChatRoom room) {
    boolean hasChatRoom = false;

    for (BaseCell cell : recyclerView.getAllCells()) {
      ChatRoomCell chatRoomCell = (ChatRoomCell) cell;
      ChatRoom chatRoom = chatRoomCell.getChatRoom();

      if (chatRoom.chatRoomId.equals(room.chatRoomId)) {
        chatRoomCell.increaseUnreadCount();
        chatRoomCell.updateLastMessage(room.lastMessage);

        recyclerView.removeCell(chatRoomCell);
        recyclerView.addCell(chatRoomCell, 0);
        int fromPos = recyclerView.getAllCells().indexOf(cell);
        if (fromPos == 0) {
          recyclerView.getAdapter().notifyItemChanged(0);
        } else {
          recyclerView.getAdapter().notifyItemMoved(fromPos, 0);
        }

        hasChatRoom = true;

        break;
      }
    }

    if (!hasChatRoom) {
      // if has no this chat room, insert this new chat room at top
      DefaultChatRoomCell cell = new DefaultChatRoomCell(room);
      recyclerView.addCell(cell, 0);
      recyclerView.getAdapter().notifyItemInserted(0);
    }

  }

  private void loadChatRoomsFromDB() {
    DatabaseManager.getInstance().getChatRooms()
      .subscribe(chatRooms -> {
          recyclerView.removeAllCells();
          bindChatRooms(chatRooms);
        },
        error -> {
          LogUtils.e("loadChatRoomsFromDB:" + error.getMessage());
        });
  }

  private void bindChatRooms(List<ChatRoom> chatRooms) {
    for (ChatRoom chatRoom : chatRooms) {
      ChatRoomCell cell = new DefaultChatRoomCell(chatRoom);
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
    intent.putExtra(ChatRoomActivity.EXTRA_CHATROOM_ID, room.info.chatRoomId);
    startActivity(intent);
  }

}
