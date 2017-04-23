package com.manishgo.mobile.android.exceptionreporter.resourceresolver;

import android.content.Context;
import android.support.annotation.RawRes;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class RawResourceResolver {
  private Context context;

  public RawResourceResolver(Context context) {
    this.context = context;
  }

  public String getRawString(@RawRes int id) throws IOException {
    try(InputStream inputStream = context.getResources().openRawResource(id)) {
      return IOUtils.toString(inputStream);
    }
  }
}
