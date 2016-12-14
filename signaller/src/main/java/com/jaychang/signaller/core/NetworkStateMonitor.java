package com.jaychang.signaller.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import rx.Observable;
import rx.Subscriber;

public class NetworkStateMonitor {

  private static final NetworkStateMonitor INSTANCE = new NetworkStateMonitor();

  private NetworkStateMonitor() {
  }

  public static NetworkStateMonitor getInstance() {
    return INSTANCE;
  }

  public Observable<NetworkState> monitor(final Context context) {
    final IntentFilter filter = new IntentFilter();
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

    return Observable.<NetworkState>create(new Observable.OnSubscribe<NetworkState>() {
      @Override
      public void call(final Subscriber<? super NetworkState> subscriber) {
        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
            NetworkState state = getNetworkState(context.getApplicationContext());
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(state);
            }
          }
        }, filter);
      }
    }).defaultIfEmpty(NetworkState.DISCONNECTED);
  }

  public NetworkState getNetworkState(final Context context) {
    final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo networkInfo = manager.getActiveNetworkInfo();

    if (networkInfo == null) {
      return NetworkState.DISCONNECTED;
    }

    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
      return NetworkState.WIFI_CONNECTED;
    } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
      return NetworkState.MOBILE_CONNECTED;
    }

    return NetworkState.DISCONNECTED;
  }

  public enum NetworkState {
    WIFI_CONNECTED, MOBILE_CONNECTED, DISCONNECTED;
  }

}