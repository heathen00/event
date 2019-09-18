package com.nickmlanglois.event.core;

public class MutableAccumulatorSubscriberStub extends AccumulatorSubscriberStub {
  private String name;

  public MutableAccumulatorSubscriberStub(String name) {
    super(name);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
