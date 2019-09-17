package com.nickmlanglois.event.core;

public abstract class Subscriber implements SubscriberPublished<Subscriber> {
  private ChannelInternal channelInternal;

  @Override
  public final void setChannel(Channel channel) {
    channelInternal = (ChannelInternal) channel;
  }

  @Override
  public final Channel getChannel() {
    return channelInternal;
  }

  @Override
  public void processPublishEventCallback(Event event) {}

  @Override
  public void processUnpublishEventCallback(Event event) {}

  @Override
  public final Subscriber getExternalSubscriber() {
    return this;
  }

  @Override
  public final void resendAllCurrentPublishedEvents() {
    channelInternal.resendAllCurrentPublishedEventsToExternalSubscriber(this);
  }
}
