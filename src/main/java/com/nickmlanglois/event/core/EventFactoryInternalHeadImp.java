package com.nickmlanglois.event.core;

final class EventFactoryInternalHeadImp extends EventFactoryInternalBaseImp {

  EventFactoryInternalHeadImp() {
    super();
    EventFactoryInternal parameterValidator = new EventFactoryInternalParameterValidatorImp();
    EventFactoryInternal cacher = new EventFactoryInternalCacherImp();
    EventFactoryInternal creator = new EventFactoryInternalCreatorImp();
    EventFactoryInternal tail = new EventFactoryInternalTailImp();
    setHeadEventFactoryInternal(this);
    setNextEventFactoryInternal(parameterValidator);
    parameterValidator.setHeadEventFactoryInternal(this);
    parameterValidator.setNextEventFactoryInternal(cacher);
    cacher.setHeadEventFactoryInternal(this);
    cacher.setNextEventFactoryInternal(creator);
    creator.setHeadEventFactoryInternal(this);
    creator.setNextEventFactoryInternal(tail);
    tail.setHeadEventFactoryInternal(this);
    tail.setNextEventFactoryInternal(null);
  }
}
