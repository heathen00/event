package com.ht.event.core;

import java.util.List;

final class ChannelInternalRootImp implements ChannelInternal {
  private final ChannelInternal nextChannelInternal;

  ChannelInternalRootImp(EventFactoryInternal eventFactoryInternal, String name) {
    this.nextChannelInternal = new ChannelInternalImp(eventFactoryInternal, this, name);
  }

  @Override
  public String getName() {
    return nextChannelInternal.getName();
  }

  @Override
  public List<EventDescription> getEventDescriptionList() {
    return nextChannelInternal.getEventDescriptionList();
  }

  @Override
  public List<Subscriber> getSubscriberList() {
    return nextChannelInternal.getSubscriberList();
  }

  @Override
  public List<Publisher> getPublisherList() {
    return nextChannelInternal.getPublisherList();
  }

  @Override
  public boolean isOpen() {
    return nextChannelInternal.isOpen();
  }

  @Override
  public int compareTo(Channel o) {
    return nextChannelInternal.compareTo(o);
  }

  @Override
  public EventFactoryInternal getEventFactoryInternal() {
    return nextChannelInternal.getEventFactoryInternal();
  }

  @Override
  public void publish(Publisher publisher, EventDescription eventDescription) {
    nextChannelInternal.publish(publisher, eventDescription);
  }

  @Override
  public void publish(Publisher publisher, EventDescription eventDescription, Subject subject) {
    nextChannelInternal.publish(publisher, eventDescription, subject);
  }

  @Override
  public void unpublish(Publisher publisher, EventDescription eventDescription) {
    nextChannelInternal.unpublish(publisher, eventDescription);
  }

  @Override
  public void unpublish(Publisher publisher, EventDescription eventDescription, Subject subject) {
    nextChannelInternal.unpublish(publisher, eventDescription, subject);
  }

  @Override
  public void open() {
    nextChannelInternal.open();
  }

  @Override
  public ChannelInternal getRootChannelInternal() {
    return this;
  }

}
