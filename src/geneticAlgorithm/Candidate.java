package geneticAlgorithm;

/**
 * @author NgFamily
 * 
 * Stores a candidate of feature weights
 *
 */
public class Candidate {

	public static final int NUMBER_OF_FEATURES = 6;
	
	public final float minWeight = -10;
	public final float maxWeight = 10;
	
	
	// holds our solution
	private double[] mWeights = new double[NUMBER_OF_FEATURES];
	
	// cache
	private double fitness = 0;
	private int distance = 0;		// contextualise later
	
	// constructs a blank candidate
	public Candidate() {
		for(int i = 0; i < NUMBER_OF_FEATURES; i++) mWeights[i] = 0;
	}
	
	// create a random candidate
	public void generateCandidate() {
		for(int i = 0; i < NUMBER_OF_FEATURES; i++) {
			double weight = Math.random() * (maxWeight - minWeight) + minWeight;
			mWeights[i] = weight;
		}
	}
	
	// getters and setters
	
	public double getFitness() {
		if(fitness == 0) fitness = FitnessCalc.getFitness(this);
		return fitness;
	}
	
	public double getGene(int index) {
		return mWeights[index];
	}

	public void setGene(int index, double value) {
		mWeights[index] = value;
		fitness = 0;
	}
	
	public String toString() {
		StringBuilder string = new StringBuilder();
		string.append("Weights: ");
		string.append(mWeights[0]);
		for(int i = 1; i < NUMBER_OF_FEATURES; i++) 
			string.append(", " + mWeights[i]);
		return string.toString();
	}

	public int size() {
		return mWeights.length;
	}
	
}
