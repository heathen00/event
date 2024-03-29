package com.nickmlanglois.event.core;

interface NaturalOrder<T> extends Comparable<T> {
  public int hashCode();

  public boolean equals(Object o);
}
