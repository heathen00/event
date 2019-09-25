package com.nickmlanglois.event.core;

/**
 * An Event is an "occurrence" of interest to like minded Publishers and Subscribers. It is only
 * defined within a given Channel. The Event is defined based on an EventDescription. Optionally, an
 * Event may also have a Subject that identifies that a given Event occurred with respect to that
 * Subject. Thus if the same Event is sent on the Channel twice, but the Event specifies two
 * distinct Subjects then the Events are treated as separate Events within that channel.
 * 
 * @author nickl
 *
 */
public interface Event extends NaturalOrder<Event> {

  /**
   * Get the description for this event.
   * 
   * @return The EventDescription
   */
  EventDescription getEventDescription();

  /**
   * Get the optional Subject. If no Subject is associated with this event, then this method returns
   * the NoSubject subject whose isDefined() method returns false.
   * 
   * @return The Event's Subject
   */
  Subject getSubject();
}
