package com.manishgo.mobile.android.exceptionreporter.viewmodel;

import android.content.res.Resources;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.manishgo.mobile.android.exceptionreporter.BR;
import com.manishgo.mobile.android.exceptionreporter.R;
import com.manishgo.mobile.android.exceptionreporter.model.AppInfo;
import com.manishgo.mobile.android.exceptionreporter.model.ThrowableModel;
import com.manishgo.mobile.android.exceptionreporter.resourceresolver.RawResourceResolver;
import com.manishgo.mobile.android.exceptionreporter.util.Preconditions;

import java.io.IOException;
import java.util.Locale;

public class ExceptionViewModel extends BaseObservable {

  private String stepsToReplicate;
  private RawResourceResolver rawResourceResolver;
  private Resources resources;
  private ThrowableModel throwableModel;
  private AppInfo appInfo;

  public ExceptionViewModel(RawResourceResolver rawResourceResolver, Resources resources) {
    Preconditions.checkNotNull(rawResourceResolver);
    Preconditions.checkNotNull(resources);
    this.rawResourceResolver = rawResourceResolver;
    this.resources = resources;
  }

  public void populate(ThrowableModel throwableModel, AppInfo appInfo) {
    Preconditions.checkNotNull(throwableModel);
    Preconditions.checkNotNull(appInfo);
    this.throwableModel = throwableModel;
    this.appInfo = appInfo;
    notifyPropertyChanged(BR.title);
    notifyPropertyChanged(BR.stackTrace);
  }


  @Bindable
  public String getTitle() {
    return String.format(Locale.getDefault(), "%s\n%s",
        getFileNameWithLineNumber(), getFullyQualifedMethodName());
  }

  @Bindable
  public String getStackTrace() {
    return throwableModel.getStackTraceFormatted();
  }

  public void setStepsToReplicate(String stepsToReplicate) {
    this.stepsToReplicate = stepsToReplicate;
  }

  public String getStepsToReplicate() {
    return stepsToReplicate;
  }

  public String getEmailSubject() {
    return  getFileNameWithLineNumber();
  }

  public String getEmailBody() {
    try {
      String stepsToReplicate = getStepsToReplicate();
      if(stepsToReplicate==null) {
        stepsToReplicate = resources.getString(R.string.default_steps_to_replicate);
      }
      return String.format(Locale.getDefault(), rawResourceResolver.getRawString(R.raw.email_template),
          appInfo.getAppName(), appInfo.getAppName(), appInfo.getAppVersionName(), appInfo.getVersionCode(), getFileNameWithLineNumber(), getFullyQualifedMethodName(), getStackTrace(), stepsToReplicate);
    } catch (IOException e) {
      return "";
    }
  }

  private String getFileNameWithLineNumber() {
    StackTraceElement topmostStackTraceElement = topMostStackTraceElement();
    return String.format(Locale.getDefault(), "%s line %d", topmostStackTraceElement.getFileName(),
        topmostStackTraceElement.getLineNumber());
  }

  private String getFullyQualifedMethodName() {
    StackTraceElement topmostStackTraceElement = topMostStackTraceElement();
    return String.format(Locale.getDefault(), "%s.%s", topmostStackTraceElement.getClassName(), topmostStackTraceElement.getMethodName());
  }

  private StackTraceElement topMostStackTraceElement() {
    return throwableModel.getStackTraceElements()[0];
  }

}
