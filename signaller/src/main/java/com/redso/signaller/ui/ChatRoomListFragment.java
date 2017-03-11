package com.redso.signaller.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redso.signaller.R;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleRecyclerView;
import com.redso.signaller.core.ChatRoomMeta;
import com.redso.signaller.core.Signaller;
import com.redso.signaller.core.SignallerDataManager;
import com.redso.signaller.core.SignallerDbManager;
import com.redso.signaller.core.SignallerEvents;
import com.redso.signaller.core.model.SignallerChatRoom;
import com.redso.signaller.core.model.SignallerReceiver;
import com.redso.signaller.util.LogUtils;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ChatRoomListFragment extends RxFragment {

  private static final int OFF_SCREEN_CELLS_THRESHOLD = 24;
  private SimpleRecyclerView roomsRecyclerView;
  private ChatRoomCellProvider chatRoomCellProvider;
  private int emptyStateViewRes;
  private View emptyStateView;
  private int dividerColorRes;
  private int[] dividerPadding;
  private HashMap<String, SignallerChatRoom> chatRooms = new HashMap<>();

  public static ChatRoomListFragment newInstance() {
    return new ChatRoomListFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.sig_fragment_chatroom_list, container, false);
    roomsRecyclerView = (SimpleRecyclerView) view.findViewById(R.id.roomsRecyclerView);
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
    insertOrUpdateChatRoom(chatRoom, event.updateUnreadCount);
  }

  @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
  public void clearUnreadCount(SignallerEvents.ClearUnreadCountEvent event) {
    EventBus.getDefault().removeStickyEvent(event);
    clearUnreadCount(event.chatRoomId);
  }

  public void init() {
    initUIConfig();
    initRecyclerView();
    removePendingEvents();
  }

  private void initUIConfig() {
    UIConfig uiConfig = Signaller.getInstance().getUiConfig();
    chatRoomCellProvider = uiConfig.getChatRoomCellProvider();
    emptyStateViewRes = uiConfig.getChatRoomListEmptyStateViewRes();
    emptyStateView = uiConfig.getChatRoomListEmptyStateView();
    dividerColorRes = uiConfig.getChatRoomListDividerColorRes();
    dividerPadding = uiConfig.getChatRoomListDividerPaddingDp();
  }

  private void initRecyclerView() {
    roomsRecyclerView.setAutoLoadMoreThreshold(OFF_SCREEN_CELLS_THRESHOLD);
    roomsRecyclerView.setOnLoadMoreListener(simpleRecyclerView -> {
      if (ChatRoomMeta.getInstance().hasMoreData()) {
        loadChatRoomsFromNetwork();
      }
    });

    // empty state view
    if (emptyStateViewRes != 0) {
      roomsRecyclerView.setEmptyStateView(emptyStateViewRes);
    } else if (emptyStateView != null) {
      roomsRecyclerView.setEmptyStateView(emptyStateView);
    }

    // divider
    if (dividerColorRes != 0) {
      roomsRecyclerView.showDivider(dividerColorRes, dividerPadding[0], dividerPadding[1], dividerPadding[2], dividerPadding[3]);
    }
  }

  private void removePendingEvents() {
    // remove all extra events that no subscribers, prevent duplicate chatroom list loading
    EventBus.getDefault().removeStickyEvent(SignallerEvents.UpdateChatRoomListEvent.class);
  }

  private void loadChatRooms() {
    SignallerDataManager.getInstance().getChatRooms()
      .compose(bindUntilEvent(FragmentEvent.DESTROY))
      .subscribe(
        chatRooms -> {
          saveChatRooms(chatRooms);
          bindChatRooms();
          LogUtils.d(String.format("Load %1$s chatrooms", chatRooms.size()));
        },
        error -> {
          LogUtils.e("Fail to load chatrooms:" + error.getMessage());
        });
  }

  private void loadChatRoomsFromNetwork() {
    SignallerDataManager.getInstance().getChatRoomsFromNetwork(ChatRoomMeta.getInstance().getCursor())
      .compose(bindUntilEvent(FragmentEvent.DESTROY))
      .subscribe(
        chatRooms -> {
          saveChatRooms(chatRooms);
          bindChatRooms();
          LogUtils.d(String.format("Load %1$s chatrooms from network", chatRooms.size()));
        },
        error -> {
          LogUtils.e("Fail to load chatrooms from network:" + error.getMessage());
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

  private void insertOrUpdateChatRoom(SignallerChatRoom room, boolean updateUnreadCount) {
    // if has no this chat room, call api to update
    if (room == null) {
      ChatRoomMeta.getInstance().setCursor(null);
      loadChatRoomsFromNetwork();
      return;
    }

    // update ChatRoom cell
    ChatRoomCell chatRoomCell = getChatRoomCell(room.getChatRoomId());
    if (chatRoomCell != null) {
      if (!room.getLastMessage().isOwnMessage() && updateUnreadCount) {
        chatRoomCell.increaseUnreadCount();
      }

      chatRoomCell.updateLastMessage(room.getLastMessage());

      roomsRecyclerView.removeCell(chatRoomCell);
      roomsRecyclerView.addCell(0, chatRoomCell);

      roomsRecyclerView.scrollToPosition(0);
    }
  }

  private void clearUnreadCount(String chatRoomId) {
    SignallerDbManager.getInstance().clearUnreadMessageCount(chatRoomId);

    SignallerDataManager.getInstance().clearUnreadCount(chatRoomId);

    ChatRoomCell chatRoomCell = getChatRoomCell(chatRoomId);
    if (chatRoomCell != null) {
      chatRoomCell.clearUnreadCount();
      roomsRecyclerView.getAdapter().notifyItemChanged(roomsRecyclerView.getAllCells().indexOf(chatRoomCell));
    }

    LogUtils.d("Clear unread count for chat room " + chatRoomId);
  }

  private ChatRoomCell getChatRoomCell(String chatRoomId) {
    for (SimpleCell cell : roomsRecyclerView.getAllCells()) {
      ChatRoomCell chatRoomCell = (ChatRoomCell) cell;
      SignallerChatRoom chatRoom = chatRoomCell.getChatRoom();

      if (chatRoom.getChatRoomId().equals(chatRoomId)) {
        return chatRoomCell;
      }
    }
    return null;
  }

  private void bindChatRooms() {
    roomsRecyclerView.removeAllCells();

    List<SignallerChatRoom> sortedChatRooms = sort(new ArrayList<>(chatRooms.values()));
    List<ChatRoomCell> cells = new ArrayList<>();

    for (SignallerChatRoom chatRoom : sortedChatRooms) {
      ChatRoomCell cell = chatRoomCellProvider.getChatRoomCell(chatRoom);
      cell.setOnCellClickListener(new SimpleCell.OnCellClickListener<SignallerChatRoom>() {
        @Override
        public void onCellClicked(SignallerChatRoom chatRoom) {
          chatWith(chatRoom.getReceiver());
        }
      });
      cells.add(cell);
    }
    roomsRecyclerView.addCells(cells);
  }

  private void chatWith(SignallerReceiver receiver) {
    Signaller.getInstance().chatWith(getContext(), receiver.getUserId(), receiver.getName());
  }

}
