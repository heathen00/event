package com.nickmlanglois.event.core;

import java.util.List;

/**
 * A communications Channel of interest between like minded Publishers and Subscribers. It is
 * identified by a name (String) and can have any number of Publishers and Subscribers subscribed.
 * It can also have any number of Events defined for it. Events from one Channel cannot be sent to
 * another Channel. Sending the same Event over the Channel does nothing.
 * 
 * @author nickl
 *
 */
public interface Channel extends NaturalOrder<Channel> {

  /**
   * The Channel's unique name.
   * 
   * @return The Channel's name.
   */
  String getName();

  /**
   * Get the list of EventDescriptions created in this Channel.
   * 
   * @return The non-modifiable list of EventDescriptions in this Channel.
   */
  List<EventDescription> getEventDescriptionList();

  /**
   * Get the list of Subscribers added to this Channel.
   * 
   * @return The non-modifiable list of Subscribers in this Channel.
   */
  List<Subscriber> getSubscriberList();

  /**
   * Get the list of Publishers created in this Channel.
   * 
   * @return The non-modifiable list of Subscribers in this Channel.
   */
  List<Publisher> getPublisherList();

  /**
   * Checks if this Channel is open and so ready to process events.
   * 
   * @return Whether the Channel is open.
   */
  boolean isOpen();
}
