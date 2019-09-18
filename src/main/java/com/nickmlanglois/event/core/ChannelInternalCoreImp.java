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
  private boolean isOpen;

  ChannelInternalCoreImp(EventFactoryInternal eventFactoryInternal,
      ChannelInternal rootChannelInternal, String name) {
    this.eventFactoryInternal = eventFactoryInternal;
    this.rootChannelInternal = rootChannelInternal;
    this.name = name;
    publishedEventToPublisherMap = new HashMap<>();
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

  private boolean shouldEventBeUnublishedAndUpdatePublishedEvents(Event event,
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
    }
    publishedEventToPublisherMap.get(event).add(publisher);
  }

  private void removePublishedEvent(Event event, Publisher publisher) {
    publishedEventToPublisherMap.get(event).remove(publisher);
    if (0 == publishedEventToPublisherMap.get(event).size()) {
      publishedEventToPublisherMap.remove(event);
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
    return Collections.unmodifiableList(getChannelCache().getPublisherList());
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
    if (!shouldEventBeUnublishedAndUpdatePublishedEvents(event, publisher)) {
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
    ChannelInternalCoreImp other = (ChannelInternalCoreImp) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
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
    return Collections.unmodifiableList(getChannelCache().getEventDescriptionList());
  }

  @Override
  public ChannelInternal getRootChannelInternal() {
    return rootChannelInternal;
  }

  @Override
  public void resendAllCurrentPublishedEventsToExternalSubscriber(Subscriber subscriber) {
    for (Event publishedEvent : publishedEventToPublisherMap.keySet()) {
      subscriber.processPublishEventCallback(publishedEvent);
    }
  }
}
