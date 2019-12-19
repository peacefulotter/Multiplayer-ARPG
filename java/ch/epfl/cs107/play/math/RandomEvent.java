package ch.epfl.cs107.play.math;


/**
 * Random Event implement notion of Bernoulli experiment with probability p of success
 */
final class RandomEvent {

    // Probability of success of the Bernoulli experiment
    private final float p;
    // Indicate if the experiment already happened at least once
    private boolean happened;
    // Indicate if the experiment can happen only once (i.e always fails after first success)
    private final boolean onlyOnce;


    /**
     * Default Bernoulli Experiment Constructor
     * Notice: by default the event can happen multiple times
     * @param p (float): the probability of success
     */
    public RandomEvent(float p){
        this(p, false);
    }

    /**
     * Extended Bernoulli Experiment Constructor
     * @param p (float): the probability of success
     * @param onlyOnce (boolean): Specify if event can happen only once
     */
    private RandomEvent(float p, boolean onlyOnce){
        this.p = Math.max(0.0f, Math.min(1.0f, p));
        this.onlyOnce = onlyOnce;
        this.happened = false;
    }


    /**
     * Random Constructor for Bernoulli experiment
     * (i.e p is selected randomly)
     * Notice: the random event can happen multiple times
     */
    public RandomEvent(){
        this.p = RandomGenerator.getInstance().nextFloat();
        this.onlyOnce = false;
        this.happened = false;
    }


    /**
     * Run the Bernoulli experiment and return if it succeed
     * @return (boolean): true if the experiment succeed, false otherwise
     */
    public boolean happend(){

        if(this.happened && this.onlyOnce)
            return false;

        float threshold = RandomGenerator.getInstance().nextFloat();
        if(threshold <= p){
            this.happened = true;
            return true;
        }
        return false;
    }


    /**
     * Random integer generator tool:
     * Generate an int between the two given bounds
     * @param min (int): Low bound, inclusive
     * @param max (int): High bound, exclusive
     * @return (int): between [min, max)
     */
    public int nextInt(int min, int max){
        return RandomGenerator.getInstance().nextInt(max - min) + min;
    }

}
