// Carlos Ricoveri

// The generic implementation was modified from LinkedList.java and
import java.io.*;
import java.util.*;

// Node class has been modified to be generic. It takes data of any type
// (AnyType).
class Node<AnyType>
{
	// Nodes for the left and right subtrees of the BST have been modified
	// to be generic.
	AnyType data;
	Node<AnyType> left, right;

	// Generic data is passed to the node and the data for the node is then
	// updated.
	Node(AnyType data)
	{
		this.data = data;
	}
}

// BST class that has been modified to be generic. The generic type extends
// comparable so that any of the types passed to this code can call the
// compareTo() method.
public class GenericBST<AnyType extends Comparable<AnyType>>
{
	// Root is set to be a generic node.
	private Node<AnyType> root;

	// Void method that takes a generic parameter and updates the tree with a
	// new insertion.
	public void insert(AnyType data)
	{
		root = insert(root, data);
	}

	// Generic node method that takes a generic root and data as its
	// parameter. The method inserts the passed data into the tree adhering
	// to the ordering property of BST's. Returns the root of the tree.
	private Node<AnyType> insert(Node<AnyType> root, AnyType data)
	{
		// Checks to see if the root is null. If it is, a new generic node
		// is created. If it isn't, then the data is compared to the root's data
		// using the compareTo() method.
		if (root == null)
		{
			return new Node<AnyType>(data);
		}
		// If compareTo() returns a value less than zero, then the data passed
		// to the function MUST go to the left of the root.
		else if (data.compareTo(root.data) < 0)
		{
			root.left = insert(root.left, data);
		}
		// If compareTo() returns a value greater than zero, then the data passed
		// to the function MUST go to the right of the root.
		else if (data.compareTo(root.data) > 0)
		{
			root.right = insert(root.right, data);
		}
		else
		{
			// Empty statement in the case that duplicate values are detected. This
			// ensures that no duplicate values end up on the generic BST.
			;
		}

		return root;
	}

	// Delete method that takes a generic parameter and calls the private
	// delete method to delete node containing the given data.
	public void delete(AnyType data)
	{
		root = delete(root, data);
	}

	// Generic node delete method that takes a generic root and data as its
	// parameter. Traverses the generic BST to delete the node containing the
	// passed data. Returns the root of the modified tree.
	private Node<AnyType> delete(Node<AnyType> root, AnyType data)
	{
		// Checks to see if root is null. If it is, then deletion can't occur and
		// null is returned. Otherwise, data and the root's data are compared using
		// the compareTo() method.
		if (root == null)
		{
			return null;
		}
		// If compareTo() returns a value less than zero, then the node containing
		// the data MUST be to the left of the root.
		else if (data.compareTo(root.data) < 0)
		{
			root.left = delete(root.left, data);
		}
		// If compareTo() returns a value greater than zero, then the node
		// containing the data MUST be to the right of the root. Else we have
		// found the node we're trying to delete.
		else if (data.compareTo(root.data) > 0)
		{
			root.right = delete(root.right, data);
		}
		else
		{
			// If the node has no children, then delete it away by setting it to null.
			if (root.left == null && root.right == null)
			{
				return null;
			}
			// If the node has only one child then either the left child or the
			// right child takes the place of the deleted node depending on which
			// one is not null.
			else if (root.left == null)
			{
				return root.right;
			}
			else if (root.right == null)
			{
				return root.left;
			}
			// Else, the node has two children. In that case, the max of the left
			// subtree takes the place of the deleted node. The node is then deleted
			// from the left subtree.
			else
			{
				root.data = findMax(root.left);
				root.left = delete(root.left, root.data);
			}
		}

		return root;
	}

	// This method assumes root is non-null, since this is only called by
	// delete() on the left subtree, and only when that subtree is not empty.
	// The method returns the right most node of the generic BST.
	private AnyType findMax(Node<AnyType> root)
	{
		// Traverses the right subtree of the generic BST as long as it's not null.
		while (root.right != null)
		{
			root = root.right;
		}

		return root.data;
	}

	// Method that checks to see if the generic BST contains a particular node.
	public boolean contains(AnyType data)
	{
		return contains(root, data);
	}

	private boolean contains(Node<AnyType> root, AnyType data)
	{
		// If the root is null then it must not contain the node that we're looking
		// for.
		if (root == null)
		{
			return false;
		}
		// compareTo() is called. The data that was passed to the contains method
		// is compared to the root's data. If the value is less than zero then,
		// the value could possibly be in the left subtree.
		else if (data.compareTo(root.data) < 0)
		{
			return contains(root.left, data);
		}
		// If compareTo() returns a value greater than zero, then the node we're
		// looking for could possibly be in the right subtree. Else, we have found
		// the node we're looking for.
		else if (data.compareTo(root.data) > 0)
		{
			return contains(root.right, data);
		}
		else
		{
			return true;
		}
	}

	// Method that performs and prints an inorder traversal of the generic BST.
	public void inorder()
	{
		System.out.print("In-order Traversal:");
		inorder(root);
		System.out.println();
	}

	private void inorder(Node<AnyType> root)
	{
		// If the root is null, then NO traversal can occur.
		if (root == null)
			return;

		// Inorder traversal will print the leftmost value, then the middlest
		// value, and then the rightmost value of every call (LMR).
		inorder(root.left);
		System.out.print(" " + root.data);
		inorder(root.right);
	}

	// Method that performs and prints a preorder traversal of the generic BST.
	public void preorder()
	{
		System.out.print("Pre-order Traversal:");
		preorder(root);
		System.out.println();
	}

	private void preorder(Node<AnyType> root)
	{
		if (root == null)
			return;

		// Preorder traversal will print the middlest value, then the leftmost
		// value, and then the rightmost value of every call (MLR).
		System.out.print(" " + root.data);
		preorder(root.left);
		preorder(root.right);
	}

	// Method that performs and prints a postorder traversal of the generic BST.
	public void postorder()
	{
		System.out.print("Post-order Traversal:");
		postorder(root);
		System.out.println();
	}

	private void postorder(Node<AnyType> root)
	{
		if (root == null)
			return;

		// Postorder traversal will print the leftmost value, then the rightmost
		// value, and then the middlest value (LRM).
		postorder(root.left);
		postorder(root.right);
		System.out.print(" " + root.data);
	}

	// Method that returns the approximated difficulty of the assignment.
	public static double difficultyRating()
	{
		return 1.0;
	}

	// Method that returns the estimated amount of hours spent on the assignment.
	public static double hoursSpent()
	{
		return 1.5;
	}
}
