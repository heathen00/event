package com.nickmlanglois.event.core;

/**
 * Confusing name. What it means is this is the published interface, i.e. the attributes that are
 * exposed to the API clients, for the Subscriber type. So, "published versus internal API" instead
 * of "publisher versus subscriber of events". Again, confusing name.
 * 
 * @param <T>
 */
interface SubscriberPublished<T> {
  void processPublishEventCallback(Event event);

  void processUnpublishEventCallback(Event event);

  String getName();

  T getExternalSubscriber();

  void resendAllCurrentPublishedEvents();

  Channel getChannel();
}
