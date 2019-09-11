package com.ht.event.core;

import java.security.InvalidParameterException;

final class EventFactoryInternalParameterValidatorImp implements EventFactoryInternal {
  private final EventFactoryInternal rootEventFactoryInternal;
  private final EventFactoryInternal nextEventFactoryInternal;

  EventFactoryInternalParameterValidatorImp(EventFactoryInternal rootEventFactoryInternal,
      EventFactoryInternal nextEventFactoryInternal) {
    this.rootEventFactoryInternal = rootEventFactoryInternal;
    this.nextEventFactoryInternal = nextEventFactoryInternal;
  }

  private void ensureParameterNotNull(String parameterName, Object parameter) {
    if (null == parameter) {
      throw new NullPointerException(parameterName + " cannot equal null");
    }
  }

  private void ensureChannelDisabled(Channel eventChannel, String message) {
    if (eventChannel.isEnabled()) {
      throw new UnsupportedOperationException(message);
    }
  }

  private void ensureExpectedImplementation(String parameterName, Class<?> expectedClazz,
      Object parameter) {
    if (!expectedClazz.isInstance(parameter)) {
      throw new InvalidParameterException("unknown " + parameterName + " implementation");
    }
  }

  private void ensureExpectedNamingConvention(String parameterName, String parameter) {
    if (!parameter.matches("^[a-z0-9.]+$") || parameter.startsWith(".")
        || parameter.endsWith(".")) {
      throw new InvalidParameterException(parameterName
          + " can only contain lower case letters, numbers, and periods, and cannot start or end with a period");
    }
  }

  @Override
  public Channel createChannel(String channelName) {
    ensureParameterNotNull("channelName", channelName);
    ensureExpectedNamingConvention("channelName", channelName);
    return nextEventFactoryInternal.createChannel(channelName);
  }

  @Override
  public Event createEvent(Channel eventChannel, String eventFamily, String eventName) {
    ensureParameterNotNull("eventChannel", eventChannel);
    ensureExpectedImplementation("eventChannel", ChannelInternal.class, eventChannel);
    ensureChannelDisabled(eventChannel, "cannot create events after enabling channel");
    ensureParameterNotNull("eventFamily", eventFamily);
    ensureExpectedNamingConvention("eventFamily", eventFamily);
    ensureParameterNotNull("eventName", eventName);
    ensureExpectedNamingConvention("eventName", eventName);
    return nextEventFactoryInternal.createEvent(eventChannel, eventFamily, eventName);
  }

  @Override
  public Event createEvent(Event event, Subject subject) {
    return nextEventFactoryInternal.createEvent(event, subject);
  }

  @Override
  public Publisher createPublisher(Channel eventChannel) {
    ensureParameterNotNull("eventChannel", eventChannel);
    ensureExpectedImplementation("eventChannel", ChannelInternal.class, eventChannel);
    ensureChannelDisabled(eventChannel, "cannot create publishers after enabling channel");
    return nextEventFactoryInternal.createPublisher(eventChannel);
  }

  @Override
  public void addSubscriber(Channel eventChannel, Subscriber eventSubscriber) {
    ensureParameterNotNull("eventChannel", eventChannel);
    ensureExpectedImplementation("eventChannel", ChannelInternal.class, eventChannel);
    ensureChannelDisabled(eventChannel, "cannot add subscribers after enabling channel");
    ensureParameterNotNull("eventSubscriber", eventSubscriber);
    nextEventFactoryInternal.addSubscriber(eventChannel, eventSubscriber);
  }

  @Override
  public void enableChannel(Channel eventChannel) {
    ensureParameterNotNull("eventChannel", eventChannel);
    ensureExpectedImplementation("eventChannel", ChannelInternal.class, eventChannel);
    nextEventFactoryInternal.enableChannel(eventChannel);
  }

  @Override
  public EventFactoryInternal getRootEventFactoryInternal() {
    return rootEventFactoryInternal;
  }

  @Override
  public InstanceCache getInstanceCache() {
    return nextEventFactoryInternal.getInstanceCache();
  }

  @Override
  public Subject getNoSubject() {
    return nextEventFactoryInternal.getNoSubject();
  }
}