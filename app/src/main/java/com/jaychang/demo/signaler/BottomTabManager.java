package com.jaychang.demo.signaler;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.List;

public class BottomTabManager {

  private SmartTabLayout tabLayout;
  private ViewPager viewPager;
  private FragmentPagerItemAdapter adapter;

  private BottomTabManager() {
  }

  private enum Tab {
    TAB1(R.string.people, PeopleFragment.class),
    CHAT(R.string.chatroom, ChatRoomsFragment.class),
    TAB3(R.string.other, OtherFragment.class);

    int title;
    Class<? extends Fragment> fragment;

    Tab(int title, Class<? extends Fragment> fragment) {
      this.title = title;
      this.fragment = fragment;
    }
  }

  public static BottomTabManager init(final AppCompatActivity activity) {
    final BottomTabManager instance = new BottomTabManager();

    FragmentPagerItems.Creator pages = FragmentPagerItems.with(activity);
    for (int i = 0; i < Tab.values().length; i++) {
      Tab tab = Tab.values()[i];
      pages.add(tab.title, tab.fragment);
    }

    instance.tabLayout = (SmartTabLayout) activity.findViewById(R.id.tabLayout);
    instance.viewPager = (ViewPager) activity.findViewById(R.id.viewPager);
    instance.adapter = new FragmentPagerItemAdapter(
      activity.getSupportFragmentManager(), pages.create());

    instance.viewPager.setAdapter(instance.adapter);

    instance.tabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override
      public void onPageSelected(int position) {
        instance.onTabSelected(position);
      }

      @Override
      public void onPageScrollStateChanged(int state) {
      }
    });

    instance.tabLayout.setViewPager(instance.viewPager);

    return instance;
  }

  public void selectTab(int pos) {
    viewPager.setCurrentItem(pos);
  }

  public void onTabSelected(int pos) {
    Fragment fragment = adapter.getPage(pos);
    if (fragment instanceof OnTabSelectListener) {
      ((OnTabSelectListener) fragment).onTabSelected(pos);
    } else {
      if (fragment == null) {
        return;
      }

      List<Fragment> childFragments = fragment.getChildFragmentManager().getFragments();
      if (childFragments == null) {
        return;
      }
      for (Fragment child : childFragments) {
        if (child instanceof OnTabSelectListener) {
          ((OnTabSelectListener) child).onTabSelected(pos);
        }
      }
    }
  }

  public void reloadTab(int pos) {
    Fragment fragment = adapter.getPage(pos);
    if (fragment instanceof Reloadable) {
      ((Reloadable) fragment).reload();
    }
  }

  public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
    for (int i = 0; i < Tab.values().length; i++) {
      int toPos = i;
      View tab = tabLayout.getTabAt(i);
      if (onTabClickListener == null) {
        tab.setOnClickListener(null);
      } else {
        tab.setOnClickListener(view -> {
          int fromPos = viewPager.getCurrentItem();
          if (fromPos != toPos) {
            onTabClickListener.onTabClicked(fromPos, toPos);
          }
        });
      }
    }
  }

  public void show() {
    tabLayout.setVisibility(View.VISIBLE);
  }

  public void hide() {
    tabLayout.setVisibility(View.GONE);
  }

  public interface Reloadable {
    void reload();
  }

  public interface OnTabSelectListener {
    void onTabSelected(int pos);
  }

  public interface OnTabClickListener {
    void onTabClicked(int fromPos, int toPos);
  }

}
