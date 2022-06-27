package models;

public class Train {
    private final String origin;
    private final String destination;
    private final Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /* three helper methods that are useful in other methods */
    public boolean hasWagons() {
        return this.getFirstWagon() != null;
    }

    public boolean isPassengerTrain() {
        return this.getFirstWagon() instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        return this.getFirstWagon() instanceof FreightWagon;
    }

    /**
     * @return the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        if (!this.hasWagons()) {
            return 0;
        } else {
            return this.getFirstWagon().getTailLength() + 1;
        }
    }

    /**
     * @return the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        Wagon last = null;
        for (Wagon i = this.getFirstWagon(); i != null; i = i.getNextWagon()) {
            last = i;
        }
        return last;
    }

    /**
     * @return the total number of seats on a passenger train
     * (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        int noOfSeats = 0;
        if (this.isPassengerTrain()) {
            for (int i = 1; i <= this.getNumberOfWagons(); i++) {
                PassengerWagon temp = (PassengerWagon) this.findWagonAtPosition(i);
                noOfSeats += temp.getNumberOfSeats();
            }
        }
        return noOfSeats;
    }


    /**
     * calculates the total maximum weight of a freight train
     *
     * @return the total maximum weight of a freight train
     * (return 0 for a passenger train)
     */
    public int getTotalMaxWeight() {
        int maxWeight = 0;
        if (this.isFreightTrain()) {
            for (int i = 1; i <= this.getNumberOfWagons(); i++) {
                FreightWagon temp = (FreightWagon) this.findWagonAtPosition(i);
                maxWeight += temp.getMaxWeight();
            }
        }
        return maxWeight;
    }

    /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     *
     * @param position
     * @return the wagon found at the given position
     * (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        int wPosition = 1;
        if (firstWagon != null && position <= getNumberOfWagons() && position > 0) {
            Wagon wagon = firstWagon;
            while (wagon.hasNextWagon() && wPosition != position) {
                wPosition++;
                wagon = wagon.getNextWagon();
            }
            return wagon;
        }
        return null;
    }

    /**
     * Finds the wagon with a given wagonId
     *
     * @param wagonId
     * @return the wagon found
     * (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon found = this.getFirstWagon();
        while (found != null) {
            if (found.getId() == wagonId) {
                return found;
            }
            found = found.getNextWagon();
        }
        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to the train
     * Verfies of the type of wagons match the type of train (Passenger or Freight)
     * Verfies that the capacity of the engine is sufficient to pull the additional wagons
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return
     */
    public boolean canAttach(Wagon wagon) {
        boolean canAttach = false;
        if (!this.hasWagons() || wagon.getClass() == this.getFirstWagon().getClass()) {
            if (!wagon.hasPreviousWagon()) {
                if (engine.getMaxWagons() >= this.getNumberOfWagons() + (wagon.getTailLength() + 1)) {
                    canAttach = true;
                }
            }
        }
        return canAttach;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon wagon) {
        // no wagon can be attached
        if (!canAttach(wagon)) {
            return false;
        }

        // the wagon is has no value
        if (firstWagon == null) {
            // set the first wagon
            setFirstWagon(wagon);
        } else {
            if (wagon.hasPreviousWagon()) {
                wagon.detachFront();
            }
            firstWagon.getLastWagonAttached().attachTail(wagon);
        }
        return true;
    }


    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon wagon) {
        Wagon temp;
        if (this.findWagonById(wagon.getId()) == null) {
            if (canAttach(wagon)) {
                if (this.hasWagons()) {
                    temp = this.getFirstWagon();
                    this.getFirstWagon().detachFront();
                    temp.reAttachTo(wagon.getLastWagonAttached());
                }
                this.setFirstWagon(wagon);
                return true;
            }
        }
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at/before the given wagon position in the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible of the engine has insufficient capacity
     * or the given position is not valid in this train)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     * @return whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon wagon) {
        int wagonPosition = 1;

        // the first wagon has a value
        if (firstWagon != null) {
            Wagon next = firstWagon;
            // get the next wagon while it has one
            while (next.hasNextWagon() && position != wagonPosition) {
                next = next.getNextWagon();
                wagonPosition++;
            }

            // if there is one wagon and the maximum of wagon is not reached for the engine
            if (position == wagonPosition && (wagon.getTailLength() + getNumberOfWagons()) <= engine.getMaxWagons()) {
                if (wagon.hasNextWagon()) {
                    Wagon sequenceOfTrains;
                    sequenceOfTrains = next.getNextWagon();
                    wagon.getLastWagonAttached().attachTail(sequenceOfTrains);
                    next.detachTail();
                }
                next.attachTail(wagon);
                return true;
            }
            // there is only one wagon and the engine can only hold that much wagons
        } else if (wagonPosition == position && engine.getMaxWagons() >= wagonPosition) {
            firstWagon = wagon;
            return true;
        }
        // if there are no wagons
        return false;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param wagonId
     * @param toTrain
     * @return whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // find the wagon by id
        Wagon wagonById = findWagonById(wagonId);
        Wagon current = firstWagon;

        // if there is no wagon with id
        if (wagonById == null) {
            return false;
        }

        // checks on the firstwagon
        if (toTrain.firstWagon != null && this.firstWagon instanceof FreightWagon) {
            if (toTrain.firstWagon instanceof PassengerWagon) return false;
        }

        if (toTrain.firstWagon != null && this.firstWagon instanceof PassengerWagon) {
            if (toTrain.firstWagon instanceof FreightWagon) return false;
        }

        // if there are wagons
        if (this.hasWagons()) {
            Wagon newSequenceWagons = wagonById.getNextWagon();

            // if there is not previous wagon then update the id of the next wagon
            if (!wagonById.hasPreviousWagon()) {
                this.setFirstWagon(wagonById.getNextWagon());
            }

            wagonById.detachFront();

            // if there are next wagons
            if (wagonById.hasNextWagon()) {
                newSequenceWagons.detachFront();
                current.attachTail(newSequenceWagons);
            }

            // detach the tail of the wagon
            wagonById.detachTail();

            // if there are wagons at the train linked
            if (toTrain.firstWagon != null) {
                if (canAttach(wagonById)) {
                    wagonById.reAttachTo(toTrain.getLastWagonAttached());
                }
            } else {
                toTrain.setFirstWagon(wagonById);
            }
        }
        return true;
    }

    /**
     * Tries to split this train before the given position and move the complete sequence
     * of wagons from the given position to the rear of toTrain.
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     *
     * @param position
     * @param toTrain
     * @return whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        // find the wagon at the position
        Wagon wagonAtPosition = findWagonAtPosition(position);

        // if the position is null return
        if (wagonAtPosition == null) {
            return false;
        }

        // if the wagon at the position is equal to the first wagon
        if (wagonAtPosition == this.firstWagon) {
            // and if the wagon at the position has a next wagon it cannot be split
            if (wagonAtPosition.hasNextWagon()) {
                return false;
                // if the wagon at the position doesn't have a next wagon then there is no first wagon
            } else if (!wagonAtPosition.hasNextWagon()) {
                this.firstWagon = null;
            }
        }

        // detach the front to split
        wagonAtPosition.detachFront();

        // if the train can attach the wagon
        if (toTrain.canAttach(wagonAtPosition)) {
            // attach the wagon
            toTrain.attachToRear(wagonAtPosition);
            return true;
        }
        return false;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     * the previous wagon of the last wagon becomes the second wagon
     * etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // get the amount of wagons
        int reverseWagons = this.getNumberOfWagons();
        Wagon frontWagon;

        // the first wagon is now the last wagon
        Wagon reverseFirstToLastWagon = firstWagon;

        // if it doesn't has wagons or the first wagon doesn't has a next wagon
        if (!this.hasWagons() || !firstWagon.hasNextWagon()) {
            return;
        }

        // detach the front so it can be reversed
        firstWagon.detachFront();

        // loop through all the wagons
        for (int i = 0; i < reverseWagons; i++) {
            // attach the lastwagon
            frontWagon = reverseFirstToLastWagon.getLastWagonAttached();

            frontWagon.detachFront();

            // if the first wagon was not yet reversed
            if (firstWagon == reverseFirstToLastWagon) {
                setFirstWagon(frontWagon);
            } else {
                // attach the last wagon and attach the tail to the first wagon
                firstWagon.getLastWagonAttached().attachTail(frontWagon);
            }
        }
    }

    //Getters + Setters
    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     *
     * @param wagon the first wagon of a sequence of wagons to be attached
     */
    public void setFirstWagon(Wagon wagon) {
        this.firstWagon = wagon;
    }

    public String getAllWagons() {
        StringBuilder wagons = new StringBuilder();
        for (int i = 1; i <= this.getNumberOfWagons(); i++) {
            wagons.append(this.findWagonAtPosition(i).toString());
        }
        return wagons.toString();
    }

    @Override
    public String toString() {
        return String.format("%s%s with %d wagons from %s to %s.", this.getEngine(), this.getAllWagons(),
                this.getNumberOfWagons(), this.getOrigin(), this.getDestination());
    }
}
