package com.nickmlanglois.event.core;

final class EventFactoryInternalHeadImp extends EventFactoryInternalBaseImp {
  private final InstanceCacheInternal instanceCacheInternal;

  EventFactoryInternalHeadImp() {
    super();
    instanceCacheInternal = InstanceCacheInternal.createInstanceCacheInternal();

    EventFactoryInternal parameterValidator = new EventFactoryInternalParameterValidatorImp();
    EventFactoryInternal mapper = new EventFactoryInternalPublishedToInternalImp();
    EventFactoryInternal cacher = new EventFactoryInternalCacherImp();
    EventFactoryInternal creator = new EventFactoryInternalCreatorImp();
    EventFactoryInternal tail = new EventFactoryInternalTailImp();

    setHeadEventFactoryInternal(this);
    setNextEventFactoryInternal(parameterValidator);
    parameterValidator.setHeadEventFactoryInternal(this);
    parameterValidator.setNextEventFactoryInternal(mapper);
    mapper.setHeadEventFactoryInternal(this);
    mapper.setNextEventFactoryInternal(cacher);
    cacher.setHeadEventFactoryInternal(this);
    cacher.setNextEventFactoryInternal(creator);
    creator.setHeadEventFactoryInternal(this);
    creator.setNextEventFactoryInternal(tail);
    tail.setHeadEventFactoryInternal(this);
    tail.setNextEventFactoryInternal(null);
  }

  @Override
  public InstanceCacheInternal getInstanceCacheInternal() {
    return instanceCacheInternal;
  }
}
