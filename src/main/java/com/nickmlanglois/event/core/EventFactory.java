package com.nickmlanglois.event.core;

public interface EventFactory {
  static EventFactory createFactory() {
    return new EventFactoryInternalRootImp();
  }

  Channel createChannel(String name);

  EventDescription createEventDescription(Channel channel, String family, String name);

  Publisher createPublisher(Channel channel);

  void addSubscriber(Channel channel, Subscriber subscriber);

  void openChannel(Channel channel);
}
