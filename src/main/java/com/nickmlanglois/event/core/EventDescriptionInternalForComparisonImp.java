package com.nickmlanglois.event.core;

final class EventDescriptionInternalForComparisonImp implements EventDescriptionInternal {
  private ChannelInternal channelInternal;
  private String family;
  private String name;
  private final EventDescriptionNaturalOrderImp eventDescriptionNaturalOrder;

  EventDescriptionInternalForComparisonImp() {
    eventDescriptionNaturalOrder = new EventDescriptionNaturalOrderImp(this);
  }

  @Override
  public Channel getChannel() {
    return channelInternal;
  }

  @Override
  public String getFamily() {
    return family;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public void setName(String name) {
    this.name = name;
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
    return "EventDescriptionForComparisonImp [getFullyQualifiedName()=" + getFullyQualifiedName()
        + "]";
  }

  @Override
  public void setChannelInternal(ChannelInternal channelInternal) {
    this.channelInternal = channelInternal;
  }
}

