package com.nickmlanglois.event.core;

final class NoSubject extends Subject {
  private final String description = "the 'no subject' subject";

  @Override
  public boolean isDefined() {
    return false;
  }

  @Override
  public int compareTo(Subject o) {
    return (this == o ? 0 : -1);
  }

  @Override
  public int hashCode() {
    return description.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return (this == obj ? true : false);
  }

  @Override
  public String toString() {
    return "NoSubject [description=" + description + "]";
  }
}

