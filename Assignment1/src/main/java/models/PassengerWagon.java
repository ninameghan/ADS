package models;

public class PassengerWagon extends Wagon{

    private final int numberOfSeats;

    public PassengerWagon(int wagonId, int numberOfSeats) {
        super(wagonId);
        this.numberOfSeats = numberOfSeats;
    }

    //Getters + Setters
    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    @Override
    public String toString() {
        return String.format("[Wagon-%d]", super.getId());
    }
}
