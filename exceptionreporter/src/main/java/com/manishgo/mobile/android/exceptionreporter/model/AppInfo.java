package com.manishgo.mobile.android.exceptionreporter.model;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class AppInfo implements Serializable {
  private String appName;
  private String appVersionName;
  private int versionCode;

  public AppInfo(String appName, String appVersionName, int versionCode) {
    this.appName = appName;
    this.appVersionName = appVersionName;
    this.versionCode = versionCode;
  }

  public String getAppName() {
    return appName;
  }

  public String getAppVersionName() {
    return appVersionName;
  }

  public int getVersionCode() {
    return versionCode;
  }
}
