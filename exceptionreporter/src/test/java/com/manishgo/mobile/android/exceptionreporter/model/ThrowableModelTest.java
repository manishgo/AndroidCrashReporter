package com.manishgo.mobile.android.exceptionreporter.model;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ThrowableModelTest {
  @Test
  public void shouldReturnStackTraceElementsOfGivenThrowable() throws Exception {
    NullPointerException throwable = new NullPointerException();
    ThrowableModel model = new ThrowableModel(throwable);

    assertThat(model.getStackTraceElements(), is(throwable.getStackTrace()));
  }

  @Test
  public void shouldReturnFormattedStackTraceOfGivenThrowable() throws Exception {
    NullPointerException throwable = new NullPointerException();
    ThrowableModel model = new ThrowableModel(throwable);
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);

    assertThat(model.getStackTraceFormatted(), is(stringWriter.toString()));
  }
}
