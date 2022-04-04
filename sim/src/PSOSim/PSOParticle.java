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
class PSOParticle {
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
    PSOParticle (FunctionType function, String name, int dim, HashMap<Integer, List<RTable>> mapRTable, List<RTable> rtable ) {
    	System.out.println("---> init particles");

    	this.function = function;
    	position = new PSOVector(dim);
        velocity = new PSOVector(dim);
        this.name = name;
        this.mapRTable = mapRTable;
        this.rtable=rtable;
        
        setRandomPosition();
        configRandom();
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
    		System.out.println("id: " + id);
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
    	
    	
    	
//    	List<Integer> otherPaths = new ArrayList<Integer>();
//    	for (Integer id : mapRTable.keySet()) {
//			List<RTable> rTable2 = mapRTable.get(id);
//			for(RTable r:rTable2) {
//			if(paths.get(r.getDes())==1){
//				position.setById(j,0.25);
//			}else {
//				otherPaths.add(j);
//			}
//				j++;
//			}
//    	}
    	
    	
    	
 /*   	
    	int numM=1;
		for (Integer id : mapRTable.keySet()) {
			List<RTable> rtableMap = mapRTable.get(id);
			List<String> check = new ArrayList<String>();
			for (RTable r : rtableMap) {
				String nodeID = r.getDes();
				if (!check.contains(nodeID)) {
					numM++;
					check.add(nodeID);
				}
			}
		}

		numM /= mapRTable.size();
		double NM = Constants.NUM_REQ / numM;
    	
    	int j2=0;
    	for (Integer idmap:mapRTable.keySet()) {
    		List<RTable> rTableMap = mapRTable.get(idmap);
			List<Integer> otherPaths = new ArrayList<Integer>();
			int j3=0;
    		for(RTable r:rTableMap) {
    			if(paths.get(r.getDes())==1){
    				position.setById(j2,0.25);
    			}else {
    				otherPaths.add(j2);
    			}
    			j2++;
    			j3++;
    		}
    		
    		for(RTable r:rTableMap) {		
    	    	double sum3=1-0.25*(rTableMap.size()-otherPaths.size());
    	    	double sum2=0;
    	    	PSOVector p = new PSOVector(rtable.size());
    	    	for (Integer id:otherPaths) {
    	    		p.setById(id, rand());
    	    		sum2 += p.getById(id);
    	    	}
    	    	
    	    	for (Integer id:otherPaths) {
    	    		double value = p.getById(id);
    	    		p.setById(id, value/sum2);
    	    		position.setById(id, value*sum3/sum2);
    	    	}		
    				j++;
    			}
    	}
    	
*/    	
    	
   // 	System.out.println(position.toStringOutput());
    	
    	// Random p in number of node >< no. paths
   
    	

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
    //			System.out.println("Process: " + j + " Des: " + r.getDes() + " Path: " + paths.get(r.getDes())  );
    			j++;
    		}
    		
    		
    	//	System.out.println(randP);
    	}
  //  	System.out.println(position.toStringOutput());
    	//original 
//    	for (Integer id:mapRTable.keySet()) {
//    		List<RTable> rTable = mapRTable.get(id);
//    	 	PSOVector p = new PSOVector(rTable.size());
//    	 	
//
//        	double sum = 0;
//        	for(int i = 0; i< p.getVectorCoordinate().length; i++){
//        		p.setById(i, rand());
//        		sum += p.getById(i);
//        	}
//        	
//        	for(int i = 0; i<p.getVectorCoordinate().length; i++){
//        		double value = p.getById(i);
//        		p.setById(i, value/sum);
//        		position.setById(j, value/sum);
//        		j++;
//
//        		
//        	}
//   }

    	
    	
    	
    	
    	
    	
    	
        //	System.out.println("p in req: " +  id);

//        	System.out.println(p.toStringOutput());

//    	}
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

