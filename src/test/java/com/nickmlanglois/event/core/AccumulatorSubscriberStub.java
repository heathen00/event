package com.nickmlanglois.event.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.nickmlanglois.event.core.Event;
import com.nickmlanglois.event.core.Subscriber;

public class AccumulatorSubscriberStub extends Subscriber {
  public static AccumulatorSubscriberStub createAccumulatorSubscriber(String name) {
    return new AccumulatorSubscriberStub(name);
  }

  private final String name;
  private final List<Event> processedPublishedEventList;
  private final List<Event> processedUnpublishedEventList;

  AccumulatorSubscriberStub(String name) {
    this.name = name;
    processedPublishedEventList = new ArrayList<>();
    processedUnpublishedEventList = new ArrayList<>();
  }

  @Override
  public void processPublishEventCallback(Event event) {
    processedPublishedEventList.add(event);
  }

  @Override
  public void processUnpublishEventCallback(Event event) {
    processedUnpublishedEventList.add(event);
  }

  public List<Event> getProcessedPublishedEventList() {
    return Collections.unmodifiableList(processedPublishedEventList);
  }

  public List<Event> getProcessedUnpublishedEventList() {
    return Collections.unmodifiableList(processedUnpublishedEventList);
  }

  @Override
  public String toString() {
    return "AccumulatorSubscriberStub [hashCode()=" + hashCode() + "]";
  }

  @Override
  public String getName() {
    return name;
  }
}

