package com.nickmlanglois.event.core;

public abstract class Subscriber implements SubscriberPublished<Subscriber> {

  @Override
  public void processPublishEvent(Event event) {}

  @Override
  public void processUnpublishEvent(Event event) {}

  @Override
  public final Subscriber getExternalSubscriber() {
    return this;
  }
}
