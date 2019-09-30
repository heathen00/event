package com.nickmlanglois.event.core;

import java.util.HashMap;
import java.util.Map;

final class InstanceCacheInternalImp implements InstanceCacheInternal {
  private final Map<String, ChannelCacheInternal> channelNameToChannelCacheInternalMap;

  InstanceCacheInternalImp() {
    channelNameToChannelCacheInternalMap = new HashMap<>();
  }

  @Override
  public ChannelCacheInternal getChannelCacheInternal(String channelName) {
    return channelNameToChannelCacheInternalMap.get(channelName);
  }

  @Override
  public void addChannelCacheInternal(String channelName, ChannelInternal channelInternal) {
    channelNameToChannelCacheInternalMap.put(channelName, new ChannelCacheInternalImp(channelInternal));
  }

  @Override
  public ChannelInternal getChannelInternalForSubscriberInternal(
      SubscriberInternal subscriberInternal) {
    ChannelInternal subscribersChannel = null;
    for (String channelName : channelNameToChannelCacheInternalMap.keySet()) {
      if (channelNameToChannelCacheInternalMap.get(channelName).getSubscriberInternalList()
          .contains(subscriberInternal)) {
        subscribersChannel = channelNameToChannelCacheInternalMap.get(channelName).getChannelInternal();
      }
    }
    return subscribersChannel;
  }

  @Override
  public void deleteChannelCacheInternal(String channelName) {
    channelNameToChannelCacheInternalMap.remove(channelName);
  }
}
