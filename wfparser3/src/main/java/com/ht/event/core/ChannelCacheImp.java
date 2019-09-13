package com.ht.event.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class ChannelCacheImp implements ChannelCache {
  private final ChannelInternal channelInternal;
  private final List<Subscriber> subscriberList;
  private final List<Publisher> publisherList;
  private final List<EventDescription> eventDescriptionList;
  private final EventDescriptionForComparisonImp eventDescriptionForComparison;

  ChannelCacheImp(ChannelInternal channelInternal) {
    this.channelInternal = channelInternal;
    subscriberList = new ArrayList<>();
    publisherList = new ArrayList<>();
    eventDescriptionList = new ArrayList<>();
    eventDescriptionForComparison = new EventDescriptionForComparisonImp();
  }

  public ChannelInternal getChannelInternal() {
    return channelInternal;
  }

  @Override
  public List<Subscriber> getSubscriberList() {
    return Collections.unmodifiableList(subscriberList);
  }

  @Override
  public void addSubscriber(Subscriber eventSubscriber) {
    if (!subscriberList.contains(eventSubscriber)) {
      subscriberList.add(eventSubscriber);
    }
  }

  @Override
  public void addPublisher(Publisher eventPublisher) {
    publisherList.add(eventPublisher);
  }

  @Override
  public List<Publisher> getPublisherList() {
    return Collections.unmodifiableList(publisherList);
  }

  @Override
  public String toString() {
    return "ChannelCacheImp [getChannelInternal()=" + getChannelInternal()
        + ", getSubscriberList()=" + getSubscriberList() + ", getPublisherList()="
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
    return Collections.unmodifiableList(eventDescriptionList);
  }
}
