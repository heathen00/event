package com.nickmlanglois.event.core;

import java.util.List;

interface ChannelCache {
  ChannelInternal getChannelInternal();

  List<SubscriberInternal> getSubscriberInternalList();

  void addSubscriberInternal(SubscriberInternal subscriberInternal);

  List<PublisherInternal> getPublisherInternalList();

  void addPublisher(PublisherInternal publisherInternal);

  List<EventDescription> getEventDescriptionList();

  void addEventDescription(EventDescription eventDescription);

  EventDescription getEventDescription(Channel channel, String family, String name);

  SubscriberInternal getInternalSubscriberForExternalSubscriber(Subscriber subscriber);

  void removeSubscriberInternal(SubscriberInternal subscriberInternal);

  void removePublisher(PublisherInternal publisherInternal);
}

