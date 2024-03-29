package com.nickmlanglois.event.core;

interface EventFactoryInternal extends EventFactory {
  InstanceCache getInstanceCache();

  EventFactoryInternal getRootEventFactoryInternal();

  Subject getNoSubject();

  Event createEvent(EventDescription eventDescription, Subject subject);

  ChannelInternal getDeletedChannelInternal(String channelName);
}
