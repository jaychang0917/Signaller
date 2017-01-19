package com.jaychang.signaller.ui;

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
import com.jaychang.signaller.core.ChatRoomMeta;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.core.SignallerDataManager;
import com.jaychang.signaller.core.SignallerDbManager;
import com.jaychang.signaller.core.SignallerEvents;
import com.jaychang.signaller.core.model.SignallerChatRoom;
import com.jaychang.signaller.core.model.SignallerReceiver;
import com.jaychang.signaller.ui.config.ChatRoomCellProvider;
import com.jaychang.signaller.ui.part.ChatRoomCell;
import com.jaychang.signaller.util.LogUtils;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoomListFragment extends RxFragment {

  @BindView(R2.id.recyclerView)
  NRecyclerView recyclerView;

  private static final int OFF_SCREEN_CELLS_THRESHOLD = 24;
  private ChatRoomCellProvider chatRoomCellProvider;
  private HashMap<String, SignallerChatRoom> chatRooms = new HashMap<>();

  public static ChatRoomListFragment newInstance() {
    return new ChatRoomListFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.sig_fragment_chatroom_list, container, false);
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
  public void updateChatRoomList(SignallerEvents.UpdateChatRoomListEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    SignallerChatRoom chatRoom = SignallerDbManager.getInstance().getChatRoom(event.chatRoomId);
    insertOrUpdateChatRoom(chatRoom);
  }

  public void init() {
    initUIConfig();
    initRecyclerView();
    removePendingEvents();
  }

  private void initUIConfig() {
    chatRoomCellProvider = Signaller.getInstance().getUiConfig().getChatRoomCellProvider();
  }

  private void initRecyclerView() {
    recyclerView.useVerticalLinearMode();
    recyclerView.showDivider();
    recyclerView.setOnLoadMoreListener(new OnLoadMorePageListener() {
      @Override
      public void onLoadMore(int i) {
        if (ChatRoomMeta.hasMoreData) {
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
    EventBus.getDefault().removeStickyEvent(SignallerEvents.UpdateChatRoomListEvent.class);
  }

  private void loadChatRooms() {
    SignallerDataManager.getInstance().getChatRooms()
      .compose(bindUntilEvent(FragmentEvent.DESTROY))
      .subscribe(
        rooms -> {
          saveChatRooms(rooms);
          bindChatRooms();
        },
        error -> {
          LogUtils.e("loadChatRooms:" + error.getMessage());
        });
  }

  private void loadChatRoomsFromNetwork() {
    SignallerDataManager.getInstance().getChatRoomsFromNetwork(ChatRoomMeta.cursor)
      .compose(bindUntilEvent(FragmentEvent.DESTROY))
      .subscribe(
        chatRooms -> {
          saveChatRooms(chatRooms);
          bindChatRooms();
        },
        error -> {
          LogUtils.e("loadChatRoomsFromNetwork:" + error.getMessage());
        });
  }

  private void saveChatRooms(List<SignallerChatRoom> rooms) {
    for (SignallerChatRoom room : rooms) {
      chatRooms.put(room.getChatRoomId(), room);
    }
  }

  private List<SignallerChatRoom> sort(List<SignallerChatRoom> rooms) {
    Collections.sort(rooms, (chatRoom, other) -> other.getLastMessageTime().compareTo(chatRoom.getLastMessageTime()));
    return rooms;
  }

  private void insertOrUpdateChatRoom(SignallerChatRoom room) {
    // if has no this chat room, call api to update
    if (room == null) {
      ChatRoomMeta.cursor = null;
      loadChatRoomsFromNetwork();
      return;
    }

    for (BaseCell cell : recyclerView.getAllCells()) {
      ChatRoomCell chatRoomCell = (ChatRoomCell) cell;
      SignallerChatRoom chatRoom = chatRoomCell.getChatRoom();

      if (chatRoom.getChatRoomId().equals(room.getChatRoomId())) {
        if (!room.getLastMessage().isOwnMessage()) {
          chatRoomCell.increaseUnreadCount();
        }

        chatRoomCell.updateLastMessage(room.getLastMessage());

        recyclerView.removeCell(chatRoomCell);
        recyclerView.addCell(chatRoomCell, 0);
        int fromPos = recyclerView.getAllCells().indexOf(cell);
        if (fromPos == 0) {
          recyclerView.getAdapter().notifyItemChanged(0);
        } else {
          recyclerView.getAdapter().notifyItemMoved(fromPos, 0);
        }

        break;
      }
    }
  }

  private void bindChatRooms() {
    recyclerView.removeAllCells();

    List<SignallerChatRoom> sortedChatRooms = sort(new ArrayList<>(chatRooms.values()));
    for (SignallerChatRoom chatRoom : sortedChatRooms) {
      ChatRoomCell cell = chatRoomCellProvider.getChatRoomCell(chatRoom);
      cell.setCallback(room -> {
        chatWith(room.getReceiver());
      });
      recyclerView.addCell(cell);
    }
    recyclerView.getAdapter().notifyDataSetChanged();
  }

  private void chatWith(SignallerReceiver receiver) {
    Signaller.getInstance().chatWith(getContext(), receiver.getUserId(), receiver.getName());
  }

}
