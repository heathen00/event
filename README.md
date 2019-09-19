# EVENT


## Introduction

The Event project is an event processing system following a simple event publishing and subscribing model.
There are two phases to using the event system.  The first phase, setup, is where channels, events, publishers,
and subscribers are defined.  The end of the setup phase is marked by opening the channel.  The second
phase is the event processing phase.  In the event processing phase, publishers publish events to channels and
subscribers on that channel are notified about all channel events.


## Building And IDE

The Event project uses Gradle and follows standard Gradle conventions.  I use Eclipse with the Buildship
Gradle plugin.  Importing the Event project using Buildship's "Import existing Gradle project" sets up the
project with no additional work.


## Testing

All testing is automated and divided into two categories, "unit tests" and "acceptance tests".  All unit tests
and testing utilities are in the "src/test/" folder.  All acceptance tests, that is automated tests that prove
the project implements the user stories' acceptance criteria, are in the "src/acceptance_test/" folder.  An
easy rule of thumb to make a distinction between these two sets of automated tests is that the acceptance tests
test the externally visible or "published" behaviour of the software system, whereas the unit tests focus on
testing internal or "implementation" behaviour of the software system.  I tend to favour acceptance tests over
unit tests so if you run test coverage reports you'll likely find that testing coverage is not ideal.

If you are looking for specific client use cases, you will see them in the acceptance tests. 


## Design

WARNING: If the code and these design notes differ, the code is right.

I see the use of this API from three different roles:

Initializer: The person who creates the Event and Channel definitions.

Publisher: The person who sends the predefined events created by the EventInitializer to a
specified Channel.

Subscriber: The person who receives messages sent on a channel.

How the Event model concepts related to one another:

Channel: A communications Channel of interest between like minded Publishers and Subscribers. It
is identified by a name (String) and can have any number of Publishers and Subscribers
subscribed. It can also have any number of Events defined for it. Events from one Channel cannot
be sent to another Channel. Sending the same Event over the Channel does nothing.

Event: A "happening" of interest to like minded Publishers and Subscribers. It is only defined
within a given Channel. The Event is defined based on a Family and Name. The Family accumulates
related Events together (I don't want to use the name Group because that implies incorrectly that
subscribers and not the events are being grouped). The Name is the unique name for that event
within the given Channel and Family. Thus, two different Events may have the same Family and Name
if they belong to different Channels. And two different Events may have the same Name if they are
in the same Channel BUT different Families. Optionally, an Event may also have a Subject that
identifies that a given Event occurred with respect to that Subject. Thus if the same Event is
sent on the Channel twice, but the Event specifies two distinct Subjects then the Events are
treated as separate Events within that channel. Events must be initialized before they can be
processed in a Channel, however Subjects are specified when the Event is published.

Family: An accumulation of related Event instances. The idea comes from the HTTP protocol where
the HTTP response codes are subdivided into 5 Families as identified by the first digit of the
HTTP response code, so (2XX for success cases, 5XX for internal server errors, 4XX for client
errors, etc).

Name: A unique identifier for an Event within the context of the Event's Channel and Family. In
the absence of a Subject registered with the event, the Name is the only attribute that uniquely
identifies an Event on a Channel for a given Family. And the Subject differentiates two Events
that have the same Channel, Family, and Name assuming the Subjects are themselves unique.

Subject: Events can optionally be published including a Subject that the event is about.

Subscriber: A Subscriber receives Events published on a Channel. A Subscriber can only register
to a single Channel. Multiple unique Subscribers can register to the same Channel. All
Subscribers receive all Events published on the Channel. Optionally, a Subscriber can request
all Events currently published on its Channel.

Publisher: A Publisher publishes and unpublishes Events to a Channel. A Publisher can only
register with a single Channel. Multiple Publishers can register to the same Channel.

