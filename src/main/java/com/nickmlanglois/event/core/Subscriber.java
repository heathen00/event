package com.nickmlanglois.event.core;

/**
 * A Subscriber receives Events published on a Channel. A Subscriber can only register to a single
 * Channel. Multiple unique Subscribers can register to the same Channel. All Subscribers receive
 * all Events published on the Channel. Optionally, a Subscriber can request a resend of all Events
 * currently published on its Channel.
 * 
 * @author nickl
 *
 */
public abstract class Subscriber implements SubscriberPublished<Subscriber> {
  private ChannelInternal channelInternal;

  /**
   * The Subscriber's unique name. It must be a non-empty String. It should be immutable.
   * 
   * @return The Subscriber's unique name.
   */
  @Override
  public abstract String getName();

  final void setChannel(Channel channel) {
    channelInternal = (ChannelInternal) channel;
  }

  /**
   * The Channel this Subscriber was added to. Subscribers can only belong to one Channel at a time.
   */
  @Override
  public final Channel getChannel() {
    return channelInternal;
  }

  /**
   * The Event System will call this method whenever a Publisher publishes an Event in this Channel.
   * The default implementation does nothing. Override this method to receive published Events in
   * this Channel.
   * 
   * @param event The Event published in this Channel.
   */
  @Override
  public void processPublishEventCallback(Event event) {}

  /**
   * The Event System will call this method whenever a Publisher unpublishes an Event in this
   * Channel. The default implementation does nothing. Override this method to receive published
   * Events in this Channel.
   * 
   * @param The Event unpublished in this Channel.
   */
  @Override
  public void processUnpublishEventCallback(Event event) {}

  /**
   * Get the external Subscriber implementation that was added to this Channel. This is an
   * implementation detail that can safely be ignored. Internally, the Event System wraps all
   * external Subscribers to ensure safety and stability. The implementation in this external
   * Subscriber base class simply returns "this". The internal Subscriber implementation returns the
   * wrapped, external Subscriber instance. This is an abstraction leak and I will remove it if I
   * can find another solution that does not require this method nor any other leaked implementation
   * details.
   */
  @Override
  public final Subscriber getExternalSubscriber() {
    return this;
  }

  /**
   * Request that the Channel resend all currently published Events to this Subscriber. Note, this
   * simply sends those Events that are currently published. It does not replay all publish and
   * unpublish Events that have occurred since the Channel was opened. All published events will be
   * sent to this Subscriber's processPublishedEventCallback() method.
   */
  @Override
  public final void resendAllCurrentPublishedEvents() {
    channelInternal.resendAllCurrentPublishedEventsToExternalSubscriber(this);
  }
}