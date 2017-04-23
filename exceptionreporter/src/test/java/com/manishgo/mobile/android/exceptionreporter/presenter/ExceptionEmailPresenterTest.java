package com.manishgo.mobile.android.exceptionreporter.presenter;

import android.content.res.Resources;

import com.manishgo.mobile.android.exceptionreporter.email.EmailSender;
import com.manishgo.mobile.android.exceptionreporter.log.LogService;
import com.manishgo.mobile.android.exceptionreporter.model.AppInfo;
import com.manishgo.mobile.android.exceptionreporter.model.ThrowableModel;
import com.manishgo.mobile.android.exceptionreporter.resourceresolver.RawResourceResolver;
import com.manishgo.mobile.android.exceptionreporter.viewmodel.ExceptionViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileNotFoundException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionEmailPresenterTest {

  @Mock
  EmailSender emailSender;

  @Mock
  RawResourceResolver rawResourceResolver;

  @Mock
  Resources resources;

  @Mock
  LogService logService;

  private ExceptionEmailPresenter exceptionEmailPresenter;

  @Before
  public void setUp() throws Exception {
    exceptionEmailPresenter = new ExceptionEmailPresenter(emailSender, logService);
  }

  @Test
  public void shouldPopulateViewModelOnCreate() throws Exception {
    String stackTrace = "Stack Trace";
    ThrowableModel throwableModel = mock(ThrowableModel.class);
    when(throwableModel.getStackTraceFormatted()).thenReturn(stackTrace);
    ExceptionViewModel exceptionViewModel = new ExceptionViewModel(rawResourceResolver, resources);
    exceptionEmailPresenter.onCreate(throwableModel, exceptionViewModel, mock(AppInfo.class));

    assertThat(exceptionViewModel.getStackTrace(), is(stackTrace));
  }

  @Test
  public void shouldSendMailWithLogsAttachment() throws Exception {
    String emailSubject = "Email Subject";
    String emailBody = "<b>Email body</b>";
    File logsFile = mock(File.class);
    ExceptionViewModel viewModel = mock(ExceptionViewModel.class);
    when(viewModel.getEmailSubject()).thenReturn(emailSubject);
    when(viewModel.getEmailBody()).thenReturn(emailBody);
    when(logService.getLastCapturedLogsFile()).thenReturn(logsFile);

    exceptionEmailPresenter.sendMail(viewModel);

    verify(emailSender).sendEmail(emailSubject, emailBody, logsFile);
  }

  @Test
  public void shouldSendMailWithoutLogsOnExceptionWhileFetchingLogs() throws Exception {
    String emailSubject = "Email Subject";
    String emailBody = "<b>Email body</b>";
    File logsFile = mock(File.class);
    ExceptionViewModel viewModel = mock(ExceptionViewModel.class);
    when(viewModel.getEmailSubject()).thenReturn(emailSubject);
    when(viewModel.getEmailBody()).thenReturn(emailBody);
    when(logService.getLastCapturedLogsFile()).thenThrow(new FileNotFoundException("Error while fetching logs"));

    exceptionEmailPresenter.sendMail(viewModel);

    verify(emailSender).sendEmail(emailSubject, emailBody);
  }
}
