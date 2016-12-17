package com.jaychang.demo.signaler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jaychang.signaller.core.ChatRoomJoinCallback;
import com.jaychang.signaller.core.ChatRoomLeaveCallback;
import com.jaychang.signaller.core.Signaller;
import com.jaychang.signaller.ui.ChatRoomActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PeopleFragment extends Fragment {


  @BindView(R.id.joinJay10Button)
  Button joinJay10Button;
  @BindView(R.id.joinJay11Button)
  Button joinJay11Button;
  @BindView(R.id.joinJay12Button)
  Button joinJay12Button;
  @BindView(R.id.leaveJay10Button)
  Button leaveJay10Button;
  @BindView(R.id.leaveJay11Button)
  Button leaveJay11Button;
  @BindView(R.id.leaveJay12Button)
  Button leaveJay12Button;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_friends, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @OnClick(R.id.joinJay10Button)
  void chatWithJay10() {
    Signaller.getInstance().chatWith(Constant.USER_ID_JAY10, new ChatRoomJoinCallback() {
      @Override
      public void onChatRoomJoined(String userId, String chatRoomId) {
        ChatRoomActivity.start(getContext(), userId, "jay10", chatRoomId);
      }
    });
  }

  @OnClick(R.id.joinJay11Button)
  void chatWithJay11() {
    Signaller.getInstance().chatWith(Constant.USER_ID_JAY11, new ChatRoomJoinCallback() {
      @Override
      public void onChatRoomJoined(String userId, String chatRoomId) {
        ChatRoomActivity.start(getContext(), userId, "jay11", chatRoomId);
      }
    });
  }

  @OnClick(R.id.joinJay12Button)
  void chatWithJay12() {
    Signaller.getInstance().chatWith(Constant.USER_ID_JAY12, new ChatRoomJoinCallback() {
      @Override
      public void onChatRoomJoined(String userId, String chatRoomId) {
        ChatRoomActivity.start(getContext(), userId, "jay12", chatRoomId);
      }
    });
  }

  @OnClick(R.id.joinJay13Button)
  void chatWithJay13() {
    Signaller.getInstance().chatWith(Constant.USER_ID_JAY13, new ChatRoomJoinCallback() {
      @Override
      public void onChatRoomJoined(String userId, String chatRoomId) {
        ChatRoomActivity.start(getContext(), userId, "jay13", chatRoomId);
      }
    });
  }

  private String makeChatRoomId(String userId) {
    String ownUserId = App.currentUserId;
    return ownUserId.compareTo(userId) < 0 ?
      ownUserId + "_" + userId :
      userId + "_" + ownUserId;
  }

  @OnClick(R.id.leaveJay10Button)
  void leaveChatRoomWithJay10() {
    Signaller.getInstance().leaveChatRoom(makeChatRoomId(Constant.USER_ID_JAY10), new ChatRoomLeaveCallback() {
      @Override
      public void onChatRoomLeft(String chatRoomId) {
        Utils.showToast(getActivity(), "onChatRoomLeft: " + chatRoomId);
      }
    });
  }

  @OnClick(R.id.leaveJay11Button)
  void leaveChatRoomWithJay11() {
    Signaller.getInstance().leaveChatRoom(makeChatRoomId(Constant.USER_ID_JAY11), new ChatRoomLeaveCallback() {
      @Override
      public void onChatRoomLeft(String chatRoomId) {
        Utils.showToast(getActivity(), "onChatRoomLeft: " + chatRoomId);
      }
    });
  }

  @OnClick(R.id.leaveJay12Button)
  void leaveChatRoomWithJay12() {
    Signaller.getInstance().leaveChatRoom(makeChatRoomId(Constant.USER_ID_JAY12), new ChatRoomLeaveCallback() {
      @Override
      public void onChatRoomLeft(String chatRoomId) {
        Utils.showToast(getActivity(), "onChatRoomLeft: " + chatRoomId);
      }
    });
  }

  @OnClick(R.id.leaveJay13Button)
  void leaveChatRoomWithJay13() {
    Signaller.getInstance().leaveChatRoom(makeChatRoomId(Constant.USER_ID_JAY13), new ChatRoomLeaveCallback() {
      @Override
      public void onChatRoomLeft(String chatRoomId) {
        Utils.showToast(getActivity(), "onChatRoomLeft: " + chatRoomId);
      }
    });
  }
}
