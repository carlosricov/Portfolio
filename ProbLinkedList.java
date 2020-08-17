// Carlos Ricoveri

import java.util.*;
import java.io.*;

// Node class modified to be generic.
class Node<T extends Comparable<T>>
{
  T data;
  int height;

  // The levels for each node will be stored in an arraylist.
  ArrayList<Node<T>> ref = new ArrayList<>();

  // Constructor used to create an initial head node.
  Node(int height)
  {
    // The head of a SkipList does not store data.
    this.data = null;

    // Height of the head node is updated.
    this.height = height;

    // Crucial to initialize the references to null for a new Skiplist.
    for (int i = 0; i < height; i++)
    {
      ref.add(null);
    }
  }

  // Constructor used to create new nodes within the Skiplist.
  Node(T data, int height)
  {
    // This new node's data is updated.
    this.data = data;
    this.height = height;

    // Reference initialization.
    for (int i = 0; i < height; i++)
    {
      ref.add(null);
    }
  }

  // Function that returns the data that's stored in the curent node.
  public T value()
  {
    return this.data;
  }

  // Function that returns the height of the current node.
  public int height()
  {
    return this.height;
  }

  // Function that returns the reference at a particular level.
  public Node<T> next(int level)
  {
    // Out of bound checks.
    if (level < 0 || level >= height)
      return null;

    return ref.get(level);
  }

  // Function that updates the reference at a level in this node.
  public void setNext(int level, Node<T> node)
  {
    // Out of bound check.
    if (level < 0 || level >= height)
      return;

    ref.set(level, node);
  }

  // Function that increments the height of the node by one. Levels must be
  // updated too.
  public void grow()
  {
    this.height++;
    ref.add(null);
  }

  // If the max height of the Skiplist is increased, there's a 50% chance of
  // height increase for all nodes.
  public void maybeGrow()
  {
    // Range: [0,1)
    double rand = Math.random();

    // 50% chance of height increase.
    if (rand >= 0.5)
    {
      this.height++;
      ref.add(null);
    }
  }

  // Function that trims the node to the specified height.
  public void trim(int height)
  {
    // Out of bound check.
    if (height < 0 || height >= this.height)
      return;

    // Set the top most references to null.
    for (int i = this.height; i > height; i--)
    {
      ref.remove(this.height - 1);
      this.height--;
    }
  }
}

// Class that creates a Skiplist data structure. Modified to be generic. Since
// it's generic and we're comparing objects, it must extend comparable.
public class ProbLinkedList<T extends Comparable<T>>
{
  int maxHeight, size = 0;

  // This boolean variable indicates whether or not the skiplist was created
  // with a designated height.
  boolean manuallyCreated;

  Node<T> head;

  // Constructor that initializes an empty Skiplist with a default height of 1.
  SkipList()
  {
    // The list will begin with a default height of 1.
    maxHeight = 1;

    manuallyCreated = false;

    head = new Node<T>(maxHeight);
  }

  // Constructor that initializes the Skiplist with the specified height.
  SkipList(int height)
  {
    // Defaults to the minimum height.
    if (height < 1)
      maxHeight = 1;

    manuallyCreated = true;
    this.maxHeight = height;
    head = new Node<T>(maxHeight);
  }

  // Function that returns the size of the Skiplist.
  public int size()
  {
    return this.size;
  }

  // Function that returns the max height of the Skiplist.
  public int height()
  {
    return this.maxHeight;
  }

  // Function that returns the head of the Skiplist.
  public Node<T> head()
  {
    return head;
  }

  // Helper function that returns the max height of a Skiplist with n nodes.
  private static int getMaxHeight(int n)
  {
    // Converting to log base 2.
    double height = (Math.log(n)/Math.log(2));

    // Ternary operator is used in the case that n = 1.
    return (n == 1) ? 1 : (int)Math.ceil(height);
  }

  // Helper function that generates a probabilistic height for a node.
  private static int generateRandomHeight(int maxHeight)
  {
    double rand = Math.random();

    // Start off with the default height.
    int height = 1;

    // 50% for each iteration, abiding to the max height.
    while (rand > 0.5 && height < maxHeight)
    {
      height++;
      rand = Math.random();
    }

    return height;
  }

  // Nonstatic method that will grow the skiplist when the max possible height
  // is updated.
  private void growSkipList()
  {
    // Keep track of the old max height.
    int oldHeadHeight = head.height();

    // Two node references will be necessary here. One to traverse the top most
    // level of the skiplist, and another to update previous references.
    Node<T> prevSpot = head;
    Node<T> spot;

    // Indicators if the height of the node grew.
    int spotHeight, newHeight;

    // The height of the head will always increment when growing the skiplist.
    head.grow();
    // Update spot to the next node that was of the old max height.
    spot = prevSpot.next(oldHeadHeight - 1);

    // List traversal.
    while (spot != null)
    {
      spotHeight = spot.height();

      // Every node (besides the head) with a height of the old max height has
      // a 50% chance of growing.
      if (spotHeight == oldHeadHeight)
        spot.maybeGrow();

      newHeight = spot.height();

      // If the height of the node has changed it must mean it grew.
      if (newHeight - spotHeight == 1)
      {
        // References are updated.
        prevSpot.setNext(prevSpot.height() - 1, spot);

        // Traversal continues.
        prevSpot = spot;
        spot = spot.next(spot.height() - 2);
      }
      else
      {
        spot = spot.next(spot.height() - 1);
      }
    }

    // Max height of the list is increased due to the growth.
    this.maxHeight++;
  }

  // If necessary, the max height of the skip list will decrease with deletion.
  // Any node that's left taller than the new height must be trimmed.
  private void trimSkipList()
  {
    // Stores the logn conventional height of the skiplist.
    int expectedHeight = getMaxHeight(this.size);

    // In the event where the skiplist was created manually with a set height,
    // we must revert back to a conventional height. In either case, max height
    // decreases.
    if (manuallyCreated && this.maxHeight > expectedHeight)
      this.maxHeight = expectedHeight;
    else
      this.maxHeight--;

    int newHeight = this.maxHeight;

    // Two node references will be necessary here. One to traverse the top most
    // level of the skiplist, and another to update previous references.
    Node<T> prevSpot = head;
    Node<T> spot = head.next(newHeight);

    // Start off by trimming the head.
    head.trim(newHeight);

    // List traversal.
    while (spot != null)
    {
      // Every node (besides the head) with a height of the old max height has
      // must be trimmed. References must be updated first.
      prevSpot = spot;
      spot = spot.next(newHeight);
      prevSpot.trim(newHeight);
    }
  }

  // Function that inserts a node into the Skiplist in O(logn) fashion.
  public void insert(T data)
  {
    // Keeps track of the curent maxHeight.
    int oldMaxHeight = this.maxHeight;

    // Probabilisitic generation of a node's height.
    int nodeHeight = generateRandomHeight(this.maxHeight);

    // A red dot is a point where we drop down one level in a node. Keeping
    // track of these points is important for updating references.
    ArrayList<Node<T>> redDot = new ArrayList<Node<T>>();

    // Node to be added is created with the generated height.
    Node<T> node = new Node<T>(data, nodeHeight);

    // Temporary node for traversal within the list.
    Node<T> tempNode = head;

    // Traversal begins at the head of the skiplist which will always have the
    // max height of the list.
    for (int i = head.height() - 1; i >= 0; i--)
    {
      // In the case where the list is empty or we've reached the last node,
      // its last reference should point to null.
      while (tempNode != null)
      {
        // A null reference within the list causes us to drop down a level.
        if (tempNode.next(i) == null)
        {
          // If the index could be a valid index within the node-to-be-added,
          // then it will block old references--therefore we must keep track.
          if (i < node.height())
          {
            redDot.add(tempNode);
          }

          // Break out of the while loop to complete the level drop.
          break;
        }
        // In the case that the level points to a value bigger than or equal to
        // what we're inserting, we drop down a level.
        else if (tempNode.next(i).value().compareTo(data) >= 0)
        {
          // References must be updated after insertion.
          if (i < node.height())
          {
            redDot.add(tempNode);
          }

          break;
        }
        // In the case that the level points to a value smaller than what we're
        // inserting, then we proceed to the node containing that value.
        else if (tempNode.next(i).value().compareTo(data) < 0)
        {
          // Note: no red dot is added here becasue we don't drop down a level.
          tempNode = tempNode.next(i);
          continue;
        }
      }
    }

    // Counter variable to access appropiate index.
    int cnt = redDot.size() - 1 ;

    // This for loop updates the next references of the new node.
    for (int j = 0; j < redDot.size(); j++)
    {
      node.setNext(j, redDot.get(cnt).next(j));
      cnt--;
    }

    // Counter is restored.
    cnt = redDot.size() - 1 ;

    // This for loop takes care of updating the references for each node
    // in the redDot arraylist--if it's original reference is going to
    // be blocked by the new node.
    for (int k = 0; k < redDot.size(); k++)
    {
        redDot.get(k).setNext(cnt, node);
        cnt--;
    }

    // Empty list check.
    if (size == 0)
      head.setNext(0, node);

    // Insertion causes the size of the skiplist to increment by one.
    this.size++;

    // After insertion it's important to check if the max possible height for
    // the list has changed at all.
    int newMaxHeight = getMaxHeight(this.size);

    // If the max possible height changes, the list must grow.
    if ((newMaxHeight - oldMaxHeight) > 0)
      growSkipList();
    else
      return;
  }

  // Insert function that allows the custom input of node heights.
  public void insert(T data, int height)
  {
    // Keeps track of the curent maxHeight.
    int oldMaxHeight = this.maxHeight;

    // Non-probabilisitic generation of a node's height.
    int nodeHeight = height;

    // Level drop references.
    ArrayList<Node<T>> redDot = new ArrayList<Node<T>>();

    // Node to be added is created with the set height.
    Node<T> node = new Node<T>(data, nodeHeight);

    // Temporary node for traversal within the list.
    Node<T> tempNode = head;

    // Traversal begins at the head and stops once we've dropped from the
    // lowest level.
    for (int i = head.height() - 1; i >= 0; i--)
    {
      while (tempNode != null)
      {
        // A null reference within the list causes us to drop down a level.
        if (tempNode.next(i) == null)
        {
          // If the index could be a valid index within the node-to-be-added,
          // then it will block old references--therefore we must keep track.
          if (i < node.height())
          {
            redDot.add(tempNode);
          }

          // Break out of the while loop to complete the level drop.
          break;
        }
        // In the case that the level points to a value bigger than or equal to
        // what we're inserting, we drop down a level.
        else if (tempNode.next(i).value().compareTo(data) >= 0)
        {
          // References must be updated after insertion.
          if (i < node.height())
          {
            redDot.add(tempNode);
          }

          break;
        }
        // In the case that the level points to a value smaller than what we're
        // inserting, then we proceed to the node containing that value.
        else if (tempNode.next(i).value().compareTo(data) < 0)
        {
          // No level drop.
          tempNode = tempNode.next(i);
          continue;
        }
      }
    }

    // Counter variable.
    int cnt = redDot.size() - 1 ;

    // New node referece update.
    for (int j = 0; j < redDot.size(); j++)
    {
      node.setNext(j, redDot.get(cnt).next(j));
      cnt--;
    }

    // Counter variable is restored.
    cnt = redDot.size() - 1 ;

    // Updates level-drop node reference.
    for (int k = 0; k < redDot.size(); k++)
    {
      redDot.get(k).setNext(cnt, node);
      cnt--;
    }

    // Empty list check.
    if (size == 0)
      head.setNext(0, node);

    // Insertion causes the size of the skiplist to increment by one.
    this.size++;

    // After insertion it's important to check if the max possible height for
    // the list has changed at all.
    int newMaxHeight = getMaxHeight(this.size);

    // If the max possible height changes, the list must grow.
    if ((newMaxHeight - oldMaxHeight) > 0)
      growSkipList();
    else
      return;
  }

  // Method that deletes a node from the list in O(logn) time.
  public void delete(T data)
  {
    // Initial check to see if the data is in the list.
    if (!contains(data))
      return;

    // The spot to be deleted is obtained.
    Node<T> spot = get(data);

    // Keeps track of the curent maxHeight.
    int oldMaxHeight = this.maxHeight;

    // Level-drop references.
    ArrayList<Node<T>> redDot = new ArrayList<Node<T>>();

    // Temporary node for traversal within the list.
    Node<T> tempNode = head;

    // Same procedure as insertion. Start at the head; drop when needed.
    for (int i = head.height() - 1; i >= 0; i--)
    {
      while (tempNode != null)
      {
        // A null reference automatically causes us to drop down a level.
        if (tempNode.next(i) == null)
        {
          break;
        }
        // A value of zero indicates we have found the node.
        else if (tempNode.next(i).value().compareTo(data) == 0)
        {
          // We must know where to point from after deletion.
          if (i < spot.height())
          {
            redDot.add(tempNode);
          }

          // Break out of the while loop to complete the level drop.
          break;
        }
        // Bigger values cause us to drop a level.
        else if (tempNode.next(i).value().compareTo(data) > 0)
        {
          break;
        }
        // In the case that the level points to a value smaller than what we're
        // inserting, then we proceed to the node containing that value.
        else if (tempNode.next(i).value().compareTo(data) < 0)
        {
          // Note: no red dot is added here becasue we don't drop down a level.
          tempNode = tempNode.next(i);
          continue;
        }
      }
    }

    // Counter variable.
    int cnt = redDot.size() - 1 ;

    // This for loop updates the next references of the redDot nodes.
    for (int j = 0; j < redDot.size(); j++)
    {
      redDot.get(cnt).setNext(j, spot.next(j));
      cnt--;
    }

    // Deletion causes the size of the skiplist to decrease by one.
    this.size--;

    // After deletion it's important to check if the max possible height for
    // the list has changed at all.
    int newMaxHeight = getMaxHeight(this.size);

    // If the max possible height changes, the list must shrink.
    if ((newMaxHeight - oldMaxHeight) < 0)
      trimSkipList();
    else
      return;
  }

  // Function that indicates if a node is present in the list.
  public boolean contains(T data)
  {
    Node<T> tempNode = head;

    // Start at the head.
    for (int i = head.height() - 1; i >= 0; i--)
    {
      while (tempNode != null)
      {
        // Null reference drop.
        if (tempNode.next(i) == null)
        {
          break;
        }
        // Bigger value drop.
        else if (tempNode.next(i).value().compareTo(data) > 0)
          break;
        // In the case that the level points to a value smaller than what we're
        // inserting, then we proceed to the node containing that value.
        else if (tempNode.next(i).value().compareTo(data) < 0)
        {
          tempNode = tempNode.next(i);
          continue;
        }
        // Bingo--we have found the node.
        else if (tempNode.next(i).value().compareTo(data) == 0)
        {
          return true;
        }
      }
    }

    // If we traverse the entire list and no value is found, automatically
    // return false.
    return false;
  }

  // Function that returns the node containing the data.
  public Node<T> get(T data)
  {
    Node<T> tempNode = head;

    // Start at the head.
    for (int i = head.height() - 1; i >= 0; i--)
    {
      while (tempNode != null)
      {
        // Null reference drop.
        if (tempNode.next(i) == null)
        {
          break;
        }
        // Bigger value drop.
        else if (tempNode.next(i).value().compareTo(data) > 0)
          break;
        // Smaller value check.
        else if (tempNode.next(i).value().compareTo(data) < 0)
        {
          tempNode = tempNode.next(i);
          continue;
        }
        // We have found the node!
        else if (tempNode.next(i).value().compareTo(data) == 0)
        {
          return tempNode.next(i);
        }
      }
    }

    // No node found.
    return null;
  }
}
