package com.manishgo.mobile.android.exceptionreporter.exceptionhandler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.manishgo.mobile.android.exceptionreporter.R;
import com.manishgo.mobile.android.exceptionreporter.activity.ExceptionEmailActivity;
import com.manishgo.mobile.android.exceptionreporter.model.AppInfo;
import com.manishgo.mobile.android.exceptionreporter.model.ThrowableModel;
import com.manishgo.mobile.android.exceptionreporter.service.LogCapturingService;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashReporterExceptionHandler implements UncaughtExceptionHandler{
  public static final String TAG = CrashReporterExceptionHandler.class.getName();
  private Context context;
  private UncaughtExceptionHandler defaultUncaughtExceptionHandler;
  private AppInfo appInfo;

  public CrashReporterExceptionHandler(Context context, UncaughtExceptionHandler defaultUncaughtExceptionHandler, AppInfo appInfo) {
    this.context = context;
    this.defaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
    this.appInfo = appInfo;
  }

  @Override
  public void uncaughtException(Thread thread, Throwable ex) {
    Log.d(TAG, "CrashReporter caugh exception ", ex);
    Intent serviceIntent = new Intent(context, LogCapturingService.class);
    context.startService(serviceIntent);

    Intent intent = new Intent(context, ExceptionEmailActivity.class)
      .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      .putExtra(ExceptionEmailActivity.EXTRA_THROWABLE, new ThrowableModel(ex))
      .putExtra(ExceptionEmailActivity.EXTRA_APPINFO, appInfo);
    sendNotification(intent);

    defaultUncaughtExceptionHandler.uncaughtException(thread, ex);
  }

  private void sendNotification(Intent intent) {
    String messageTitle =  context.getString(R.string.notification_message_title);
    String message = context.getString(R.string.notification_message);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
      .setContentTitle(messageTitle)
      .setContentText(message)
      .setSmallIcon(android.R.drawable.stat_notify_error)
      .setTicker(messageTitle)
      .setAutoCancel(true)
      .setOngoing(false)
      .setStyle(new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(messageTitle));

    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 9099, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(resultPendingIntent);
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    builder.setSound(alarmSound);
    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1011, builder.build());
  }
}
