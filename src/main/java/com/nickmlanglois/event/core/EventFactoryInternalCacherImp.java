package com.nickmlanglois.event.core;

final class EventFactoryInternalCacherImp extends EventFactoryInternalBaseImp {
  @Override
  public Channel createChannel(String name) {
    ChannelInternal channelInternal = null;
    if (null == getInstanceCache().getChannelCache(name)) {
      channelInternal = (ChannelInternal) getNextEventFactoryInternal().createChannel(name);
      getInstanceCache().addChannelCache(name, channelInternal);
    } else {
      channelInternal = getInstanceCache().getChannelCache(name).getChannelInternal();
    }
    return channelInternal;
  }

  @Override
  public EventDescription createEventDescription(Channel channel, String family, String name) {
    EventDescription eventDescription =
        getChannelCache(channel).getEventDescriptionInternal(channel, family, name);
    if (null == eventDescription) {
      eventDescription =
          getNextEventFactoryInternal().createEventDescription(channel, family, name);
      getChannelCache(channel)
          .addEventDescriptionInternal((EventDescriptionInternal) eventDescription);
    }
    return eventDescription;
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    PublisherInternal newPublisher =
        (PublisherInternal) getNextEventFactoryInternal().createPublisher(channel);
    getChannelCache(channel).addPublisherInternal(newPublisher);
    return newPublisher;
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    SubscriberInternal subscriberInternal =
        getChannelCache(channel).getSubscriberInternalForExternalSubscriber(subscriber);
    if (null != subscriberInternal) {
      return;
    }
    subscriberInternal = new SubscriberInternalImp(subscriber);
    ChannelInternal subscriberInternalsCurrentChannelInternal =
        getInstanceCache().getChannelInternalForSubscriberInternal(subscriberInternal);
    if (null != subscriberInternalsCurrentChannelInternal
        && !channel.equals(subscriberInternalsCurrentChannelInternal)) {
      throw new UnsupportedOperationException("subscriber already subscribed to channel "
          + subscriberInternalsCurrentChannelInternal.getName());
    }
    getChannelCache(channel).addSubscriberInternal(subscriberInternal);
    subscriber.setChannel(channel);
  }

  @Override
  public void openChannel(Channel channel) {
    getChannelCache(channel).getChannelInternal().open();
  }

  private ChannelCache getChannelCache(Channel channel) {
    return getInstanceCache().getChannelCache(channel.getName());
  }

  @Override
  public void removeSubcriber(Subscriber subscriber) {
    SubscriberInternal subscriberInternal = getChannelCache(subscriber.getChannel())
        .getSubscriberInternalForExternalSubscriber(subscriber);
    if (null == subscriberInternal) {
      return;
    }
    getChannelCache(subscriber.getChannel()).removeSubscriberInternal(subscriberInternal);
    subscriber.setChannel(
        getHeadEventFactoryInternal().getDeletedChannelInternal(subscriber.getChannel().getName()));
  }

  @Override
  public void deletePublisher(Publisher publisher) {
    getChannelCache(publisher.getChannel()).removePublisherInternal((PublisherInternal) publisher);
    ((PublisherInternal) publisher).setChannelInternal(
        getHeadEventFactoryInternal().getDeletedChannelInternal(publisher.getChannel().getName()));
  }

  @Override
  public void deleteEventDescription(EventDescription eventDescription) {
    getChannelCache(eventDescription.getChannel())
        .removeEventDescriptionInternal((EventDescriptionInternal) eventDescription);
    ((EventDescriptionInternal) eventDescription).setChannelInternal(getHeadEventFactoryInternal()
        .getDeletedChannelInternal(eventDescription.getChannel().getName()));
  }

  @Override
  public void deleteChannel(Channel channel) {
    getInstanceCache().deleteChannelCache(channel.getName());
    ((ChannelInternal) channel).setDeleted();
  }
}
