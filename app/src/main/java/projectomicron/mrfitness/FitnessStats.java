package projectomicron.mrfitness;

/**
 * This class represents the contents of a user's fitness stats.
 * Created by Emanuel Guerrero on 12/2/2015.
 */
public class FitnessStats {
    /**
     * Instance variables that are only visible in the FitnessStats class. These represent the
     * contents of a user's fitness stats.
     */
    private int weight;
    private int heightFeet;
    private int heightInches;
    private int BMI;

    /**
     * Constructs a FitnessStats object.
     * @precondition aWeigth > 0
     * @precondition aHeightFeet > 0
     * @precondition aHeightInches > 0
     * @precondition aBMI > 0
     * @param aWeight the weight of the user
     * @param aHeightFeet the feet part of the height of the user
     * @param aHeightInches the inches part of the height of user
     */
    public FitnessStats(int aWeight, int aHeightFeet, int aHeightInches, int aBMI) {
        this.weight = aWeight;
        this.heightFeet = aHeightFeet;
        this.heightInches = aHeightInches;
        this.BMI = aBMI;
    }

    /**
     * Gets the weight of the user.
     * @precondition weight > 0
     * @return the weight of the user
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Gets the feet part of the user's height.
     * @precondition heightFeet > 0
     * @return the feet component of the user's height
     */
    public int getHeightFeet() {
        return heightFeet;
    }

    /**
     * Gets the inches part of the user's height.
     * @precondition heightInches > 0
     * @return the inches component of the user's height
     */
    public int getHeightInches() {
        return heightInches;
    }

    /**
     * Gets the BMI of the user.
     * @precondition BMI > 0
     * @return the BMI of the user
     */
    public int getBMI() {
        return BMI;
    }
}
