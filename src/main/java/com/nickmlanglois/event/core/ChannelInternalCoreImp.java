package com.nickmlanglois.event.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ChannelInternalCoreImp extends NaturalOrderBase<Channel> implements ChannelInternal {
  private final EventFactoryInternal eventFactoryInternal;
  private final ChannelInternal rootChannelInternal;
  private final String name;
  private final Map<Event, Set<Publisher>> publishedEventToPublisherMap;
  private final List<Event> publishedEventList;
  private boolean isOpen;

  ChannelInternalCoreImp(EventFactoryInternal eventFactoryInternal,
      ChannelInternal rootChannelInternal, String name) {
    this.eventFactoryInternal = eventFactoryInternal;
    this.rootChannelInternal = rootChannelInternal;
    this.name = name;
    publishedEventToPublisherMap = new HashMap<>();
    publishedEventList = new ArrayList<>();
    isOpen = false;
  }

  private ChannelCache getChannelCache() {
    return eventFactoryInternal.getInstanceCache().getChannelCache(getName());
  }

  private boolean shouldEventBePublishedAndUpdatePublishedEvents(Event event, Publisher publisher) {
    if (publishedEventToPublisherMap.containsKey(event)) {
      publishedEventToPublisherMap.get(event).add(publisher);
      return false;
    }
    return true;
  }

  private boolean shouldEventBeUnpublishedAndUpdatePublishedEvents(Event event,
      Publisher publisher) {
    if (!publishedEventToPublisherMap.containsKey(event)
        || !publishedEventToPublisherMap.get(event).contains(publisher)) {
      return false;
    }
    if (1 < publishedEventToPublisherMap.get(event).size()
        && publishedEventToPublisherMap.get(event).contains(publisher)) {
      publishedEventToPublisherMap.get(event).remove(publisher);
      return false;
    }
    return true;
  }

  private void addPublishedEvent(Event event, Publisher publisher) {
    if (!publishedEventToPublisherMap.containsKey(event)) {
      publishedEventToPublisherMap.put(event, new HashSet<>());
      publishedEventList.add(event);
    }
    publishedEventToPublisherMap.get(event).add(publisher);
  }

  private void removePublishedEvent(Event event, Publisher publisher) {
    publishedEventToPublisherMap.get(event).remove(publisher);
    if (0 == publishedEventToPublisherMap.get(event).size()) {
      publishedEventToPublisherMap.remove(event);
      publishedEventList.remove(event);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<Subscriber> getSubscriberList() {
    List<Subscriber> externalSubscriberList = new ArrayList<>();
    for (SubscriberInternal subscriberInternal : getChannelCache().getSubscriberInternalList()) {
      externalSubscriberList.add(subscriberInternal.getExternalSubscriber());
    }
    return Collections.unmodifiableList(externalSubscriberList);
  }

  @Override
  public List<Publisher> getPublisherList() {
    return Collections.unmodifiableList(getChannelCache().getPublisherInternalList());
  }

  @Override
  public void publish(Publisher publisher, EventDescription eventDescription) {
    publish(publisher, eventDescription, eventFactoryInternal.getNoSubject());
  }

  @Override
  public void publish(Publisher publisher, EventDescription eventDescription, Subject subject) {
    Event event = eventFactoryInternal.createEvent(eventDescription, subject);
    if (!shouldEventBePublishedAndUpdatePublishedEvents(event, publisher)) {
      return;
    }
    for (SubscriberInternal subscriber : getChannelCache().getSubscriberInternalList()) {
      subscriber.processPublishEventCallback(event);
    }
    addPublishedEvent(event, publisher);
  }

  @Override
  public void unpublish(Publisher publisher, EventDescription eventDescription) {
    unpublish(publisher, eventDescription, eventFactoryInternal.getNoSubject());
  }

  @Override
  public void unpublish(Publisher publisher, EventDescription eventDescription, Subject subject) {
    Event event = eventFactoryInternal.createEvent(eventDescription, subject);
    if (!shouldEventBeUnpublishedAndUpdatePublishedEvents(event, publisher)) {
      return;
    }
    for (SubscriberInternal subscriber : getChannelCache().getSubscriberInternalList()) {
      subscriber.processUnpublishEventCallback(event);
    }
    removePublishedEvent(event, publisher);
  }

  @Override
  public void open() {
    isOpen = true;
  }

  @Override
  public boolean isOpen() {
    return isOpen;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ChannelInternal other = (ChannelInternal) obj;
    if (name == null) {
      if (other.getName() != null) {
        return false;
      }
    } else if (!getName().equals(other.getName())) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(Channel o) {
    return getName().compareTo(o.getName());
  }

  @Override
  public EventFactoryInternal getEventFactoryInternal() {
    return eventFactoryInternal;
  }

  @Override
  public List<EventDescription> getEventDescriptionList() {
    return Collections.unmodifiableList(getChannelCache().getEventDescriptionInternalList());
  }

  @Override
  public ChannelInternal getRootChannelInternal() {
    return rootChannelInternal;
  }

  @Override
  public void resendAllCurrentPublishedEventsToExternalSubscriber(Subscriber subscriber) {
    for (Event publishedEvent : publishedEventList) {
      subscriber.processPublishEventCallback(publishedEvent);
    }
  }

  @Override
  public boolean isDefined() {
    return true;
  }
}
