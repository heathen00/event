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


## START: OLD

The following description is just a set of guidelines for how I envision this subsystem to work.  Once you
implement the system, then delete this section since the software should be descriptive enough on its own.

This software system consists of a number of subsystems.  These subsystems require a common mechanism for reporting
status messages while they perform their tasks in such a way that is not as disruptive as throwing an exception.
These status messages may not be reported immediately as they may delayed and presented in a final report document
after the subsystem completes its task.

User stories require that the parser is supported at the UNIX command line.  This implies that the generated
messages are compatible with the standard UNIX command line.  Thus, the task name, priority name, and status
message must fit on a single standard UNIX terminal (80 characters at 12pts).  Thus, max lengths of:
   * task name: 10 characters
   * priority name: 10 characters
   * message: 50 characters
   * additional whitespace and formatting: 10 characters
   * overall line length (total of above): 80 characters

Alternatively, you may have an optional auto-wrap or simply ignore the line length limit.  I've certainly used
UNIX command line tools whose status messages are not limited to the standard terminal width.  But it would be
easier to simply start with the defined limit.  If the limits are implemented to be flexible, as they should be,
then they can easily be tweaked later.

Following are some relevant, high level usage scenarios.

Scenario: Remove status message during OBJ data model validation.
   1. Start OBJ data model validation.
   1. Create an empty OBJ validation report.
   1. Initialize status of GeoVertex statement to "not used" in validation report.
   1. Perform OBJ document validation task.
   1. Validation task determines GeoVertex statement is used by a Face statement.
   1. Remove "not used" status of GeoVertex statement.

Scenario: Parsing OBJ file requires linking status messages
   1. Parse OBJ file.
   1. Find GeoVertex that is incorrectly defined.
   1. Complete parsing.
   1. Report that "GeoVertex" statement at line number "XXX" is incorrectly defined because of specific reason.

In the above scenario, the strings in quotes would likely need to be parameterized.  Defining a different status
event for every statement may be too cumbersome.  What about "specific reason"?  To be more generic, you could
specify the character number, so it would be 'at character number "YYY"'.  At any rate, you'll likely want to
start with simple message reporting first, then enhance it with these features.

Scenario: Message system bootstrapping
   1. Message system is started with the client(s) specifying the configuration for their event messages.
   1. Message system initializes, possibly hard-coded, "primordial" messages for itself for messages related to the
      bootstrapping process.
   1. Message system reads the client configuration specified to it.
   1. Message system may generate bootstrapping messages if the proper bootstrapping events occur as set in its
      bootsrapping message configuration.

Since tasks, priorities, and status messages are defined during the reporting bootstrapping process, they can all
be checked to ensure they conform to the text width limitations, etc.  Alternatively, it may be sufficient for the
system to throw exceptions while bootstrapping if a non-recoverable error occurs.

Scenario: Validation requires report generation
   1. Parsing of OBJ file has completed and created OBJ document model successfully.
   1. Validation subsystem walks across OBJ document model to ensure valid relationships between statements defined
      on OBJ document model.
   1. Validation subsystem generates event messages based on information it determines while analyzing the OBJ
      document model and saves these event messages in a validation report.
   1. Validation subsystem completes validation of the OBJ document model.
   1. Validation subsystem publishes results of validation process in validation report.

It was mentioned above that a report is generated.  This will be a simple model, and it will be the responsibility
of some as of yet undetermined subsystem(s) to format the messages according to those subsystems' requirements.
However, tools for activities like sorting based on priority/message/etc. should be provided.  At the very least,
a default natural ordering should be provided.

The messages consist of the following data:
   * Unique message identifier
   * Topic
   * Description
   * Priority   
   * Associated object?: The message is optionally associated with an object from another subsystem.  Alternative 1:
        The tracking is performed by the client system.  As long as the message properly implements equals, hashcode,
        and compareTo, then the tracking can be done in standard maps, lists, etc.  Alternative 2: Do not implement
        removing messages at all, and just assume that message reports will be regenerated if the system state
        changes.  You should delay message removal as long as possible to see if it is actually needed or not.  If
        it is, then move to "alternative 1".

For the description, the intention is just to use "sprintf", so maybe read up about it, then figure out a simple
way to construct a formatted message that passing in all the required parameters.

All domain objects are implemented as immutable value objects.  The event handling would likely best be implemented
using a producer/consumer model.  Thus, if logging of events is desired, then a logging consumer could be
implemented that immediately logs events.  If a validation report generation is required, then the consumer would
generate the report by appending the messages to an appropriate, simple data structure.

It may be necessary to provide the ability to disable / enable events for scenarios like a given event was
incorrectly defined (or something) and should not be used anymore.
     
