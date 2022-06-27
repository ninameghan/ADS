package nl.hva.ict.ads;

import java.util.Comparator;
import java.util.List;

public class SorterImpl<E> implements Sorter<E>{

    /**
     * Sorts all items by selection or insertion sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    public List<E> selInsSort(List<E> items, Comparator<E> comparator) {
        // insertion sort

        // a for loop to loop through the items of the list
        for (int i = 0; i < items.size(); i++) {
            // because the i is already in use I use the j so I can use that in the while loop
            int j = i;
            // get an item with the type E
            E temp = items.get(i);

            // compare with the comparator when the j item is bigger then the i item
            while (j > 0 && comparator.compare(items.get(j - 1), temp) > 0) {
                // set two items to equal
                items.set((j), (items.get(j - 1)));
                j -= 1;
            }
            // set the list and what you want to compare equal to each other
            items.set(j, temp);
        }
        return items;
    }

    /**
     * Sorts all items by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    public List<E> quickSort(List<E> items, Comparator<E> comparator) {
        // sort the complete list of items from position 0 till size-1, encluding position size
        this.quickSortPart(items, 0, items.size()-1, comparator);
        return items;
    }

    /**
     * Sorts all items between index positions 'from' and 'to' inclusive by quick sort using the provided comparator
     * for deciding relative ordening of two items
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     *
     * @param items
     * @param comparator
     * @return  the items sorted in place
     */
    private void quickSortPart(List<E> items, int from, int to, Comparator<E> comparator) {
        // only one item or less to sort...
        if (to <= from) return;

        // partition the subarray around a pivot, and return the pivot position
        int pivotIndex = partition(items, from, to, comparator);

        // Sort the left part before the pivot
        quickSortPart(items, from, pivotIndex - 1, comparator);
        // Sort the right part after the pivot
        quickSortPart(items, pivotIndex + 1, to, comparator);
    }

    // added method
    private int partition(List<E> items, int begin, int end, Comparator<E> comparator) {
        // choose the first item in the list as pivot
        E pivot = items.get(begin);

        // collect all items smaller than or equal to the pivot at the left of lo
        int lo = begin + 1;

        // collect all items greater than or equal to the pivot at the right of hi
        int hi = end;

        // shuffle items to left or to right until all are in place
        do {
             // skip over smaller items that are at the left already: items[lo] < pivot
            while (lo <= hi && comparator.compare(items.get(lo), pivot) < 0){
                lo++;
            }

            // skip over greater items that are at the right already: items[hi] > pivot
            while (lo <= hi && comparator.compare(items.get(hi), pivot) > 0) {
                hi--;
            }

            // swap items[lo] with items[hi] and proceed
            if (lo <= hi) {
                E temp = items.get(lo); items.set(lo, items.get(hi)); items.set(hi, temp);
                lo++; hi--;
            }

        } while (lo <= hi);

        // now lo > hi && from <= hi <= to && lo > from
        // hi is a suitable pivot index
        items.set(begin, items.get(hi)); items.set(hi, pivot);
        return hi;
    }

    /**
     * Identifies the lead collection of numTops items according to the ordening criteria of comparator
     * and organizes and sorts this lead collection into the first numTops positions of the list
     * with use of (zero-based) heapSwim and heapSink operations.
     * The remaining items are kept in the tail of the list, in arbitrary order.
     * Items are sorted 'in place' without use of an auxiliary list or array or other positions in items
     * @param numTops       the size of the lead collection of items to be found and sorted
     * @param items
     * @param comparator
     * @return              the items list with its first numTops items sorted according to comparator
     *                      all other items >= any item in the lead collection
     */
    public List<E> topsHeapSort(int numTops, List<E> items, Comparator<E> comparator) {
        // check 0 < numTops <= items.size()
        if (numTops <= 0) return items;
        else if (numTops > items.size()) return quickSort(items, comparator);

        // the lead collection of numTops items will be organised into a (zero-based) heap structure
        // in the first numTops list positions using the reverseComparator for the heap condition.
        // that way the root of the heap will contain the worst item of the lead collection
        // which can be compared easily against other candidates from the remainder of the list
        Comparator<E> reverseComparator = comparator.reversed();

        // initialise the lead collection with the first numTops items in the list
        for (int heapSize = 2; heapSize <= numTops; heapSize++) {
            // repair the heap condition of items[0..heapSize-2] to include new item items[heapSize-1]
            heapSwim(items, heapSize, reverseComparator);
        }

        // insert remaining items into the lead collection as appropriate
        for (int i = numTops; i < items.size(); i++) {
            // loop-invariant: items[0..numTops-1] represents the current lead collection in a heap data structure
            //  the root of the heap is the currently trailing item in the lead collection,
            //  which will lose its membership if a better item is found from position i onwards
            E item = items.get(i);
            E worstLeadItem = items.get(0);
            if (comparator.compare(item, worstLeadItem) < 0) {
                // item < worstLeadItem, so shall be included in the lead collection
                items.set(0, item);
                // demote worstLeadItem back to the tail collection, at the orginal position of item
                items.set(i, worstLeadItem);
                // repair the heap condition of the lead collection
                heapSink(items, numTops, reverseComparator);
            }
        }

        // the first numTops positions of the list now contain the lead collection
        // the reverseComparator heap condition applies to this lead collection
        // now use heapSort to realise full ordening of this collection
        for (int i = numTops-1; i > 0; i--) {
            // loop-invariant: items[i+1..numTops-1] contains the tail part of the sorted lead collection
            // position 0 holds the root item of a heap of size i+1 organised by reverseComparator
            // this root item is the worst item of the remaining front part of the lead collection

            // TODO swap item[0] and item[i];
            //  this moves item[0] to its designated position


            // TODO the new root may have violated the heap condition
            //  repair the heap condition on the remaining heap of size i

        }
        // alternatively we can realise full ordening with a partial quicksort:
        // quickSortPart(items, 0, numTops-1, comparator);

        return items;
    }

    /**
     * Repairs the zero-based heap condition for items[heapSize-1] on the basis of the comparator
     * all items[0..heapSize-2] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    private void heapSwim(List<E> items, int heapSize, Comparator<E> comparator) {
        // TODO swim items[heapSize-1] up the heap until
        //      i==0 || items[(i-1]/2] <= items[i]

    }
    /**
     * Repairs the zero-based heap condition for its root items[0] on the basis of the comparator
     * all items[1..heapSize-1] are assumed to satisfy the heap condition
     * The zero-bases heap condition says:
     *                      all items[i] <= items[2*i+1] and items[i] <= items[2*i+2], if any
     * or equivalently:     all items[i] >= items[(i-1)/2]
     * @param items
     * @param heapSize
     * @param comparator
     */
    private void heapSink(List<E> items, int heapSize, Comparator<E> comparator) {
        // TODO sink items[0] down the heap until
        //      2*i+1>=heapSize || (items[i] <= items[2*i+1] && items[i] <= items[2*i+2])

    }
}
