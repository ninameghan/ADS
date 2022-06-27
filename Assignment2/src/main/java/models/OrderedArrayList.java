package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BinaryOperator;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> ordening;   // the comparator that has been used with the latest sort
    protected int nSorted;
    // the number of items that have been ordered by barcode in the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given ordening comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> ordening) {
        super();
        this.ordening = ordening;
        this.nSorted = 0;
    }

    public Comparator<? super E> getOrdening() {
        return this.ordening;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.ordening = c;
        this.nSorted = this.size();
    }

    @Override
    public void add(int index, E item) {
        if (index >= 0 && index < nSorted) nSorted = index - 1;
        super.add(index, item);
    }

    @Override
    public E remove(int index) {
        // decrement nSorted to remove index
        if (index < nSorted && nSorted != 0) nSorted--;
        return super.remove(index);
    }

    @Override
    public boolean remove(Object object) {
        int index = indexOf(object);
        if (index < nSorted) {
            nSorted--;
        }
        return super.remove(object);
    }

    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.ordening);
        }
    }

    @Override
    public int indexOf(Object item) {
        if (item != null) {
            return indexOfByIterativeBinarySearch((E) item);
        } else {
            return -1;
        }
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            return indexOfByRecursiveBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    private int linearSearch(int left, int right, E searchItem) {
        for (int i = left; i < right; i++) {
            if (this.ordening.compare(get(i), searchItem) == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.ordening
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {
        int min = 0;
        int max = nSorted;

        while (min <= max) {
            int middle = (min + max) / 2;
            E middleValue = this.get(middle);

            //compare the two values using ordening
            int result = this.ordening.compare(middleValue, searchItem);

            if (result < 0) {
                min = middle + 1;
            } else if (result > 0) {
                max = middle - 1;
            } else return middle;
        }

        min = nSorted;
        max = this.size();

        return linearSearch(min, max, searchItem);
    }

    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.ordening
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {
        int recursiveReturnValue = recursiveBinarySearch(searchItem, 0, nSorted);
        if (recursiveReturnValue != -1) return recursiveReturnValue;

        int min = nSorted;
        int max = size();
        return linearSearch(min, max, searchItem);
    }

    public int recursiveBinarySearch(E searchItem, int left, int right) {
        if (right < left || left == size() || nSorted == 0) {
            return -1;
        }

        int middleItem = (left + right) / 2;

        E middleObject = this.get(middleItem);
        int result = ordening.compare(middleObject, searchItem);

        if (result < 0) {
            return recursiveBinarySearch(searchItem, middleItem + 1, right);
        } else if (result > 0) {
            return recursiveBinarySearch(searchItem, left, middleItem - 1);
        } else {
            return middleItem;
        }
    }

    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     *
     * @param newItem
     * @param merger  a function that takes two items and returns an item that contains the merged content of
     *                the two items according to some merging rule.
     *                e.g. a merger could add the value of attribute X of the second item
     *                to attribute X of the first item and then return the first item
     * @return whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        if (newItem == null) return false;
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);

        if (matchedItemIndex < 0) {
            this.add(newItem);
            return true;
        } else {
            E matchedItem = get(matchedItemIndex);
            E mergedItem = merger.apply(matchedItem, newItem);
            this.set(matchedItemIndex, mergedItem);
            return false;
        }
    }
}
