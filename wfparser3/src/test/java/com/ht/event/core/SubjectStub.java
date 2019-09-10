package com.ht.event.core;

public final class SubjectStub extends Subject {
  private final String subjectName;

  public SubjectStub(String subjectName) {
    this.subjectName = subjectName;
  }

  @Override
  public int compareTo(Subject o) {
    SubjectStub other = (SubjectStub) o;
    return getSubjectName().compareTo(other.getSubjectName());
  }

  @Override
  public int hashCode() {
    return subjectName.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!SubjectStub.class.isInstance(obj)) {
      return false;
    }
    SubjectStub other = (SubjectStub) obj;
    return getSubjectName().equals(other.getSubjectName());
  }

  String getSubjectName() {
    return subjectName;
  }

  @Override
  public boolean isDefined() {
    return true;
  }

  @Override
  public String toString() {
    return "SubjectStub [subjectName=" + subjectName + ", getSubjectName()=" + getSubjectName()
        + ", isDefined()=" + isDefined() + "]";
  }
}
