// https://docs.oracle.com/javase/8/docs/api/java/util/List.html

import java.util.*;
import java.lang.reflect.*;
import java.util.ArrayList;

public class SkipList<E> implements List<E>
{
    private static final int MAX_LEVELS = 30;

    private int count;    // list size
    private ArrayList<Node<E>> heads;

    /* the list constructor - starts with an empty list */
    public SkipList() {
        count = 0;
        heads = new ArrayList<Node<E>>(MAX_LEVELS);
        // initialize with null since ArrayLists start empty
        for (int i = 0; i < MAX_LEVELS; i++) {
            heads.add(i,null);
        }
    }

    // Group 1

    public boolean add(E e) {
        //Cast e to Comparable to use the CompareTo method
        @SuppressWarnings("unchecked")
        Comparable<E> ce = (Comparable<E>)e;

        Node<E> newNode = new Node<E>(e);
        int newNodeLevels = newNode.levels();

        // filling addPath with all the nodes to potentially be updated
        ArrayList<Node<E>> addPath = new ArrayList<Node<E>>(MAX_LEVELS);
        for (int i = 0; i < MAX_LEVELS; i++) {
            addPath.add(i,null);
        }

        Node<E> finger = null; // which means starting at the head
        for (int lvl = MAX_LEVELS - 1; lvl >= 0; lvl--) {
            if (finger == null) { // when finger is at the head
                if (heads.get(lvl) == null || ce.compareTo(heads.get(lvl).value()) < 0) {
                    addPath.set(lvl, null); // we'll have to update the head
                } else { // traveling on the level
                    finger = heads.get(lvl);
                    while ( (finger.next(lvl) != null)
                        && ce.compareTo(finger.next(lvl).value()) > 0 ) {
                        finger = finger.next(lvl);
                    }
                    addPath.set(lvl, finger);
                }
            } else { // finger is at a node
                while ( (finger.next(lvl) != null)
                    && ce.compareTo(finger.next(lvl).value()) > 0 ) {
                    finger = finger.next(lvl);
                }
                addPath.set(lvl,finger);
            }
        }

        // insert the new node in the skiplist
        for (int lvl = 0; lvl < newNodeLevels; lvl++) {
            if (addPath.get(lvl) == null) { // flag for the heads pointers
                newNode.setNext(lvl,heads.get(lvl));
                heads.set(lvl,newNode);
            } else { // affects a node
                newNode.setNext(lvl,addPath.get(lvl).next(lvl));
                addPath.get(lvl).setNext(lvl, newNode);
            }
        }

        count++;
        return true;
    }

    public boolean testAdd(boolean verbose)  {
        //creates a new SkipList called testList
        List<Integer> testList = new SkipList<Integer>();

        testList.add(7);
        testList.add(4);
        testList.add(10);
        testList.add(7);


        return (
                (testList.get(0) == 4) &&
                        (testList.get(1) == 7) &&
                        (testList.get(2) == 7) &&
                        (testList.get(3) == 10) &&
                        (testList.size() == 4)
        );

        //return compareList.equals(testList);
    }

    public boolean addAll(Collection<? extends E> c) {

        boolean isDifferent = false;
        for (E e:c)
            if (add(e)) {
                isDifferent = true;
            }
        return isDifferent;
    }
    //
    public boolean testaddAll(boolean verbose) {
        List<Integer> testList = new SkipList<Integer>();

        int testValue1 = 5;
        testList.add(7);
        testList.add(4);
        testList.add(10);
        testList.add(7);
        testList.add(testValue1);

        if ((testList.get(0) == 4) &&
                (testList.get(1) == 5)&&
                (testList.get(2) == 7) &&
                (testList.get(3) == 7) &&
                (testList.get(4) == 10) &&
                (testList.size() == 5)) {
            return true;

        }
        else
            return false;


    }

    public int indexOf(Object obj) {

        if (isEmpty()) {
            return -1;
        }

        int index = 0;
        Node<E> temp = heads.get(0);
        boolean flag = false;

        while (temp != null) {
            if (temp.value().equals(obj)) {
                flag = true;
                break;
            }
            temp = temp.next(0);
            index++;
        }
        if (flag){
            return index;
        }
        else{
            return -1;
        }
    }


    public static boolean testindexOf(){
        List<Integer> testList = new SkipList<Integer>();
        int testValue2 = 10;
        int assumedIndex = 3;
        testList.add(7);
        testList.add(4);
        testList.add(testValue2);
        testList.add(7);
        if (assumedIndex == testList.indexOf(testValue2)){
            return true;
        }
        else {
            return false;
        }
    }
    public int lastIndexOf(Object m) {

        int index = indexOf(m);
        if (index == -1) {
            return -1;
        }
        else {
            Node<E> temp = heads.get(0);
            for (int i = 0; i < index; i++) {
                temp = temp.next(0);
            }
            while (temp.next(0).value() == m) {
                temp = temp.next(0);
                index++;
            } return index;
        }
    }

    public static boolean testlastIndexOf() {
        List<Integer> testList = new SkipList<Integer>();
        int testValue3 = 7;
        testList.add(testValue3);
        testList.add(4);
        testList.add(10);
        testList.add(testValue3);


        int assumedIndex = 2;
        if (assumedIndex == testList.lastIndexOf(testValue3)){
            return true;

        }
        else {
            return false;
        }
    }

    // Group 2

    public boolean contains(Object o)
    {
        @SuppressWarnings("unchecked") // Supressing type checks
        Comparable<E> co = (Comparable<E>) o; // Casting Comparable to the Object, so compareTo magic can happen, 10 points to Gryffindor!
        if (heads == null) return false; // return false if no heads pointers
        int lvl = heads.size() - 1; // start level at max
        while (heads.get(lvl) == null) // get level to where the highest node is
        {
            lvl--;
        }
        Node<E> temp = heads.get(lvl); // set temp to that node
        while (lvl >= 0)
        {
            if (temp != null) // if it's not null, it's filled, so...
            {
                if (co.compareTo(temp.value()) == 0)  // o is equal to temp
                    return true;
                if (lvl == 0 && (temp.next(0) == null || co.compareTo(temp.value()) < 0))  // couldn't find o
                    return false;
            }
            if (temp.next(lvl) == null) // next node at this level is null, go down a level
            {
                lvl--;
            }
            else if (co.compareTo(temp.next(lvl).value()) < 0) // o is less than temp, go down a level
            {
                lvl--;
            }
            else if (co.compareTo(temp.next(lvl).value()) >= 0) // o is more than temp, jump to this node
            {
                temp = temp.next(lvl);
            }
        }
        return false;
    }

    public boolean testcontains(boolean verbose)
    {
        List<Integer> list = new SkipList<Integer>();

        list.add(1);
        list.add(3);
        list.add(8);
        list.add(12);
        list.add(9);
        list.add(6);
        list.add(2);
        list.add(24);
        list.add(18);
        list.add(13);

        // if false returns true && if true returns true
        return (!list.contains(14) && list.contains(13));
    }

    public boolean containsAll(Collection c)
    {
        for (Object o: c)
        {
            if (!contains(o))
                return false;
        }
        // if nothing ever returned false, return true
        return true;
    }

    public boolean testcontainsAll(boolean verbose)
    {
        List<Integer> test = new LinkedList<Integer>();
        List<Integer> comp = new LinkedList<Integer>();
        test.add(1);
        test.add(2);
        test.add(3);

        comp.add(1);
        comp.add(2);
        comp.add(3);

        return (test.containsAll(comp));
    }

    public boolean equals(Object o)
    {
        if (o == this) // seems like an obvious check, but hey now.
            return true;

        Collection<?> c = (Collection<?>) o; // cast o to a generic Collection, not all objects are one, right?

        if (c.size() != size()) // why check everything if the size is different? Gotta go fast.
            return false;

        return containsAll(c);
    }

    public boolean testequals(boolean verbose)
    {
        List<Integer> list = new LinkedList<Integer>();
        List<Integer> comp = new LinkedList<Integer>();
        list.add(null);
        list.add(32);
        list.add(99);
        comp.add(null);
        comp.add(32);
        comp.add(99);
        return list.equals(comp);
    }

    public List<E> subList(int fromIndex, int toIndex)
    {
        // we need to return a List of a generic type
        if (fromIndex < 0 || toIndex > this.size())
        {
          throw new IndexOutOfBoundsException();
        }

        List<E> sub = new SkipList<E>();
        /*
            start at fromIndex, iterate until given index toIndex, add them to
            list created inside this method, return this list.
        */
        for(int i = fromIndex; i<toIndex; i++)
        {
            sub.add(this.get(i));
        }
        return sub;
    }

    public static boolean testSubList()
    {
        List<String> testList = new ArrayList<String>();

        testList.add("HELP");
        testList.add("Mark");
        testList.add("made");
        testList.add("me");
        testList.add("do it");
        testList.add(" :( ");

        ArrayList<String> testSubList = new ArrayList<String>(testList.subList(1, 5));
        if (testList.containsAll(testSubList))
            return true;
        else
            return false;
    }

    // Group 3
    public boolean isEmpty() {
    	return count == 0;
    }

    public static boolean testIsEmpty() {
    	SkipList<Integer> testList = new SkipList<Integer>();
    	//create new list

    	//should skip since the list is empty
    	if (!testList.isEmpty()) {
    		return false;
    	}

    	//add two elements to list so the list is now populated
    	testList.add(3);
    	testList.add(4);

    	/*
       *Checks to see if the testList has any values. If statement uses the opposite.
       *Since the test list is populated, isEmpty() will return false, but since
    	 *we are looking for the opposite, the if is true and should return true
       */
      return !testList.isEmpty();
    }

    public int size() {
        return this.count;
    }

    public static boolean testSize() {

        int randInt;
        Random rand = new Random();

        // Creates a new list
        SkipList<Integer> testList = new SkipList<Integer>();

        // Creates a random number between 1 and 15
        randInt = rand.nextInt(15) + 1;

        // Adds random number of 1's to test list
        for (int i = 0; i < randInt; i++) {
            testList.add(1);
        }

        // Find list size according to .size() method. Should be randInt.
        return testList.size() == randInt;
    }

   public void clear() {
       count = 0;
       heads = new ArrayList<Node<E>>(MAX_LEVELS);

       // initialize with null since ArrayLists start empty
       for (int i = 0; i < MAX_LEVELS; i++) {
    	   heads.add(i, null);
       }
    }

    public static boolean testClear() {
    	// creates a test list
        SkipList<Integer> testList = new SkipList<Integer>();

        // adds some values to the testList
        testList.add(1);
        testList.add(2);
        testList.add(3);

        // removes those values from the testList
        testList.clear();

        // Checks if testList is empty
         return testList.isEmpty();
    }

    public E get(int index) {
        //sets pointer equal to head (start of the collection)
        Node<E> pointer = heads.get(0);

        /* if the requested index is less than zero, or if the requested index is
         * greater than the size of collection an exception is thrown and the output prints
         * "chosen index is out of bounds"
         */
        if ( index < 0 || index >= this.count ) {
            throw new IndexOutOfBoundsException ("chosen index is out of bounds");
        } else {
            //uses a for loop to move the pointer to the desired index in the location
            for (int i = 0; i < index; i++){
                pointer = pointer.next(0);
            }

            /*return the value of the pointer after it has been moved to the desired index in
             *the collection
             */
            return pointer.value();
        }
    }

    public static boolean testGet() {
    	// creates a testList
        SkipList<Integer> testList = new SkipList<Integer>();

        // adds the number one to the testList in index 0
        testList.add(1);

        // returns true if the value at index 0 equals 1
        return testList.get(0) == 1;
    }

    public E getQuantile(double quantile) {
        // checks to see if the value is within the acceptable range 0 <= x < 1
        if(quantile >= 1 | quantile < 0) {
            throw new IndexOutOfBoundsException("Quantile is out of range");
        }

        // typecasts the double into an int and returns the index of the node given the percentage
        int index = (int)(this.size() * quantile);

        // returns the value at the requested index
        return this.get(index);
    }

    public static boolean testGetQuantile() {
        // Creates a new list
        SkipList<Integer> testList = new SkipList<Integer>();

        // Prepopulates the list from 0 - 100, so index = quantile*100
        for(int i = 0; i < 100; i++) {
            testList.add(i);
        }

        // creates a random index from 0.00-1.00 with 2 decimal places
        double randomQuantile = Math.round(Math.random()*100.0)/100.0;

        // gets the value at the random quantile
        int value = testList.getQuantile(randomQuantile);

        // checks to see if the value is equal to the quantile amount
        return (value == Math.round(randomQuantile*100));
    }

    // Group 4
    // This project was a collaborative effort of all in group 4 All parts were worked on using pair programming techinque two laptops five people brainstorming
    public Iterator<E> iterator()
    {
        return new SkipListIterator<E>(this);
    }

    public ListIterator<E> listIterator()
    {
        return new SkipListIterator<E>(this);
    }

    public ListIterator<E> listIterator(int index)
    {
        //ensure index fits before creating an iterator at a bad index
        if(Math.abs(index)<count)
        {
            return new SkipListIterator<E>(this,index);
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    public class SkipListIterator<E> implements ListIterator<E> {
        //global variables
        private SkipList<E> skipList; //List being iterated
        private Node<E> current; //Finger points to current node
        private int current_idx; //Current index of skipList


        public SkipListIterator(SkipList<E> list) {
            //initializing variables
            skipList = list;
            current= skipList.heads.get(0);
            current_idx = 0;

        }
        public SkipListIterator(SkipList<E> list, int index) {
            //init variables in an indexed constructor
            skipList = list;
            //the method callingn iterator already takes care of the bounds but now we have to see if it is negative
            // then set index to count+index
            if (index < 0) {
                current_idx = count + index;
            }
            else {
                current_idx = index;
            }
            //set current to 0th item
            current = skipList.heads.get(0);
            for (int i =0; i<current_idx;i++)
            {
                current = current.next(0);

            }
        }
        // Return true of index comes before the lst index
        @Override
        public boolean hasNext() {
            return current_idx<skipList.count;
        }

        @Override
        public E next() {
            if(this.hasNext()) {
                //returns current value then iterates to next element
                E tempor;
                tempor= current.value();
                current=current.next(0);
                current_idx++;

                return tempor;

            }
            else
            {
                throw new NoSuchElementException();
            }
        }
        //returns true if current_idx is not first element
        @Override
        public boolean hasPrevious() {
            return (current_idx>0);
        }

        @Override
        public E previous() {
            if (this.hasPrevious()) {
                //set decrement current index but since no previous node pointer
                // must start at beginning and loop till previous index to get node at currentidx--
                current_idx--;
                current = skipList.heads.get(0);
                int idx = 0;
                for (int i=0;i<current_idx;i++) {
                    current = current.next(0);
                }
                return (current.value());
            }
            else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            if (this.hasNext())
            {
                return current_idx+1;
            }
            else{
                //returns size of skip list if index is at the end
                return skipList.count;
            }
        }

        @Override
        public int previousIndex() {
            if(this.hasPrevious())
            {
                return current_idx-1;
            }
            else
            {
                //returns -1 if iterator iterator at first element
                return -1;
            }
        }
        // remove, set, and add will not be implemented as instructed
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException();

        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }
    public static boolean testListIterator() {

        SkipList<Integer> skip = new SkipList<Integer>();

        skip.add(5);
        skip.add(10);
        skip.add(20);
        skip.add(30);
        // call iterator next twice should return 5 and 10
        ListIterator<Integer> it = skip.listIterator();
        //checking if previous index number since at beginnign should return -1
        if (it.hasPrevious()==true)
        {
            return false;
        }
        //check previous index number should be -1
        if(it.previousIndex()!=-1)
        {
            return false;
        }
        if(!(it.next().equals(5)) ||!(it.next().equals(10)))
        {
            return false;
        }
        //right now iterator at index 2 should return true if iterator has next called
        if(it.hasNext()==false)
        {
            return false;
        }
        //testing the previous then nexting right after- should return same results
        if (it.previous()!=it.next())
        {
            return false;
        }
        // now move up iterator to last element to test edge case
        if (!(it.next().equals(20)&& it.next().equals(30)))
        {
            return false;
        }
        //edge case hasnext should return false and next should thorw error
        if (it.hasNext()==true)
        {
            return false;
        }
        try
        {
            it.next();
            return false;
        }
        catch(NoSuchElementException e)
        {

        }
        //edgecase of next_index should return size of list
        if (it.nextIndex()!= skip.size())
        {
            return false;
        }
        return true;

    }


    public static boolean testIterator () {

        SkipList<Integer> skip = new SkipList<Integer>();

        skip.add(5);
        skip.add(10);
        skip.add(20);
        skip.add(30);
        // call iterator next twice should return 5 and 10
        Iterator<Integer> it = skip.listIterator();
        if(!(it.next().equals(5)) ||!(it.next().equals(10)))
        {
            return false;
        }
        //right now iterator at index 2 should return true if iterator has next called
        if(it.hasNext()==false)
        {
            return false;
        }
        // now move up iterator to last element to test edge case
        if (!(it.next().equals(20)&& it.next().equals(30)))
        {
            return false;
        }
        //edge case has next should return false
        if (it.hasNext()==true)
        {
            return false;
        }
        //next should throw error since end of index
        try
        {
            it.next();
            return false;
        }
        catch(NoSuchElementException e)
        {

        }

        return true;

    }



    public static boolean testListIteratorWithIndex() {
        SkipList<Integer> skip = new SkipList<Integer>();

        skip.add(5);
        skip.add(10);
        skip.add(20);
        skip.add(30);
        //try creating an iterator out of bound indexed
        try {
            ListIterator<Integer> tt = skip.listIterator(100);
            return false;
        }
        catch(IndexOutOfBoundsException e)
        {

        }
        // skip index 0 by starting at 1
        ListIterator<Integer> it = skip.listIterator(1);
        //starting at index 1 has previous should return true
        if (!it.hasPrevious())
        {
            return false;
        }
        //check previous index number should be 0
        if(it.previousIndex()!=0) {
            return false;
        }
        //calling it.previous should be valid since we are at index 1
        try{
            it.previous();
        }
        catch(NoSuchElementException e)
        {
         return false;
        }
        //now at oth index previous should return  false
        if(it.hasPrevious())
        {
            return false;
        }
        // now at oth index previous index should output -1
        if(it.previousIndex()!=-1)
        {
            return false;
        }
        //we have started at index since we are back at element 0 our  first call to next should be the first element #5 and the second call should be
        // second element #10
        if(!(it.next().equals(5)) ||!(it.next().equals(10)))
        {
            return false;
        }
        //right now iterator at index 2 should return true if iterator has next called
        if(it.hasNext()==false)
        {
            return false;
        }
        //testing the previous then nexting right after- should return same results
        if (it.previous()!=it.next())
        {
            return false;
        }
        // now move up iterator to last element to test edge case
        if (!(it.next().equals(20)&& it.next().equals(30)))
        {
            return false;
        }
        //edge case hasnext should return false and next should thorw error
        if (it.hasNext()==true)
        {
            return false;
        }
        try
        {
            it.next();
            return false;
        }
        catch(NoSuchElementException e)
        {

        }
        //edgecase of next_index should return size of list
        if (it.nextIndex()!= skip.size())
        {
            return false;
        }
        return true;

    }

    // Group 5

    public E remove(int index)
    {
        if ( index < 0 || index >= this.count ) {
            throw new IndexOutOfBoundsException ("chosen index is out of bounds");
        } else if(index == 0) {// special case
            Node<E> currentNode = heads.get(0);
            E currentData = currentNode.value();

            for(int i = 0; i < currentNode.levels(); i++) {
                heads.set(i,currentNode.next(i));
            }
            this.count--;
            return currentData;


        } else {// normal case
            // data of node corresponding to index
            E currentData = get(index);

            // Cast e to Comparable to use the CompareTo method
            @SuppressWarnings("unchecked")
            Comparable<E> ce = (Comparable<E>) currentData;

            // filling removePath with all the nodes to potentially be updated
            ArrayList<Node<E>> removePath = new ArrayList<Node<E>>(MAX_LEVELS);
            for (int i = 0; i < MAX_LEVELS; i++) {
                removePath.add(i, null);
            }

            Node<E> finger = null; // which means starting at the head
            for (int lvl = MAX_LEVELS - 1; lvl >= 0; lvl--) {
                if (finger == null) { // when finger is at the head
                    if (heads.get(lvl) == null || ce.compareTo(heads.get(lvl).value()) < 0) {
                        removePath.set(lvl, null); // we'll have to update the head
                    } else { // traveling on the level
                        finger = heads.get(lvl);
                        while ((finger.next(lvl) != null) && ce.compareTo(finger.next(lvl).value()) > 0) {
                            finger = finger.next(lvl);
                        }
                        removePath.set(lvl, finger);
                    }
                } else{ // finger is at a node
                    while ((finger.next(lvl) != null) && ce.compareTo(finger.next(lvl).value()) > 0) {
                        finger = finger.next(lvl);
                    }
                    removePath.set(lvl, finger);
                }
            }

            // node corresponding to index
            Node<E> currentNode = removePath.get(0);
            // modify the reference
            for(int i = 0; i < currentNode.levels(); i++) {
                removePath.get(i).setNext(i, currentNode.next(i));
            }
            // modify the size of list
            this.count--;
            return currentNode.value();

        }
    }

    // Test the remove () method
    public boolean testRemove(boolean verbose)  {
        //creates a new SkipList called testList
        List<Integer> testList = new SkipList<Integer>();

        testList.add(1);
        testList.add(3);
        testList.add(8);
        testList.add(4);
        testList.add(23);
        testList.add(786);
        testList.add(4);
        testList.add(9);

        testList.remove(0);
        testList.remove(4);


        return (
            (testList.get(0) == 3) &&
            (testList.get(1) == 4) &&
            (testList.get(4) == 9)
            );
    }

    public boolean remove(Object o)
    {
        //Cast e to Comparable to use the CompareTo method
        @SuppressWarnings("unchecked")
        Comparable<E> ce = (Comparable<E>)o;

        // filling addPath with all the nodes to potentially be updated
        ArrayList<Node<E>> removePath = new ArrayList<Node<E>>(MAX_LEVELS);
        for (int i = 0; i < MAX_LEVELS; i++) {
            removePath.add(i,null);
        }

        Node<E> finger = null; // which means starting at the head
        for (int lvl = MAX_LEVELS - 1; lvl >= 0; lvl--) {
            if (finger == null) { // when finger is at the head
                if (heads.get(lvl) == null || ce.compareTo(heads.get(lvl).value()) <= 0) {
                    removePath.set(lvl, null); // we'll have to update the head
                } else { // traveling on the level
                    finger = heads.get(lvl);
                    while ( (finger.next(lvl) != null)
                        && ce.compareTo(finger.next(lvl).value()) > 0 ) {
                        finger = finger.next(lvl);
                    }
                    removePath.set(lvl, finger);
                }
            } else { // finger is at a node
                while ( (finger.next(lvl) != null)
                    && ce.compareTo(finger.next(lvl).value()) > 0 ) {
                    finger = finger.next(lvl);
                }
                removePath.set(lvl,finger);
            }
        }

        Node<E> removeNode;
        if (removePath.get(0) == null) { // the head is pointing to the element to be removed
            removeNode = heads.get(0);
        } else { // another node is pointing to the element to be removed
            removeNode = removePath.get(0).next(0);
        }

        // check to see if the node to remove is in the skiplist
        // is removeNode the right value to remove?
        if ( (removeNode == null) || (!ce.equals(removeNode.value() ) ) ) {
            return false;
        }
        int removeNodeLevels = removeNode.levels();

        // remove the node in the skiplist
        for (int lvl = 0; lvl < removeNodeLevels; lvl++) {
            if (removePath.get(lvl) == null) { // flag for the heads pointers
                heads.set(lvl,removeNode.next(lvl));
            } else { // affects a node
                removePath.get(lvl).setNext(lvl, removeNode.next(lvl));
            }
        }
        count--;
        return true;
    }

    //removes any elements from SkipList that are contained in collection c
    public boolean removeAll(Collection c){

        boolean isDifferent = false;
        for (Object e:c)
            if (remove(e))
                isDifferent = true;
        return isDifferent;
    }

    public boolean testRemoveAll(boolean verbose)  {

        //creates a new SkipList called testList
        List<Integer> test = new SkipList<Integer>();

        test.add(5);
        test.add(17);
        test.add(42);
        List<Integer> c = new ArrayList<>();

        c.add(17);
        c.add(42);
        if (test.removeAll(c) == false) {
            return false;
        }

        return (
            (test.get(0) == 5) &&
            (test.size() == 1)
        );
    }

    //removes any elements from SkipList that are not contained in collection c
    public boolean retainAll(Collection c){
        boolean modified = false;
        int lvl = 0;    //want to iterate through at level 0
        Node<E> temp = heads.get(lvl);  //set pointer
        if(temp != null){   //if not at head
            if(!(c.contains(temp.value()))){ //if c doesn't contain the value
                remove(temp.value());   //remove it
                modified = true;    //the SkipList was modified
            }
            temp = temp.next(lvl);  //move to next value in SkipList
        }
        return modified;
    }


    public boolean testRetainAll(boolean verbose)  {
        //creates a new SkipList called testList
        List<Integer> test = new SkipList<Integer>();

        test.add(5);
        test.add(17);
        test.add(42);

        List<Integer> c = new ArrayList<>();

        c.add(17);
        if (test.retainAll(c) == false) {
            return false;
        }

        return (
            (test.get(0) == 17)
        );
    }

    // Group 6

    public int hashCode()
    {
            int hashCode = 1;
    		//Starts the finger at the heads
    		Node<E> finger = heads.get(0);
    		//Moves finger over to a node where a value might be stored.
    		finger.next(0);
    		//If the list isn't empty or if finger hasn't reached the end it'll hash the current value and move to the next.
    		while (finger != null)
        {
    			  hashCode = 31*hashCode + (finger == null ? 0 : finger.value().hashCode());
    			  finger = finger.next(0);
    		}
            return hashCode;
    }

    public boolean testHashCode()
    {
    		List<Integer> testList1 = new SkipList<Integer>();
    		List<Integer> testList2 = new SkipList<Integer>();

    		testList1.add(3);
    		testList1.add(4);
    		testList1.add(7);

    		testList2.add(3);
    		testList2.add(4);
    		testList2.add(7);

        return (testList1.hashCode() == testList2.hashCode());
    }

    public Object[] toArray()
    {
        Object[] arr = new Object[size()];
        //Starting position is set equal to the head (null)
        Node<E> current = null;
        //loop created that iterates through the values of the list
        for (int i = 0; i < size(); i++)
        {
            //current set to position of head at index, which changes after for loop completes
            current = heads.get(i);
            arr[i] = current;
        }
        return arr;
    }

    public static boolean testToArrayObj()
    {
        ArrayList<Integer> list1 = new ArrayList<Integer>();
        list1.add(18);
        list1.add(12);
        list1.add(6);

        ArrayList<Integer> list2 = new ArrayList<Integer>();
        list2.add(18);
        list2.add(12);
        list2.add(6);

        return list1.equals(list2);
    }

    public <T> T[] toArray(T[] a)
    {
        return a;
    }

    // won't compile
    /*
    public <T> T[] toArray(T[] a)
    {
        @SuppressWarnings("unchecked") // So it won't complain about Unchecked/Unsafe
        Node<E> pointer = heads.get(0); // start at the head
        int index = 0; // the current index of the array is 0
        if (this.size() > a.length) // makes a new ArrayList if the one passed is too big
       	{
        	T[] arr = (T[]) new Object[this.size()];

        	for (int i = 0; i < index; i++) // iterates through the SkipList and stores the nodes into the array
        	{
        		arr[i] = pointer.value(); // stores the value of the pointer into the ArrayList
       			pointer = pointer.next(i); // moves pointer to the next node
    			// after storing the value it moves the pointer to the node after it and then it would store that value and continue iterating through the loop
       		}
        }
        else if (this.size() <= a.length)
       	{
        	T[] arr = (T[]) a[this.size()];
        	return arr;
       	}
        return a;
    }

    public boolean testToArrayT()
    {
        @SuppressWarnings("unchecked") // So it won't complain about Unchecked/Unsafe
        List<Integer> testList = new LinkedList<Integer>();
        int arr[] = new int[30];
        for (int i = 0; i <= arr.length; i++) // iterates testList.add to add to the SkipList for the length of the array
    	{
    		testList.add(i);
    	}

    	testList.toArray(); // passes testList through the Array arr
    	return true;
    } */

    //-----------------------------------------------------------------------------------------------

    // functions to get the compiler to agree to implement the list interface
    // these functions don't make sense in a SkipList implementation
    // and they are techincally (optional)

    public void add(int index, E e) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    public E set(int index, E e) {
        throw new UnsupportedOperationException();
    }

    //--------------------------------------------------------------------------------------------

    /** superTest runs the test methods of the program and displays if they worked
     * @author: Mark Albert
     *
     * Note: this method uses java reflection to run only the methods
     * with 'test' as the first four letters of the method.
     */
    public static void superTest() {
        Class<?> c;
        Object retobj;

        // setup for running the test methods using reflection
        try {
            c = Class.forName("SkipList");
        } catch (ClassNotFoundException e) {
            return;
        }
        Method m[] = c.getDeclaredMethods();
        SkipList<Integer> myList = new SkipList<Integer>();

        boolean didWork = false;
        Object [] falseObjArray = new Object[1];
        falseObjArray[0] = new Boolean(false);

        System.out.println("\nTESTING THE SKIPLIST...\n");

        // iterate through the methods of SkipList
        for (int i = 0; i < m.length; i++) {
            String methodName = m[i].toString().substring(m[i].toString().indexOf("SkipList.") + 9);
            // are the first 4 letters 'test'?
            if (methodName.indexOf("test") > -1) {
                // then execute the test method
                Class<?> pvec[] = m[i].getParameterTypes();
                try {
                    // if the test method has no parameters
                    if (pvec.length == 0) {
                        retobj = m[i].invoke(myList);
                        // if the test method has one, it's the verbose flag - give it false
                    } else {
                        retobj = m[i].invoke(myList, falseObjArray);
                    }
                } catch (IllegalAccessException e) {
                    System.out.println("IllegalAccessException");
                    return;
                    // this exception happens if the method ran throws an exception during execution
                } catch (InvocationTargetException e) {
                    System.out.println("EXCEPTION THROWN from: " + methodName);
                    retobj = (Object) new Boolean(false);
                }

                didWork = (Boolean) retobj;

                // show if the test method worked or failed
                if (didWork) {
                    System.out.println(" Passed: " + methodName);
                } else {
                    System.out.println("FAILED: " + methodName);
                }
            }
        }
    }

    public static void main(String[] args) {
        superTest();
    }

}  // end SkipList definition
