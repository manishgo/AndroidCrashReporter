package com.manishgo.mobile.android.exceptionreporter.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.manishgo.mobile.android.exceptionreporter.R;
import com.manishgo.mobile.android.exceptionreporter.databinding.ExceptionEmailBinding;
import com.manishgo.mobile.android.exceptionreporter.email.EmailSender;
import com.manishgo.mobile.android.exceptionreporter.log.LogService;
import com.manishgo.mobile.android.exceptionreporter.model.AppInfo;
import com.manishgo.mobile.android.exceptionreporter.model.ThrowableModel;
import com.manishgo.mobile.android.exceptionreporter.presenter.ExceptionEmailPresenter;
import com.manishgo.mobile.android.exceptionreporter.resourceresolver.RawResourceResolver;
import com.manishgo.mobile.android.exceptionreporter.viewmodel.ExceptionViewModel;

public class ExceptionEmailActivity extends Activity {

  public static final String EXTRA_THROWABLE = "com.manishgo.mobile.android.exceptionreporter.EXTRA_THROWABLE";
  public static final String EXTRA_APPINFO = "com.manishgo.mobile.android.exceptionreporter.EXTRA_APPINFO";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ExceptionEmailBinding viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_exception_email);
    Resources resources = getResources();
    ExceptionViewModel viewModel =  new ExceptionViewModel(new RawResourceResolver(resources), resources);

    ExceptionEmailPresenter presenter = new ExceptionEmailPresenter(new EmailSender(this), new LogService(this));
    ThrowableModel throwableModel = (ThrowableModel) getIntent().getSerializableExtra(EXTRA_THROWABLE);
    AppInfo appInfo = (AppInfo) getIntent().getSerializableExtra(EXTRA_APPINFO);
    presenter.onCreate(throwableModel, viewModel, appInfo);

    viewDataBinding.setViewModel(viewModel);
    viewDataBinding.setPresenter(presenter);
  }
}
