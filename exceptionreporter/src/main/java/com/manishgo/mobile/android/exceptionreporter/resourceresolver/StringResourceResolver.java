package com.manishgo.mobile.android.exceptionreporter.resourceresolver;

import android.content.Context;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

import com.google.repacked.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class StringResourceResolver {
  private Context context;

  public StringResourceResolver(Context context) {
    this.context = context;
  }

  public String getString(@StringRes int id) {
    return context.getResources().getString(id);
  }
}
