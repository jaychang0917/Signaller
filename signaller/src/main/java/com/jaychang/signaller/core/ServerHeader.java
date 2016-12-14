package com.jaychang.signaller.core;

import java.util.HashMap;

public class ServerHeader {

  public static HashMap<String, String> getDefaultHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("X-REDSO-SECURITY-ACCESS-TOKEN", Signaller.getInstance().getAccessToken());
    headers.put("Content-Type", "application/json");
    return headers;
  }

}
