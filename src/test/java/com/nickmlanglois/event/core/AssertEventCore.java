package com.nickmlanglois.event.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import com.nickmlanglois.event.core.Channel;
import com.nickmlanglois.event.core.Event;
import com.nickmlanglois.event.core.EventDescription;
import com.nickmlanglois.event.core.Publisher;
import com.nickmlanglois.event.core.Subject;
import com.nickmlanglois.event.core.Subscriber;

public class AssertEventCore {
  public static AssertEventCore createAssertEventCore() {
    return new AssertEventCore();
  }

  public void assertExpectedChannel(String expectedChannelName, boolean expectedIsOpen,
      List<EventDescription> expectedEventDescriptionList, List<Publisher> expectedPublisherList,
      List<Subscriber> expectedSubscriberList, Channel channel) {
    assertNotNull(channel);
    assertEquals(expectedChannelName, channel.getName());
    assertEquals(expectedIsOpen, channel.isOpen());
    assertEquals(expectedEventDescriptionList, channel.getEventDescriptionList());
    assertEquals(expectedPublisherList, channel.getPublisherList());
    assertEquals(expectedSubscriberList, channel.getSubscriberList());
  }

  public void assertExpectedEventDescription(Channel expectedChannel, String expectedfamily,
      String expectedEventName, EventDescription eventDescription) {
    assertNotNull(eventDescription);
    assertEquals(expectedChannel, eventDescription.getChannel());
    assertEquals(expectedfamily, eventDescription.getFamily());
    assertEquals(expectedEventName, eventDescription.getName());
    assertEquals(String.join(".", eventDescription.getChannel().getName(),
        eventDescription.getFamily(), eventDescription.getName()),
        eventDescription.getFullyQualifiedName());
  }

  public void assertExpectedEventDescription(EventDescription expectedEventDescription,
      EventDescription eventDescription) {
    assertNotNull(eventDescription);
    assertExpectedEventDescription(expectedEventDescription.getChannel(),
        expectedEventDescription.getFamily(), expectedEventDescription.getName(), eventDescription);
  }

  public void assertExpectedEvent(EventDescription expectedEventDescription,
      Subject expectedSubject, Event event) {
    assertNotNull(event);
    assertExpectedEventDescription(expectedEventDescription, event.getEventDescription());
    assertExpectedSubject(expectedSubject, event.getSubject());
  }

  public void assertExpectedEvent(EventDescription expectedEventDescription, Event event) {
    assertNotNull(event);
    assertExpectedEventDescription(expectedEventDescription, event.getEventDescription());
    assertExpectedSubject(event.getSubject());
  }

  public void assertExpectedSubject(Subject subject) {
    assertNotNull(subject);
  }

  public void assertExpectedSubject(Subject expectedSubject, Subject subject) {
    assertNotNull(subject);
    assertEquals(expectedSubject, subject);
  }

  public void assertExpectedPublisher(Channel expectedChannel, Publisher publisher) {
    assertNotNull(publisher);
    assertEquals(expectedChannel, publisher.getChannel());
  }
}
