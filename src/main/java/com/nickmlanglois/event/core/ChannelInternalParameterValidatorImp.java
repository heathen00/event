package com.nickmlanglois.event.core;

import java.util.List;

final class ChannelInternalParameterValidatorImp implements ChannelInternal {
  private final ChannelInternal rootChannelInternal;
  private ChannelInternal nextChannelInternal;

  ChannelInternalParameterValidatorImp(ChannelInternal rootChannelInternal,
      ChannelInternal nextChannelInternal) {
    this.rootChannelInternal = rootChannelInternal;
    this.nextChannelInternal = nextChannelInternal;
  }

  private ChannelCacheInternal getChannelCache() {
    return nextChannelInternal.getEventFactoryInternal().getInstanceCacheInternal()
        .getChannelCacheInternal(getName());
  }

  private void ensureParameterIsNotNull(String parameterName, Object parameter) {
    if (null == parameter) {
      throw new NullPointerException(parameterName + " cannot be null");
    }
  }

  private void ensureChannelIsOpen() {
    if (!isOpen()) {
      throw new UnsupportedOperationException("channel is not open");
    }
  }

  private void ensureEventDescriptionIsDefined(EventDescription eventDescription) {
    if (!eventDescription.isDefined()) {
      throw new UnsupportedOperationException("event description deleted");
    }
  }

  private void ensureEventDescriptionDefinedInChannel(EventDescription eventDescription) {
    if (!getChannelCache().getEventDescriptionInternalList().contains(eventDescription)) {
      throw new UnsupportedOperationException("eventDescription "
          + eventDescription.getFullyQualifiedName() + " is not defined in this channel");
    }
  }

  private void ensureSubjectIsDefined(Subject subject) {
    if (!subject.isDefined()) {
      throw new IllegalArgumentException("Subject.isDefined() cannot return false");
    }
  }

  private void ensureExpectedImplementation(String parameterName, Class<?> expectedClazz,
      Object parameter) {
    if (!expectedClazz.isInstance(parameter)) {
      throw new IllegalArgumentException("unknown " + parameterName + " implementation");
    }
  }

  @Override
  public String getName() {
    return nextChannelInternal.getName();
  }

  @Override
  public List<EventDescription> getEventDescriptionList() {
    return nextChannelInternal.getEventDescriptionList();
  }

  @Override
  public List<Subscriber> getSubscriberList() {
    return nextChannelInternal.getSubscriberList();
  }

  @Override
  public List<Publisher> getPublisherList() {
    return nextChannelInternal.getPublisherList();
  }

  @Override
  public boolean isOpen() {
    return nextChannelInternal.isOpen();
  }

  @Override
  public int compareTo(Channel o) {
    return nextChannelInternal.compareTo(o);
  }

  @Override
  public EventFactoryInternal getEventFactoryInternal() {
    return nextChannelInternal.getEventFactoryInternal();
  }

  @Override
  public void publish(Publisher publisher, EventDescription eventDescription) {
    ensureParameterIsNotNull("eventDescription", eventDescription);
    ensureChannelIsOpen();
    ensureExpectedImplementation("eventDescription", EventDescriptionInternal.class,
        eventDescription);
    ensureEventDescriptionIsDefined(eventDescription);
    ensureEventDescriptionDefinedInChannel(eventDescription);
    nextChannelInternal.publish(publisher, eventDescription);
  }

  @Override
  public void publish(Publisher publisher, EventDescription eventDescription, Subject subject) {
    ensureParameterIsNotNull("eventDescription", eventDescription);
    ensureParameterIsNotNull("subject", subject);
    ensureSubjectIsDefined(subject);
    ensureChannelIsOpen();
    ensureExpectedImplementation("eventDescription", EventDescriptionInternal.class,
        eventDescription);
    ensureEventDescriptionIsDefined(eventDescription);
    ensureEventDescriptionDefinedInChannel(eventDescription);
    nextChannelInternal.publish(publisher, eventDescription, subject);
  }

  @Override
  public void unpublish(Publisher publisher, EventDescription eventDescription) {
    ensureParameterIsNotNull("eventDescription", eventDescription);
    ensureChannelIsOpen();
    ensureExpectedImplementation("eventDescription", EventDescriptionInternal.class,
        eventDescription);
    ensureEventDescriptionIsDefined(eventDescription);
    ensureEventDescriptionDefinedInChannel(eventDescription);
    nextChannelInternal.unpublish(publisher, eventDescription);
  }

  @Override
  public void unpublish(Publisher publisher, EventDescription eventDescription, Subject subject) {
    ensureParameterIsNotNull("eventDescription", eventDescription);
    ensureParameterIsNotNull("subject", subject);
    ensureSubjectIsDefined(subject);
    ensureChannelIsOpen();
    ensureExpectedImplementation("eventDescription", EventDescriptionInternal.class,
        eventDescription);
    ensureEventDescriptionIsDefined(eventDescription);
    ensureEventDescriptionDefinedInChannel(eventDescription);
    nextChannelInternal.unpublish(publisher, eventDescription, subject);
  }

  @Override
  public void open() {
    nextChannelInternal.open();
  }

  @Override
  public ChannelInternal getRootChannelInternal() {
    return rootChannelInternal;
  }

  @Override
  public void resendAllCurrentPublishedEventsToExternalSubscriber(Subscriber subscriber) {
    nextChannelInternal.resendAllCurrentPublishedEventsToExternalSubscriber(subscriber);
  }

  @Override
  public boolean isDefined() {
    return nextChannelInternal.isDefined();
  }

  @Override
  public void setDeleted() {
    nextChannelInternal = getEventFactoryInternal().getDeletedChannelInternal(getName());
  }
}

