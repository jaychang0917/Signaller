package com.wiser.kol;

import android.content.Context;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

public class Utils {

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

  public static void showToast(Context context, String msg) {
    Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
  }

}
