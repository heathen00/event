package com.nickmlanglois.event.core;

public final class PublisherImp implements Publisher {
  private final EventFactoryInternal eventFactoryInternal;
  private final ChannelInternal channelInternal;

  PublisherImp(EventFactoryInternal eventFactoryInternal, ChannelInternal channelInternal) {
    this.eventFactoryInternal = eventFactoryInternal;
    this.channelInternal = channelInternal;
  }

  @Override
  public Channel getChannel() {
    return channelInternal;
  }

  @Override
  public void publish(EventDescription eventDescription) {
    channelInternal.publish(this, eventDescription);
  }

  @Override
  public void publish(EventDescription eventDescription, Subject subject) {
    channelInternal.publish(this, eventDescription, subject);
  }

  @Override
  public void unpublish(EventDescription eventDescription) {
    channelInternal.unpublish(this, eventDescription);
  }

  @Override
  public void unpublish(EventDescription eventDescription, Subject subject) {
    channelInternal.unpublish(this, eventDescription, subject);
  }
}
