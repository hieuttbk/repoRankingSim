package PSOSim;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import multihop.RTable;

/**
 * Represents a particle from the Particle Swarm Optimization algorithm.
 * @prams position is value of F() as time
 */
class PSOParticle {
    public enum FunctionType {
        A,B,C
    }
	
	private String name;
    private PSOVector position;        // Current position.
    private PSOVector velocity;
    private PSOVector bestPosition;    // Personal best solution.
    private double bestEval = Double.POSITIVE_INFINITY;        // Personal best value.
    
    private FunctionType function; 
	HashMap<Integer, List<RTable>> mapRTable = new HashMap<Integer, List<RTable>>();

  
    /**
     * Construct a Particle with a random starting position.
     * @param mapRTable 
     * @param beginRange    the minimum xyz values of the position (inclusive)
     * @param endRange      the maximum xyz values of the position (exclusive)
     */
    PSOParticle (FunctionType function, String name, int dim, HashMap<Integer, List<RTable>> mapRTable ) {
    	//System.out.println("---------------------init1");

    	this.function = function;
    	position = new PSOVector(dim);
        velocity = new PSOVector(dim);
        this.name = name;
        this.mapRTable = mapRTable;
        
        setRandomPosition();
        bestPosition = position.clone();
        
        
        //TODO ?
//        bestPosition = velocity.clone();
//        bestEval = eval();
        
    }

//   
    // create p = rand/ sum(rand) 
    private void setRandomPosition () {
    	//System.out.println("---------------------init2");
    			
    	
    	int j=0;
    	
    	for (Integer id:mapRTable.keySet()) {
    		List<RTable> rTable = mapRTable.get(id);
    	 	PSOVector p = new PSOVector(rTable.size());
        	double sum = 0;
        	for(int i = 0; i< p.getVectorCoordinate().length; i++){
        		p.setById(i, rand());
        		sum += p.getById(i);
        	}
        	
        	for(int i = 0; i<p.getVectorCoordinate().length; i++){
        		double value = p.getById(i);
        		p.setById(i, value/sum);
        		position.setById(j, value/sum);
        		j++;

        		
        	}
        	
        //	System.out.println("p in req: " +  id);

//        	System.out.println(p.toStringOutput());

    	}
    //	System.out.println(position.toStringOutput());

    	//    	mapRTable.forEach((reqId,rTable)->{
//    	PSOVector p = new PSOVector(rTable.size());
//    	double sum = 0;
//    	for(int i = 0; i< p.getVectorCoordinate().length; i++){
//    		p.setById(i, rand());
//    		sum += p.getById(i);
//    	}
//    	
//    	for(int i = 0; i<p.getVectorCoordinate().length; i++){
//    		double value = p.getById(i);
//    		p.setById(i, value/sum);
//    		//j++;
//    		
//    	}
//    	});
    	
    	
    	
    	
//    	double sum = 0;
//    	for(int i = 0; i< position.getVectorCoordinate().length; i++){
//    		position.setById(i, rand());
//    		sum += position.getById(i);
//    	//	System.out.println(">>>>DEBUG: " + position.getById(i));
//    	}
//    	
//    	for(int i = 0; i<position.getVectorCoordinate().length; i++){
//    		double value = position.getById(i);
//    		position.setById(i, value/sum);
//    		//System.out.println(">>>>DEBUG: " + i + value/sum);
//    	}
    }

    
    /**
     * Generate a random number between 0.0 and 1.0.
     * @return              the randomly generated value
     */
    private static double rand () {
        Random r = new java.util.Random();
        return r.nextDouble(); //generate random from [0.0,1.0)
    }
    // if use rand/sum(rand), the value needn't 0-1
    
    /**
     * Update the personal best if the current evaluation is better.
     */
    void updatePersonalBest (double eval) {
    	if (eval < bestEval) {
    		bestPosition = position.clone();
    		bestEval = eval;
    	}
    }

    /**
     * Get a copy of the position of the particle.
     * @return  the x position
     */
    PSOVector getPosition () {
        return position.clone();
    }

    /**
     * Get a copy of the velocity of the particle.
     * @return  the velocity
     */
    PSOVector getVelocity () {
        return velocity.clone();
    }

    /**
     * Get a copy of the personal best solution.
     * @return  the best position
     */
    PSOVector getBestPosition() {
        return bestPosition.clone();
    }

    /**
     * Get the value of the personal best solution.
     * @return  the evaluation
     */
    double getBestEval () {
        return bestEval;
    }

    /**
     * Update the position of a particle by adding its velocity to its position.
     */
    void updatePosition () {
        this.position.add(velocity.getVectorCoordinate());
    }

    /**
     * Set the velocity of the particle.
     * @param velocity  the new velocity
     */
    void setVelocity (PSOVector velocity) {
        this.velocity = velocity.clone();
    }
    
    void setBestEval (double bestEval){
    	this.bestEval = bestEval;
    }
    
    void setPosition (PSOVector pos){
    	this.position = pos;
    }
    
    String getName(){
    	return name;
    }
}

