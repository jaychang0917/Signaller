package com.wiser.kol;

import java.util.concurrent.TimeUnit;

public class Main {

  public static void main(String[] args) {
    int hour = (int) TimeUnit.MILLISECONDS.toHours(60 * 60 * 1000L);
    System.out.println("hour:" + hour);
  }

}
