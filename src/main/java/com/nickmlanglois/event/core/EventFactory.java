package com.nickmlanglois.event.core;

/**
 * The EventFactory creates instances for all concepts used by the Event System. The EventFactory is
 * intended to be used during the setup phase for an Channel. All EventDescriptions, Publishers, and
 * Subscribers must be created or added in a Channel during the setup phase. The Channel's setup
 * phase is complete once the Channel is set to open.
 * 
 * @author nickl
 *
 */
public interface EventFactory {
  static EventFactory createFactory() {
    return new EventFactoryInternalRootImp();
  }

  /**
   * Request to create a Channel instance with the specified name. If a Channel instance already
   * exists with the specified name, then that Channel instance is returned. The Channel name must
   * consist of lower case letters, numbers, and ".". The Channel name cannot start nor end with
   * ".". New Channel instances are closed by default, however, if this method returns an existing
   * Channel instance it may already be open.
   * 
   * @param name The unique Channel name.
   * @throws NullPointerException if name is null.
   * @throws IllegalArgumentException if name does not follow correct naming convention.
   * @return A Channel instance with the specified name.
   */
  Channel createChannel(String name);

  /**
   * Request to create an EventDescription instance in the Channel. If an EventDescription with the
   * same family and name already exists in the Channel then that EventDescription instance is
   * returned. The EventDescription family and name must consist of lower case letters, numbers, and
   * ".". The EventDescription family and name cannot start nor end with ".".
   * 
   * @param channel The Channel that the EventDescription should be created in.
   * @param family The family of related EventDescriptions this EventDescription belongs to.
   * @param name The name of EventDescription. It is unique within the EventDescription family.
   * @throws NullPointerException if channel is null.
   * @throws NullPointerException if family is null.
   * @throws NullPointerException if name is null.
   * @throws IllegalArgumentException if channel is an unknown implementation.
   * @throws UnsupportedOperationException if the channel is open.
   * @throws IllegalArgumentException if family does not follow correct naming convention.
   * @throws IllegalArgumentException if name does not follow correct naming convention.
   * @return An EventDescription instance defined within the specified Channel with the specified
   *         family and name.
   */
  EventDescription createEventDescription(Channel channel, String family, String name);

  /**
   * Request to create a Publisher instance in the Channel.
   * 
   * @param channel The Channel to create the Publisher in.
   * @throws NullPointerException if channel is null.
   * @throws IllegalArgumentException if channel is an unknown implementation.
   * @throws UnsupportedOperationException if the channel is open.
   * 
   * @return A Publisher instance for the specified Channel.
   */
  Publisher createPublisher(Channel channel);

  /**
   * Add a Subscriber instance to the Channel. Adding the same Subscriber instance multiple times
   * does nothing.
   * 
   * @param channel The Channel the Subscriber should be added to.
   * @param subscriber The subscriber to add to the Channel.
   * @throws NullPointerException if channel is null.
   * @throws NullPointerException if subscriber is null.
   * @throws IllegalArgumentException if channel is an unknown implementation.
   * @throws UnsupportedOperationException if the channel is open.
   * @throws IllegalArgumentException if subscriber.getName() threw an exception.
   * @throws NullPointerException if subscriber.getName() is null.
   * @throws IllegalArgumentException if subscriber.getName() returns an empty String.
   */
  void addSubscriber(Channel channel, Subscriber subscriber);

  /**
   * Open the specified Channel. Opening the same Channel multiple times does nothing. Opening the
   * Channel means that the Channel setup phase is complete and now the Channel is ready to start
   * processing Events.
   * 
   * @param channel The Channel to open.
   * @throws NullPointerException if channel is null.
   * @throws IllegalArgumentException if channel is an unknown implementation.
   */
  void openChannel(Channel channel);

  void removeSubcriber(Subscriber subscriber);

  void deletePublisher(Publisher publisher);

  void deleteEventDescription(EventDescription eventDescription);
}
