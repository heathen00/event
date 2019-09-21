package com.nickmlanglois.event.core;

import java.util.ArrayList;
import java.util.List;

final class ChannelCacheImp implements ChannelCache {
  private final ChannelInternal channelInternal;
  private final List<SubscriberInternal> subscriberInternalList;
  private final List<Publisher> publisherList;
  private final List<EventDescription> eventDescriptionList;
  private final EventDescriptionForComparisonImp eventDescriptionForComparison;

  ChannelCacheImp(ChannelInternal channelInternal) {
    this.channelInternal = channelInternal;
    subscriberInternalList = new ArrayList<>();
    publisherList = new ArrayList<>();
    eventDescriptionList = new ArrayList<>();
    eventDescriptionForComparison = new EventDescriptionForComparisonImp();
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
  public void addPublisher(Publisher eventPublisher) {
    publisherList.add(eventPublisher);
  }

  @Override
  public List<Publisher> getPublisherList() {
    return publisherList;
  }

  @Override
  public String toString() {
    return "ChannelCacheImp [getChannelInternal()=" + getChannelInternal()
        + ", getSubscriberList()=" + getSubscriberInternalList() + ", getPublisherList()="
        + getPublisherList() + ", getEventDescriptionList()=" + getEventDescriptionList()
        + ", hashCode()=" + hashCode() + "]";
  }

  @Override
  public EventDescription getEventDescription(Channel channel, String family, String name) {
    eventDescriptionForComparison.setChannel(channel);
    eventDescriptionForComparison.setFamily(family);
    eventDescriptionForComparison.setName(name);
    EventDescription eventDescription = null;
    int existingEventDescriptionIndex = eventDescriptionList.indexOf(eventDescriptionForComparison);
    if (-1 != existingEventDescriptionIndex) {
      eventDescription = eventDescriptionList.get(existingEventDescriptionIndex);
    }
    return eventDescription;
  }

  @Override
  public void addEventDescription(EventDescription eventDescription) {
    eventDescriptionList.add(eventDescription);
  }

  @Override
  public List<EventDescription> getEventDescriptionList() {
    return eventDescriptionList;
  }

  @Override
  public SubscriberInternal getInternalSubscriberForExternalSubscriber(Subscriber subscriber) {
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
}
