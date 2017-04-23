package com.manishgo.mobile.android.exceptionreporter.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.manishgo.mobile.android.exceptionreporter.log.LogService;

import java.io.FileNotFoundException;

public class LogCapturingService extends IntentService {

  public LogCapturingService() {
    super(LogCapturingService.class.getName());
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    LogService logService = new LogService(this);
    logService.captureLogs();
  }
}
