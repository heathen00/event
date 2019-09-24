package com.nickmlanglois.event.core;

import java.util.Collections;
import java.util.List;

final class DeletedChannelInternalImp implements ChannelInternal {
  private final EventFactoryInternal eventFactoryInternal;
  private final String name;

  DeletedChannelInternalImp(EventFactoryInternal eventFactoryInternal, String channelName) {
    this.eventFactoryInternal = eventFactoryInternal;
    this.name = channelName;
  }

  DeletedChannelInternalImp(EventFactoryInternal eventFactoryInternal) {
    this(eventFactoryInternal, "__DELETED__");
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<EventDescription> getEventDescriptionList() {
    return Collections.emptyList();
  }

  @Override
  public List<Subscriber> getSubscriberList() {
    return Collections.emptyList();
  }

  @Override
  public List<Publisher> getPublisherList() {
    return Collections.emptyList();
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public boolean isDefined() {
    return false;
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
  public void publish(Publisher publisher, EventDescription eventDescription) {
    defaultDeletedChannelAction();
  }

  @Override
  public void publish(Publisher publisher, EventDescription eventDescription, Subject subject) {
    defaultDeletedChannelAction();
  }

  @Override
  public void unpublish(Publisher publisher, EventDescription eventDescription) {
    defaultDeletedChannelAction();
  }

  @Override
  public void unpublish(Publisher publisher, EventDescription eventDescription, Subject subject) {
    defaultDeletedChannelAction();
  }

  @Override
  public void open() {}

  @Override
  public ChannelInternal getRootChannelInternal() {
    return this;
  }

  @Override
  public void resendAllCurrentPublishedEventsToExternalSubscriber(Subscriber subscriber) {}

  @Override
  public void setDeleted() {}

  private void defaultDeletedChannelAction() {
    throw new UnsupportedOperationException("publisher or channel deleted");
  }
}
