package com.nickmlanglois.event.core;

/**
 * The description for a potential Event. It is only defined within a given Channel. The
 * EventDescription is defined based on a Family and Name. The Family accumulates related
 * EventDescriptions together. I don't want to use the name Group because that implies incorrectly
 * that subscribers and not the event descriptions are being grouped. The Name is the unique name
 * for that event description within the given Channel and Family. Thus, two different
 * EventDescritions may have the same Family and Name if they belong to different Channels. And two
 * different EventDescriptions may have the same Name if they are in the same Channel BUT different
 * Families. EventDescriptions must be created before the Channel is set to open.
 * 
 * The family is an accumulation of related EventDescription instances. The idea comes from the HTTP
 * protocol where the HTTP response codes are subdivided into 5 Families as identified by the first
 * digit of the HTTP response code, so (2XX for success cases, 5XX for internal server errors, 4XX
 * for client errors, etc).
 * 
 * @author nickl
 *
 */
public interface EventDescription extends NaturalOrder<EventDescription> {

  /**
   * Get the Channel this EventDescription was created in.
   * 
   * @return The Channel this EventDescription was created in.
   * 
   */
  Channel getChannel();

  /**
   * Get the Family of related EventDescription's this EventDescription is a member of.
   * 
   * @return The Family of related EventDescription's this EventDescription is a member of.
   */
  String getFamily();

  /**
   * Get the name of this EventDescription. The name is unique to this EventDescription within its
   * Family membership.
   * 
   * @return The name of this EventDescription.
   */
  String getName();

  /**
   * Get the fully qualified name of this EventDescription. It is defined based on the Channel name,
   * EventDescription family name, and EventDescription name.
   * 
   * @return
   */
  String getFullyQualifiedName();

  boolean isDefined();
}
