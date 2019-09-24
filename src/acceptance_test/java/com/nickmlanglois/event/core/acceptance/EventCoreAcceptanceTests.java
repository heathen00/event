package com.nickmlanglois.event.core.acceptance;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
import com.nickmlanglois.event.core.MutableAccumulatorSubscriberStub;
import com.nickmlanglois.event.core.Publisher;
import com.nickmlanglois.event.core.Subject;
import com.nickmlanglois.event.core.SubjectStub;
import com.nickmlanglois.event.core.Subscriber;
import com.nickmlanglois.event.core.UndefinedSubjectStub;

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

      @Override
      public boolean isDefined() {
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
  public void EventCore_createChannelWithEmptyChannelName_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(
        "name can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createChannel("\n");
  }

  @Test
  public void EventCore_createChannelWithInvalidCharactersInChannelName_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
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
  public void EventCore_createEventDescriptionWithUnknownExternalChannelImplementation_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
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
  public void EventCore_createEventDescriptionWithEmptyFamilyParameter_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(
        "family can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createEventDescription(defaultTestChannel, "", "test.name");
  }

  @Test
  public void EventCore_createEventDescriptionWithInvalidCharactersInFamilyParameter_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
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
  public void EventCore_createEventDescriptionWithEmptyNameParameter_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage(
        "name can only contain lower case letters, numbers, and periods, and cannot start or end with a period");

    eventFactory.createEventDescription(defaultTestChannel, "test.family", " \n \t");
  }

  @Test
  public void EventCore_createEventDescriptionWithInvalidCharactersInNameParameter_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
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
  public void EventCore_createPublisherWithUnknownExternalChannelImplementation_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
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
  public void EventCore_addSubscriberWithUnknownExternalChannelImplementation_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
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
  public void EventCore_openChannelWithUnknownExternalChannelImplementation_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
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
    thrown.expectMessage("operation not permitted while channel is open");
    eventFactory.openChannel(defaultTestChannel);

    eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name");
  }

  @Test
  public void EventCore_createPublisherForChannelAfterEnablingChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("operation not permitted while channel is open");
    eventFactory.openChannel(defaultTestChannel);

    eventFactory.createPublisher(defaultTestChannel);
  }

  @Test
  public void EventCore_addSubscriberForChannelAfterEnablingChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("operation not permitted while channel is open");
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
  public void EventCore_subscriberRequestsPublishedEventResendWhenOnePublishedEvent_subscriberReceivesOnePublishedEvent() {
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
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("subscriber.getName() cannot return null");
    final Subscriber badSubscriber = new Subscriber() {

      @Override
      public String getName() {
        return null;
      }
    };

    eventFactory.addSubscriber(defaultTestChannel, badSubscriber);
  }

  @Test
  public void EventCore_addSubscriberWhenSubscriberGetNameThrowsException_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("subscriber.getName() threw an exception");
    NullPointerException expectedNullPointerException =
        new NullPointerException("test NullPointerException");
    thrown.expectCause(is(expectedNullPointerException));
    final Subscriber badSubscriber = new Subscriber() {

      @Override
      public String getName() {
        throw expectedNullPointerException;
      }
    };

    eventFactory.addSubscriber(defaultTestChannel, badSubscriber);
  }

  @Test
  public void EventCore_addSubscriberWhenSubscriberGetNameReturnsEmptyString_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("subscriber.getName() cannot return an empty String");
    final Subscriber badSubscriber = new Subscriber() {

      @Override
      public String getName() {
        return " \t\n";
      }
    };

    eventFactory.addSubscriber(defaultTestChannel, badSubscriber);
  }

  @Test
  public void EventCore_addSubscriberWithMutableName_subscriberContinuesToReceiveEvents() {
    final String expectedOriginalName = "original.subscriber.name";
    final String expectedNewName = "new.subscriber.name";
    final MutableAccumulatorSubscriberStub badSubscriber =
        new MutableAccumulatorSubscriberStub(expectedOriginalName);
    EventDescription eventDescriptionOne =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.one");
    EventDescription eventDescriptionTwo =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.name.two");
    eventFactory.addSubscriber(defaultTestChannel, badSubscriber);
    eventFactory.openChannel(defaultTestChannel);

    defaultTestPublisher.publish(eventDescriptionOne);
    badSubscriber.setName(expectedNewName);
    defaultTestPublisher.publish(eventDescriptionTwo);

    assertEventCore.assertExpectedEvent(eventDescriptionOne,
        badSubscriber.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(eventDescriptionTwo,
        badSubscriber.getProcessedPublishedEventList().get(1));
    assertEquals(expectedNewName, badSubscriber.getName());
    assertEquals(expectedNewName, defaultTestChannel.getSubscriberList().get(0).getName());
  }

  @Test
  public void EventCore_addSubscriberThenMutateNameThenAddSubscriber_subscriberOnlyAddedOnce() {
    final int expectedSubscriberListSize = 1;
    final String expectedOriginalName = "original.subscriber.name";
    final String expectedNewName = "new.subscriber.name";
    final MutableAccumulatorSubscriberStub badSubscriber =
        new MutableAccumulatorSubscriberStub(expectedOriginalName);

    eventFactory.addSubscriber(defaultTestChannel, badSubscriber);
    badSubscriber.setName(expectedNewName);
    eventFactory.addSubscriber(defaultTestChannel, badSubscriber);

    assertEquals(expectedSubscriberListSize, defaultTestChannel.getSubscriberList().size());
    assertEquals(expectedNewName, defaultTestChannel.getSubscriberList().get(0).getName());
  }

  @Test
  public void EventCore_subsRequestsPublishedEventResendWhenOnePublishedEventWithSubj_subsReceivesOnePublishedEventWithSubj() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    defaultTestPublisher.publish(defaultTestEventDescription, defaultTestSubject);

    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();

    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(defaultTestEventDescription, defaultTestSubject,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
  }

  @Test
  public void EventCore_subsRequestPublishedEventResendWhenMultiplePublishedEvents_subsReceivesAllPublishedEventsInOrder() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    List<EventDescription> expectedEventDescriptionList = Arrays.asList(
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.zzz"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.yyy"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.xxx"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.www"));
    eventFactory.openChannel(defaultTestChannel);
    for (EventDescription eventDescription : expectedEventDescriptionList) {
      defaultTestPublisher.publish(eventDescription);
    }

    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();

    assertEquals(expectedEventDescriptionList.size() * 2,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    int resentEventsBaseIndex = expectedEventDescriptionList.size();
    for (int i = 0; i < expectedEventDescriptionList.size(); i++) {
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          accumulatorSubscriberStub.getProcessedPublishedEventList().get(i));
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          accumulatorSubscriberStub.getProcessedPublishedEventList()
              .get(resentEventsBaseIndex + i));
    }
  }

  @Test
  public void EventCore_subsRequestPublishedEventResendWhenMultiplePublishedEventsWithSubj_subsReceivesAllPublishedEventsWithsSubjsInOrder() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    List<EventDescription> expectedEventDescriptionList = Arrays.asList(
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.zzz"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.yyy"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.xxx"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.www"));
    List<Subject> expectedSubjectList =
        Arrays.asList(createSubjectStub("test.subject.one"), createSubjectStub("test.subject.two"),
            createSubjectStub("test.subject.three"), createSubjectStub("test.subject.four"));
    eventFactory.openChannel(defaultTestChannel);
    for (int i = 0; i < expectedEventDescriptionList.size(); i++) {
      defaultTestPublisher.publish(expectedEventDescriptionList.get(i), expectedSubjectList.get(i));
    }

    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();

    assertEquals(expectedEventDescriptionList.size() * 2,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    int resentEventsBaseIndex = expectedEventDescriptionList.size();
    for (int i = 0; i < expectedEventDescriptionList.size(); i++) {
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          expectedSubjectList.get(i),
          accumulatorSubscriberStub.getProcessedPublishedEventList().get(i));
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          expectedSubjectList.get(i), accumulatorSubscriberStub.getProcessedPublishedEventList()
              .get(resentEventsBaseIndex + i));
    }
  }

  @Test
  public void EventCore_subsRequestPublishedEventResendWhenMultiplePublishedEventsWithAndWithoutSubj_subsReceivesAllPublishedEventsWithAndWithoutSubjInOrder() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    final int expectedProcessedPublishedEventsListSize = 8;
    final EventDescription expectedEventDescriptionOne =
        eventFactory.createEventDescription(defaultTestChannel, "publisher.one", "event.one");
    final EventDescription expectedEventDescriptionTwo =
        eventFactory.createEventDescription(defaultTestChannel, "publisher.one", "event.two");
    final EventDescription expectedEventDescriptionThree =
        eventFactory.createEventDescription(defaultTestChannel, "publisher.two", "event.three");
    final EventDescription expectedEventDescriptionFour =
        eventFactory.createEventDescription(defaultTestChannel, "publisher.two", "event.four");
    eventFactory.openChannel(defaultTestChannel);
    final Subject expectedSubjectOne = createSubjectStub("test.subject.one");
    final Subject expectedSubjectTwo = createSubjectStub("test.subject.two");
    defaultTestPublisher.publish(expectedEventDescriptionOne, expectedSubjectOne);
    defaultTestPublisher.publish(expectedEventDescriptionTwo);
    defaultTestPublisher.publish(expectedEventDescriptionThree, expectedSubjectTwo);
    defaultTestPublisher.publish(expectedEventDescriptionFour);

    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();

    assertEquals(expectedProcessedPublishedEventsListSize,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne, expectedSubjectOne,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(0));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(1));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree, expectedSubjectTwo,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(2));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionFour,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(3));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionOne, expectedSubjectOne,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(4));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionTwo,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(5));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionThree, expectedSubjectTwo,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(6));
    assertEventCore.assertExpectedEvent(expectedEventDescriptionFour,
        accumulatorSubscriberStub.getProcessedPublishedEventList().get(7));
  }

  @Test
  public void EventCore_subsRequestPublishedEventResendWhenMultiplePublishedEventsFromMultiplePublishers_subsReceivesAllPublishedEventsFromAllPublishersInOrder() {
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    final Publisher publisherOne = eventFactory.createPublisher(defaultTestChannel);
    final Publisher publisherTwo = eventFactory.createPublisher(defaultTestChannel);
    final Publisher publisherThree = eventFactory.createPublisher(defaultTestChannel);

    final List<EventDescription> publisherOnePublishedEventDescriptionList = Arrays.asList(
        eventFactory.createEventDescription(defaultTestChannel, "publisher.one", "event.one"),
        eventFactory.createEventDescription(defaultTestChannel, "publisher.one", "event.two"));
    final List<EventDescription> publisherTwoPublishedEventDescriptionList = Arrays.asList(
        eventFactory.createEventDescription(defaultTestChannel, "publisher.two", "event.one"),
        eventFactory.createEventDescription(defaultTestChannel, "publisher.two", "event.two"));
    final List<EventDescription> publisherThreePublishedEventDescriptionList = Arrays.asList(
        eventFactory.createEventDescription(defaultTestChannel, "publisher.three", "event.one"),
        eventFactory.createEventDescription(defaultTestChannel, "publisher.three", "event.two"));
    final List<EventDescription> expectedEventDescriptionList =
        Arrays.asList(publisherOnePublishedEventDescriptionList.get(0),
            publisherTwoPublishedEventDescriptionList.get(0),
            publisherThreePublishedEventDescriptionList.get(0),
            publisherOnePublishedEventDescriptionList.get(1),
            publisherTwoPublishedEventDescriptionList.get(1),
            publisherThreePublishedEventDescriptionList.get(1));
    eventFactory.openChannel(defaultTestChannel);
    final int numberOfPublishedEventsPerPublisher = 2;
    for (int i = 0; i < numberOfPublishedEventsPerPublisher; i++) {
      publisherOne.publish(publisherOnePublishedEventDescriptionList.get(i));
      publisherTwo.publish(publisherTwoPublishedEventDescriptionList.get(i));
      publisherThree.publish(publisherThreePublishedEventDescriptionList.get(i));
    }

    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();

    assertEquals(expectedEventDescriptionList.size() * 2,
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    int resentPublishedEventsBaseIndex = expectedEventDescriptionList.size();
    for (int i = 0; i < expectedEventDescriptionList.size(); i++) {
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          accumulatorSubscriberStub.getProcessedPublishedEventList().get(i));
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          accumulatorSubscriberStub.getProcessedPublishedEventList()
              .get(resentPublishedEventsBaseIndex + i));
    }
  }

  @Test
  public void EventCore_subscriberRequestPublishedEventResendWhenMultipleSubscribers_onlySubscriberThatRequestedResendReceivesAllEvents() {
    List<EventDescription> expectedEventDescriptionList = Arrays.asList(
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.one"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.two"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.three"));
    AccumulatorSubscriberStub subscriberOne =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("subscriber.one");
    AccumulatorSubscriberStub subscriberTwo =
        AccumulatorSubscriberStub.createAccumulatorSubscriber("subscriber.two");
    eventFactory.addSubscriber(defaultTestChannel, subscriberOne);
    eventFactory.addSubscriber(defaultTestChannel, subscriberTwo);
    eventFactory.openChannel(defaultTestChannel);
    for (EventDescription eventDescription : expectedEventDescriptionList) {
      defaultTestPublisher.publish(eventDescription);
    }

    subscriberOne.resendAllCurrentPublishedEvents();

    assertEquals(expectedEventDescriptionList.size() * 2,
        subscriberOne.getProcessedPublishedEventList().size());
    assertEquals(expectedEventDescriptionList.size(),
        subscriberTwo.getProcessedPublishedEventList().size());
    int publishedEventResendBaseIndex = expectedEventDescriptionList.size();
    for (int i = 0; i < expectedEventDescriptionList.size(); i++) {
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          subscriberOne.getProcessedPublishedEventList().get(i));
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          subscriberOne.getProcessedPublishedEventList().get(publishedEventResendBaseIndex + i));
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          subscriberTwo.getProcessedPublishedEventList().get(i));
    }
  }

  @Test
  public void EventCore_subscriberRequestsPublishedEventResendWhenSomeEventsUnpublished_subscriberDoesNotReceiveUnpublishedEvents() {
    final List<EventDescription> publishedEventDescriptionList = Arrays.asList(
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.one"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.two"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.three"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.four"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.five"));
    final List<EventDescription> unpublishedEventDescriptionList =
        Arrays.asList(publishedEventDescriptionList.get(0), publishedEventDescriptionList.get(2),
            publishedEventDescriptionList.get(4));
    final List<EventDescription> expectedEventDescriptionList =
        Arrays.asList(publishedEventDescriptionList.get(0), publishedEventDescriptionList.get(1),
            publishedEventDescriptionList.get(2), publishedEventDescriptionList.get(3),
            publishedEventDescriptionList.get(4), publishedEventDescriptionList.get(1),
            publishedEventDescriptionList.get(3));
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    for (EventDescription eventDescription : publishedEventDescriptionList) {
      defaultTestPublisher.publish(eventDescription);
    }
    for (EventDescription eventDescription : unpublishedEventDescriptionList) {
      defaultTestPublisher.unpublish(eventDescription);
    }

    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();

    assertEquals(expectedEventDescriptionList.size(),
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    for (int i = 0; i < expectedEventDescriptionList.size(); i++) {
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          accumulatorSubscriberStub.getProcessedPublishedEventList().get(i));
    }
  }

  @Test
  public void EventCore_subscriberRequestsPublishedEventsResendMultipleTimes_subscriberReceivesAllPublishedEventsEachTime() {
    final List<EventDescription> publishedEventDescriptionList = Arrays.asList(
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.one"),
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event.two"));
    final List<EventDescription> expectedEventDescriptionList =
        Arrays.asList(publishedEventDescriptionList.get(0), publishedEventDescriptionList.get(1),
            publishedEventDescriptionList.get(0), publishedEventDescriptionList.get(1),
            publishedEventDescriptionList.get(0), publishedEventDescriptionList.get(1));
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);
    for (EventDescription eventDescription : publishedEventDescriptionList) {
      defaultTestPublisher.publish(eventDescription);
    }

    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();
    accumulatorSubscriberStub.resendAllCurrentPublishedEvents();

    assertEquals(expectedEventDescriptionList.size(),
        accumulatorSubscriberStub.getProcessedPublishedEventList().size());
    for (int i = 0; i < expectedEventDescriptionList.size(); i++) {
      assertEventCore.assertExpectedEvent(expectedEventDescriptionList.get(i),
          accumulatorSubscriberStub.getProcessedPublishedEventList().get(i));
    }
  }

  @Test
  public void EventCore_publishEventWithUndefinedSubject_illegalArgumentExceptionIsThrown() {
    final Subject undefinedSubject = new UndefinedSubjectStub("undefined.subject");
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Subject.isDefined() cannot return false");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);

    defaultTestPublisher.publish(defaultTestEventDescription, undefinedSubject);
  }

  @Test
  public void EventCore_unpublishEventWithUndefinedSubject_illegalArgumentExceptionIsThrown() {
    final Subject undefinedSubject = new UndefinedSubjectStub("undefined.subject");
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Subject.isDefined() cannot return false");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);

    defaultTestPublisher.unpublish(defaultTestEventDescription, undefinedSubject);
  }

  @Test
  public void EventCore_removeSubscriberWithNullParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("subscriber cannot equal null");

    eventFactory.removeSubcriber((Subscriber) null);
  }

  @Test
  public void EventCore_removeSubscriberThatWasNeverAdded_nothingHappens() {
    eventFactory.removeSubcriber(accumulatorSubscriberStub);
  }

  @Test
  public void EventCore_removeSubscriberMultipleTimes_subscriberRemovedOnce() {
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);
    eventFactory.addSubscriber(channel, accumulatorSubscriberStub);
    assertTrue(channel.getSubscriberList().contains(accumulatorSubscriberStub));
    assertEquals(channel, accumulatorSubscriberStub.getChannel());
    eventFactory.removeSubcriber(accumulatorSubscriberStub);
    assertFalse(channel.getSubscriberList().contains(accumulatorSubscriberStub));
    assertEquals(expectedChannelName, accumulatorSubscriberStub.getChannel().getName());
    assertFalse(accumulatorSubscriberStub.getChannel().isDefined());

    eventFactory.removeSubcriber(accumulatorSubscriberStub);
    eventFactory.removeSubcriber(accumulatorSubscriberStub);
    eventFactory.removeSubcriber(accumulatorSubscriberStub);

    assertEquals(expectedChannelName, accumulatorSubscriberStub.getChannel().getName());
    assertFalse(accumulatorSubscriberStub.getChannel().isDefined());
  }

  @Test
  @Ignore("not worked on")
  public void EventCore_removeSubscriberAfterDeletingChannel_nothingHappens() {
    fail("not implemented");
  }

  @Test
  public void EventCore_removeSubscriberWhenChannelOpen_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("operation not permitted while channel is open");
    eventFactory.addSubscriber(defaultTestChannel, accumulatorSubscriberStub);
    eventFactory.openChannel(defaultTestChannel);

    eventFactory.removeSubcriber(accumulatorSubscriberStub);
  }

  @Test
  public void EventCore_removeSubscriberWhenChannelClosed_subscriberRemoved() {
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);
    eventFactory.addSubscriber(channel, accumulatorSubscriberStub);
    assertTrue(channel.getSubscriberList().contains(accumulatorSubscriberStub));
    assertEquals(channel, accumulatorSubscriberStub.getChannel());

    eventFactory.removeSubcriber(accumulatorSubscriberStub);
    assertFalse(channel.getSubscriberList().contains(accumulatorSubscriberStub));
    assertEquals(expectedChannelName, accumulatorSubscriberStub.getChannel().getName());
    assertFalse(accumulatorSubscriberStub.getChannel().isDefined());
  }

  @Test
  public void EventCore_deletePublisherWithNullParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("publisher cannot equal null");

    eventFactory.deletePublisher((Publisher) null);
  }

  @Test
  public void EventCore_deletePublisherMultipleTimes_publisherDeletedOnce() {
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);
    Publisher publisher = eventFactory.createPublisher(channel);

    assertTrue(channel.getPublisherList().contains(publisher));
    assertEquals(channel, publisher.getChannel());
    eventFactory.deletePublisher(publisher);
    assertFalse(channel.getPublisherList().contains(publisher));
    assertEquals(expectedChannelName, publisher.getChannel().getName());
    assertFalse(publisher.getChannel().isDefined());
    eventFactory.deletePublisher(publisher);
    assertFalse(channel.getPublisherList().contains(publisher));
    assertEquals(expectedChannelName, publisher.getChannel().getName());
    assertFalse(publisher.getChannel().isDefined());
  }

  @Test
  @Ignore("not worked on")
  public void EventCore_deletePublisherAfterDeletingChannel_nothingHappens() {
    fail("not implemented");
  }

  @Test
  public void EventCore_deletePublisherWhenChannelOpen_unsupportedOperationExceptionOccurs() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("operation not permitted while channel is open");
    Publisher publisher = eventFactory.createPublisher(defaultTestChannel);
    eventFactory.openChannel(defaultTestChannel);

    eventFactory.deletePublisher(publisher);
  }

  @Test
  public void EventCore_deleteUnknownExternalPublisherImplementation_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("unknown publisher implementation");
    Publisher unknownExternalPublisherImplementation = new Publisher() {

      @Override
      public Channel getChannel() {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }

      @Override
      public void publish(EventDescription eventDescription) {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }

      @Override
      public void unpublish(EventDescription eventDescription) {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }

      @Override
      public void publish(EventDescription eventDescription, Subject subject) {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }

      @Override
      public void unpublish(EventDescription eventDescription, Subject subject) {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }
    };

    eventFactory.deletePublisher(unknownExternalPublisherImplementation);
  }

  @Test
  public void EventCore_deletePublisherWhenChannelClosed_publisherDeleted() {
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);
    Publisher publisher = eventFactory.createPublisher(channel);
    assertTrue(channel.getPublisherList().contains(publisher));
    assertEquals(channel, publisher.getChannel());

    eventFactory.deletePublisher(publisher);

    assertFalse(channel.getPublisherList().contains(publisher));
    assertEquals(expectedChannelName, publisher.getChannel().getName());
    assertFalse(publisher.getChannel().isDefined());
  }

  @Test
  public void EventCore_deleteEventDescriptionWithNullParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("eventDescription cannot equal null");

    eventFactory.deleteEventDescription((EventDescription) null);
  }

  @Test
  public void EventCore_deleteEventDescriptionMultipleTimes_eventDescriptionDeletedOnce() {
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);
    EventDescription eventDescription =
        eventFactory.createEventDescription(channel, "test.family", "test.event");

    assertTrue(channel.getEventDescriptionList().contains(eventDescription));
    assertEquals(channel, eventDescription.getChannel());
    eventFactory.deleteEventDescription(eventDescription);
    assertFalse(channel.getEventDescriptionList().contains(eventDescription));
    assertEquals(expectedChannelName, eventDescription.getChannel().getName());
    assertFalse(eventDescription.getChannel().isDefined());
    eventFactory.deleteEventDescription(eventDescription);
    eventFactory.deleteEventDescription(eventDescription);
    eventFactory.deleteEventDescription(eventDescription);
    assertEquals(expectedChannelName, eventDescription.getChannel().getName());
    assertFalse(eventDescription.getChannel().isDefined());
  }

  @Test
  @Ignore("not worked on")
  public void EventCore_deleteEventDescriptionAfterDeletingChannel_nothingHappens() {
    fail("not implemented");
  }

  @Test
  public void EventCore_deleteEventDescriptionWhenChannelOpen_unsupportedOperationException() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("operation not permitted while channel is open");
    EventDescription eventDescription =
        eventFactory.createEventDescription(defaultTestChannel, "test.family", "test.event");
    eventFactory.openChannel(defaultTestChannel);

    eventFactory.deleteEventDescription(eventDescription);
  }

  @Test
  public void EventCore_deleteUnknownExternalEventDescriptionImplementation_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("unknown eventDescription implementation");
    EventDescription unknownExternalEventDescriptionImplementation = new EventDescription() {

      @Override
      public int compareTo(EventDescription o) {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }

      @Override
      public Channel getChannel() {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }

      @Override
      public String getFamily() {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }

      @Override
      public String getName() {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }

      @Override
      public String getFullyQualifiedName() {
        throw new UnsupportedOperationException("operation not supported by stub.");
      }
    };

    eventFactory.deleteEventDescription(unknownExternalEventDescriptionImplementation);
  }

  @Test
  public void EventCore_deleteEventDescriptionWhenChannelClosed_eventDescriptionIsDeleted() {
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);
    EventDescription eventDescription =
        eventFactory.createEventDescription(channel, "test.family", "test.event");

    assertTrue(channel.getEventDescriptionList().contains(eventDescription));
    assertEquals(channel, eventDescription.getChannel());
    eventFactory.deleteEventDescription(eventDescription);
    assertFalse(channel.getEventDescriptionList().contains(eventDescription));
    assertEquals(expectedChannelName, eventDescription.getChannel().getName());
    assertFalse(eventDescription.getChannel().isDefined());
  }

  @Test
  public void EventCore_deleteChannelWithNullParameter_nullPointerExceptionIsThrown() {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("channel cannot equal null");

    eventFactory.deleteChannel((Channel) null);
  }

  @Test
  public void EventCore_deleteUnknownExternalChannelImplementation_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("unknown channel implementation");

    eventFactory.deleteChannel(createUnsupportedExternalChannelImplementation());
  }

  @Test
  public void EventCore_deleteOpenChannel_unsupportedOperationExceptionIsThrown() {
    thrown.expect(UnsupportedOperationException.class);
    thrown.expectMessage("operation not permitted while channel is open");
    Channel channel = eventFactory.createChannel("test.channel");
    eventFactory.openChannel(channel);

    eventFactory.deleteChannel(channel);
  }

  @Test
  public void EventCore_deleteChannelMultipleTimes_channelDeletedOnce() {
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);

    assertTrue(channel.isDefined());
    assertFalse(channel.isOpen());
    assertEquals(expectedChannelName, channel.getName());
    eventFactory.deleteChannel(channel);
    assertFalse(channel.isDefined());
    assertFalse(channel.isOpen());
    assertEquals(expectedChannelName, channel.getName());

    eventFactory.deleteChannel(channel);
    eventFactory.deleteChannel(channel);
    eventFactory.deleteChannel(channel);
    assertFalse(channel.isDefined());
    assertFalse(channel.isOpen());
    assertEquals(expectedChannelName, channel.getName());
  }

  @Test
  public void EventCore_openChannelThatIsDeleted_illegalArgumentExceptionIsThrown() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("cannot open a deleted channel");
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);
    eventFactory.deleteChannel(channel);

    eventFactory.openChannel(channel);
  }

  @Test
  public void EventCore_deleteEmptyClosedChannel_channelIsDeleted() {
    final String expectedChannelName = "test.channel";
    Channel channel = eventFactory.createChannel(expectedChannelName);

    assertTrue(channel.isDefined());
    assertFalse(channel.isOpen());
    assertEquals(expectedChannelName, channel.getName());
    eventFactory.deleteChannel(channel);
    assertFalse(channel.isDefined());
    assertFalse(channel.isOpen());
    assertEquals(expectedChannelName, channel.getName());
  }


  /**
   * 
   * Rough list of test scenarios:
   * 
   * delete channel that contains event descriptions. Event descriptions deleted, then channel
   * deleted.
   * 
   * delete channel that contains publisher. Publishers deleted then channel deleted.
   * 
   * delete channel that contains subscribers. Subscribers removed then channel deleted.
   * 
   * add an event description to a deleted channel. IllegalArgumentException is thrown.
   * 
   * add a publisher to a deleted channel. IllegalArgumentException is thrown.
   * 
   * add a subscriber to a deleted channel. IllegalArgumentException
   * 
   * 
   * publisher publishes event to deleted channel. UnsupportedOperationException
   * 
   * publisher unpublishes event to deleted channel. UnsupportedOperationException
   * 
   * publisher publishes event and subject to deleted channel. Unsupported ...
   * 
   * publisher unpublishes event and subject to deleted channel. Unsupported ...
   * 
   * deleted publisher publishes event to open channel. Unsupported ...
   * 
   * deleted publisher unpublishes event to open channel. Unsupported ...
   * 
   * deleted publisher publishes event and subject to open channel. Unsupported ...
   * 
   * deleted publisher unpublishes event and subject to open channel. Unsupported ...
   * 
   * publisher publishes deleted event to open channel. Unsupported ...
   * 
   * publisher unpublishes deleted event to open channel. Unsupported ...
   * 
   * publisher publishes deleted event and valid subject to open channel. Unsupported ...
   * 
   * publisher unpublishes deleted event and valid subject to open channel. Unsupported ...
   * 
   * publisher publishes / unpublishes unknown external implementation of EventDescription. What
   * happens? Not implemented but not related to delete functionality either.
   * 
   * remove, add, then remove a subscriber. Should work fine.
   * 
   */

  @Test
  @Ignore("not worked on")
  public void EventCore_getFullyQualifiedNameForDeletedEventDescription_unsupportedOperationExceptionIsThrown() {
    fail("not implemented");
  }
}
