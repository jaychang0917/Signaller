package com.jaychang.signaller.core;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * transformer to transform observable to observable subscribing on io thread and
 * observing on main thread.
 * */
public class SchedulerTransformer<T> implements Observable.Transformer<T, T> {

  @Override
  public Observable<T> call(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }
}
