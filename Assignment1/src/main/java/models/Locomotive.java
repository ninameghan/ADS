package models;

public class Locomotive {

    private final int locNumber;
    private final int maxWagons;

    public Locomotive(int locNumber, int maxWagons) {
        this.locNumber = locNumber;
        this.maxWagons = maxWagons;
    }

    //Getters + Setters
    public int getLocNumber() {
        return locNumber;
    }

    public int getMaxWagons() {
        return maxWagons;
    }

    @Override
    public String toString() {
        return String.format("[Loc-%d]", this.getLocNumber());
    }
}
