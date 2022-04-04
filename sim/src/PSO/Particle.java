//package PSO;
//
//import java.util.Random;
//
///**
// * Represents a particle from the Particle Swarm Optimization algorithm.
// */
//class Particle {
//	private String name;
//    private Vector position;        // Current position.
//    private Vector velocity;
//    private Vector bestPosition;    // Personal best solution.
//    private double bestEval = Double.POSITIVE_INFINITY;        // Personal best value.
//  
//    /**
//     * Construct a Particle with a random starting position.
//     * @param beginRange    the minimum xyz values of the position (inclusive)
//     * @param endRange      the maximum xyz values of the position (exclusive)
//     */
//    Particle (String name, int nodes) {
//        position = new Vector(nodes);
//        velocity = new Vector(nodes);
//        setRandomPosition();
//        bestPosition = position.clone();
//        this.name = name;
//    }
//
////   
//
//    private void setRandomPosition () {
//    	double sum = 0;
//    	for(int i = 0; i< position.getVectorCoordinate().length; i++){
//    		position.setPAt(i, rand());
//    		sum += position.getPAt(i);
//    	}
//    	
//    	for(int i = 0; i<position.getVectorCoordinate().length; i++){
//    		double value = position.getPAt(i);
//    		position.setPAt(i, value/sum);
//    	}
//    }
//
//    
//    /**
//     * Generate a random number between 0.0 and 1.0.
//     * @return              the randomly generated value
//     */
//    private static double rand () {
//        Random r = new java.util.Random();
//        return r.nextDouble(); //generate random from [0.0,1.0)
//    }
//
//    
//    /**
//     * Update the personal best if the current evaluation is better.
//     */
//    void updatePersonalBest (double eval) {
//    	if (eval < bestEval) {
//    		bestPosition = position.clone();
//    		bestEval = eval;
//    	}
//    }
//
//    /**
//     * Get a copy of the position of the particle.
//     * @return  the x position
//     */
//    Vector getPosition () {
//        return position.clone();
//    }
//
//    /**
//     * Get a copy of the velocity of the particle.
//     * @return  the velocity
//     */
//    Vector getVelocity () {
//        return velocity.clone();
//    }
//
//    /**
//     * Get a copy of the personal best solution.
//     * @return  the best position
//     */
//    Vector getBestPosition() {
//        return bestPosition.clone();
//    }
//
//    /**
//     * Get the value of the personal best solution.
//     * @return  the evaluation
//     */
//    double getBestEval () {
//        return bestEval;
//    }
//
//    /**
//     * Update the position of a particle by adding its velocity to its position.
//     */
//    void updatePosition () {
//        this.position.add(velocity.getVectorCoordinate());
//    }
//
//    /**
//     * Set the velocity of the particle.
//     * @param velocity  the new velocity
//     */
//    void setVelocity (Vector velocity) {
//        this.velocity = velocity.clone();
//    }
//    
//    void setBestEval (double bestEval){
//    	this.bestEval = bestEval;
//    }
//    
//    void setPosition (Vector pos){
//    	this.position = pos;
//    }
//    
//    String getName(){
//    	return name;
//    }
//}
//
