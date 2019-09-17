package com.nickmlanglois.event.core;

import java.util.HashMap;
import java.util.Map;

final class InstanceCacheImp implements InstanceCache {
  private final Map<String, ChannelCache> channelNameToChannelCacheMap;

  InstanceCacheImp() {
    channelNameToChannelCacheMap = new HashMap<>();
  }

  @Override
  public ChannelCache getChannelCache(String channelName) {
    return channelNameToChannelCacheMap.get(channelName);
  }

  @Override
  public void addChannelCache(String channelName, ChannelInternal channelInternal) {
    channelNameToChannelCacheMap.put(channelName, new ChannelCacheImp(channelInternal));
  }

  @Override
  public ChannelInternal getChannelInternalForSubscriberInternal(SubscriberInternal subscriberInternal) {
    ChannelInternal subscribersChannel = null;
    for (String channelName : channelNameToChannelCacheMap.keySet()) {
      if (channelNameToChannelCacheMap.get(channelName).getSubscriberInternalList()
          .contains(subscriberInternal)) {
        subscribersChannel = channelNameToChannelCacheMap.get(channelName).getChannelInternal();
      }
    }
    return subscribersChannel;
  }
}
