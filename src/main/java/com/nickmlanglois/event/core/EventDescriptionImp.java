package com.nickmlanglois.event.core;

final class EventDescriptionImp implements EventDescription {
  private final Channel channel;
  private final String family;
  private final String name;
  private final EventDescriptionNaturalOrderImp eventDescriptionNaturalOrder;

  EventDescriptionImp(Channel channel, String family, String name) {
    this.channel = channel;
    this.family = family;
    this.name = name;
    eventDescriptionNaturalOrder = new EventDescriptionNaturalOrderImp(this);
  }

  @Override
  public Channel getChannel() {
    return channel;
  }

  @Override
  public String getFamily() {
    return family;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getFullyQualifiedName() {
    return eventDescriptionNaturalOrder.getFullyQualifiedName();
  }

  @Override
  public int hashCode() {
    return eventDescriptionNaturalOrder.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return eventDescriptionNaturalOrder.equals(obj);
  }

  @Override
  public int compareTo(EventDescription o) {
    return eventDescriptionNaturalOrder.compareTo(o);
  }

  @Override
  public String toString() {
    return "EventDescriptionImp [getFullyQualifiedName()=" + getFullyQualifiedName() + "]";
  }
}

