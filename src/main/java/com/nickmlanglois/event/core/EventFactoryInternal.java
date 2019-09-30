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

  EventDescriptionInternal createEventDescriptionInternal(ChannelInternal channelInternal,
      String family, String name);

  void deleteEventDescriptionInternal(EventDescriptionInternal eventDescriptionInternal);

  PublisherInternal createPublisherInternal(ChannelInternal channelInternal);

  void deletePublisherInternal(PublisherInternal publisherInternal);

  ChannelInternal createChannelInternal(String name);

  void deleteChannelInternal(ChannelInternal channelInternal);
}
