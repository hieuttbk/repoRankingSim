package PSO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;





/**
 * Represents a swarm of particles from the Particle Swarm Optimization algorithm.
 */
public class Swarm {

    private int numOfParticles, epochs;
    private double inertia_max, inertia_min, cognitiveComponent, socialComponent;
    private int workLoad;
    private static int nodes;
    private static Vector currentWorkload;
    private static Function model;
    private Vector bestPosition;
    private double bestEval;
    public static final double INERTIA_MAX = 0.9;
    public static final double INERTIA_MIN = 0.4;
    public static final double DEFAULT_COGNITIVE = 1; // Cognitive component.
    public static final double DEFAULT_SOCIAL = 1.05; // Social component.
    
    public static final double INFINITY = Double.POSITIVE_INFINITY;
    
    //private static final Logger LOGGER = LogManager.getLogger(Swarm.class);
    /**
     * Construct the Swarm with default values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     */
    public Swarm (int particles, int epochs, int nodes, int workLoad, Vector currentWorkload) {
        this(particles, epochs, nodes, INERTIA_MAX, INERTIA_MIN, DEFAULT_COGNITIVE, DEFAULT_SOCIAL,
        																workLoad, currentWorkload);
    }

    
    /**
     * Construct the Swarm with custom values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     * @param inertia       the particles resistance to change
     * @param cognitive     the cognitive component or introversion of the particle
     * @param social        the social component or extroversion of the particle
     */
    public Swarm (int particles, int epochs,int nodes, double inertia_max, double inertia_min, 
    													double cognitive, double social,
    													int workLoad, Vector currentWorkload) {
        this.numOfParticles = particles;
        this.epochs = epochs;
        this.nodes = nodes;
        this.inertia_max = inertia_max;
        this.inertia_min = inertia_min;
        this.cognitiveComponent = cognitive;
        this.socialComponent = social;
        this.workLoad = workLoad;
        this.currentWorkload = currentWorkload;
        bestPosition = new Vector(nodes);
        double[] initialBestPosition = new double[nodes];
        Arrays.fill(initialBestPosition, INFINITY);
        bestPosition.set(initialBestPosition);
        bestEval = INFINITY;
       
        model = new Function(nodes); //model 
    }

    /**
     * Execute the algorithm.
     */
    public Map<String, String> run (String serviceId) {
        Particle[] particles = initialize();

        double oldEval = bestEval;
        //System.out.println("--------------------------EXECUTING-------------------------");
        //System.out.println("Global Best Evaluation (Epoch " + 0 + "):\t"  + bestEval);
        //System.out.println("---------------------------------------------------------------");
        for (int i = 0; i < epochs; i++) {
        	//update inertia 
        	double inertia = inertia_max - (((inertia_max - inertia_min)*(i+1)) / epochs);

            if (bestEval < oldEval) {
            	//System.out.println("---------------------------------------------------------------");
               // System.out.println("Global Best Evaluation (Epoch " + (i + 1) + "):\t" + bestEval);
                oldEval = bestEval;
            }

            for (Particle p : particles) {
            	double eval = eval(p);
                p.updatePersonalBest(eval);
                updateGlobalBest(p);
            }

            for (Particle p : particles) {
                updateVelocity(p, inertia);
                p.updatePosition();
            }
            
        }

        
        Particle bestParticle = new Particle("bestParticle", nodes);
        bestParticle.setPosition(bestPosition);
        Map<String, String> map = mappingRatio(bestParticle, workLoad);
       // System.out.println("---------------------------COMPLETE-------------------------");
        //LOGGER.info("Final ratio for service {} : {}",serviceId, bestParticle.getPosition().toStringOutput());
        return map;
    }

    /**
     * Create a set of particles, each with random starting positions.
     * @return  an array of particles
     */
    private Particle[] initialize () {
        Particle[] particles = new Particle[numOfParticles];
        for (int i = 0; i < numOfParticles; i++) {
            Particle particle = new Particle("p"+i, nodes);
            double initialEval =  model.mainFunction(particle, workLoad, currentWorkload).getSum();
            
            particle.updatePersonalBest(initialEval);;
            //Checked sum = 100 
            particles[i] = particle;
            updateGlobalBest(particle);
        }
        return particles;
    }
    
    private double eval(Particle p){
    	double eval = model.mainFunction(p, workLoad, currentWorkload).getSum();

    	if(Function.constraintF1(p))
    		eval = INFINITY;
    	
    	if(Function.constraintF2(p))
    		eval = INFINITY;

    	if(Function.constraintF3(p, workLoad , currentWorkload ))
    		eval = INFINITY;
    	
    	if(Function.constraintF4(p, workLoad))
    		eval = INFINITY;
    	
    	return eval;
    }

    /**
     * Update the global best solution if a the specified particle has
     * a better solution
     * @param particle  the particle to analyze
     */
    private void updateGlobalBest (Particle particle) {
        if (particle.getBestEval() < bestEval) {
            bestPosition = particle.getBestPosition();
            bestEval = particle.getBestEval();
        }
    }

    /**
     * Update the velocity of a particle using the velocity update formula
     * @param particle  the particle to update
     */
    private void updateVelocity (Particle particle, double inertia) {
        Vector oldVelocity = particle.getVelocity();
        Vector pBest = particle.getBestPosition();
        Vector gBest = bestPosition.clone();
        Vector pos = particle.getPosition();
      
        Random random = new Random();
        double r1 = random.nextDouble();
        double r2 = random.nextDouble();

        // The first product of the formula.
        Vector newVelocity = oldVelocity.clone();
        newVelocity.mul(inertia);

        // The second product of the formula.
        pBest.sub(pos.getVectorCoordinate());
        pBest.mul(cognitiveComponent);
        pBest.mul(r1);
        newVelocity.add(pBest.getVectorCoordinate());

        // The third product of the formula.
        gBest.sub(pos.getVectorCoordinate());
        gBest.mul(socialComponent);
        gBest.mul(r2);
        newVelocity.add(gBest.getVectorCoordinate());
        
        particle.setVelocity(newVelocity);
    }
    
    private static Map<String, String> mappingRatio (Particle bestParticle, int workLoad){
    	Map<String, String> workLoadRatio = new HashMap<String, String>();
    	Vector result_workload = bestParticle.getPosition(); //cloning
    	result_workload.mul(workLoad);
    	double[] ratio = result_workload.getVectorCoordinate();
    	
    	
    	int sum = 0;
    	for(int i = 0; i < ratio.length; i++){ //skip 0 because 0 is for Manager already
    		int nImages = (int)Math.round(ratio[i]);
    		int workerid=i+1;
    		workLoadRatio.put("worker-"+workerid+"-id", sum+1 + "-" + (sum+nImages));
    		sum += nImages;
    	}
    	
    //	workLoadRatio.put("MANAGER", sum+1+"-"+workLoad);
    	return workLoadRatio;
    }
    

}