package com.manishgo.mobile.android.exceptionreporter.email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;

import com.manishgo.mobile.android.exceptionreporter.R;

import java.io.File;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.createChooser;

public class EmailSender {
  private Context context;

  public EmailSender(Context context) {
    this.context = context;
  }

  public void sendEmail(String subject, String body, File attachmentFile) {
    Intent emailIntent = getEmailIntent(subject, body);
    emailIntent.setType("*/*");
    emailIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authorities), attachmentFile));
    emailIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
    Log.d(getClass().getName(), "Going to send mail with attachment file " + attachmentFile.getAbsolutePath());
    context.startActivity(createChooser(emailIntent,  context.getString(R.string.email_application_chooser_title)));
  }

  public void sendEmail(String subject, String body) {
    Intent emailIntent = getEmailIntent(subject, body);
    context.startActivity(createChooser(emailIntent,  context.getString(R.string.email_application_chooser_title)));
  }

  @NonNull
  private Intent getEmailIntent(String subject, String body) {
    Intent emailIntent = new Intent(ACTION_SEND);
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    emailIntent.putExtra(Intent.EXTRA_TEXT, body);

    return emailIntent;
  }
}
