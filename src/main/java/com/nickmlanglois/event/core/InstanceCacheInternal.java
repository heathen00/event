package com.nickmlanglois.event.core;

interface InstanceCacheInternal {
  static InstanceCacheInternal createInstanceCacheInternal() {
    return new InstanceCacheInternalImp();
  }

  ChannelCacheInternal getChannelCacheInternal(String channelName);

  void addChannelCacheInternal(String channelName, ChannelInternal channelInternal);

  ChannelInternal getChannelInternalForSubscriberInternal(SubscriberInternal subscriberInternal);

  void deleteChannelCacheInternal(String channelName);
}
