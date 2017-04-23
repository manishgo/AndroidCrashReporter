package com.manishgo.mobile.android.exceptionreporter.resourceresolver;

import android.content.res.Resources;
import android.support.annotation.RawRes;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class RawResourceResolver {
  private Resources resources;

  public RawResourceResolver(Resources resources) {
    this.resources = resources;
  }

  public String getRawString(@RawRes int id) throws IOException {
    try(InputStream inputStream = resources.openRawResource(id)) {
      return IOUtils.toString(inputStream, Charset.defaultCharset());
    }
  }
}
