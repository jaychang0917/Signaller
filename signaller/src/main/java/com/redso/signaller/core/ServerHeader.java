package com.redso.signaller.core;

import java.util.HashMap;

class ServerHeader {

  static HashMap<String, String> getDefaultHeaders() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("X-REDSO-SECURITY-ACCESS-TOKEN", UserData.getInstance().getAccessToken());
    headers.put("Content-Type", "application/json");
    return headers;
  }

}
