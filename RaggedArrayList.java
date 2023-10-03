package student;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * RaggedArrayList.java
 *  
 * Initial starting code by Prof. Boothe Sep 2015
 *
 * The RaggedArrayList is a 2 level data structure that is an array of arrays.
 *  
 * It keeps the items in sorted order according to the comparator.
 * Duplicates are allowed.
 * New items are added after any equivalent items.
 *
 * NOTE: normally fields, internal nested classes and non API methods should 
 *  all be private, however they have been made public so that the tester code 
 *  can set them
 * @author boothe 2015
 * Revised by Anne Applin 2016
 * Revised by Nick Harris and Delaney Paterson (Part 3) 2017
 * Revised by Nick Harris (Part 4) 2017
 * Revised by Shamthosh Devarajan (Part 4) 2017
 * Revised by Nick Harris (pre-Part 5) 2017. Changed "||" in add() loops to "&&".
 * Revised by Nick Harris (pre-Part 5) 2017. Incremented "size" in add().
 * Revised by Nick Harris (Part 5) 2017.
 * 
 * @param <E>
 */

public class RaggedArrayList<E> implements Iterable<E> {

    private static final int MINIMUM_SIZE = 4;    // must be even so when split get two equal pieces

    /**
     *
     */
    public int size;

    /**
     * really is an array of L2Array, but compiler won't let me cast to that
     */
    public Object[] l1Array;  

    /**
     *
     */
    public int l1NumUsed;
    private Comparator<E> comp;


    /**
     * create an empty list
     * always have at least 1 second level array even if empty, makes 
     * code easier 
     * (DONE - do not change)
     * @param c a comparator object
     */
    public RaggedArrayList(Comparator<E> c) {
        size = 0;
        l1Array = new Object[MINIMUM_SIZE];                // you can't create an array of a generic type
        l1Array[0] = new L2Array(MINIMUM_SIZE);  // first 2nd level array
        l1NumUsed = 1;
        comp = c;
    }

    /**
     * nested class for 2nd level arrays
     * (DONE - do not change)
     */
    public class L2Array {

        /**
         *  the array of items
         */
        public E[] items;

        /**
         * number of item in this L2Array with values
         */
        public int numUsed;

        /**
         * Constructor for the L2Array
         * @param capacity the initial length of the array
         */
        public L2Array(int capacity) {
             // you can't create an array of a generic type
            items = (E[]) new Object[capacity]; 
            numUsed = 0;
        }
    }// end of nested class L2Array

    /**
     *  total size (number of entries) in the entire data structure
     *  (DONE - do not change)
     * @return total size of the data structure
     */
    public int size() {
            return size;
    }


    /**
     * null out all references so garbage collector can grab them
     * but keep otherwise empty l1Array and 1st L2Array
     * (DONE - Do not change)
     */
    public void clear() {
        size = 0;
        // clear all but first l2 array
        Arrays.fill(l1Array, 1, l1Array.length, null);  
        l1NumUsed = 1;
        L2Array l2Array = (L2Array) l1Array[0];
        // clear out l2array
        Arrays.fill(l2Array.items, 0, l2Array.numUsed, null); 
        l2Array.numUsed = 0;
    }


    /**
     *  nested class for a list position
     *  used only internally
     *  2 parts: level 1 index and level 2 index
     */
    public class ListLoc {

        /**
         * Level 1 index
         */
         public int level1Index;

        /**
         * Level 2 index
         */
         public int level2Index;

        /**
         * Parameterized constructor
         * @param level1Index input value for property
         * @param level2Index input value for property
         */
        public ListLoc(int level1Index, int level2Index) {
           this.level1Index = level1Index;
           this.level2Index = level2Index;
        }


        /**
         * test if two ListLoc's are to the same location 
         * (done -- do not change)
         * @param otherObj 
         * @return
         */
        public boolean equals(Object otherObj) {
            // not really needed since it will be ListLoc
            if (getClass() != otherObj.getClass()) {
                return false;
            }
            ListLoc other = (ListLoc) otherObj;

            return level1Index == other.level1Index && 
                    level2Index == other.level2Index;
        }
        /**
         * move ListLoc to next entry
         *  when it moves past the very last entry it will be 1 index past the 
         *  last value in the used level 2 array can be used internally to 
         *  scan through the array for sublist also can be used to implement 
         *  the iterator
         * author: Nick Harris
        */
        public void moveToNext() {
            level2Index++;        
        }
    }


    /**
     * find 1st matching entry
     * @param item  we are searching for a place to put.
     * @return ListLoc of 1st matching item or of 1st item greater than the 
     * item if no match this might be an unused slot at the end of a 
     * level 2 array
     * author: Nick Harris
     */
    public ListLoc findFront(E item) {

        int i=0;                        //keep track  
        int rowi_count = 0;             //helps shorten while statement
        if(l1Array[i] != null)
            rowi_count = ((L2Array) l1Array[i]).numUsed;

        //find row i
        while(i < l1Array.length - 1
                && l1Array[i+1] != null
                && rowi_count > 0
                && comp.compare(((L2Array) l1Array[i]).items[rowi_count-1], item) <  0)
        {
            i++;
            rowi_count = ((L2Array) l1Array[i]).numUsed;
        }
        
        //we have found the correct L2Array; give it a name
        L2Array l2Array = (L2Array) l1Array[i];
        
        //find column j
        int j=0;
        while(j < l2Array.numUsed
                && comp.compare(l2Array.items[j], item) < 0){
            j++;
        }
        
        return new ListLoc(i,j);
    }
    /**
     * find location after the last matching entry or if no match, it finds 
     * the index of the next larger item this is the position to add a new 
     * entry this might be an unused slot at the end of a level 2 array
     * @param item
     * @return the location where this item should go 
     * authors: Nick Harris and Delaney Paterson
     */
    public ListLoc findEnd(E item) {
        
        int i=0;                        //keep track  
        int rowi_count = 0;             //helps shorten while statement
        if(l1Array[i] != null)
            rowi_count = ((L2Array) l1Array[i]).numUsed;

        //find row i
        while(i < l1Array.length - 1
                && l1Array[i+1] != null
                && rowi_count > 0
                && comp.compare(((L2Array) l1Array[i]).items[rowi_count-1], item) <=  0)
        {
            i++;
            rowi_count = ((L2Array) l1Array[i]).numUsed;
        }
        
        //we have found the correct L2Array; give it a name
        L2Array l2Array = (L2Array) l1Array[i];
        
        //find column j
        int j=0;
        while(j < l2Array.numUsed
                && comp.compare(l2Array.items[j], item) <= 0){
            j++;
        }
        
        return new ListLoc(i,j);
    }

    /**
     * add object after any other matching values findEnd will give the
     * insertion position
     * @param item
     * @return 
     * author: Nick Harris
     */
    public boolean add(E item) {
        //get location to insert at, and insert
        ListLoc location = findEnd(item);
        int rowX = location.level1Index;
        int colX = location.level2Index;
        
        //move stuff to the left as necessary
        L2Array theRow = (L2Array) l1Array[rowX];
        E jElement = item;
        for(int j= colX; j < theRow.items.length && jElement != null; j++){
            E nextElement = theRow.items[j];
            theRow.items[j] = jElement;
            jElement = nextElement;
        }
        theRow.numUsed++;
        
        //Are we done? In other words is there still space in the row?
        boolean allDone = (theRow.numUsed < theRow.items.length);

        //we may need to restructure the RaggedArrayList
        if(!allDone){
            //do we want to double the length of the row?
            int oldLength = theRow.items.length;
            boolean doubleIt = (oldLength < l1Array.length);
            
            if(doubleIt){
                E[] doubleArray = Arrays.copyOf(theRow.items,2*oldLength);
                l1Array[rowX] = new L2Array(2*oldLength);
                ((L2Array) l1Array[rowX]).items = doubleArray;
                ((L2Array) l1Array[rowX]).numUsed = oldLength;
                allDone = true;
            }
            else{       //otherwise, we're splitting it
                E[] shortArray1 = Arrays.copyOfRange(theRow.items, 0, oldLength/2);
                E[] shortArray2 = Arrays.copyOfRange(theRow.items, oldLength/2, oldLength);
                
                L2Array firstL2 = new L2Array(oldLength);
                firstL2.items = Arrays.copyOf(shortArray1, oldLength);
                firstL2.numUsed = oldLength / 2;
                L2Array secondL2 = new L2Array(oldLength);
                secondL2.items = Arrays.copyOf(shortArray2, oldLength);
                secondL2.numUsed = oldLength / 2;
                
                //place first L2:
                l1Array[rowX] = firstL2;
                
                //place second L2:
                //move stuff down as necessary
                Object iElement = secondL2;
                for(int i= rowX+1; i < l1Array.length && iElement != null; i++){
                    Object nextElement = l1Array[i];
                    l1Array[i] = iElement;
                    iElement = nextElement;
                }
                l1NumUsed++;
                
                //are we done? is there space remaining?
                allDone = (l1NumUsed < l1Array.length);

                //double the l1Array if necessary
                if(!allDone){
                    Object[] bigArray = Arrays.copyOf(l1Array, 2*l1Array.length);
                    l1Array = bigArray;
                    allDone = true;
                }
            }
        }
        size++;
        return true;
    }

    /**
     * check if list contains a match
     * @param item
     * @return 
     * author: Nick Harris
     */
    public boolean contains(E item) {
        
        ListLoc loc = findFront(item);
        L2Array row = (L2Array) l1Array[loc.level1Index];
        return comp.compare(item, row.items[loc.level2Index]) == 0;
    }

    /**
     * copy the contents of the RaggedArrayList into the given array
     *
     * @param a - an array of the actual type and of the correct size
     * @return the filled in array
     * Author: Nick Harris
     */
    public E[] toArray(E[] a) throws IllegalArgumentException
    {
        
//      System.out.println("NDH: toArray input size = "+a.length);
        
        if(size != a.length){
            System.err.println("\nERROR: toArray() input wrong size.");
            throw new IllegalArgumentException();
        }
        
        Iterator<E> iterator = iterator();

        int aIndex = 0;
        while(iterator.hasNext()){
            a[aIndex] = iterator.next();
            aIndex++;
        }
        return a;
    }

    /**
     * returns a new independent RaggedArrayList whose elements range from
     * fromElemnt, inclusive, to toElement, exclusive the original list is
     * unaffected findStart and findEnd will be useful
     *
     * @param fromElement
     * @param toElement
     * @return the sublist
     * Author: Nick Harris
     */
    public RaggedArrayList<E> subList(E fromElement, E toElement) {

        RaggedArrayList<E> result = new RaggedArrayList<E>(comp);
        
        ListLoc loc = findFront(fromElement); 
        L2Array row = (L2Array) l1Array[loc.level1Index];
        ListLoc end = findFront(toElement); 

        while(!loc.equals(end)){
            result.add(row.items[loc.level2Index]); // add element
            
            //find next
            loc.moveToNext();
            if(row.items[loc.level2Index] == null){ //go to next line
                loc = new ListLoc(loc.level1Index+1,0);
                row = (L2Array) l1Array[loc.level1Index];
            }
        }

        return result;
    }

    /**
     * returns an iterator for this list this method just creates an instance of
     * the inner Itr() class (DONE)
     * @return 
     */
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Iterator is just a list loc it starts at (0,0) and finishes with index2 1
     * past the last item in the last block
     */
    private class Itr implements Iterator<E> {

        private ListLoc loc;

        /*
         * create iterator at start of list
         * (DONE)
         */
        Itr() {
            loc = new ListLoc(0, 0);
        }

        /**
         * check if more items
         * Author: Nick Harris
         */
        public boolean hasNext() {
            
            L2Array myRow = (L2Array) l1Array[loc.level1Index];
            if(myRow == null)
                return false;
            else
                return myRow.items[loc.level2Index] != null;
        }

        /**
         * return item and move to next throws NoSuchElementException if off end
         * of list
         * Author: Nick Harris
         */
        public E next() {
            //get result
            L2Array myRow = (L2Array) l1Array[loc.level1Index];
            if (myRow == null)
                throw new NoSuchElementException();
            E result = myRow.items[loc.level2Index];
            if (myRow == null)
                throw new NoSuchElementException();

            //advance position
            if (myRow.items[loc.level2Index+1] != null)
                loc.level2Index++;
            else{
                loc.level1Index++;
                loc.level2Index = 0;
            }
            
            //return result
            return result;
        }

        /**
         * Remove is not implemented. Just use this code. 
         * (DONE)
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}