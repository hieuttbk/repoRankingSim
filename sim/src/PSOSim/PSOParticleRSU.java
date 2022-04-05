package PSOSim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import multihop.Constants;
import multihop.RTable;

/**
 * Represents a particle from the Particle Swarm Optimization algorithm.
 * @prams position is value of F() as time
 */
class PSOParticleRSU {
	
    public enum FunctionType {
        A,B,C
    }
	
	private String name;
    private PSOVector position;        // Current position.
    private PSOVector velocity;
    private PSOVector bestPosition;    // Personal best solution.
    private double bestEval = Double.POSITIVE_INFINITY;        // Personal best value.
	private List<RTable> rtable = new ArrayList<RTable>();

    private FunctionType function; 
	HashMap<Integer, List<RTable>> mapRTable = new HashMap<Integer, List<RTable>>();

  
    /**
     * Construct a Particle with a random starting position.
     * @param mapRTable 
     * @param rtable2 
     * @param beginRange    the minimum xyz values of the position (inclusive)
     * @param endRange      the maximum xyz values of the position (exclusive)
     */
    PSOParticleRSU (FunctionType function, String name, int dim, HashMap<Integer, List<RTable>> mapRTable, List<RTable> rtable ) {
    	//System.out.println("---> init particles");

    	this.function = function;
    	position = new PSOVector(dim);
        velocity = new PSOVector(dim);
        this.name = name;
        this.mapRTable = mapRTable;
        this.rtable=rtable;
        
        setRandomPosition();
       // configRandom();
        bestPosition = position.clone();
        
        
        //TODO ?
//        bestPosition = velocity.clone();
//        bestEval = eval();
        
    }
    
    



	private void configRandom() {
    	int j=0;
    	
    	Set<Integer> keySet= mapRTable.keySet();
    	List<Integer> sortedList = new ArrayList<>(keySet);
    	Collections.sort(sortedList);
    	
    	
    	for (Integer id:sortedList) { // req 0, 1
    		//System.out.println("id: " + id);
    		List<RTable> rTableMap = mapRTable.get(id);  // rtable of req 0  		
			double compensation = 0;
			double sumOther =1;
			int idSelf = j;
    		for(RTable r:rTableMap) {
    			double pai = position.getById(j);
    			double lambWL = r.getResource() / r.getReq().getWL();
    			double pen = pai-lambWL;
    			if (!r.getDes().equals(r.getReq().getSrcNode().getName())) {
    				if (pen > 0) {
    					pai = lambWL;
    					position.setById(j, pai);
    					compensation +=pen;
    				}
    				sumOther-=position.getById(j);
    			}
    			else {  // source = des 
    				
    				idSelf = j;
    			}    			
    			j++;
    		}
  //  		System.out.println("idSelf: " + id  + " " + idSelf  + " " + sumOther );
    		position.setById(idSelf, (sumOther));
    		
//    		double sum =0;
//    		int jj=0;
//    		for(RTable r:rTableMap) {
//    			sum += position.getById(jj);
//    			jj++;
//    		}
//    		System.out.println("sum = " + sum);
    		
    	
    	}
 //   	System.out.println(position.toStringOutput());
    	
    	

		
	}

//   
    // create p = rand/ sum(rand) 
    private void setRandomPosition () {
    	//System.out.println("---------------------init2");
    	int j=0;
    	
 

    	Set<Integer> keySet= mapRTable.keySet();
    	List<Integer> sortedList = new ArrayList<>(keySet);
    	Collections.sort(sortedList);
    	
    	
    	for (Integer id:sortedList) { // req 0, 1
    		List<RTable> rTableMap = mapRTable.get(id);
    		
    		
    		HashMap<String, Integer> paths = PSOUtils.getPahts(rTableMap);
    		
    		int len = paths.size();
    		double[] randP = PSOUtils.getRandP(len); 
    		HashMap<String, Double> ratios = new HashMap<String, Double>();
    		
    		int irandP=0;
    		for (String ipath:paths.keySet()) {
    			 ratios.put(ipath, randP[irandP]);
    			//ratios.put(ipath, 1.0/len);
    			irandP++;
    		}
    		
    		for(RTable r:rTableMap) {
    			if(paths.get(r.getDes())==1){
    				position.setById(j,ratios.get(r.getDes()));
    			}else {
    				position.setById(j,ratios.get(r.getDes())/paths.get(r.getDes()));
    			}
    			j++;
    		}
    		
    		
    	}

    }

    
    /**
     * Generate a random number between 0.0 and 1.0.
     * @return              the randomly generated value
     */
    private static double rand () {
        Random r = new java.util.Random();
        return r.nextDouble(); //generate random from [0.0,1.0)
    }
    
    /**
     * Update the personal best if the current evaluation is better.
     */
    boolean updatePersonalBest (double eval) {
    	if (eval < bestEval) {
    		bestPosition = position.clone();
    		bestEval = eval;
    		return true;
    	}
		return false;
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

