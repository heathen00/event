package com.nickmlanglois.event.core;

import org.apache.commons.lang3.StringUtils;

final class EventFactoryInternalParameterValidatorImp extends EventFactoryInternalBaseImp {
  private void ensureParameterNotNull(String parameterName, Object parameter) {
    if (null == parameter) {
      throw new NullPointerException(parameterName + " cannot equal null");
    }
  }

  private void ensureChannelIsClosed(Channel channel) {
    if (channel.isOpen()) {
      throw new UnsupportedOperationException("operation not permitted while channel is open");
    }
  }

  private void ensureExpectedImplementation(String parameterName, Class<?> expectedClazz,
      Object parameter) {
    if (!expectedClazz.isInstance(parameter)) {
      throw new IllegalArgumentException("unknown " + parameterName + " implementation");
    }
  }

  private void ensureExpectedNamingConvention(String parameterName, String parameter) {
    if (!parameter.matches("^[a-z0-9.]+$") || parameter.startsWith(".")
        || parameter.endsWith(".")) {
      throw new IllegalArgumentException(parameterName
          + " can only contain lower case letters, numbers, and periods, and cannot start or end with a period");
    }
  }

  private void ensureSubscriberGetNameValid(Subscriber subscriber) {
    String name = null;
    try {
      name = subscriber.getName();
    } catch (Exception e) {
      throw new IllegalArgumentException("subscriber.getName() threw an exception", e);
    }
    if (null == name) {
      throw new NullPointerException("subscriber.getName() cannot return null");
    }
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("subscriber.getName() cannot return an empty String");
    }
  }

  private void ensureChannelIsDefined(Channel channel) {
    if (!channel.isDefined()) {
      throw new IllegalArgumentException("operation not permitted for deleted channel");
    }
  }

  private void ensureChannelBelongsToFactory(Channel channel) {
    if (null == getInstanceCache().getChannelCache(channel.getName())) {
      throw new UnsupportedOperationException(
          "channel " + channel.getName() + " does not exist in this factory");
    }
  }

  @Override
  public Channel createChannel(String name) {
    ensureParameterNotNull("name", name);
    ensureExpectedNamingConvention("name", name);
    return getNextEventFactoryInternal().createChannel(name);
  }

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    ensureChannelIsDefined(channel);
    ensureChannelIsClosed(channel);
    ensureChannelBelongsToFactory(channel);
    ensureParameterNotNull("family", family);
    ensureExpectedNamingConvention("family", family);
    ensureParameterNotNull("name", name);
    ensureExpectedNamingConvention("name", name);
    return getNextEventFactoryInternal().createEventDescription(channel, family, name);
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    ensureChannelIsDefined(channel);
    ensureChannelIsClosed(channel);
    ensureChannelBelongsToFactory(channel);
    return getNextEventFactoryInternal().createPublisher(channel);
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    ensureChannelIsDefined(channel);
    ensureChannelIsClosed(channel);
    ensureChannelBelongsToFactory(channel);
    ensureParameterNotNull("subscriber", subscriber);
    ensureSubscriberGetNameValid(subscriber);
    getNextEventFactoryInternal().addSubscriber(channel, subscriber);
  }

  @Override
  public void openChannel(Channel channel) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    if (!channel.isDefined()) {
      throw new IllegalArgumentException("cannot open a deleted channel");
    }
    ensureChannelBelongsToFactory(channel);
    getNextEventFactoryInternal().openChannel(channel);
  }

  @Override
  public void removeSubcriber(Subscriber subscriber) {
    ensureParameterNotNull("subscriber", subscriber);
    if (null == subscriber.getChannel()) {
      return;
    }
    if (!subscriber.getChannel().isDefined()) {
      return;
    }
    ensureChannelIsClosed(subscriber.getChannel());
    getNextEventFactoryInternal().removeSubcriber(subscriber);
  }

  @Override
  public void deletePublisher(Publisher publisher) {
    ensureParameterNotNull("publisher", publisher);
    ensureExpectedImplementation("publisher", PublisherInternal.class, publisher);
    if (!publisher.getChannel().isDefined()) {
      return;
    }
    ensureChannelIsClosed(publisher.getChannel());
    getNextEventFactoryInternal().deletePublisher(publisher);
  }

  @Override
  public void deleteEventDescription(EventDescription eventDescription) {
    ensureParameterNotNull("eventDescription", eventDescription);
    ensureExpectedImplementation("eventDescription", EventDescriptionInternal.class,
        eventDescription);
    if (!eventDescription.getChannel().isDefined()) {
      return;
    }
    ensureChannelIsClosed(eventDescription.getChannel());
    getNextEventFactoryInternal().deleteEventDescription(eventDescription);
  }

  @Override
  public void deleteChannel(Channel channel) {
    ensureParameterNotNull("channel", channel);
    ensureExpectedImplementation("channel", ChannelInternal.class, channel);
    if (!channel.isDefined()) {
      return;
    }
    ensureChannelIsClosed(channel);
    getNextEventFactoryInternal().deleteChannel(channel);
  }
}
