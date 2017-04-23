package com.manishgo.mobile.android.exceptionreporter.log;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.manishgo.mobile.android.exceptionreporter.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public class LogService {
  public static final String ADB_LOGS_FILE_NAME = "adb_logs.txt";
  private Context context;
  private final int logLinesCaptureCount;

  public LogService(Context context) {
    this.context = context;
    logLinesCaptureCount = context.getResources().getInteger(R.integer.log_lines_capture_count);
  }

  public void captureLogs() {
    resetAdbLogsFile();
    String command = "logcat -d -f " + getAdbLogsFile().getAbsolutePath() + " -t " + logLinesCaptureCount;
    try {
      Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      Log.e(getClass().getName(), "Error while capturing logs", e);
    }
  }

  public File getLastCapturedLogsFile() throws FileNotFoundException {
    File adbLogsFile = getAdbLogsFile();
    if(!adbLogsFile.exists()) {
      Log.w(getClass().getName(), String.format(Locale.getDefault(),
          "Error while fetching Logs file. %s doesn't exist", adbLogsFile.getAbsolutePath()));
      throw new FileNotFoundException("Logs not captured yet");
    }
    return adbLogsFile;
  }

  private void resetAdbLogsFile() {
    File adbLogsFile = getAdbLogsFile();
    if(adbLogsFile.exists()) {
      adbLogsFile.delete();
    }

  }

  @NonNull
  private File getAdbLogsFile() {
    File logsDir = new File(context.getFilesDir(), "logs");
    if(!logsDir.exists()) {
      logsDir.mkdir();
    }

    return new File(logsDir, ADB_LOGS_FILE_NAME);
  }
}
