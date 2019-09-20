package com.nickmlanglois.event.core;

public interface SubjectPublished<T> extends NaturalOrder<T> {
  boolean isDefined();
}
