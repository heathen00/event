package com.nickmlanglois.event.core;

final class EventFactoryInternalCacherImp implements EventFactoryInternal {
  private final EventFactoryInternal rootEventFactoryInternal;
  private final EventFactoryInternal nextEventFactoryInternal;
  private final InstanceCache instanceCache;

  EventFactoryInternalCacherImp(EventFactoryInternal rootEventFactoryInternal,
      EventFactoryInternal nextEventFactoryInternal) {
    this.rootEventFactoryInternal = rootEventFactoryInternal;
    this.nextEventFactoryInternal = nextEventFactoryInternal;
    this.instanceCache = InstanceCache.createInstanceCache();
  }


  private void ensureChannelBelongsToFactory(Channel channel) {
    if (null == instanceCache.getChannelCache(channel.getName())) {
      throw new UnsupportedOperationException(
          "channel " + channel.getName() + " does not exist in this factory");
    }
  }

  @Override
  public Channel createChannel(String name) {
    ChannelInternal channelInternal = null;
    if (null == instanceCache.getChannelCache(name)) {
      channelInternal = (ChannelInternal) nextEventFactoryInternal.createChannel(name);
      instanceCache.addChannelCache(name, channelInternal);
    } else {
      channelInternal = instanceCache.getChannelCache(name).getChannelInternal();
    }
    return channelInternal;
  }

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    ensureChannelBelongsToFactory(channel);
    EventDescription eventDescription =
        getChannelCache(channel).getEventDescription(channel, family, name);
    if (null == eventDescription) {
      eventDescription = nextEventFactoryInternal.createEventDescription(channel, family, name);
      getChannelCache(channel).addEventDescription(eventDescription);
    }
    return eventDescription;
  }

  @Override
  public Event createEvent(EventDescription eventDescription, Subject subject) {
    return nextEventFactoryInternal.createEvent(eventDescription, subject);
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    ensureChannelBelongsToFactory(channel);
    Publisher newPublisher = nextEventFactoryInternal.createPublisher(channel);
    getChannelCache(channel).addPublisher(newPublisher);
    return newPublisher;
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    ensureChannelBelongsToFactory(channel);
    SubscriberInternal subscriberInternal =
        getChannelCache(channel).getInternalSubscriberForExternalSubscriber(subscriber);
    if (null != subscriberInternal) {
      return;
    }
    subscriberInternal = new SubscriberInternalImp(subscriber);
    ChannelInternal subscriberInternalsCurrentChannelInternal =
        getInstanceCache().getChannelInternalForSubscriberInternal(subscriberInternal);
    if (null != subscriberInternalsCurrentChannelInternal
        && !channel.equals(subscriberInternalsCurrentChannelInternal)) {
      throw new UnsupportedOperationException("subscriber already subscribed to channel "
          + subscriberInternalsCurrentChannelInternal.getName());
    }
    getChannelCache(channel).addSubscriberInternal(subscriberInternal);
    subscriber.setChannel(channel);
  }

  @Override
  public void openChannel(Channel channel) {
    ensureChannelBelongsToFactory(channel);
    getChannelCache(channel).getChannelInternal().open();
  }

  private ChannelCache getChannelCache(Channel channel) {
    return instanceCache.getChannelCache(channel.getName());
  }

  @Override
  public InstanceCache getInstanceCache() {
    return instanceCache;
  }

  @Override
  public EventFactoryInternal getRootEventFactoryInternal() {
    return rootEventFactoryInternal;
  }

  @Override
  public Subject getNoSubject() {
    return nextEventFactoryInternal.getNoSubject();
  }
}
