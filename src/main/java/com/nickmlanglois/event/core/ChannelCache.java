package com.nickmlanglois.event.core;

import java.util.List;

interface ChannelCache {
  ChannelInternal getChannelInternal();

  List<SubscriberInternal> getSubscriberInternalList();

  void addSubscriberInternal(SubscriberInternal subscriberInternal);

  List<Publisher> getPublisherList();

  void addPublisher(Publisher eventPublisher);

  List<EventDescription> getEventDescriptionList();

  void addEventDescription(EventDescription eventDescription);

  EventDescription getEventDescription(Channel channel, String family, String name);

  SubscriberInternal getInternalSubscriberForExternalSubscriber(Subscriber subscriber);
}

