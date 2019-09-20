package com.nickmlanglois.event.core;

public class UndefinedSubjectStub extends SubjectStub {
  public UndefinedSubjectStub(String subjectName) {
    super(subjectName);
  }

  @Override
  public boolean isDefined() {
    return false;
  }
}
