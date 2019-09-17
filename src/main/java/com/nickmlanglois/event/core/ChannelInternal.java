package com.nickmlanglois.event.core;

interface ChannelInternal extends Channel {
  EventFactoryInternal getEventFactoryInternal();

  void publish(Publisher publisher, EventDescription eventDescription);

  void publish(Publisher publisher, EventDescription eventDescription, Subject subject);

  void unpublish(Publisher publisher, EventDescription eventDescription);

  void unpublish(Publisher publisher, EventDescription eventDescription, Subject subject);

  void open();

  ChannelInternal getRootChannelInternal();

  void resendAllCurrentPublishedEventsToExternalSubscriber(Subscriber subscriber);
}
