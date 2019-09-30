package com.nickmlanglois.event.core;

final class EventFactoryInternalCreatorImp extends EventFactoryInternalBaseImp {
  private final Subject noSubjectSingleton;

  EventFactoryInternalCreatorImp() {
    noSubjectSingleton = new NoSubject();
  }

  @Override
  public ChannelInternal createChannelInternal(String name) {
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
  public PublisherInternal createPublisherInternal(ChannelInternal channelInternal) {
    return new PublisherInternalImp(channelInternal);
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
