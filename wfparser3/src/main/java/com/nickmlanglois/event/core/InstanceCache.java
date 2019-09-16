package com.nickmlanglois.event.core;

interface InstanceCache {
  static InstanceCache createInstanceCache() {
    return new InstanceCacheImp();
  }

  ChannelCache getChannelCache(String channelName);

  void addChannelCache(String channelName, ChannelInternal channelInternal);

  ChannelInternal getChannelInternalForSubscriber(Subscriber eventSubscriber);
}
