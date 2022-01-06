package edu.uwm.cs351;
import java.util.function.Consumer;

import edu.uwm.cs.junit.LockedTestCase;

/**
 * Set of strings, sorted lexicographically.
 */
public class Lexicon {
	
	private static class Node {
		String string;
		Node left, right;
		Node (String s) { string = s; }
	}
	
	private Node _root;
	private int _manyNodes;
	
	/**
	 * Check the invariant.  
	 * Returns false if any problem is found.  It uses
	 * {@link #_report(String)} to report any problem.
	 * @return whether invariant is currently true.
	 */
	private boolean _wellFormed() {
		int n = _checkInRange(_root, null, null);
		if (n < 0) return false; // problem already reported
		if (n != _manyNodes) return _report("_manyNodes is " + _manyNodes + " but should be " + n);
		return true;
	}
	
	private static boolean _doReport = true;
	
	/**
	 * Used to report an error found when checking the invariant.
	 * @param error string to print to report the exact error found
	 * @return false always
	 */
	private boolean _report(String error) {
		if (_doReport) System.out.println("Invariant error found: " + error);
		return false;
	}

	private int _reportNeg(String error) {
		_report(error);
		return -1;
	}
	
	/**
	 * Check that all strings in the subtree are in the parameter range,
	 * and none of them are null.
	 * Report any errors.  If there is an error return a negative number.
	 * (Write "return _reportNeg(...);" when detecting a problem.)
	 * Otherwise return the number of nodes in the subtree.
	 * Note that the range should be updated when doing recursive calls.
	 * 
	 * @param n the root of the subtree to check
	 * @param lo if non-null then all strings in the subtree rooted
	 * 				at n must be [lexicographically] greater than this parameter
	 * @param hi if non-null then all strings in the subtree rooted
	 * 				at n must be [lexicographically] less than this parameter
	 * @return number of nodes in the subtree
	 */
	private int _checkInRange(Node n, String lo, String hi)
	{
		if(n == null) return 0;
		if(n.string == null) return _reportNeg("null work found");
		
		if(lo != null && (n.string.equals(lo) || n.string.compareTo(lo) < 0))
			return _reportNeg("Detected node outside of lower bound: " +n.string);
		
		if(hi != null && (n.string.equals(hi) || n.string.compareTo(hi) > 0))
			return _reportNeg("Detected node outside of upper bound: " +n.string);
		
		int leftSubtree = _checkInRange(n.left, lo, n.string);
		int rightSubtree = _checkInRange(n.right, n.string, hi);
		
		if(leftSubtree < 0 || rightSubtree < 0) return -1;
		
		return 1 + leftSubtree + rightSubtree;
	}
	
	/**
	 * Creates an empty lexicon.
	 */
	public Lexicon() {
		_root = null;
		_manyNodes = 0;
		assert _wellFormed() : "invariant false at end of constructor";
	}
	

	/** Gets the size of this lexicon.
	 * @return the count of strings in this lexicon
	 */
	public int size() {
		assert _wellFormed() : "invariant false at start of size()";
		return _manyNodes;
	}
	
	/**
	 * Gets the [lexicographically] least string in the lexicon.
	 * @return the least string or null if empty
	 */
	public String getMin() {
		assert _wellFormed() : "invariant false at start of getMin()";
		
		if(_root == null) return null;
		
		Node n = _root;
		while(n.left != null) n = n.left;
		
		return n.string;
	}
	
	/**
	 * Checks if the given string is in the lexicon.
	 * @param str the string to search for (maybe null)
	 * @return true if str is in the lexicon, false otherwise
	 */
	public boolean contains(String str) {
		assert _wellFormed() : "invariant false at start of contains()";
		
		if(str == null) return false;
		
		Node node = _root;
		while(node != null) {
			int c = str.compareTo(node.string);
			
			if(c == 0) return true;
			else if(c < 0)
				node = node.left;
			else
				node = node.right;
		}
		
		return false;
	}
	
	/**
	 * Gets the next [lexicographically] greater string than the given string.
	 * @param str the string of which to find the next greatest
	 * @return the next string greater than str
	 * @throws NullPointerException if str is null
	 */
	//Recursion is not allowd for this method!
	public String getNext(String str) {
		assert _wellFormed() : "invariant false at start of getNext()";
		
		if(str == null) throw new NullPointerException("Cannot get next of null");
		
		Node n = _root;
		String result = null;
		while(n != null) {
			if(n.string.compareTo(str) <= 0)
				n = n.right;
			else
				result = n.string;
				n = n.left;
		}
		
		return result;
	}
	
	/**
	 * Accept into the consumer all strings in this lexicon.
	 * @param consumer the consumer to accept the strings
	 * @throws NullPointerException if consumer is null
	 */
	public void consumeAll(Consumer<String> consumer) {
		consumeAllWithPrefix(consumer,"");
	}
	
	/**
	 * Accept into the consumer all strings that start with the given prefix.
	 * @param consumer the consumer to accept the strings
	 * @param prefix the prefix to find all strings starting with
	 * @throws NullPointerException if consumer or prefix is null
	 */
	public void consumeAllWithPrefix(Consumer<String> consumer, String prefix) {
		assert _wellFormed() : "invariant false at start of consumeAllWithPrefix()";
		if (consumer == null) throw new NullPointerException("Can't accept into null consumer");
		if (prefix == null) throw new NullPointerException("Prefix can't be null");
		consumeAllHelper(consumer, prefix, _root);
	}
	
	private void consumeAllHelper(Consumer<String> consumer, String prefix, Node n) {
		if(n == null) return;
		if(n.left != null && prefix.compareTo(n.string) < 0)
			consumeAllHelper(consumer, prefix, n.left);
		
		if(n.string.startsWith(prefix))
			consumer.accept(n.string);
		
		if(n.right != null && (prefix.compareTo(n.string) > 0 || n.string.startsWith(prefix)))
			consumeAllHelper(consumer, prefix, n.right);
	}
	
	/// Mutators
	
	/**
	 * Add a new string to the lexicon. If it already exists, do nothing and return false.
	 * @param str the string to add (must not be null)
	 * @return true if str was added, false otherwise
	 * @throws NullPointerException if str is null
	 */
	public boolean add(String str) {
		assert _wellFormed() : "invariant false at start of add()";
		boolean result = false;
		if(str == null) throw new NullPointerException("cannot add null");
		
		Node n = _root;
		Node lag = null;
		while(n != null) {
			if(n.string.equals(str)) break;
			
			lag = n;
			if(str.compareTo(n.string) > 0) n = n.right;
			else n = n.left;
		}
		if(n == null) {
			n = new Node(str);
			placeUnder(n, str, lag);
			++_manyNodes;
			result = true;
		}
		
		assert _wellFormed() : "invariant false at end of add()";
		return result;
	}
	
	// Optional: you may wish to define a helper method or two.
	private void placeUnder(Node toAdd, String str, Node lag) {
		if(lag == null)
			_root = toAdd;
		else if(str.compareTo(lag.string) > 0 || str.equals(lag.string))
			lag.right = toAdd;
		else
			lag.left = toAdd;
	}
	

	/**
	 * Add all strings in the array into this lexicon from the range [lo,hi).
	 * The elements are added recursively from the middle, so that
	 * if the array was sorted, the tree will be balanced.
	 * All the tree mutations should be done by add.
	 * Return number of strings actually added; some might not be added
	 * if they are duplicates.
	 * @param array source
	 * @param lo index lower bound
	 * @param hi index upper bound
	 * @return number of strings added
	 * @throws NullPointerException if array is null
	 */
	public int addAll(String[] array, int lo, int hi) {
		assert _wellFormed() : "invariant false at start of addAll()";
		if(array == null) throw new NullPointerException("Cannot add from null array");
		
		if(lo == hi) return 0;
		
		int mid = (lo + (hi - lo) / 2);
		int n1 = add(array[mid]) ? 1 : 0;
		int n2 = addAll(array, lo, mid);
		int n3 = addAll(array, mid+1, hi);
		
		// NB: As long as you never touch any fields directly (or call private methods)
		// you shouldn't *need* to check the invariant. We will anyway.
		assert _wellFormed() : "invariant false at end of addAll()";
		return n1 + n2 + n3;
	}
	
	/**
	 * Copy all the strings from lexicon (in sorted order) into the array starting
	 * at the given index.  Return the next index for (later) elements.
	 * This is a helper method for {@link #toArray(String[])}.
	 * @param array destination of copy
	 * @param root the subtree whose elements should be copied
	 * @param index the index to place the next element
	 * @return the next spot in the array to use after this subtree is done
	 */
	private int copyInto(String[] array, Node root, int index) {
		if(root == null) return index;
		
		index = copyInto(array, root.left, index);
		array[index++] = root.string;
		
		return copyInto(array, root.right, index);
	}
	
	/**
	 * Return an array of all the strings in this lexicon (in order).
	 * @param array to use unless null or too small
	 * @return array copied into
	 */
	public String[] toArray(String[] array) {
		assert _wellFormed() : "invariant false at the start of toArray()";
		
		if(array == null || array.length < size()) array = new String[_manyNodes];
		copyInto(array, _root, 0);
		
		return array;
	}
	
	public abstract static class TestInternals extends LockedTestCase {

		Lexicon lex;
		@Override
		protected void setUp() throws Exception {
			super.setUp();
			lex = new Lexicon();
			_doReport = false;
		}
		
		
		
		/** 
		 * 0x: Tests for _checkInRange
		 * 
		 * Dependencies: none
		 */
		
		public void test00() {
			Node a1 = new Node("a");
			Node a2 = new Node("a");
			Node a3 = new Node("a");
			
			a1.left = a2;
			assertEquals("malformed tree",-1, lex._checkInRange(a1, null, null));
			a1.left=null;
			a1.right = a2;
			assertEquals("malformed tree",-1, lex._checkInRange(a1, null, null));
			a1.left=a3;
			assertEquals("malformed tree",-1, lex._checkInRange(a1, null, null));
			a1.left = a1.right = null;
			assertEquals("good tree",1, lex._checkInRange(a1, null, null));
		}
		

		public void test01() {
			Node a = new Node("a");
			Node b = new Node("b");
			Node c = new Node("c");
			Node d = new Node("d");
			Node e = new Node("e");
			Node f = new Node("f");
			
			c.left = b;
			b.left = a;
			c.right = e;
			e.left = d;
			
			e.string = null;
			assertEquals("null string in tree",-1, lex._checkInRange(c, null, null));
			e.string = "e";
			assertEquals("good tree",5, lex._checkInRange(c, null, null));
			
			e.left=f;
			f.left=d;
			assertEquals("malformed tree",-1, lex._checkInRange(c, null, null));
			f.left=null;
			e.right=f;
			e.left=d;
			assertEquals("good tree",6, lex._checkInRange(c, null, null));
			
			Node aa = new Node("aa");
			a.left=aa;
			assertEquals("malformed tree",-1, lex._checkInRange(c, null, null));
			a.left=null;
			a.right=aa;
			assertEquals("good tree",7, lex._checkInRange(c, null, null));
		}
		
		public void test02() {
			Node a = new Node("a");
			Node b = new Node("b");
			Node c = new Node("c");
			Node d = new Node("d");
			Node e = new Node("e");
			Node f = new Node("f");
			
			a.right = b;
			b.right = c;
			c.right = d;
			d.right = e;
			e.left=f;
			assertEquals("malformed tree",-1, lex._checkInRange(a, null, null));
			e.left=null;
			a.left=f;
			assertEquals("malformed tree",-1, lex._checkInRange(a, null, null));
			a.left=b;
			a.right=null;
			assertEquals("malformed tree",-1, lex._checkInRange(a, null, null));
			b.right=null;
			a.left=null;
			a.right=c;
			c.left=b;
			assertEquals("good tree",5, lex._checkInRange(a, null, null));
		}
		
		
		
		/** 
		 * 1x: Tests for Invariant
		 * 
		 * Dependencies: _checkInRange
		 */
		
		public void test10() {
			lex._manyNodes = 1;
			assertFalse(lex._wellFormed());
			lex._manyNodes = 0;
			_doReport = true;
			assertTrue(lex._wellFormed());
		}
		
		public void test11() {
			lex._root = new Node("a");
			assertFalse(lex._wellFormed());
			lex._manyNodes = 1;
			assertTrue(lex._wellFormed());
			lex._manyNodes = 2;
			assertFalse(lex._wellFormed());
			lex._manyNodes = 1;
			lex._root.string = null;
			assertFalse(lex._wellFormed());
		}
		
		public void test12() {
			Node a1 = new Node("a");
			Node a2 = new Node("a");
			Node b = new Node("b");
			lex._manyNodes = 2;
			assertFalse(lex._wellFormed());
			lex._root = a1;
			assertFalse(lex._wellFormed());
			a1.right = a2;
			assertEquals(Tb(460914689), lex._wellFormed());
			a1.right = a1;
			assertEquals(false, lex._wellFormed());
			a1.right = b;
			assertEquals(Tb(1835848904), lex._wellFormed());
			
			b.left = a1;
			assertFalse(lex._wellFormed());
			lex._root = b;
			assertFalse(lex._wellFormed());
			a1.right = null;
			assertTrue(lex._wellFormed());
			
			b.right = b;
			assertFalse(lex._wellFormed());
		}
		
		public void test13() {
			Node a = new Node("a");
			Node b = new Node("b");
			Node c = new Node("c");
			c.left = a;
			c.right = b;
			lex._root = c;
			
			lex._manyNodes = 3;			
			assertEquals(Tb(1544974432), lex._wellFormed());
			lex._manyNodes = 1;
			assertFalse(lex._wellFormed());
		}
		
		public void test14() {
			Node a = new Node("aa");
			Node b = new Node("bb");
			Node c = new Node("cc");
			Node d = new Node("dd");
			Node e = new Node("ee");
			Node f = new Node("ff");
			Node g = new Node("gg");
			Node h = new Node("hh");
			Node i = new Node("ii");
			
			lex._root = e;
			e.left = c;
			c.right = d;
			c.left = a;
			a.right = b;
			e.right = h;
			h.left = g;
			g.left = f;
			h.right = i;
			lex._manyNodes = 9;
			//you may want to draw a picture
			assertEquals(Tb(447286989), lex._wellFormed());
			
			lex._manyNodes = 10;
			assertFalse("incorrect count", lex._wellFormed());
			
			a.left = new Node("ab");
			assertFalse(lex._wellFormed());
			a.left = null;
			
			b.left = new Node("a");
			assertFalse(lex._wellFormed());
			b.left = null;
			b.right = new Node("cd");
			assertFalse(lex._wellFormed());
			b.right = null;
			
			--lex._manyNodes;
			assertTrue(lex._wellFormed());
			++lex._manyNodes;
			
			d.left = new Node("bc");
			assertFalse(lex._wellFormed());
			d.left = null;
			d.right = new Node("ef");
			assertFalse(lex._wellFormed());
			d.right = null;
			
			f.left = new Node("de");
			assertFalse(lex._wellFormed());
			f.left = null;
			f.right = new Node("gh");
			assertFalse(lex._wellFormed());
			f.right = null;
			
			g.right = new Node("hi");
			assertFalse(lex._wellFormed());
			g.right = null;
			
			--lex._manyNodes;
			assertTrue(lex._wellFormed());
			++lex._manyNodes;
			
			i.left = new Node("gh");
			assertFalse(lex._wellFormed());
			i.left = null;
			i.right = new Node("hi");
			assertFalse(lex._wellFormed());
			i.right = null;
			
			--lex._manyNodes;
			assertTrue(lex._wellFormed());			
		}
	}
}
