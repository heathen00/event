package com.nickmlanglois.event.core;

import java.util.List;

interface ChannelCache {
  ChannelInternal getChannelInternal();

  List<SubscriberInternal> getSubscriberInternalList();

  void addSubscriberInternal(SubscriberInternal subscriberInternal);

  List<PublisherInternal> getPublisherInternalList();

  void addPublisherInternal(PublisherInternal publisherInternal);

  List<EventDescriptionInternal> getEventDescriptionInternalList();

  void addEventDescriptionInternal(EventDescriptionInternal eventDescriptionInternal);

  EventDescriptionInternal getEventDescriptionInternal(Channel channel, String family, String name);

  SubscriberInternal getSubscriberInternalForExternalSubscriber(Subscriber subscriber);

  void removeSubscriberInternal(SubscriberInternal subscriberInternal);

  void removePublisherInternal(PublisherInternal publisherInternal);

  void removeEventDescriptionInternal(EventDescriptionInternal eventDescriptionInternal);
}

