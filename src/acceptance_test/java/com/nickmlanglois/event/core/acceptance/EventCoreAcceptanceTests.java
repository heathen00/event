package com.nickmlanglois.event.core.acceptance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.nickmlanglois.event.core.AccumulatorSubscriberStub;
import com.nickmlanglois.event.core.AssertEventCore;
import com.nickmlanglois.event.core.AssertNaturalOrder;
import com.nickmlanglois.event.core.AssertNaturalOrder.Relation;
import com.nickmlanglois.event.core.Channel;
import com.nickmlanglois.event.core.Event;
import com.nickmlanglois.event.core.EventDescription;
import com.nickmlanglois.event.core.EventFactory;
import com.nickmlanglois.event.core.Publisher;
import com.nickmlanglois.event.core.Subject;
import com.nickmlanglois.event.core.SubjectStub;
import com.nickmlanglois.event.core.Subscriber;

public class EventCoreAcceptanceTests {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private AssertEventCore assertEventCore;
  private AssertNaturalOrder assertNaturalOrder;
  private EventFactory eventFactory;
  private Channel defaultTestChannel;
  private EventDescription defaultTestEventDescription;
  private Subject defaultTestSubject;
  private Publisher defaultTestPublisher;
  private AccumulatorSubscriberStub accumulatorSubscriberStub;


  private Channel createUnsupportedExternalChannelImplementation() {
    return new Channel() {

      @Override
      public boolean isOpen() {
        throw new UnsupportedOperationException("method not supported by stub");
      }

      @Override
      public List<Subscriber> getSubscriberList() {
        throw new UnsupportedOperationException("method not supported by stub");
      }

      @Override
      public List<Publisher> getPublisherList() {
        throw new UnsupportedOperationException("method not supported by stub");
      }

      @Override
      public String getName() {
        throw new UnsupportedOperationException("method not supported by stub");
      }

      @Override
      public int compareTo(Channel o) {
        throw new UnsupportedOperationException("method not supported by stub");
      }

      @Override
      public List<EventDescription> getEventDescriptionList() {
        throw new UnsupportedOperationException("method not supported by stub");
      }
    };
  }

  private Subject createSubjectStub(String subjectName) {
    return new SubjectStub(subjectName);
  }

  @Before
  public void setup() {
    assertEventCore = AssertEventCore.createAssertEventCore();
    assertNaturalOrder = AssertNaturalOrder.createAssertNaturalOrder();
    eventFactory = EventFactory.createFactory();
    defaultTestChannel = eventFactory.createChannel("default.test.channel");
    defaultTestEventDescription = eventFactory.createEventDescription(defaultTestChannel,
        "default.test.family", "default.test.name");
    defaultTestSubject = createSubjectStub("default.test.subject");
    defaultTestPublisher = eventFactory.createPublisher(defaultTestChannel);
    accumulatorSubscriberStub =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("default.test.subscriber");
  }

  @Test
  public void EventCore_createTestingAssets_testingAssetsCreated() {
    assertNotNull(assertEventCore);
    assertNotNull(assertNaturalOrder);
    assertNotNull(eventFactory);
    assertNotNull(defaultTestChannel);
    assertNotNull(defaultTestEventDescription);
    assertNotNull(defaultTestSubject);
    assertNotNull(defaultTestPublisher);
    assertNotNull(accumulatorSubscriberStub);
  }

  @Test
  public void EventCore_createChannelWithValidChannelName_channelCreated() {
    final String expectedChannelName = "test.channel";
    final boolean expectedIsOpen = false;
    final List<EventDescription> expectedEventDescriptionsList = Collections.emptyList();
    final List<Publisher> expectedPublishersList = Collections.emptyList();
    final List<Subscriber> expectedSubscribersList = Collections.emptyList();

    Channel channel = eventFactory.createChannel(expectedChannelName);

    assertEventCore.assertExpectedChannel(expectedChannelName, expectedIsOpen,
        expectedEventDescriptionsList, expectedPublishersList, expectedSubscribersList, channel);
  }

  @Test
  public void EventCore_createEventDescriptionWithValidChannelFamilyAndNameBeforeChannelIsOpen_eventDescriptionCreated() {
    final String expectedChannelName = "test.channel";
    final boolean expectedIsOpen = false;
    final List<Publisher> expectedPublishersList = Collections.emptyList();
    final List<Subscriber> expectedSubscribersList = Collections.emptyList();
    final Channel expectedChannel = eventFactory.createChannel(expectedChannelName);
    final String expectedfamily = "test.family";
    final String expectedEventName = "test.name";

    EventDescription event =
        eventFactory.createEventDescription(expectedChannel, expectedfamily, expectedEventName);

    assertEventCore.assertExpectedEventDescription(expectedChannel, expectedfamily,
        expectedEventName, event);
    assertEventCore.assertExpectedChannel(expectedChannelName, expectedIsOpen, Arrays.asList(event),
        expectedPublishersList, expectedSubscribersList, expectedChannel);
  }

  @Test
  public void EventCore_createPublisherWithValidChannelBeforeChannelIsOpen_publisherCreated() {
    final String expectedChannelName = "test.channel";
    final boolean expectedIsOpen = false;
    final List<EventDescription> expectedEventDescriptionsList = Collections.emptyList();
    final List<Subscriber> expectedSubscribersList = Collections.emptyList();
    final Channel expectedChannel = eventFactory.createChannel(expectedChannelName);

    Publisher publisher = eventFactory.createPublisher(expectedChannel);

    assertEventCore.assertExpectedPublisher(expectedChannel, publisher);
    assertEventCore.assertExpectedChannel(expectedChannelName, expectedIsOpen,
        expectedEventDescriptionsList, Arrays.asList(publisher), expectedSubscribersList,
        expectedChannel);
  }

  @Test
  public void EventCore_addSubscriberToChannelBeforeChannelIsOpen_subscriberSuccessfullyRegistered() {
    final String expectedChannelName = "test.channel";
    final boolean expectedIsOpen = false;
    final List<EventDescription> expectedEventDescriptionsList = Collections.emptyList();
    final List<Publisher> expectedPublishersList = Collections.emptyList();
    final Channel expectedChannel = eventFactory.createChannel(expectedChannelName);
    final Subscriber subscriber = accumulatorSubscriberStub;

    eventFactory.addSubscriber(expectedChannel, subscriber);

    assertEventCore.assertExpectedChannel(expectedChannelName, expectedIsOpen,
        expectedEventDescriptionsList, expectedPublishersList, Arrays.asList(subscriber),
        subscriber.getChannel());
    assertEquals(expectedChannel, subscriber.getChannel());
  }

  @Test
  public void EventCore_publishValidEvent_subscriberReceivesEventPublish() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription);

    assertEquals(
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0).getEventDescription(),
        defaultTestEventDescription);
  }

  @Test
  public void EventCore_unpublishValidEvent_subscriberReceivesEventUnPublish() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription);
    defaultTestPublisher.unpublish(defaultTestEventDescription);

    assertEquals(
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0).getEventDescription(),
        defaultTestEventDescription);
    assertEquals(
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0).getEventDescription(),
        defaultTestEventDescription);
  }

  @Test
  public void EventCore_createChannelWithNullChannelName_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("name cannot equal null");

    eventFactory.createChannel(null);
  }

  @Test
  public void EventCore_createChannelWithEmptyChannelName_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage(
        "name can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createChannel("\n");
  }

  @Test
  public void EventCore_createChannelWithInvalidCharactersInChannelName_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage(
        "name can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createChannel("test.#@#@$channel");
  }

  @Test
  public void EventCore_createEventDescriptionWithNullChannelParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("channel cannot equal null");

    eventFactory.createEventDescription(null, "test.family", "test.name");
  }

  @Test
  public void EventCore_createEventDescriptionWithUnknownExternalChannelImplementation_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage("unknown channel implementation");

    eventFactory.createEventDescription(createUnsupportedExternalChannelImplementation(),
        "test.family", "test.name");
  }

  @Test
  public void EventCore_createEventDescriptionWithNullFamilyParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("family cannot equal null");

    eventFactory.createEventDescription(defaultTestChannel, null, "test.name");
  }

  @Test
  public void EventCore_createEventDescriptionWithEmptyFamilyParameter_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage(
        "family can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createEventDescription(defaultTestChannel, "", "test.name");
  }

  @Test
  public void EventCore_createEventDescriptionWithInvalidCharactersInFamilyParameter_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage(
        "family can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createEventDescription(defaultTestChannel, ".not.valid", "test.name");
  }

  @Test
  public void EventCore_createEventDescriptionWithNullNameParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("name cannot equal null");

    eventFactory.createEventDescription(defaultTestChannel, "test.family", null);
  }

  @Test
  public void EventCore_createEventDescriptionWithEmptyNameParameter_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage(
        "name can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createEventDescription(defaultTestChannel, "test.family", " \n \t");
  }

  @Test
  public void EventCore_createEventDescriptionWithInvalidCharactersInNameParameter_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage(
        "name can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createEventDescription(defaultTestChannel, "test.family", "invalid.name.");
  }

  @Test
  public void EventCore_createPublisherWithNullChannelParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("channel cannot equal null");

    eventFactory.createPublisher(null);
  }

  @Test
  public void EventCore_createPublisherWithUnknownExternalChannelImplementation_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage("unknown channel implementation");

    eventFactory.createPublisher(createUnsupportedExternalChannelImplementation());
  }

  @Test
  public void EventCore_addSubscriberWithNullChannelParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("channel cannot equal null");

    eventFactory.addSubscriber(null, accumulatorSubscriberStub);
  }

  @Test
  public void EventCore_addSubscriberWithUnknownExternalChannelImplementation_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage("unknown channel implementation");

    eventFactory.addSubscriber(createUnsupportedExternalChannelImplementation(),
        accumulatorSubscriberStub);
  }

  @Test
  public void EventCore_addSubscriberWithNullSubscriberParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("subscriber cannot equal null");

    eventFactory.addSubscriber(defaultTestChannel, null);
  }

  @Test
  public void EventCore_openChannelWithNullChannelParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("channel cannot equal null");

    eventFactory.openChannel(null);
  }

  @Test
  public void EventCore_openChannelWithUnknownExternalChannelImplementation_invalidParameterExceptionIsThrown() {
    thrown.expect(InvalidParameterException.class);
    thrown.expectMessage("unknown channel implementation");

    eventFactory.openChannel(createUnsupportedExternalChannelImplementation());
  }

  @Test
  public void EventCore_publishEventOnChannelBeforeEnablingChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("channel is not open");

    assertFalse(defaultTestChannel.isOpen());
    defaultTestPublisher.publish(defaultTestEventDescription);
  }

  @Test
  public void EventCore_unpublishEventOnChannelBeforeEnablingChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("channel is not open");

    assertFalse(defaultTestChannel.isOpen());
    defaultTestPublisher.unpublish(defaultTestEventDescription);
  }

  @Test
  public void EventCore_createEventForChannelAfterEnablingChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("cannot create event descriptions after enabling channel");
    eventFactory.openChannel(defaultTestChannel);

    eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name");
  }

  @Test
  public void EventCore_createPublisherForChannelAfterEnablingChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("cannot create publishers after enabling channel");
    eventFactory.openChannel(defaultTestChannel);

    eventFactory.createPublisher(defaultTestChannel);
  }

  @Test
  public void EventCore_addSubscriberForChannelAfterEnablingChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("cannot add subscribers after enabling channel");
    eventFactory.openChannel(defaultTestChannel);

    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
  }

  @Test
  public void EventCore_createSameEventMultipleTimes_eventOnlyCreatedOnce() {
    final String expectedChannelName = "test.channel";
    final boolean expectedIsChannelOpen = false;
    final List<Publisher> expectedPublishersList = Collections.emptyList();
    final List<Subscriber> expectedSubscribers = Collections.emptyList();
    final String expectedfamily = "test.family";
    final String expectedEventName = "test.name";
    Channel expectedchannel = eventFactory.createChannel(expectedChannelName);

    EventDescription firstEventDescription =
        eventFactory.createEventDescription(expectedchannel, expectedfamily, expectedEventName);
    EventDescription secondEventDescription =
        eventFactory.createEventDescription(expectedchannel, expectedfamily, expectedEventName);

    assertEventCore.assertExpectedEventDescription(expectedchannel, expectedfamily,
        expectedEventName, firstEventDescription);
    assertEventCore.assertExpectedEventDescription(expectedchannel, expectedfamily,
        expectedEventName, secondEventDescription);
    assertEventCore.assertExpectedChannel(expectedChannelName, expectedIsChannelOpen,
        Arrays.asList(firstEventDescription), expectedPublishersList, expectedSubscribers,
        expectedchannel);
    assertEquals(firstEventDescription, secondEventDescription);
    assertTrue(firstEventDescription == secondEventDescription);
  }

  @Test
  public void EventCore_addSameSubscriberToChannelMultipleTimes_subscriberOnlyAddedOnce() {
    final String expectedChannelName = "test.channel";
    final boolean expectedIsOpen = false;
    final List<EventDescription> expectedEventsList = Collections.emptyList();
    final List<Publisher> expectedPublishersList = Collections.emptyList();
    Channel expectedChannel = eventFactory.createChannel(expectedChannelName);
    Subscriber subscriber = accumulatorSubscriberStub;

    eventFactory.addSubscriber(expectedChannel, subscriber);
    eventFactory.addSubscriber(expectedChannel, subscriber);

    assertEventCore.assertExpectedChannel(expectedChannelName, expectedIsOpen, expectedEventsList,
        expectedPublishersList, Arrays.asList(subscriber), subscriber.getChannel());
  }

  @Test
  public void EventCore_addSameSubscriberToMultipleChannels_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("subscriber already subscribed to channel ");
    Channel firstChannel = eventFactory.createChannel("first.channel");
    Channel secondChannel = eventFactory.createChannel("second.channel");

    eventFactory.addSubscriber(firstChannel, accumulatorSubscriberStub);
    eventFactory.addSubscriber(secondChannel, accumulatorSubscriberStub);
  }

  @Test
  public void EventCore_publishSameValidEventTwice_subscriberOnlyNotifiedOfEventOnce() {
    final int expectedSubscriberProcessedPublishedEventsSize = 1;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription);
    defaultTestPublisher.publish(defaultTestEventDescription);

    assertEquals(expectedSubscriberProcessedPublishedEventsSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEventDescription(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0).getEventDescription());
  }

  @Test
  public void EventCore_unpublishSameValidEventTwice_subscriberOnlyNotifiedOfUnbpublishOnce() {
    final int expectedSubscriberProcessedUnpublishedEventsSize = 1;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription);

    defaultTestPublisher.unpublish(defaultTestEventDescription);
    defaultTestPublisher.unpublish(defaultTestEventDescription);
    assertEquals(expectedSubscriberProcessedUnpublishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEventDescription(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0).getEventDescription());
  }

  @Test
  public void EventCore_publishEventFromOneChannelToAnotherChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(" is not defined in this channel");
    Channel channelOne = eventFactory.createChannel("test.channel.one");
    Channel channelTwo = eventFactory.createChannel("test.channel.two");
    EventDescription eventDescriptionForChannelOne =
        eventFactory.createEventDescription(channelOne, "test.family", "test.name");
    eventFactory.createEventDescription(channelTwo, "test.family", "test.name");
    Publisher publisherForChannelTwo = eventFactory.createPublisher(channelTwo);
    eventFactory.openChannel(channelTwo);

    publisherForChannelTwo.publish(eventDescriptionForChannelOne);
  }

  @Test
  public void EventCore_unpublishEventFromOneChannelInAnotherChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(" is not defined in this channel");
    Channel channelOne = eventFactory.createChannel("test.channel.one");
    Channel channelTwo = eventFactory.createChannel("test.channel.two");
    EventDescription eventDescriptionForChannelOne =
        eventFactory.createEventDescription(channelOne, "test.family", "test.name");
    eventFactory.createEventDescription(channelTwo, "test.family", "test.name");
    Publisher publisherForChannelTwo = eventFactory.createPublisher(channelTwo);
    eventFactory.openChannel(channelTwo);

    publisherForChannelTwo.unpublish(eventDescriptionForChannelOne);
  }

  @Test
  public void EventCore_unpublishEventThatWasNeverPublished_subscribersReceiveNoNotification() {
    final int expectedSubscriberProcessedUnpublishedEventsSize = 0;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.unpublish(defaultTestEventDescription);

    assertEquals(expectedSubscriberProcessedUnpublishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
  }

  @Test
  public void EventCore_publishNullEventDescription_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("eventDescription cannot be null");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(null);
  }

  @Test
  public void EventCore_unpublishNullEventDescription_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("eventDescription cannot be null");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.unpublish(null);
  }

  @Test
  public void EventCore_publishEventDescriptionWithNullSubject_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("subject cannot be null");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription, (Subject) null);
  }

  @Test
  public void EventCore_unpublishEventDescriptionWithNullSubject_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("subject cannot be null");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.unpublish(defaultTestEventDescription, (Subject) null);
  }

  @Test
  public void EventCore_publishNullEventDescriptionWithValidSubject_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("eventDescription cannot be null");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(null, defaultTestSubject);
  }

  @Test
  public void EventCore_unpublishNullEventDescriptionWithValidSubject_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("eventDescription cannot be null");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.unpublish(null, defaultTestSubject);
  }

  @Test
  public void EventCore_publishEventDescriptionWithSubjectWhenChannelNotOpen_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("channel is not open");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);

    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);
  }

  @Test
  public void EventCore_unpublishEventDescriptionWithSubjectWhenChannelNotOpen_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("channel is not open");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);

    defaultTestPublisher.unpublish(defaultTestEventDescription, defaultTestSubject);
  }

  @Test
  public void EventCore_publishValidEventDescriptionWithValidSubject_publishEventWithSubjectReceivedBySubscribers() {
    int expectedProcessedPubishedEventsSize = 1;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);

    assertEquals(expectedProcessedPubishedEventsSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
  }

  @Test
  public void EventCore_unpublishValidPublishedEventDescWithValidSubject_unpublishEventWithSubjectReceivedBySubscribers() {
    int expectedProcessedUnpubishedEventsSize = 1;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);

    defaultTestPublisher.unpublish(defaultTestEventDescription, defaultTestSubject);

    assertEquals(expectedProcessedUnpubishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));
  }

  @Test
  public void EventCore_publishSameEventDescWithSameSubjectTwice_publishEventWithSubjectReceivedOnceBySubscribers() {
    int expectedProcessedPublishedEventsSize = 1;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);
    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);

    assertEquals(expectedProcessedPublishedEventsSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
  }

  @Test
  public void EventCore_unpublishSamePublishedEventDWithSameSubjectTwice_unpublishEventWithSubjectReceivedOnceBySubscribers() {
    int expectedProcessedUnpublishedEventsSize = 1;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);

    defaultTestPublisher.unpublish(defaultTestEventDescription, defaultTestSubject);
    defaultTestPublisher.unpublish(defaultTestEventDescription, defaultTestSubject);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));
  }

  @Test
  public void EventCore_publishSameEventWithDiffSubjects_publishForAllTheSameEventsWithDifferentSubjectsReceivedBySubscribers() {
    int expectedProcessedPublishedEventsSize = 2;
    Subject expectedSubjectOne = createSubjectStub("test.subject.one");
    Subject expectedSubjectTwo = createSubjectStub("test.subject.two");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription, expectedSubjectOne);
    defaultTestPublisher.publish(defaultTestEventDescription, expectedSubjectTwo);

    assertEquals(expectedProcessedPublishedEventsSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, expectedSubjectOne,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, expectedSubjectTwo,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
  }

  @Test
  public void EventCore_unpublishSameEventWithDiffSubjs_unpublishForAllTheSameEventsWithDifferentSubjsReceivedBySubscribers() {
    int expectedProcessedUnpublishedEventsSize = 2;
    Subject expectedSubjectOne = createSubjectStub("test.subject.one");
    Subject expectedSubjectTwo = createSubjectStub("test.subject.two");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription, expectedSubjectOne);
    defaultTestPublisher.publish(defaultTestEventDescription, expectedSubjectTwo);

    defaultTestPublisher.unpublish(defaultTestEventDescription, expectedSubjectOne);
    defaultTestPublisher.unpublish(defaultTestEventDescription, expectedSubjectTwo);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, expectedSubjectOne,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, expectedSubjectTwo,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(1));
  }

  @Test
  public void EventCore_publishDiffEventsWithTheSameSubjs_publishForAllDiffEventsWithTheSameSubjsReceivedBySubscribers() {
    int expectedProcessedPublishedEventsSize = 2;
    EventDescription expectedEventDescriptionOne =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.one");
    EventDescription expectedEventDescriptionTwo =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.two");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(expectedEventDescriptionOne, defaultTestSubject);
    defaultTestPublisher.publish(expectedEventDescriptionTwo, defaultTestSubject);

    assertEquals(expectedProcessedPublishedEventsSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
  }

  @Test
  public void EventCore_unpublishDiffEventsWithTheSameSubs_unpublishForAllDiffEventsWithTheSameSubjsReceivedBySubscribers() {
    int expectedProcessedUnpublishedEventsSize = 2;
    EventDescription expectedEventDescriptionOne =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.one");
    EventDescription expectedEventDescriptionTwo =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.two");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(expectedEventDescriptionOne, defaultTestSubject);
    defaultTestPublisher.publish(expectedEventDescriptionTwo, defaultTestSubject);

    defaultTestPublisher.unpublish(expectedEventDescriptionOne, defaultTestSubject);
    defaultTestPublisher.unpublish(expectedEventDescriptionTwo, defaultTestSubject);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(1));
  }

  @Test
  public void EventCore_publishEventWithSubjAndWithNoSubj_publishForBothEventWithSubjAndNoSubjReceivedBySubscribers() {
    int expectedProcessedPublishedEventsSize = 2;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);
    defaultTestPublisher.publish(defaultTestEventDescription);

    assertEquals(expectedProcessedPublishedEventsSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
  }

  @Test
  public void EventCore_unpublishEventWithSubjAndWithNoSubj_unpublishForBothEventWithSubjAndNoSubjReceivedBySubscribers() {
    int expectedProcessedUnpublishedEventsSize = 2;
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);
    defaultTestPublisher.publish(defaultTestEventDescription);

    defaultTestPublisher.unpublish(defaultTestEventDescription, defaultTestSubject);
    defaultTestPublisher.unpublish(defaultTestEventDescription);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(1));
  }

  @Test
  public void EventCore_multiplePublishersPublishDifferentEventsToOneSubscriber_allPublishedEventsReceivedBySubscriber() {
    int expectedProcessedPublishedEventsSize = 3;
    EventDescription expectedEventDescriptionOne =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.one");
    EventDescription expectedEventDescriptionTwo =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.two");
    EventDescription expectedEventDescriptionThree =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.three");
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    publisherOne.publish(expectedEventDescriptionOne);
    publisherTwo.publish(expectedEventDescriptionTwo);
    publisherThree.publish(expectedEventDescriptionThree);

    assertEquals(expectedProcessedPublishedEventsSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(2));
  }

  @Test
  public void EventCore_multiplePublishersUnpublishDifferentEventsToOneSubscriber_allUnpublishedEventsReceivedBySubscriber() {
    int expectedProcessedUnpublishedEventsSize = 3;
    EventDescription expectedEventDescriptionOne =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.one");
    EventDescription expectedEventDescriptionTwo =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.two");
    EventDescription expectedEventDescriptionThree =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.three");
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    publisherOne.publish(expectedEventDescriptionOne);
    publisherTwo.publish(expectedEventDescriptionTwo);
    publisherThree.publish(expectedEventDescriptionThree);

    publisherOne.unpublish(expectedEventDescriptionOne);
    publisherTwo.unpublish(expectedEventDescriptionTwo);
    publisherThree.unpublish(expectedEventDescriptionThree);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(2));
  }

  @Test
  public void EventCore_multiplePublishersPublishSameEventToOneSubscriber_onePublishedEventReceivedBySubscriber() {
    int expectedProcessedPublishedEventsSize = 1;
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    publisherOne.publish(defaultTestEventDescription);
    publisherTwo.publish(defaultTestEventDescription);
    publisherThree.publish(defaultTestEventDescription);

    assertEquals(expectedProcessedPublishedEventsSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
  }

  @Test
  public void EventCore_multiplePublishersUnpublishSameEventToOneSubscriber_oneUnpublishedEventReceivedBySubscriber() {
    int expectedProcessedUnpublishedEventsSize = 1;
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    publisherOne.publish(defaultTestEventDescription);
    publisherTwo.publish(defaultTestEventDescription);
    publisherThree.publish(defaultTestEventDescription);

    publisherOne.unpublish(defaultTestEventDescription);
    publisherTwo.unpublish(defaultTestEventDescription);
    publisherThree.unpublish(defaultTestEventDescription);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));
  }

  @Test
  public void EventCore_onePublisherPublishesOneEventToMultipleSubscribers_onePublishedEventReceivedByAllSubscribers() {
    int expectedProcessedPublishedEventsSize = 1;
    AccumulatorSubscriberStub subscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.one");
    AccumulatorSubscriberStub subscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.two");
    AccumulatorSubscriberStub subscriberThree =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.three");
    eventFactory.addSubscriber(defaultTestChannel, subscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, subscriberTwo);
    eventFactory.addSubscriber(defaultTestChannel, subscriberThree);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription);

    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberOne.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberOne.getProcessedPublishedEventList().get(0));
    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberTwo.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberTwo.getProcessedPublishedEventList().get(0));
    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberThree.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberThree.getProcessedPublishedEventList().get(0));
  }

  @Test
  public void EventCore_onePublisherUnpublishesOneEventToMultipleSubscribers_oneUnpublishedEventReceivedByAllSubscribers() {
    int expectedProcessedUnpublishedEventsSize = 1;
    AccumulatorSubscriberStub subscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.one");
    AccumulatorSubscriberStub subscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.two");
    AccumulatorSubscriberStub subscriberThree =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.three");
    eventFactory.addSubscriber(defaultTestChannel, subscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, subscriberTwo);
    eventFactory.addSubscriber(defaultTestChannel, subscriberThree);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription);

    defaultTestPublisher.unpublish(defaultTestEventDescription);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberOne.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberOne.getProcessedUnpublishedEventList().get(0));
    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberTwo.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberTwo.getProcessedUnpublishedEventList().get(0));
    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberThree.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberThree.getProcessedUnpublishedEventList().get(0));
  }

  @Test
  public void EventCore_multiplePublishersPublishDiffEventsToMultipleSubscribers_allPublishedEventsReceivedByAllSubscribers() {
    int expectedProcessedPublishedEventsSize = 3;
    EventDescription expectedEventDescriptionOne =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.one");
    EventDescription expectedEventDescriptionTwo =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.two");
    EventDescription expectedEventDescriptionThree =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.three");
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);
    AccumulatorSubscriberStub subscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.one");
    AccumulatorSubscriberStub subscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.two");
    AccumulatorSubscriberStub subscriberThree =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.three");
    eventFactory.addSubscriber(defaultTestChannel, subscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, subscriberTwo);
    eventFactory.addSubscriber(defaultTestChannel, subscriberThree);
    eventFactory.openChannel(defaultTestChannel);

    publisherOne.publish(expectedEventDescriptionOne);
    publisherTwo.publish(expectedEventDescriptionTwo);
    publisherThree.publish(expectedEventDescriptionThree);

    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberOne.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne,
        subscriberOne.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        subscriberOne.getProcessedPublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree,
        subscriberOne.getProcessedPublishedEventList().get(2));

    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberTwo.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne,
        subscriberTwo.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        subscriberTwo.getProcessedPublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree,
        subscriberTwo.getProcessedPublishedEventList().get(2));

    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberThree.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne,
        subscriberThree.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        subscriberThree.getProcessedPublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree,
        subscriberThree.getProcessedPublishedEventList().get(2));
  }

  @Test
  public void EventCore_multiplePubsUnpublishDiffEventsToMultipleSubscs_allUnpublishedEventsReceivedByAllSubscs() {
    int expectedProcessedUnpublishedEventsSize = 3;
    EventDescription expectedEventDescriptionOne =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.one");
    EventDescription expectedEventDescriptionTwo =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.two");
    EventDescription expectedEventDescriptionThree =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.three");
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);
    AccumulatorSubscriberStub subscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.one");
    AccumulatorSubscriberStub subscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.two");
    AccumulatorSubscriberStub subscriberThree =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.three");
    eventFactory.addSubscriber(defaultTestChannel, subscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, subscriberTwo);
    eventFactory.addSubscriber(defaultTestChannel, subscriberThree);
    eventFactory.openChannel(defaultTestChannel);
    publisherOne.publish(expectedEventDescriptionOne);
    publisherTwo.publish(expectedEventDescriptionTwo);
    publisherThree.publish(expectedEventDescriptionThree);

    publisherOne.unpublish(expectedEventDescriptionOne);
    publisherTwo.unpublish(expectedEventDescriptionTwo);
    publisherThree.unpublish(expectedEventDescriptionThree);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberOne.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne,
        subscriberOne.getProcessedUnpublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        subscriberOne.getProcessedUnpublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree,
        subscriberOne.getProcessedUnpublishedEventList().get(2));

    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberTwo.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne,
        subscriberTwo.getProcessedUnpublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        subscriberTwo.getProcessedUnpublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree,
        subscriberTwo.getProcessedUnpublishedEventList().get(2));

    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberThree.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne,
        subscriberThree.getProcessedUnpublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        subscriberThree.getProcessedUnpublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree,
        subscriberThree.getProcessedUnpublishedEventList().get(2));
  }

  @Test
  public void EventCore_multiplePubsPublisheSameEventToMultipleSubscs_onePublishedEventReceivedByAllSubss() {
    int expectedProcessedPublishedEventsSize = 1;
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);
    AccumulatorSubscriberStub subscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.one");
    AccumulatorSubscriberStub subscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.two");
    AccumulatorSubscriberStub subscriberThree =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.three");
    eventFactory.addSubscriber(defaultTestChannel, subscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, subscriberTwo);
    eventFactory.addSubscriber(defaultTestChannel, subscriberThree);
    eventFactory.openChannel(defaultTestChannel);

    publisherOne.publish(defaultTestEventDescription);
    publisherTwo.publish(defaultTestEventDescription);
    publisherThree.publish(defaultTestEventDescription);

    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberOne.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberOne.getProcessedPublishedEventList().get(0));

    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberTwo.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberTwo.getProcessedPublishedEventList().get(0));

    assertEquals(expectedProcessedPublishedEventsSize,
        subscriberThree.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberThree.getProcessedPublishedEventList().get(0));
  }

  @Test
  public void EventCore_multiplePubsUnpublisheSameEventToMultipleSubscs_oneUnpublishedEventReceivedByAllSubscs() {
    int expectedProcessedUnpublishedEventsSize = 1;
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);
    AccumulatorSubscriberStub subscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.one");
    AccumulatorSubscriberStub subscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.two");
    AccumulatorSubscriberStub subscriberThree =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.three");
    eventFactory.addSubscriber(defaultTestChannel, subscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, subscriberTwo);
    eventFactory.addSubscriber(defaultTestChannel, subscriberThree);
    eventFactory.openChannel(defaultTestChannel);
    publisherOne.publish(defaultTestEventDescription);
    publisherTwo.publish(defaultTestEventDescription);
    publisherThree.publish(defaultTestEventDescription);

    publisherOne.unpublish(defaultTestEventDescription);
    publisherTwo.unpublish(defaultTestEventDescription);
    publisherThree.unpublish(defaultTestEventDescription);

    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberOne.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberOne.getProcessedUnpublishedEventList().get(0));

    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberTwo.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberTwo.getProcessedUnpublishedEventList().get(0));

    assertEquals(expectedProcessedUnpublishedEventsSize,
        subscriberThree.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        subscriberThree.getProcessedUnpublishedEventList().get(0));
  }

  @Test
  public void EventCore_publisheUnpublishThenPublishEventAgain_oneEachOfPublishedUnpublishedAndPublishedEventReceivedBySubsc() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription);

    assertEquals(1, accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));

    defaultTestPublisher.unpublish(defaultTestEventDescription);

    assertEquals(1, accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));

    defaultTestPublisher.publish(defaultTestEventDescription);

    assertEquals(2, accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
  }

  @Test
  public void EventCore_pubsPublishSameEventsOnePubUnpublishesSameEvents_sameEventsNotUnpublishedUntilUnpublishedByAllPubs() {
    EventDescription eventFromPublisherOne = eventFactory.createEventDescription(defaultTestChannel,
        "test.family", "event.from.publisher.one");
    EventDescription eventFromPublisherTwo = eventFactory.createEventDescription(defaultTestChannel,
        "test.family", "event.from.publisher.two");
    EventDescription eventFromBothPublishers = eventFactory
        .createEventDescription(defaultTestChannel, "test.family", "event.from.both.publishers");
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    publisherOne.publish(eventFromPublisherOne);
    publisherOne.publish(eventFromBothPublishers);
    publisherTwo.publish(eventFromPublisherTwo);
    publisherTwo.publish(eventFromBothPublishers);

    assertEquals(3, accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(eventFromPublisherOne,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(eventFromBothPublishers,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(eventFromPublisherTwo,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(2));

    publisherOne.unpublish(eventFromPublisherOne);
    publisherOne.unpublish(eventFromBothPublishers);

    assertEquals(1, accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(eventFromPublisherOne,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(0));

    publisherTwo.unpublish(eventFromBothPublishers);

    assertEquals(2, accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(eventFromBothPublishers,
        accumulatorSubscriberStub.getProcessedUnpublishedEventList().get(1));
  }

  @Test
  public void EventCore_publisherAttemptsToUnpublishAnotherPublishersEvent_subscriberReceivesNoUnpublishEvent() {
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    publisherOne.publish(defaultTestEventDescription);

    publisherTwo.unpublish(defaultTestEventDescription);

    assertEquals(1, accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEquals(0, accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
  }

  @Test
  public void EventCore_publisherAttemptsToUnpublishAnotherPublishersEventWithSubject_subscriberReceivesNoUnpublishEvent() {
    Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    publisherOne.publish(defaultTestEventDescription, defaultTestSubject);

    publisherTwo.unpublish(defaultTestEventDescription, defaultTestSubject);

    assertEquals(1, accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEquals(0, accumulatorSubscriberStub.getProcessedUnpublishedEventList().size());
  }

  @Test
  public void EventCore_createEventUsingChannelFromAnotherFactoryInstance_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(" does not exist in this factory");
    EventFactory eventFactoryOne = EventFactory.createFactory();
    EventFactory eventFactoryTwo = EventFactory.createFactory();
    Channel channel = eventFactoryOne.createChannel("test.channel");

    eventFactoryTwo.createEventDescription(channel, "test.family", "test.name");
  }

  @Test
  public void EventCore_createPublisherUsingChannelFromAnotherFactoryInstance_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(" does not exist in this factory");
    EventFactory eventFactoryOne = EventFactory.createFactory();
    EventFactory eventFactoryTwo = EventFactory.createFactory();
    Channel channel = eventFactoryOne.createChannel("test.channel");

    eventFactoryTwo.createPublisher(channel);
  }

  @Test
  public void EventCore_addSubscriberUsingChannelFromAnotherFactoryInstance_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(" does not exist in this factory");
    EventFactory eventFactoryOne = EventFactory.createFactory();
    EventFactory eventFactoryTwo = EventFactory.createFactory();
    Channel channel = eventFactoryOne.createChannel("test.channel");

    eventFactoryTwo.addSubscriber(channel, accumulatorSubscriberStub);
  }

  @Test
  public void EventCore_openChannelFromAnotherFactoryInstance_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage(" does not exist in this factory");
    EventFactory eventFactoryOne = EventFactory.createFactory();
    EventFactory eventFactoryTwo = EventFactory.createFactory();
    Channel channel = eventFactoryOne.createChannel("test.channel");

    eventFactoryTwo.openChannel(channel);
  }

  @Test
  public void EventCore_publishValidEventAndOneSubscThrowsUncheckedException_eventCoreDoesNotDieAndAllOtherSubscsReceiveEvent() {
    AccumulatorSubscriberStub stableSubscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.stable.one");
    Subscriber unstableSubscriber = new Subscriber() {

      @Override
      public void processPublishEventCallback(Event event) {
        throw new NullPointerException();
      }

      @Override
      public String getName() {
        return "test.unstable.subscriber";
      }
    };
    AccumulatorSubscriberStub stableSubscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.subscriber.stable.two");
    eventFactory.addSubscriber(defaultTestChannel, stableSubscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, unstableSubscriber);
    eventFactory.addSubscriber(defaultTestChannel, stableSubscriberTwo);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(defaultTestEventDescription);

    assertEquals(1, stableSubscriberOne.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        stableSubscriberOne.getProcessedPublishedEventList().get(0));
    assertEquals(1, stableSubscriberTwo.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        stableSubscriberTwo.getProcessedPublishedEventList().get(0));
  }

  @Test
  public void EventCore_unpublishValidEventAndOneSubscThrowsUncheckedExcept_eventCoreDoesNotDieAndAllOtherSubssReceiveEvent() {
    AccumulatorSubscriberStub stableSubscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.stable.subscriber.one");
    Subscriber unstableSubscriber = new Subscriber() {

      @Override
      public void processUnpublishEventCallback(Event event) {
        throw new NullPointerException();
      }

      @Override
      public String getName() {
        return "test.unstable.subscriber";
      }
    };
    AccumulatorSubscriberStub stableSubscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("test.stable.subscriber.two");
    eventFactory.addSubscriber(defaultTestChannel, stableSubscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, unstableSubscriber);
    eventFactory.addSubscriber(defaultTestChannel, stableSubscriberTwo);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription);

    defaultTestPublisher.unpublish(defaultTestEventDescription);

    assertEquals(1, stableSubscriberOne.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        stableSubscriberOne.getProcessedUnpublishedEventList().get(0));
    assertEquals(1, stableSubscriberTwo.getProcessedUnpublishedEventList().size());
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        stableSubscriberTwo.getProcessedUnpublishedEventList().get(0));
  }

  @Test
  public void EventCore_ensureChannelRespectsNaturalOrderContract_naturalOrderContractRespected() {
    Channel leftOperand;
    Channel rightOperand;

    leftOperand = eventFactory.createChannel("test.channel");
    rightOperand = eventFactory.createChannel("test.channel");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.EQ, rightOperand);

    leftOperand = eventFactory.createChannel("aaa");
    rightOperand = eventFactory.createChannel("bbb");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.LT, rightOperand);

    leftOperand = eventFactory.createChannel("zzz");
    rightOperand = eventFactory.createChannel("yyy");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.GT, rightOperand);
  }

  @Test
  public void EventCore_ensureEventDescriptionRespectsNaturalOrderContract_naturalOrderConstractRespected() {
    EventDescription leftOperand;
    EventDescription rightOperand;

    leftOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "same.family", "same.name");
    rightOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "same.family", "same.name");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.EQ, rightOperand);

    leftOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "same.family", "aaa");
    rightOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "same.family", "zzz");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.LT, rightOperand);

    leftOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "same.family", "zzz");
    rightOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "same.family", "aaa");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.GT, rightOperand);

    leftOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "aaa", "zzz");
    rightOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "zzz", "aaa");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.LT, rightOperand);

    leftOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "zzz", "aaa");
    rightOperand = eventFactory.createEventDescription(eventFactory.createChannel("same.channel"),
        "aaa", "zzz");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.GT, rightOperand);

    leftOperand =
        eventFactory.createEventDescription(eventFactory.createChannel("aaa"), "zzz", "zzz");
    rightOperand =
        eventFactory.createEventDescription(eventFactory.createChannel("zzz"), "aaa", "aaa");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.LT, rightOperand);

    leftOperand =
        eventFactory.createEventDescription(eventFactory.createChannel("zzz"), "aaa", "aaa");
    rightOperand =
        eventFactory.createEventDescription(eventFactory.createChannel("aaa"), "zzz", "zzz");
    assertNaturalOrder.assertExpectedRelation(leftOperand, Relation.GT, rightOperand);
  }

  @Test
  public void EventCore_subscriberRequestsPublishedEventResentWhenOnlyOnePublishedEvent_subscriberReceivesOnePublishedEvent() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription);

    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();

    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(defaultTestEventDescription,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
  }

  @Test
  public void EventCore_addSubscriberWhenSubscriberGetNameReturnsNull_nullPointerExceptionIsThrown() {
    fail("not implemented yet");
  }

  @Test
  @Ignore("not worked on")
  public void EventCore_addSubscriberWhenSubscriberGetNameThrowsException_unsupportedOperationExceptionIsThrown() {
    // TODO implementation note: wrap the thrown exception.
    fail("not implemented yet");
  }

  @Test
  @Ignore("not worked on")
  public void EventCore_addSubscriberWhenSubscriberGetNameReturnsEmptyString_invalidParameterExceptionIsThrown() {
    fail("not implemented yet");
  }

  @Test
  @Ignore("not worked on")
  public void EventCore_addSubscriberWithMutableName_subscriberContinuesToReceiveEvents() {
    fail("not implemented yet");
  }

  @Test
  @Ignore("not worked on")
  public void EventCore_addSubscriberThenMutateNameThenAddSubscriber_subscriberOnlyAddedOnce() {
    // TODO implementation note: I'm not even sure I can implement this, but I want the behaviour to
    // be similar to adding the same subscriber twice. Maybe use a map to map subscriber names to
    // external subscriber implementations.
    fail("not implemented yet");
  }

  /*
   * Rough:
   * 
   * Register two subscribers. Publish a number of events. First subscriber requests a resend of all
   * published events. First subscriber receives all published events in the order they were
   * originally sent. Second subscriber does not. As a point of clarification, the objective is NOT
   * to replay the entire publish/unpublish history for all events since the channel was opened. The
   * objective is to simply send all current published events to the subscriber that requested it.
   * 
   * Also, it would be useful for testing and debugging to get a list of published events and who
   * published them. Maybe a single method to get an event report for a given channel that lists all
   * defined events, whether the events are currently published and who published them. This would
   * be an internal interface for debugging purposes only.
   * 
   * Maybe you should also have channel specific Events that are published in band in each channel
   * so that clients can receive events like the channel is opened / closed. The events would be no
   * different in type from standard client defined events. They could be differentiated from the
   * client defined events by being in their own family. You could even enforce a specific naming
   * convention like "event.core.*", so perhaps "event.core.channel.info",
   * "event.core.system.error", etc. You can report events like when subscribers misbehave. Should
   * the messages be in band? Does that make sense from the perspective of the subscribers? The
   * subscribers should only know about what is relevant to them, so perhaps the messages should be
   * limited to those events that all subscribers care about such as when the channel is opened or
   * closed. This would break a number of existing test cases. Thus, maybe the events should not be
   * in band.
   * 
   * Maybe implement an InternalSystem that is a singleton that is created on demand. This
   * InternalSystem could have the instance cache and all factories could reference it, so for
   * scenarios where Channels and Events are being created by different factories aren't an issue.
   * The InternalSystem as its name implies would not be accessible by clients. Existing test cases
   * where UnsupportedOperationExceptions are being thrown would need to be rewritten. There is some
   * potential for a memory leak with this design since if nothing is referencing the factory, then
   * all the resources created through this factory would still exist. I would need "delete" options
   * at the Factory level which, in turn implies "disable". After that, I could potentially
   * implement some intelligence in the InternalSystem itself for monitoring to ensure instances are
   * still being used and closing them otherwise.
   * 
   */

}
