package com.nickmlanglois.event.core;

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

  private void ensureChannelDisabled(Channel channel, String message) {
    if (channel.isOpen()) {
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
  public Channel createChannel(String name) {
    ensureParameterNotNull("name", name);
    ensureExpectedNamingConvention("name", name);
    return nextEventFactoryInternal.createChannel(name);
  }

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    ensureChannelDisabled(channel, "cannot create event descriptions after enabling channel");
    ensureParameterNotNull("family", family);
    ensureExpectedNamingConvention("family", family);
    ensureParameterNotNull("name", name);
    ensureExpectedNamingConvention("name", name);
    return nextEventFactoryInternal.createEventDescription(channel, family, name);
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    ensureChannelDisabled(channel, "cannot create publishers after enabling channel");
    return nextEventFactoryInternal.createPublisher(channel);
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    ensureChannelDisabled(channel, "cannot add subscribers after enabling channel");
    ensureParameterNotNull("subscriber", subscriber);
    nextEventFactoryInternal.addSubscriber(channel, subscriber);
  }

  @Override
  public void openChannel(Channel channel) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    nextEventFactoryInternal.openChannel(channel);
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

  @Override
  public Event createEvent(EventDescription eventDescription, Subject subject) {
    return nextEventFactoryInternal.createEvent(eventDescription, subject);
  }
}
