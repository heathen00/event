package com.nickmlanglois.event.core;

final class EventFactoryInternalPublishedToInternalImp extends EventFactoryInternalBaseImp {

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    return getNextEventFactoryInternal().createEventDescriptionInternal((ChannelInternal) channel,
        family, name);
  }

  @Override
  public void deleteEventDescription(EventDescription eventDescription) {
    getNextEventFactoryInternal()
        .deleteEventDescriptionInternal((EventDescriptionInternal) eventDescription);
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    return getNextEventFactoryInternal().createPublisherInternal((ChannelInternal) channel);
  }

  @Override
  public void deletePublisher(Publisher publisher) {
    getNextEventFactoryInternal().deletePublisherInternal((PublisherInternal) publisher);
  }
}
