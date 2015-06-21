package geneticAlgorithm;

public class Population {
	
	Candidate[] candidates;
	
	/*
	 * Constructors
	 */
	
	// Create a population
	public Population(int populationSize, boolean initialise) {
		candidates = new Candidate[populationSize];
		// Intitialise the population
		if(initialise) {
			// Loop and create individuals
			for(int i = 0; i < size(); i++) {
				Candidate newCandidate = new Candidate();
				newCandidate.generateCandidate();
			}
		}
	}
	
	/* Getters */
	public Candidate getCandidate(int index) {
		return candidates[index];
	}
	
	public Candidate getFittest() {
		Candidate fittest = candidates[0];
        // Loop through individuals to find fittest
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness() <= getCandidate(i).getFitness()) {
                fittest = getCandidate(i);
            }
        }
        return fittest;
	}
	
	/* Public methods */
	// get population size
	public int size() {
		return candidates.length;
	}
	
	// Save candidate
    public void saveCandidate(int index, Candidate indiv) {
        candidates[index] = indiv;
    }

}
