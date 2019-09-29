package com.nickmlanglois.event.core;

interface EventFactoryInternal extends EventFactory {
  Subject getNoSubject();

  Event createEvent(EventDescription eventDescription, Subject subject);

  ChannelInternal getDeletedChannelInternal(String channelName);

  // TODO should this even be defined here? I would expect the ONLY layer to access the instance
  // cache to be the cacher.
  InstanceCache getInstanceCache();

  EventFactoryInternal getHeadEventFactoryInternal();

  void setHeadEventFactoryInternal(EventFactoryInternal headEventFactoryInternal);

  EventFactoryInternal getNextEventFactoryInternal();

  void setNextEventFactoryInternal(EventFactoryInternal nextEventFactoryInternal);
}
