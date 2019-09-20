package com.nickmlanglois.event.core;

/**
 * Events can optionally be published including a Subject that the event is about.
 * 
 * @author nickl
 *
 */
public abstract class Subject implements SubjectPublished<Subject> {

  /**
   * This method returns true if this is an externally defined Subject that was passed in during a
   * call to Publisher.publish() or Publisher.unpublish(). If a Subscriber receives an Event with no
   * Subject, then the Event will have a reference to a "no subject" Subject. This "no subject"
   * Subject's isDefined() method returns false.
   * 
   * @return true if this is a valid, defined Subject. false if this is the "no subject" Subject.
   */
  @Override
  public boolean isDefined() {
    return true;
  }

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int compareTo(Subject o);
}
