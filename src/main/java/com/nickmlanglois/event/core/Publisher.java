package com.nickmlanglois.event.core;

/**
 * A Publisher publishes and unpublishes Events to a Channel. A Publisher can only register with a
 * single Channel. Multiple Publishers can register to the same Channel.
 * 
 * @author nickl
 *
 */
public interface Publisher {

  /**
   * Get the Channel this publisher was created in.
   * 
   * @return The Channel this publisher was created in
   */
  Channel getChannel();

  /**
   * Publish an Event as described in the EventDescription. The published Event will have no Subject
   * associated with it. All Subscribers in the Channel will receive the published Event. Publishing
   * the same Event multiple times without unpublishing first does nothing.
   * 
   * @param eventDescription The description of the Event to publish
   * @throws NullPointerException if eventDescription is null
   * @throws UnsupportedOperationException if the Channel is closed
   * @throws UnsupportedOperationException if eventDescription is not defined in this Channel
   */
  void publish(EventDescription eventDescription);

  /**
   * Unpublish an Event with the given EventDescription. The unpublished Event will have no Subject
   * associated with it. All Subscribers in the Channel will receive the unpublished Event.
   * Unpublishing an Event that was not initially published does nothing.
   * 
   * @param eventDescription The description of the Event to unpublish
   * @throws NullPointerException if eventDescription is null
   * @throws UnsupportedOperationException if the Channel is closed
   * @throws UnsupportedOperationException if eventDescription is not defined in this Channel
   */
  void unpublish(EventDescription eventDescription);

  /**
   * Publish an Event as described in the EventDescription and pertaining to the specific Subject.
   * All Subscribers in the Channel will receive the published Event. Publishing the same
   * EventDescription and Subject multiple times without unpublishing first does nothing.
   * 
   * @param eventDescription The description of the Event to publish. That is, what the Event is
   *        about
   * @param subject The subject of the Event. That is, who the Event is about
   * @throws NullPointerException if eventDescription is null
   * @throws NullPointerException if subject is null
   * @throws UnsupportedOperationException if the Channel is closed
   * @throws UnsupportedOperationException if eventDescription is not defined in this Channel
   */
  void publish(EventDescription eventDescription, Subject subject);

  /**
   * Unpublish an Event as described in the EventDescription and pertaining to the specific Subject.
   * All Subscribers in the Channel will receive the unpublished Event. Unpublishing an
   * EventDescription and Subject that was not initially published does nothing.
   * 
   * @param eventDescription The description of the Event to publish. That is, what the Event is
   *        about
   * @param subject The subject of the Event. That is, who the Event is about
   * @throws NullPointerException if eventDescription is null
   * @throws NullPointerException if subject is null
   * @throws UnsupportedOperationException if the Channel is closed
   * @throws UnsupportedOperationException if eventDescription is not defined in this Channel
   */
  void unpublish(EventDescription eventDescription, Subject subject);

  /**
   * Checks whether this Publisher is defined. An undefined Publisher cannot be used to publish
   * Events to a Channel. A Publisher is set to undefined if it is deleted from a Channel or if the
   * Channel that contains it is deleted.
   * 
   * @return true if this Publisher is defined
   */
  boolean isDefined();
}
