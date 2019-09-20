package com.nickmlanglois.event.core;

/**
 * Events can optionally be published including a Subject that the event is about.
 * 
 * @author nickl
 *
 */
public abstract class Subject implements SubjectPublished<Subject> {

  @Override
  public abstract int hashCode();

  @Override
  public abstract boolean equals(Object obj);

  @Override
  public abstract int compareTo(Subject o);
}
