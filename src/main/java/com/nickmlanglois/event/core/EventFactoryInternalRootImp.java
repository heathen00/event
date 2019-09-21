package com.nickmlanglois.event.core;

final class EventFactoryInternalRootImp implements EventFactoryInternal {
  private final EventFactoryInternal nextEventFactoryInternal;

  EventFactoryInternalRootImp() {
    nextEventFactoryInternal = new EventFactoryInternalParameterValidatorImp(this,
        new EventFactoryInternalCacherImp(this, new EventFactoryInternalCreatorImp(this)));
  }

  @Override
  public Channel createChannel(String name) {
    return nextEventFactoryInternal.createChannel(name);
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    return nextEventFactoryInternal.createPublisher(channel);
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    nextEventFactoryInternal.addSubscriber(channel, subscriber);
  }

  @Override
  public void openChannel(Channel channel) {
    nextEventFactoryInternal.openChannel(channel);
  }

  @Override
  public EventFactoryInternal getRootEventFactoryInternal() {
    return this;
  }

  @Override
  public InstanceCache getInstanceCache() {
    return nextEventFactoryInternal.getInstanceCache();
  }

  @Override
  public Subject getNoSubject() {
    return nextEventFactoryInternal.getNoSubject();
  }

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    return nextEventFactoryInternal.createEventDescription(channel, family, name);
  }

  @Override
  public Event createEvent(EventDescription eventDescription, Subject subject) {
    return nextEventFactoryInternal.createEvent(eventDescription, subject);
  }

  @Override
  public void removeSubcriber(Subscriber subscriber) {
    nextEventFactoryInternal.removeSubcriber(subscriber);
  }
}
