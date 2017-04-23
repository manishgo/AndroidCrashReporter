package com.manishgo.mobile.android.exceptionreporter.binding;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CustomBinding {
  @BindingAdapter(value = {"text", "textAttrChanged"}, requireAll = false)
  public static void setText(EditText view, String text, final InverseBindingListener bindingListener) {
    view.setText(text);
    view.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        bindingListener.onChange();
      }
    });
  }

  @InverseBindingAdapter(attribute = "text", event = "textAttrChanged")
  public static String getText(EditText view) {
    return view.getText().toString();
  }
}
