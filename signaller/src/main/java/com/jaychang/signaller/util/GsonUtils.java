package com.jaychang.signaller.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jaychang.signaller.core.model.SignallerRealmInt;
import com.jaychang.signaller.core.model.SignallerRealmString;

import java.io.IOException;
import java.lang.reflect.Type;

import io.realm.RealmList;
import io.realm.RealmObject;

public class GsonUtils {

  public static Gson gson;

  public static Gson getGson() {
    if (gson != null) {
      return gson;
    }

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.setPrettyPrinting();

    gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
      @Override
      public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaringClass().equals(RealmObject.class);
      }

      @Override
      public boolean shouldSkipClass(Class<?> clazz) {
        return false;
      }
    });

    Type stringToken = new TypeToken<RealmList<SignallerRealmString>>() {}.getType();
    gsonBuilder.registerTypeAdapter(stringToken, new TypeAdapter<RealmList<SignallerRealmString>>() {
      @Override
      public void write(JsonWriter out, RealmList<SignallerRealmString> value) throws IOException {
      }

      @Override
      public RealmList<SignallerRealmString> read(JsonReader in) throws IOException {
        RealmList<SignallerRealmString> list = new RealmList<>();
        in.beginArray();
        while (in.hasNext()) {
          list.add(new SignallerRealmString(in.nextString()));
        }
        in.endArray();
        return list;
      }
    });
    Type intToken = new TypeToken<RealmList<SignallerRealmInt>>() {}.getType();
    gsonBuilder.registerTypeAdapter(intToken, new TypeAdapter<RealmList<SignallerRealmInt>>() {
      @Override
      public void write(JsonWriter out, RealmList<SignallerRealmInt> value) throws IOException {
      }

      @Override
      public RealmList<SignallerRealmInt> read(JsonReader in) throws IOException {
        RealmList<SignallerRealmInt> list = new RealmList<>();
        in.beginArray();
        while (in.hasNext()) {
          list.add(new SignallerRealmInt(in.nextInt()));
        }
        in.endArray();
        return list;
      }
    });

    gson = gsonBuilder.create();

    return gson;
  }


}
