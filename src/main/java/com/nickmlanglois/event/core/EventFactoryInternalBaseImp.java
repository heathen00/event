package com.nickmlanglois.event.core;

abstract class EventFactoryInternalBaseImp implements EventFactoryInternal {
  private EventFactoryInternal eventFactoryInternalHead;
  private EventFactoryInternal nextEventFactoryInternal;

  EventFactoryInternalBaseImp() {}

  @Override
  public Channel createChannel(String name) {
    return getNextEventFactoryInternal().createChannel(name);
  }

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    return getNextEventFactoryInternal().createEventDescription(channel, family, name);
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    return getNextEventFactoryInternal().createPublisher(channel);
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    getNextEventFactoryInternal().addSubscriber(channel, subscriber);
  }

  @Override
  public void openChannel(Channel channel) {
    getNextEventFactoryInternal().openChannel(channel);
  }

  @Override
  public void removeSubcriber(Subscriber subscriber) {
    getNextEventFactoryInternal().removeSubcriber(subscriber);
  }

  @Override
  public void deletePublisher(Publisher publisher) {
    getNextEventFactoryInternal().deletePublisher(publisher);
  }

  @Override
  public void deleteEventDescription(EventDescription eventDescription) {
    getNextEventFactoryInternal().deleteEventDescription(eventDescription);
  }

  @Override
  public void deleteChannel(Channel channel) {
    getNextEventFactoryInternal().deleteChannel(channel);
  }

  @Override
  public InstanceCache getInstanceCache() {
    return getNextEventFactoryInternal().getInstanceCache();
  }

  @Override
  public Subject getNoSubject() {
    return getNextEventFactoryInternal().getNoSubject();
  }

  @Override
  public Event createEvent(EventDescription eventDescription, Subject subject) {
    return getNextEventFactoryInternal().createEvent(eventDescription, subject);
  }

  @Override
  public ChannelInternal getDeletedChannelInternal(String channelName) {
    return getNextEventFactoryInternal().getDeletedChannelInternal(channelName);
  }

  @Override
  public final EventFactoryInternal getHeadEventFactoryInternal() {
    return eventFactoryInternalHead;
  }

  @Override
  public final EventFactoryInternal getNextEventFactoryInternal() {
    return nextEventFactoryInternal;
  }

  @Override
  public final void setHeadEventFactoryInternal(EventFactoryInternal headEventFactoryInternal) {
    this.eventFactoryInternalHead = headEventFactoryInternal;
  }

  @Override
  public final void setNextEventFactoryInternal(EventFactoryInternal nextEventFactoryInternal) {
    this.nextEventFactoryInternal = nextEventFactoryInternal;
  }
}
