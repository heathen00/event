package com.nickmlanglois.event.core;

final class EventFactoryInternalHeadImp extends EventFactoryInternalBaseImp {
  private final InstanceCache instanceCache;

  EventFactoryInternalHeadImp() {
    super();
    instanceCache = InstanceCache.createInstanceCache();

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

  @Override
  public InstanceCache getInstanceCache() {
    return instanceCache;
  }
}
