package com.nickmlanglois.event.core;

final class EventFactoryInternalTailImp implements EventFactoryInternal {
  private void throwExceptionBecauseTailReached(String methodName) {
    throw new UnsupportedOperationException(
        "EventFactory did not properly implement " + methodName);
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    throwExceptionBecauseTailReached("addSubscriber()");
  }

  @Override
  public void openChannel(Channel channel) {
    throwExceptionBecauseTailReached("openChannel()");
  }

  @Override
  public InstanceCacheInternal getInstanceCacheInternal() {
    throwExceptionBecauseTailReached("getInstanceCache()");
    return null;
  }

  @Override
  public void removeSubcriber(Subscriber subscriber) {
    throwExceptionBecauseTailReached("removeSubscriber()");
  }

  @Override
  public void deletePublisher(Publisher publisher) {
    throwExceptionBecauseTailReached("deletePublisher()");
  }

  @Override
  public void deleteEventDescription(EventDescription eventDescription) {
    throwExceptionBecauseTailReached("deleteEventDescription()");
  }

  @Override
  public void deleteChannel(Channel channel) {
    throwExceptionBecauseTailReached("deleteChannel()");
  }

  @Override
  public Channel createChannel(String name) {
    throwExceptionBecauseTailReached("createChannel()");
    return null;
  }

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    throwExceptionBecauseTailReached("createDescription()");
    return null;
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    throwExceptionBecauseTailReached("createPublisher()");
    return null;
  }

  @Override
  public Subject getNoSubject() {
    throwExceptionBecauseTailReached("getNoSubject()");
    return null;
  }

  @Override
  public Event createEvent(EventDescription eventDescription, Subject subject) {
    throwExceptionBecauseTailReached("createEvent()");
    return null;
  }

  @Override
  public ChannelInternal getDeletedChannelInternal(String channelName) {
    throwExceptionBecauseTailReached("getDeletedChannelInternal()");
    return null;
  }

  @Override
  public EventFactoryInternal getHeadEventFactoryInternal() {
    throwExceptionBecauseTailReached("getHeadEventFactoryInteral()");
    return null;
  }

  @Override
  public void setHeadEventFactoryInternal(EventFactoryInternal headEventFactoryInternal) {}

  @Override
  public EventFactoryInternal getNextEventFactoryInternal() {
    throwExceptionBecauseTailReached("getNextEventFactoryInternal()");
    return null;
  }

  @Override
  public void setNextEventFactoryInternal(EventFactoryInternal nextEventFactoryInternal) {}

  @Override
  public EventDescriptionInternal createEventDescriptionInternal(ChannelInternal channelInternal,
      String family, String name) {
    throwExceptionBecauseTailReached("createEventDescriptionInternal()");
    return null;
  }

  @Override
  public void deleteEventDescriptionInternal(EventDescriptionInternal eventDescriptionInternal) {
    throwExceptionBecauseTailReached("deleteEventDescriptionInternal()");
  }
}
