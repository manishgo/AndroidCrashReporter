package com.manishgo.mobile.android.exceptionreporter;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.Spanned;

import com.manishgo.mobile.android.exceptionreporter.activity.ExceptionEmailActivity;
import com.manishgo.mobile.android.exceptionreporter.model.AppInfo;
import com.manishgo.mobile.android.exceptionreporter.model.ThrowableModel;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import static android.content.Intent.EXTRA_INTENT;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.EXTRA_SUBJECT;
import static android.content.Intent.EXTRA_TEXT;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasType;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.manishgo.mobile.android.exceptionreporter.activity.ExceptionEmailActivity.EXTRA_APPINFO;
import static com.manishgo.mobile.android.exceptionreporter.activity.ExceptionEmailActivity.EXTRA_THROWABLE;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class ExceptionEmailActivityTest {
  private static final int APP_VERSION_CODE = 15136;
  private static final String APP_VERSION_NAME = "4.1";
  private static final String APP_NAME = "My App";

  @Rule
  public IntentsTestRule<ExceptionEmailActivity> rule
      = new IntentsTestRule<>(ExceptionEmailActivity.class, true, false);

  @Rule
  public TestName testName = new TestName();

  @Test
  public void shouldRenderThrowableDetailsOnLoading() throws Exception {
    NullPointerException throwable = new NullPointerException();
    int lineNumber = throwable.getStackTrace()[0].getLineNumber();
    Intent intent = new Intent();
    intent.putExtra(EXTRA_THROWABLE, new ThrowableModel(throwable));
    intent.putExtra(EXTRA_APPINFO, new AppInfo(APP_NAME, APP_VERSION_NAME, APP_VERSION_CODE));

    rule.launchActivity(intent);

    String expectedTitle = String.format(Locale.getDefault(), "%s.java line %d\n%s.%s", getClass().getSimpleName(), lineNumber, getClass().getName(), testName.getMethodName());
    String stackTraceFirstLine = throwable.getStackTrace()[0].toString();
    onView(withText(expectedTitle)).check(matches(isDisplayed()));
    onView(withText(containsString(stackTraceFirstLine))).check(matches(isDisplayed()));
    onView(withHint(R.string.steps_to_replicate_hint)).check(matches(isDisplayed()));
  }

  @Test
  public void shouldSendEmailWithExceptionDetails() throws Exception {
    NullPointerException throwable = new NullPointerException();
    int lineNumber = throwable.getStackTrace()[0].getLineNumber();
    Intent intent = new Intent();
    intent.putExtra(EXTRA_THROWABLE, new ThrowableModel(throwable));
    intent.putExtra(EXTRA_APPINFO, new AppInfo(APP_NAME, APP_VERSION_NAME, APP_VERSION_CODE));

    rule.launchActivity(intent);

    String stepsToReplicate = "Login and go to My Trips. Application crashes!!!";
    onView(withHint(R.string.steps_to_replicate_hint)).perform(typeText(stepsToReplicate));

    intending(anyIntent()).respondWith(new ActivityResult(Activity.RESULT_OK, new Intent()));

    onView(withText(R.string.mail_send_button_text)).perform(scrollTo(), click());

    String expectedSubject = String.format(Locale.getDefault(), "%s.java line %d", getClass().getSimpleName(), lineNumber);
    String expectedTitle = String.format(Locale.getDefault(), "%s.java line %d", getClass().getSimpleName(), lineNumber);
    String expectedSubtitle = String.format(Locale.getDefault(), "%s.%s", getClass().getName(), testName.getMethodName());
    StringWriter stackTraceWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stackTraceWriter));
    String expectedStackTrace = stackTraceWriter.toString();
    final String expectedEmailBody = expectedEmailBody(expectedTitle, expectedSubtitle, expectedStackTrace, stepsToReplicate);
    Matcher<Intent> wrappedIntentMatcher = allOf(
        hasAction(Intent.ACTION_SEND),
        hasFlag(FLAG_GRANT_READ_URI_PERMISSION),
        hasType("*/*"),
        hasExtras(allOf(
            hasEntry(EXTRA_SUBJECT, expectedSubject),
            hasEntry(EXTRA_TEXT, new CustomTypeSafeMatcher<Spanned>(expectedEmailBody) {
              @Override
              protected boolean matchesSafely(Spanned item) {
                return expectedEmailBody.toString().equals(item.toString());
              }
            }),
            hasEntry(EXTRA_STREAM, Uri.parse("content://com.manishgo.mobile.android.exceptionreporter.fileprovider/logs/adb_logs.txt"))
        ))
    );
    intended(allOf(
        hasAction(Intent.ACTION_CHOOSER),
        hasExtras(allOf(hasEntry(is(EXTRA_INTENT), wrappedIntentMatcher),
            hasEntry(Intent.EXTRA_TITLE, getTargetContext().getString(R.string.email_application_chooser_title))))));
  }

  private String expectedEmailBody(String title, String subtitle, String stackTrace, String stepsToReplicate) throws IOException {
    InputStream is = getContext().getAssets().open("expected_email_body.txt");
    return  String.format(Locale.getDefault(), IOUtils.toString(is), APP_NAME, APP_NAME, APP_VERSION_NAME, APP_VERSION_CODE, title, subtitle, stackTrace, stepsToReplicate);
  }
}
