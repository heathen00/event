package com.nickmlanglois.event.core;

final class SubscriberInternalImp implements SubscriberInternal {
  private final Subscriber externalSubscriber;
  private final String name;

  SubscriberInternalImp(Subscriber subscriber) {
    externalSubscriber = subscriber;
    name = externalSubscriber.getName();
  }

  @Override
  public Channel getChannel() {
    return externalSubscriber.getChannel();
  }

  @Override
  public void processPublishEventCallback(Event event) {
    try {
      externalSubscriber.processPublishEventCallback(event);
    } catch (Exception e) {
    }
  }

  @Override
  public void processUnpublishEventCallback(Event event) {
    try {
      externalSubscriber.processUnpublishEventCallback(event);
    } catch (Exception e) {
    }
  }

  @Override
  public void resendAllCurrentPublishedEvents() {
    externalSubscriber.resendAllCurrentPublishedEvents();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Subscriber getExternalSubscriber() {
    return externalSubscriber.getExternalSubscriber();
  }

  @Override
  public int compareTo(Subscriber o) {
    return getName().compareTo(o.getName());
  }

  @Override
  public boolean equals(Object obj) {
    return getName().equals(((SubscriberInternal) obj).getName());
  }

  @Override
  public int hashCode() {
    return getName().hashCode();
  }

  @Override
  public String toString() {
    return "SubscriberInternalImp [externalSubscriber.toString()=" + externalSubscriber.toString()
        + "]";
  }
}
