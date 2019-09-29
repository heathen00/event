package com.nickmlanglois.event.core;

final class EventFactoryInternalHeadImp extends EventFactoryInternalBaseImp {

  EventFactoryInternalHeadImp() {
    super();
    EventFactoryInternal parameterValidator = new EventFactoryInternalParameterValidatorImp();
    EventFactoryInternal cacher = new EventFactoryInternalCacherImp();
    EventFactoryInternal creator = new EventFactoryInternalCreatorImp();
    setHeadEventFactoryInternal(this);
    setNextEventFactoryInternal(parameterValidator);
    parameterValidator.setHeadEventFactoryInternal(this);
    parameterValidator.setNextEventFactoryInternal(cacher);
    cacher.setHeadEventFactoryInternal(this);
    cacher.setNextEventFactoryInternal(creator);
    creator.setHeadEventFactoryInternal(this);
    creator.setNextEventFactoryInternal(null);
    // TODO when you create the Tail, just make sure that every method throws an exception.
  }
}
