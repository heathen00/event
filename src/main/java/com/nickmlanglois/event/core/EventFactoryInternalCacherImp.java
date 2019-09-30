package com.nickmlanglois.event.core;

final class EventFactoryInternalCacherImp extends EventFactoryInternalBaseImp {
  @Override
  public Channel createChannel(String name) {
    ChannelInternal channelInternal = null;
    if (null == getInstanceCacheInternal().getChannelCacheInternal(name)) {
      channelInternal = (ChannelInternal) getNextEventFactoryInternal().createChannel(name);
      getInstanceCacheInternal().addChannelCacheInternal(name, channelInternal);
    } else {
      channelInternal =
          getInstanceCacheInternal().getChannelCacheInternal(name).getChannelInternal();
    }
    return channelInternal;
  }

  @Override
  public EventDescriptionInternal createEventDescriptionInternal(ChannelInternal channelInternal,
      String family, String name) {
    EventDescriptionInternal eventDescriptionInternal = getChannelCacheInternal(channelInternal)
        .getEventDescriptionInternal(channelInternal, family, name);
    if (null == eventDescriptionInternal) {
      eventDescriptionInternal = getNextEventFactoryInternal()
          .createEventDescriptionInternal(channelInternal, family, name);
      getChannelCacheInternal(channelInternal)
          .addEventDescriptionInternal(eventDescriptionInternal);
    }
    return eventDescriptionInternal;
  }

  @Override
  public Publisher createPublisher(Channel channel) {
    PublisherInternal newPublisher =
        (PublisherInternal) getNextEventFactoryInternal().createPublisher(channel);
    getChannelCacheInternal(channel).addPublisherInternal(newPublisher);
    return newPublisher;
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    SubscriberInternal subscriberInternal =
        getChannelCacheInternal(channel).getSubscriberInternalForExternalSubscriber(subscriber);
    if (null != subscriberInternal) {
      return;
    }
    subscriberInternal = new SubscriberInternalImp(subscriber);
    ChannelInternal subscriberInternalsCurrentChannelInternal =
        getInstanceCacheInternal().getChannelInternalForSubscriberInternal(subscriberInternal);
    if (null != subscriberInternalsCurrentChannelInternal
        && !channel.equals(subscriberInternalsCurrentChannelInternal)) {
      throw new UnsupportedOperationException("subscriber already subscribed to channel "
          + subscriberInternalsCurrentChannelInternal.getName());
    }
    getChannelCacheInternal(channel).addSubscriberInternal(subscriberInternal);
    subscriber.setChannel(channel);
  }

  @Override
  public void openChannel(Channel channel) {
    getChannelCacheInternal(channel).getChannelInternal().open();
  }

  private ChannelCacheInternal getChannelCacheInternal(Channel channel) {
    return getInstanceCacheInternal().getChannelCacheInternal(channel.getName());
  }

  @Override
  public void removeSubcriber(Subscriber subscriber) {
    SubscriberInternal subscriberInternal = getChannelCacheInternal(subscriber.getChannel())
        .getSubscriberInternalForExternalSubscriber(subscriber);
    if (null == subscriberInternal) {
      return;
    }
    getChannelCacheInternal(subscriber.getChannel()).removeSubscriberInternal(subscriberInternal);
    subscriber.setChannel(
        getHeadEventFactoryInternal().getDeletedChannelInternal(subscriber.getChannel().getName()));
  }

  @Override
  public void deletePublisher(Publisher publisher) {
    getChannelCacheInternal(publisher.getChannel())
        .removePublisherInternal((PublisherInternal) publisher);
    ((PublisherInternal) publisher).setChannelInternal(
        getHeadEventFactoryInternal().getDeletedChannelInternal(publisher.getChannel().getName()));
  }

  @Override
  public void deleteEventDescriptionInternal(EventDescriptionInternal eventDescriptionInternal) {
    getChannelCacheInternal(eventDescriptionInternal.getChannel())
        .removeEventDescriptionInternal(eventDescriptionInternal);
    eventDescriptionInternal.setChannelInternal(getHeadEventFactoryInternal()
        .getDeletedChannelInternal(eventDescriptionInternal.getChannel().getName()));
  }

  @Override
  public void deleteChannel(Channel channel) {
    getInstanceCacheInternal().deleteChannelCacheInternal(channel.getName());
    ((ChannelInternal) channel).setDeleted();
  }
}
