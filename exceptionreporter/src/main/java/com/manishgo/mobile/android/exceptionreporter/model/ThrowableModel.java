package com.manishgo.mobile.android.exceptionreporter.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class ThrowableModel implements Serializable {
  private String stackTraceFormatted;
  private StackTraceElement[] stackTraceElements;

  public ThrowableModel(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    stackTraceFormatted = stringWriter.toString();
    stackTraceElements = throwable.getStackTrace();
  }

  public String getStackTraceFormatted() {
    return stackTraceFormatted;
  }

  public StackTraceElement[] getStackTraceElements() {
    return stackTraceElements;
  }
}
