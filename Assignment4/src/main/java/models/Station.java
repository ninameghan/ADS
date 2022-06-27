package models;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Station {
    private final int stn;
    private final String name;
    private NavigableMap<LocalDate, Measurement> measurements;

    public Station(int id, String name) {
        this.stn = id;
        this.name = name;
        // Treemap extends the NavigableMap
        this.measurements = new TreeMap<>();
    }

    public Collection<Measurement> getMeasurements() {
        // using a hashset to store the measurements randomly
        return measurements.values();
    }

    public int getStn() {
        return stn;
    }

    public String getName() {
        return name;
    }

    /**
     * import station number and name from a text line
     *
     * @param textLine
     * @return a new Station instance for this data
     * or null if the data format does not comply
     */
    public static Station fromLine(String textLine) {
        String[] fields = textLine.split(",");
        if (fields.length < 2) return null;
        try {
            return new Station(Integer.parseInt(fields[0].trim()), fields[1].trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Add a collection of new measurements to this station.
     * Measurements that are not related to this station
     * and measurements with a duplicate date shall be ignored and not added
     *
     * @param newMeasurements
     * @return the net number of measurements which have been added.
     */
    public int addMeasurements(Collection<Measurement> newMeasurements) {
        int oldSize = this.getMeasurements().size();
        Stream<Measurement> filteredMeasurements = newMeasurements.stream()
                .filter(mes -> mes.getStation().getStn() == this.getStn())
                .filter(mes -> !measurements.containsKey(mes.getDate()));

        filteredMeasurements.forEach(measurement -> {
            measurements.put(measurement.getDate(), measurement);
        });
        return this.getMeasurements().size() - oldSize;
    }

    /**
     * calculates the all-time maximum temperature for this station
     *
     * @return the maximum temperature ever measured at this station
     * returns Double.NaN when no valid measurements are available
     */
    public double allTimeMaxTemperature() {
        double maxEntry = Double.NaN;

        // if the measurement is not empty
        if (!this.measurements.isEmpty()) {
            maxEntry = this.measurements.values().stream()
                    // get the max temperature of the station
                    .mapToDouble(Measurement::getMaxTemperature).max().getAsDouble();
        }

        return maxEntry;
    }

    /**
     * @return the date of the first day of a measurement for this station
     * returns Optional.empty() if no measurements are available
     */
    public Optional<LocalDate> firstDayOfMeasurement() {
        Optional<LocalDate> firstDate = Optional.empty();

        // if the measurement is not empty
        if (!this.measurements.isEmpty()) {
            firstDate = this.measurements.values().stream()
                    // get the first date of the measurement
                    .map(Measurement::getDate).min(Comparator.comparing(LocalDate::toEpochDay));
        }
        return firstDate;
    }

    /**
     * calculates the number of valid values of the data field that is specified by the mapper
     * invalid or empty values should be are represented by Double.NaN
     * this method can be used to check on different types of measurements each with their own mapper
     *
     * @param mapper the getter method of the data field to be checked.
     * @return the number of valid values found
     */
    public int numValidValues(Function<Measurement, Double> mapper) {
        return (int) getMeasurements().stream().map(mapper)
                .filter(m -> !Double.isNaN(m)).count();
    }

    /**
     * calculates the total precipitation at this station
     * across the time period between startDate and endDate (inclusive)
     *
     * @param startDate the start date of the period of accumulation (inclusive)
     * @param endDate   the end date of the period of accumulation (inclusive)
     * @return the total precipitation value across the period
     * 0.0 if no measurements have been made in this period.
     */
    public double totalPrecipitationBetween(LocalDate startDate, LocalDate endDate) {
        // get the days between the start and enddate of the precipitation included the start and enddate
        return this.measurements.subMap(startDate, true, endDate, true)
                // get the measurement of the dates and calculate the total
                .values().stream().map(Measurement::getPrecipitation).filter(isValid ->
                        !Double.isNaN(isValid)).mapToDouble(Double::doubleValue).sum();
    }

    /**
     * calculates the average of all valid measurements of the quantity selected by the mapper function
     * across the time period between startDate and endDate (inclusive)
     *
     * @param startDate the start date of the period of averaging (inclusive)
     * @param endDate   the end date of the period of averaging (inclusive)
     * @param mapper    a getter method that obtains the double value from a measurement instance to be averaged
     * @return the average of all valid values of the selected quantity across the period
     * Double.NaN if no valid measurements are available from this period.
     */
    public double averageBetween(LocalDate startDate, LocalDate endDate, Function<Measurement, Double> mapper) {
        // get the start and enddate of the valid measurements included the start and enddate
        return this.measurements.subMap(startDate, true, endDate, true)
                // get the valid measurements and get the average
                .values().stream().mapToDouble(mapper::apply).filter(isValid ->
                        // if there is no average then it is not a number
                        !Double.isNaN(isValid)).average().orElse(Double.NaN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return stn == station.stn && Objects.equals(name, station.name) && Objects.equals(measurements, station.measurements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stn);
    }

    @Override
    public String toString() {
        return String.format("%d/%s", getStn(), getName());
    }

}


