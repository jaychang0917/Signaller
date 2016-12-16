package com.jaychang.demo.signaler;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

public class StethoUtils {

  public static boolean debug = true;

  public static void init(Context appContext) {
    if (debug) {
      Stetho.initialize(
        Stetho.newInitializerBuilder(appContext)
          .enableDumpapp(Stetho.defaultDumperPluginsProvider(appContext))
          .enableWebKitInspector(RealmInspectorModulesProvider.builder(appContext).build())
          .build());
    }
  }

}
