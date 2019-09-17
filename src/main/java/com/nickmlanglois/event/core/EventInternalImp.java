package com.nickmlanglois.event.core;

public final class EventInternalImp extends NaturalOrderBase<Event> implements Event {
  private final EventDescription eventDescription;
  private final Subject subject;

  EventInternalImp(EventDescription eventDescription, Subject subject) {
    this.eventDescription = eventDescription;
    this.subject = subject;
  }

  EventInternalImp(EventFactoryInternal eventFactoryInternal, Event event, Subject subject) {
    this(event.getEventDescription(), subject);
  }

  @Override
  public EventDescription getEventDescription() {
    return eventDescription;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getEventDescription().hashCode() + getSubject().hashCode();
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
    if (!Event.class.isInstance(obj)) {
      return false;
    }
    Event other = (Event) obj;
    if (!getEventDescription().equals(other.getEventDescription())) {
      return false;
    }
    if (!getSubject().equals(other.getSubject())) {
      return false;
    }
    return true;
  }

  @Override
  public int compareTo(Event o) {
    int compareTo = getEventDescription().compareTo(o.getEventDescription());
    if (0 == compareTo) {
      compareTo = getSubject().compareTo(o.getSubject());
    }
    return compareTo;
  }

  @Override
  public String toString() {
    return "EventInternalImp [getEventDescription()=" + getEventDescription() + ", getSubject()="
        + getSubject() + "]";
  }

  @Override
  public Subject getSubject() {
    return subject;
  }
}
