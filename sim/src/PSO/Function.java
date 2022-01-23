package PSO;

import java.util.Arrays;

class Function {
	
	private int nodes;
	
	public Function(int nodes){
		this.nodes = nodes;
	}

    
    public Vector mainFunction(Particle p, int workLoad, Vector currentWorkload){
    	/**
         * @param workLoad 			number of images from 1 service
         * @param p    				percent workload
         * @param Theta				share service or not ( 0 or 1)
         * @param currentWorkload	current workload at that node
         * @return      			total time
         */
    	Vector position = p.getPosition().clone();
    	//Vector p_temp = position.clone(); // for 2nd part
    	
    	Vector Theta = new Vector(nodes);
    	for(int i = 0; i < position.getVectorCoordinate().length; i++){
    		if(position.getPAt(i) != 0){
    			Theta.setPAt(i, 1);
    		}
    	}
    	
    	Vector worker = p.getPosition().getWorkerVector();
    	Vector worker_temp = worker.clone();
        Vector workerCWL = currentWorkload.getWorkerVector(); //Worker current Workload
        
    //    double man = p.getPosition().getPAt(0);
   //     double manCWL = currentWorkload.getPAt(0); //Manager current Workload
        
    	
        /* 
        *Calculate time for manager
        */
//        double Tman = (man*workLoad*0.005 + 0.619)   // y = 0.005*x + 0.0619   ~ P*Wi / Bij
//        				+ (0.0199*man*workLoad+0.291)    // y = 0.0199*x + 0.291 ~ P.Wi / fi
//        				+ (0.0199*manCWL+0.291)*Theta.getPAt(0) // (y = 0.0199*x + 0.291)*Theta ~ Theta*Ni/fi
//        				+ 0.047675; // Ri / Bij
        
       
        /*
        calculate time for workers
        */
        //y = 0.004*x + 0.0619 ~ P*Wi / Bij
        worker.mul(workLoad*0.004);
        double[] coef = new double[nodes]; // workers = nodes ... ignore Man
        Arrays.fill(coef, 0.619);
        worker.add(coef);
        
        // y = 0.033*x + 0.469 ~ P.Wi / fi
    	worker_temp.mul(workLoad*0.033);         
    	Arrays.fill(coef, 0.469);
    	worker_temp.add(coef);
    	
    	//(y = 0.05*x + 1.244)*Theta ~ Theta*Ni/fi
    	workerCWL.mul(0.033);
    	workerCWL.add(coef);
    	Vector Theta_worker = Theta.getWorkerVector();
    	workerCWL.mulVector(Theta_worker);
    	
    	//Ri / Bij
    	Arrays.fill(coef, 0.047675 );
    	worker.add(coef);
    	
    	//Sum of 4 parts:
    	worker.add(worker_temp.getVectorCoordinate());
    	worker.add(workerCWL.getVectorCoordinate());
    	
    	for(int i = 0; i< position.getVectorCoordinate().length; i++){
//    		if(i==0){
//    			position.setPAt(0, Tman);
//    			continue;
//    		}
    	//	System.out.println("DEBUG" + i + " " + position.getVectorCoordinate().length);
    		position.setPAt(i, worker.getPAt(i));
    	}
    	
    	return position;
    	
    }
    /**
     * Sum pi = 1
     * @param p particle
     * @return true if not satisfy 
     */
    static boolean constraintF1(Particle p){
    	if(p.getPosition().getSum() != 1.0){
    		//System.out.println("F1 = true");
    		return true;
    	}
    	return false;
    }
    /**
     * Pi >= 0
     * @param p
     * @return true if not satisfy
     */
    static boolean constraintF2(Particle p){
    	Vector position = p.getPosition();
    	for(int i = 0; i<position.getVectorCoordinate().length; i++){
    		if(position.getPAt(i) < 0)
    			return true;
    	}
    	return false;
    }
    
    /**
     * pi*W <= (W + sum(N)) / n
     * @param p
     * @return  true if not satisfy 
     */
    static boolean constraintF3(Particle p, int workLoad, Vector currentWorkload){
    	double averageWorkload = (workLoad + currentWorkload.getSum()) / p.getPosition().getVectorCoordinate().length; 
    	for (int i = 0; i<p.getPosition().getVectorCoordinate().length; i++){
    		if(p.getPosition().getPAt(i)*workLoad > averageWorkload){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * pW >= aW 
     * @param p
     * @param workLoad
     * @return
     */
    static boolean constraintF4(Particle p, int workLoad){
    	Vector position = p.getPosition();
    	position.mul(workLoad);
    	for(int i=0; i<position.getVectorCoordinate().length;i++){
    		if(position.getPAt(i) < 10)
    			return true;
    	}
    	return false;
    }
}

