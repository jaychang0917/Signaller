package com.redso.signaller.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redso.signaller.core.Signaller;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PeopleFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_people, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @OnClick(R.id.joinJay10Button)
  void chatWithJay10() {
    Signaller.getInstance().goToIndividualChatRoomPage(getActivity(), Constant.USER_ID_JAY10, "jay10");
  }

  @OnClick(R.id.joinJay11Button)
  void chatWithJay11() {
    Signaller.getInstance().goToIndividualChatRoomPage(getActivity(), Constant.USER_ID_JAY11, "jay11");
  }

  @OnClick(R.id.joinJay12Button)
  void chatWithJay12() {
    Signaller.getInstance().goToIndividualChatRoomPage(getActivity(), Constant.USER_ID_JAY12, "jay12");
  }

  @OnClick(R.id.joinJay13Button)
  void chatWithJay13() {
    Signaller.getInstance().goToIndividualChatRoomPage(getActivity(), Constant.USER_ID_JAY13, "jay13");
  }

  @OnClick(R.id.joinJay14Button)
  void chatWithJay14() {
    Signaller.getInstance().goToIndividualChatRoomPage(getActivity(), Constant.USER_ID_JAY14, "jay14");
  }

}
