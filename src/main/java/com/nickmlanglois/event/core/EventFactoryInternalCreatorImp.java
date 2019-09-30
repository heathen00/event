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
  public EventDescriptionInternal createEventDescriptionInternal(ChannelInternal channelInternal,
      String family, String name) {
    return new EventDescriptionInternalImp(channelInternal, family, name);
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
  public Subject getNoSubject() {
    return noSubjectSingleton;
  }

  @Override
  public ChannelInternal getDeletedChannelInternal(String channelName) {
    return new DeletedChannelInternalImp(getHeadEventFactoryInternal(), channelName);
  }
}
