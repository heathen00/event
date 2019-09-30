package com.nickmlanglois.event.core;

import java.util.ArrayList;
import java.util.List;

final class ChannelCacheInternalImp implements ChannelCacheInternal {
  private final ChannelInternal channelInternal;
  private final List<SubscriberInternal> subscriberInternalList;
  private final List<PublisherInternal> publisherInternalList;
  private final List<EventDescriptionInternal> eventDescriptionInternalList;
  private final EventDescriptionInternalForComparisonImp eventDescriptionInternalForComparison;

  ChannelCacheInternalImp(ChannelInternal channelInternal) {
    this.channelInternal = channelInternal;
    subscriberInternalList = new ArrayList<>();
    publisherInternalList = new ArrayList<>();
    eventDescriptionInternalList = new ArrayList<>();
    eventDescriptionInternalForComparison = new EventDescriptionInternalForComparisonImp();
  }

  public ChannelInternal getChannelInternal() {
    return channelInternal;
  }

  @Override
  public List<SubscriberInternal> getSubscriberInternalList() {
    return subscriberInternalList;
  }

  @Override
  public void addSubscriberInternal(SubscriberInternal subscriber) {
    if (!subscriberInternalList.contains(subscriber)) {
      subscriberInternalList.add(subscriber);
    }
  }

  @Override
  public void addPublisherInternal(PublisherInternal publisherInternal) {
    publisherInternalList.add(publisherInternal);
  }

  @Override
  public List<PublisherInternal> getPublisherInternalList() {
    return publisherInternalList;
  }

  @Override
  public String toString() {
    return "ChannelCacheImp [getChannelInternal()=" + getChannelInternal()
        + ", getSubscriberInternalList()=" + getSubscriberInternalList()
        + ", getPublisherInternalList()=" + getPublisherInternalList()
        + ", getEventDescriptionInternalList()=" + getEventDescriptionInternalList()
        + ", hashCode()=" + hashCode() + "]";
  }

  @Override
  public EventDescriptionInternal getEventDescriptionInternal(Channel channel, String family,
      String name) {
    eventDescriptionInternalForComparison.setChannelInternal((ChannelInternal) channel);
    eventDescriptionInternalForComparison.setFamily(family);
    eventDescriptionInternalForComparison.setName(name);
    EventDescriptionInternal eventDescriptionInternal = null;
    int existingEventDescriptionIndex =
        eventDescriptionInternalList.indexOf(eventDescriptionInternalForComparison);
    if (-1 != existingEventDescriptionIndex) {
      eventDescriptionInternal = eventDescriptionInternalList.get(existingEventDescriptionIndex);
    }
    return eventDescriptionInternal;
  }

  @Override
  public void addEventDescriptionInternal(EventDescriptionInternal eventDescriptionInternal) {
    eventDescriptionInternalList.add(eventDescriptionInternal);
  }

  @Override
  public List<EventDescriptionInternal> getEventDescriptionInternalList() {
    return eventDescriptionInternalList;
  }

  @Override
  public SubscriberInternal getSubscriberInternalForExternalSubscriber(Subscriber subscriber) {
    for (SubscriberInternal subscriberInternal : subscriberInternalList) {
      if (subscriber == subscriberInternal.getExternalSubscriber()) {
        return subscriberInternal;
      }
    }
    return null;
  }

  @Override
  public void removeSubscriberInternal(SubscriberInternal subscriberInternal) {
    subscriberInternalList.remove(subscriberInternal);
  }

  @Override
  public void removePublisherInternal(PublisherInternal publisherInternal) {
    publisherInternalList.remove(publisherInternal);
  }

  @Override
  public void removeEventDescriptionInternal(EventDescriptionInternal eventDescriptionInternal) {
    eventDescriptionInternalList.remove(eventDescriptionInternal);
  }
}
