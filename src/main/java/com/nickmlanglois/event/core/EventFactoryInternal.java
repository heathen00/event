package com.nickmlanglois.event.core;

interface EventFactoryInternal extends EventFactory {
  Subject getNoSubject();

  Event createEvent(EventDescription eventDescription, Subject subject);

  ChannelInternal getDeletedChannelInternal(String channelName);

  InstanceCacheInternal getInstanceCacheInternal();

  EventFactoryInternal getHeadEventFactoryInternal();

  void setHeadEventFactoryInternal(EventFactoryInternal headEventFactoryInternal);

  EventFactoryInternal getNextEventFactoryInternal();

  void setNextEventFactoryInternal(EventFactoryInternal nextEventFactoryInternal);
}
