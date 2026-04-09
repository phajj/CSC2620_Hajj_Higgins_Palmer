package Utilities;

import java.util.ArrayList;
import java.util.HashMap;

import client.Message;

/**
 * This class tracks user memberships to different chats.
 * It also stores a the chat history in memory for the gui to load when
 * switching between chats
 * Handles adding/removing groups and adding/removing users from groups
 *
 * @author Jackson Higgins
 */
public class GroupManager {
  private HashMap<String, ArrayList<String>> groupMembers;
  private HashMap<String, ArrayList<Message>> groupMessages;

  public GroupManager() {
    this.groupMembers = new HashMap<>();
    this.groupMessages = new HashMap<>();
  }

  /**
   * Create a new user group
   *
   * @param group The group name
   */
  public void addGroup(String group) {
    groupMembers.put(group, new ArrayList<>());
    groupMessages.put(group, new ArrayList<>());
  }

  /**
   * Adds a user to a group.
   *
   * @param group The group name
   * @param user  The username to add
   */
  public void addUser(String group, String user) {
    if (groupMembers.containsKey(group)) {
      groupMembers.get(group).add(user);
    }
  }

  /**
   * Returns whether a group exists.
   *
   * @param group The group name
   * @return true if the group exists
   */
  public boolean hasGroup(String group) {
    return groupMembers.containsKey(group);
  }

  /**
   * Creates the group if it does not already exist.
   *
   * @param group The group name
   */
  public void ensureGroup(String group) {
    if (!hasGroup(group)) {
      addGroup(group);
    }
  }

  /**
   * Remove a group.
   * Should only be used if there are no users in the group
   *
   * @param group The group to remove
   */
  public void removeGroup(String group) {
    groupMembers.remove(group);
  }

  /**
   * Retuns a list of members of a group
   *
   * @param group The group to get the member list of
   * @return The list of members in the group
   */
  public ArrayList<String> getGroupMembers(String group) {
    return groupMembers.get(group);
  }

  /**
   * Returns a list of messages associated with a group
   *
   * @param group The group the messages belong to
   * @return The messages belonging to the group
   */
  public ArrayList<Message> getGroupMessages(String group) {
    return groupMessages.get(group);
  }

  public void addMessage(String group, Message message) {
    groupMessages.get(group).add(message);
  }
}
