package com.nickmlanglois.event.core;

final class EventFactoryInternalCacherImp extends EventFactoryInternalBaseImp {
  @Override
  public ChannelInternal createChannelInternal(String name) {
    ChannelInternal channelInternal = null;
    if (null == getInstanceCacheInternal().getChannelCacheInternal(name)) {
      channelInternal = getNextEventFactoryInternal().createChannelInternal(name);
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
  public PublisherInternal createPublisherInternal(ChannelInternal channelInternal) {
    PublisherInternal publisherInternal =
        getNextEventFactoryInternal().createPublisherInternal(channelInternal);
    getChannelCacheInternal(channelInternal).addPublisherInternal(publisherInternal);
    return publisherInternal;
  }

  @Override
  public void addSubscriber(Channel channel, Subscriber subscriber) {
    SubscriberInternal subscriberInternal =
        getChannelCacheInternal(channel).getSubscriberInternalForExternalSubscriber(subscriber);
    if (null != subscriberInternal) {
      return;
    }
    subscriberInternal = new SubscriberInternalImp(subscriber);
    ensureSubscriberNotSubscribedToAnotherChannel(channel, subscriberInternal);
    getChannelCacheInternal(channel).addSubscriberInternal(subscriberInternal);
    subscriber.setChannel(channel);
  }

  private void ensureSubscriberNotSubscribedToAnotherChannel(Channel channel,
      SubscriberInternal subscriberInternal) {
    ChannelInternal subscriberInternalsCurrentChannelInternal =
        getInstanceCacheInternal().getChannelInternalForSubscriberInternal(subscriberInternal);
    if (null != subscriberInternalsCurrentChannelInternal
        && !channel.equals(subscriberInternalsCurrentChannelInternal)) {
      throw new UnsupportedOperationException("subscriber already subscribed to channel "
          + subscriberInternalsCurrentChannelInternal.getName());
    }
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
  public void deletePublisherInternal(PublisherInternal publisherInternal) {
    getChannelCacheInternal(publisherInternal.getChannel())
        .removePublisherInternal(publisherInternal);
    publisherInternal.setChannelInternal(getHeadEventFactoryInternal()
        .getDeletedChannelInternal(publisherInternal.getChannel().getName()));
  }

  @Override
  public void deleteEventDescriptionInternal(EventDescriptionInternal eventDescriptionInternal) {
    getChannelCacheInternal(eventDescriptionInternal.getChannel())
        .removeEventDescriptionInternal(eventDescriptionInternal);
    eventDescriptionInternal.setChannelInternal(getHeadEventFactoryInternal()
        .getDeletedChannelInternal(eventDescriptionInternal.getChannel().getName()));
  }

  @Override
  public void deleteChannelInternal(ChannelInternal channelInternal) {
    getInstanceCacheInternal().deleteChannelCacheInternal(channelInternal.getName());
    channelInternal.setDeleted();
  }
}
