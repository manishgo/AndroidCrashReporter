package com.manishgo.mobile.android.exceptionreporter.presenter;

import com.manishgo.mobile.android.exceptionreporter.email.EmailSender;
import com.manishgo.mobile.android.exceptionreporter.log.LogService;
import com.manishgo.mobile.android.exceptionreporter.model.AppInfo;
import com.manishgo.mobile.android.exceptionreporter.model.ThrowableModel;
import com.manishgo.mobile.android.exceptionreporter.viewmodel.ExceptionViewModel;

import java.io.FileNotFoundException;

public class ExceptionEmailPresenter {

  private EmailSender emailSender;
  private LogService logService;

  public ExceptionEmailPresenter(EmailSender emailSender, LogService logService) {
    this.emailSender = emailSender;
    this.logService = logService;
  }

  public void onCreate(ThrowableModel throwableModel, ExceptionViewModel exceptionViewModel, AppInfo appInfo) {
    exceptionViewModel.populate(throwableModel, appInfo);
  }

  public void sendMail(ExceptionViewModel viewModel) {
    String emailSubject = viewModel.getEmailSubject();
    String emailBody = viewModel.getEmailBody();
    try {
      emailSender.sendEmail(emailSubject, emailBody, logService.getLastCapturedLogsFile());
    } catch (FileNotFoundException e) {
      emailSender.sendEmail(emailSubject, emailBody);
    }
  }
}
