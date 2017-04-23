package com.manishgo.mobile.android.exceptionreporter.viewmodel;

import android.content.res.Resources;

import com.manishgo.mobile.android.exceptionreporter.BR;
import com.manishgo.mobile.android.exceptionreporter.R;
import com.manishgo.mobile.android.exceptionreporter.model.AppInfo;
import com.manishgo.mobile.android.exceptionreporter.model.ThrowableModel;
import com.manishgo.mobile.android.exceptionreporter.resourceresolver.RawResourceResolver;
import com.manishgo.mobile.android.exceptionreporter.util.ObservableViewModelTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionViewModelTest extends ObservableViewModelTest {

  private static final int APP_VERSION_CODE = 15136;
  private static final String APP_VERSION_NAME = "4.1";
  private static final String APP_NAME = "My App";

  @Mock
  RawResourceResolver resourceResolver;

  @Mock
  Resources resources;

  @Mock
  ThrowableModel throwableModel;

  @Mock
  AppInfo appInfo;

  private ExceptionViewModel exceptionViewModel;

  private NullPointerException throwable;

  private String stackTrace;

  @Before
  public void setUp() throws Exception {
    exceptionViewModel = new ExceptionViewModel(resourceResolver, resources);
    setObservable(exceptionViewModel);
    throwable = new NullPointerException();
    stackTrace = "Stack Trace";
    when(throwableModel.getStackTraceElements()).thenReturn(throwable.getStackTrace());
    when(throwableModel.getStackTraceFormatted()).thenReturn(stackTrace);
    when(appInfo.getAppName()).thenReturn(APP_NAME);
    when(appInfo.getAppVersionName()).thenReturn(APP_VERSION_NAME);
    when(appInfo.getVersionCode()).thenReturn(APP_VERSION_CODE);
  }

  @Test
  public void shouldNotifyPropertiesOnPopulate() throws Exception {
    exceptionViewModel.populate(throwableModel, appInfo);

    shouldNotify(BR.title);
    shouldNotify(BR.stackTrace);
  }

  @Test
  public void shouldReturnTitleOfException() throws Exception {
    int lineNumber = throwable.getStackTrace()[0].getLineNumber();
    exceptionViewModel.populate(throwableModel, appInfo);

    assertThat(exceptionViewModel.getTitle(),
        is(String.format(Locale.getDefault(), "%s.java line %d\n%s.setUp", getClass().getSimpleName(), lineNumber, getClass().getName())));
  }

  @Test
  public void shouldReturnStackTrace() throws Exception {
    exceptionViewModel.populate(throwableModel, appInfo);

    assertThat(exceptionViewModel.getStackTrace(), is(stackTrace));
  }

  @Test
  public void shouldReturnStepsToReplicate() throws Exception {
    String stepsToReplicate = "Very hard to replicate";
    exceptionViewModel.setStepsToReplicate(stepsToReplicate);

    assertThat(exceptionViewModel.getStepsToReplicate(), is(stepsToReplicate));
  }

  @Test
  public void shouldReturnExceptionClassWithLineNumberAsEmailSubject() throws Exception {
    int lineNumber = throwable.getStackTrace()[0].getLineNumber();
    exceptionViewModel.populate(throwableModel, appInfo);

    assertThat(exceptionViewModel.getEmailSubject(), is(String.format(Locale.getDefault(), "%s.java line %d", getClass().getSimpleName(), lineNumber)));
  }

  @Test
  public void shouldReturnEmailBodyFromThrowableAndStepsToReplicateIfProvided() throws Exception {
    int lineNumber = throwable.getStackTrace()[0].getLineNumber();
    String stepsToReplicate = "Steps to replicate";

    exceptionViewModel.populate(throwableModel, appInfo);
    exceptionViewModel.setStepsToReplicate(stepsToReplicate);

    String title = String.format(Locale.getDefault(), "%s.java line %d", getClass().getSimpleName(), lineNumber);
    String subtitle = String.format(Locale.getDefault(), "%s.setUp", getClass().getName());
    String emailTemplate = "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s";
    when(resourceResolver.getRawString(R.raw.email_template)).thenReturn(emailTemplate);

    assertThat(exceptionViewModel.getEmailBody(), is(String.format(Locale.getDefault(),
      emailTemplate, APP_NAME, APP_NAME, APP_VERSION_NAME, APP_VERSION_CODE, title, subtitle, stackTrace, stepsToReplicate)));
  }

  @Test
  public void emailBodyContainsDefaultTextForStepsToReplicateIfNotProvided() throws Exception {
    int lineNumber = throwable.getStackTrace()[0].getLineNumber();
    String defailtStepsToReplicate = "Steps to replicate";

    exceptionViewModel.populate(throwableModel, appInfo);

    String title = String.format(Locale.getDefault(), "%s.java line %d", getClass().getSimpleName(), lineNumber);
    String subtitle = String.format(Locale.getDefault(), "%s.setUp", getClass().getName());
    String emailTemplate = "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s\n" +
      "%s";
    when(resourceResolver.getRawString(R.raw.email_template)).thenReturn(emailTemplate);
    when(resources.getString(R.string.default_steps_to_replicate)).thenReturn(defailtStepsToReplicate);

    assertThat(exceptionViewModel.getEmailBody(), is(String.format(Locale.getDefault(),
        emailTemplate, APP_NAME, APP_NAME, APP_VERSION_NAME, APP_VERSION_CODE, title, subtitle, stackTrace, defailtStepsToReplicate)));
  }
}
