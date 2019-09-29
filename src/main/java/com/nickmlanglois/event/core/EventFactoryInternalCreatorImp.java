package com.nickmlanglois.event.core;

final class EventFactoryInternalCreatorImp extends EventFactoryInternalBaseImp {
  private final Subject noSubjectSingleton;

  EventFactoryInternalCreatorImp() {
    noSubjectSingleton = new NoSubject();
  }

  @Override
  public Channel createChannel(String name) {
    return new ChannelInternalRootImp(getHeadEventFactoryInternal(), name);
  }

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    return new EventDescriptionInternalImp((ChannelInternal) channel, family, name);
  }

  @Override
  public Event createEvent(EventDescription eventDescription, Subject subject) {
    return new EventInternalImp(eventDescription, subject);
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    return new PublisherInternalImp((ChannelInternal) channel);
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    throw new UnsupportedOperationException("EventFactory creator does not create subscribers");
  }

  @Override
  public void openChannel(Channel channel) {
    throw new UnsupportedOperationException("EventFactory creator does not open channels");
  }

  @Override
  public InstanceCache getInstanceCache() {
    throw new UnsupportedOperationException("EventFactory creator does not cache instances");
  }

  @Override
  public Subject getNoSubject() {
    return noSubjectSingleton;
  }

  @Override
  public void removeSubcriber(Subscriber subscriber) {
    throw new UnsupportedOperationException("EventFactory creator does not remove subscribers");
  }

  @Override
  public void deletePublisher(Publisher publisher) {
    throw new UnsupportedOperationException("EventFactory creator does not delete publishers");
  }

  @Override
  public void deleteEventDescription(EventDescription eventDescription) {
    throw new UnsupportedOperationException(
        "EventFactory creator does not delete eventDescriptions");
  }

  @Override
  public void deleteChannel(Channel channel) {
    throw new UnsupportedOperationException("EventFactory creator does not delete channels");
  }

  @Override
  public ChannelInternal getDeletedChannelInternal(String channelName) {
    return new DeletedChannelInternalImp(getHeadEventFactoryInternal(), channelName);
  }
}
