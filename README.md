# EVENT

WARNING: Work in Progress.  This document needs to be cleaned up.

This is a simple event processing system that I pulled from a subsystem in my private "WF Parser Version 3"
ongoing project.  The original history is preserved, but I'll be deleting much of the content not related to
the event system.


WARN: I renamed the "message" subsystem to "event" but did not update the notes in this section.  The subsystem
is more accurately implemented as an event system and follows a simple event driven architecture.

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


#### Subsystems: Event Subsystem: Core

The Purpose of the event subsystem, overall, is to publish and process events.  These events are
identified by unique IDs (UID).  The purpose of the core message subsystem is to implement the core functionality
with as few external dependencies as possible.  In particular, the core event subsystem is completely unaware
of the localization functionality.  It will require the UID subsystem.  The core event subsystem responsibilities
can be divided into two top level categories: defining events and processing events.  Although not enforced,
defining and processing events can occur at the same time as long as a message being processed has already been
defined.  However, it would likely be more useful and efficient if all messages are created at initialization time
and then then processed afterwards.  The message processing can be further subdivided to publishing and consuming
messages.  In terms of architecture, the message core subsystem will be an event driven architecture.  For the message
creation, I want to follow the decorator pattern to separate concerns between:
  * input data validation.
  * message caching for reuse.
  * message creation.


     
HERE:
   * DON'T USE CHECKED EXCEPTIONS EVER!!  It messes up the interface.
   * You'll need to implement an "undefined" Priority, Topic, Description, and Message.
   * When mapping the "undefined" entities to localization configuration, you should not need to define any
     configuration, not for the root locale nor any other locale.  Not for any new resource bundle being added.
   * When implementing the message system, ensure that it continues to function even if the localization is not
     working 100% properly, at run time.  Remember, the objective is to decouple the message subsystem from the
     localization as much as possible.  The message system just passes around codified messages, like UIDs for
     messages.
   * I think the message subsystem's current design is deficient and irrecoverable.  There are
     a number of problems.  First, it contains concepts that are not relevant to messaging
     (localization, constraints, ...).  And second, it is too coupled to these subsystems.  The
     messaging system should JUST have messages that consist of UIDs to priorities, topics, and
     descriptions.  That would be the CORE of the system (...message.core).  Then, it would have
     its own internally defined messages for message subsystem events (errors, etc).  And on top
     of that, it would be loosely coupled to the constraints, localization, etc., solutions.  So,
     I will cease working on the current solution and restart with a new one with these additional
     architectural considerations.
   * As such, I will see how much of the current implementation I can recover and fit into the "core"
     message subsystem.  This subsystem should be completely decoupled from both the localization
     and the guard/validation.
     
Copy and Paste from the EventCoreAcceptanceTests.java module:

I see the use of this API from three different roles:

Initializer: The person who creates the Event and Channel definitions.

Publisher: The person who sends the predefined events created by the EventInitializer to a
specified Channel.

Subscriber: The person who receives messages sent on a channel.


And here are the Event subsystem model concepts and how they relate to one another:

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

Subscriber: A Subscriber receives Events published on a Channel. A Subscriber can only register
to a single Channel. Multiple Subscribers can register to the same Channel. All Subscribers
receive all Events published on the Channel. Optionally, a Subscriber can request all Events
currently published on its Channel.

Publisher: A Publisher publishes and unpublishes Events to a Channel. A Publisher can only
register with a single Channel. Multiple Publishers can register to the same Channel.


Implementation Notes:

Subscribers are interfaces that must be implemented and registered.

Subjects are interfaces that must be implemented.

There must be some standard means of comparing Subjects. I will also define an interface that is
called "NaturalOrder" that accumulates the "equals", "hashCode", and "compareTo" interfaces since
I like to keep them all consistent, anyway. That should make testing easier, since I could just
send in "NaturalOrder" instances into an "AssertNaturalOrder" instance to ensure the contract is
respected for all defined test scenarios.

To be consistent with the Localizer subsystem that you will be integrating with, you should use
the ID subsystem.

For the factory, ensure you separate out the creation, the validationm and the caching.

For initialization, use ONLY the actual instances of Channel, Event, when creating the instances.
However, for processing requests, use ONLY the UIDs.
     

  
### Subsystems: All Subsystems In General

Rough:
   * You should go through the implementation and mark method parameters as final to indicate that none of
     them will be modified.
   * You should also just look through the code for instances of duplication and remove them using
     inheritance, pulling out duplicate code into its own class and use composition, instead.
   * You should consider adding yet another layer of testing: integration, which tests to ensure
     that independent subsystems of the whole solution work together as expected.  The testing
     should not be too extensive to reduce redundancy and should only indicate that ... what?
     Think about what the integration testing should prove in more concrete and limited terms.
     
