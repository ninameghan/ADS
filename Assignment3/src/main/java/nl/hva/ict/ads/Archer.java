package nl.hva.ict.ads;

public class Archer{
    public static int MAX_ARROWS = 3;
    public static int MAX_ROUNDS = 10;

    private static int currentID = 135788;

    private final int id;        //id is final so that it cannot be changed once assigned.

    private String firstName;
    private String lastName;
    private int[][] scores;

    /**
     * Constructs a new instance of Archer and assigns a unique id to the instance.
     * Each new instance should be assigned a number that is 1 higher than the last one assigned.
     * The first instance created should have ID 135788;
     *
     * @param firstName the archers first name.
     * @param lastName  the archers surname.
     */
    public Archer(String firstName, String lastName) {
        //initialise the new archer
        this.firstName = firstName;
        this.lastName = lastName;

        //generate and assign a new unique id
        this.id = currentID;
        currentID++;

        //initialise the scores of the archer
        this.scores = new int[MAX_ROUNDS][MAX_ARROWS];
    }

    /**
     * Registers the points for each of the three arrows that have been shot during a round.
     *
     * @param round  the round for which to register the points. First round has number 1.
     * @param points the points shot during the round, one for each arrow.
     */
    public void registerScoreForRound(int round, int[] points) {
        //register the points for a round
        for (int i = 0; i < MAX_ARROWS; i++) {
            this.scores[round - 1][i] = points[i];
        }
    }

    /**
     * Calculates/retrieves the total score of all arrows across all rounds
     *
     * @return total score
     */
    public int getTotalScore() {
        //calculate the total score that the archer has earned across all arrows of all registered rounds
        int sum = 0;
        for (int i = 0; i < this.scores.length; i++) {
            for (int j = 0; j < this.scores[i].length; j++) {
                sum += this.scores[i][j];
            }
        }
        return sum;
    }

    /**
     * compares the scores/id of this archer with the scores/id of the other archer according to
     * the scoring scheme: highest total points -> least misses -> earliest registration
     * The archer with the lowest id has registered first
     *
     * @param other the other archer to compare against
     * @return negative number, zero or positive number according to Comparator convention
     */
    public int compareByHighestTotalScoreWithLeastMissesAndLowestId(Archer other) {
        int missesCurrent = calculateMisses(this);
        int missesOther = calculateMisses(other);
        //compare the archers by their total score
        if (this.getTotalScore() < other.getTotalScore()){
            return 1;
        }else if (this.getTotalScore() > other.getTotalScore()){
            return -1;
        }else if (this.getTotalScore() == other.getTotalScore()){
            //if total score are equal, compare archers by total misses
            if (missesCurrent > missesOther){
                return 1;
            }else if (missesCurrent < missesOther){
                return -1;
            }else {
                //if misses are also equal, compare by id (which archer registered first)
                if (this.getId() > other.getId()){
                    return 1;
                }else if (this.getId() < other.getId()){
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * calculates how many times the given archer missed a shot throughout all rounds
     *
     * @param archer the archer to calculate the misses for
     * @return int, amount of times the archer missed
     */
    public int calculateMisses(Archer archer){
        int misses = 0;
        //cycle through all shots for all rounds and count how many scores were 0 (miss)
        for (int i = 0; i < archer.scores.length; i++) {
            for (int j = 0; j < archer.scores[i].length; j++) {
                if (archer.scores[i][j] == 0){
                    misses += 1;
                }
            }
        }
        return misses;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return String.format("%d (%d) %s %s", this.getId(), this.getTotalScore(), this.getFirstName(), this.getLastName());
    }
}
