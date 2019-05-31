package com.ht.wfp3.message;

import java.util.List;

/**
 * The message system represents the system used for messaging. This is the starting point for
 * clients that intend to use the messaging system.
 * 
 * @author nickl
 *
 */
public interface MessageSystem {
  public interface Config {

    Localization getLocalization();

  }

  static MessageSystem createMessageSystem() {
    return MessageSystemImp.SINGLETON;
  }

  MessageFactory createMessageFactory();

  Messenger createMessenger();

  List<Priority> getPriorityList();

  List<Topic> getTopicList();

  List<Message> getMessageList(Topic topic);

  Config getConfig();
}
