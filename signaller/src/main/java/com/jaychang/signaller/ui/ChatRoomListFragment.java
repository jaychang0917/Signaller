package com.jaychang.signaller.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaychang.nrv.NRecyclerView;
import com.jaychang.signaller.R;
import com.jaychang.signaller.R2;

import butterknife.BindView;

public class ChatRoomListFragment extends Fragment {

  @BindView(R2.id.recyclerView)
  NRecyclerView recyclerView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_chatroom_list, container, false);
  }
}
