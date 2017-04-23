package com.manishgo.mobile.android.exceptionreporter.util;

import android.databinding.BaseObservable;
import android.databinding.Observable;

import org.junit.After;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public abstract class ObservableViewModelTest {

  private List<Integer> notifiedProperties;
  private List<Integer> expectedProperties;

  public ObservableViewModelTest() {
    notifiedProperties = new ArrayList<>();
    expectedProperties = new ArrayList<>();
  }

  protected void setObservable(BaseObservable baseObservable) {
    baseObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
      @Override
      public void onPropertyChanged(Observable observable, int propertyId) {
        notifiedProperties.add(propertyId);
      }
    });
  }

  @After
  public void tearDown() throws Exception {
    verifyAllPropertiesNotified();
  }

  protected void shouldNotify(final int property) {
    expectedProperties.add(property);
  }

  protected void shouldNotify(final int property, int noOfTimes) {
    for (int i = 0; i < noOfTimes; i++) {
      expectedProperties.add(property);
    }
  }

  protected void verifyAllPropertiesNotified() {
    assertTrue(minus(expectedProperties, notifiedProperties).isEmpty());
  }

  protected void verifyPropertyNotNotified(final int property) {
    assertFalse(notifiedProperties.contains(property));
  }

  protected void verifyNoOtherPropertiesNotified() {
    assertTrue(minus(notifiedProperties, expectedProperties).isEmpty());
  }

  private List<Integer> minus(List<Integer> list1, List<Integer> list2) {
    ArrayList<Integer> c = new ArrayList<>(list1);
    for (Integer integer : list2) {
      if(c.isEmpty()) break;

      c.remove(integer);
    }
    return c;
  }
}
