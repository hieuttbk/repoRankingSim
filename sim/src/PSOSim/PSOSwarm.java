package PSOSim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import multihop.LogPSO;
import multihop.RTable;

/**
 * Represents a swarm of particles from the Particle Swarm Optimization
 * algorithm.
 */
public class PSOSwarm {


	private int numOfParticles, epochs;
	private double inertia_max, inertia_min, cognitiveComponent, socialComponent;
	private static int nodes;
	private static PSOVector currentWorkload;
	private static PSOFunction model;
	private PSOVector bestPosition;
	private double bestEval;
	public static final double INERTIA_MAX = 0.9; // 0.9
	public static final double INERTIA_MIN = 0.4; // 0.4
	public static final double DEFAULT_COGNITIVE = 1.05; // Cognitive component.
	public static final double DEFAULT_SOCIAL = 1.05; // Social component. // 1.05

	public static final double INFINITY = Double.POSITIVE_INFINITY;
	private int testCase;

	private List<RTable> rtable = new ArrayList<RTable>();
	HashMap<Integer, List<RTable>> mapRTable = new HashMap<Integer, List<RTable>>();

	// private static final Logger LOGGER = LogManager.getLogger(Swarm.class);
	/**
	 * Construct the Swarm with default values.
	 * 
	 * @param particles the number of particles
	 * @param epochs    the number of iterates
	 * @param rtable
	 * @param bestNode  isn't need!? > cal time can be just use prams in rtable.
	 * @param mapRTable
	 * @param testCase
	 */
	public PSOSwarm(int particles, int epochs, int nodes, PSOVector currentWorkload, List<RTable> rtable,
			HashMap<Integer, List<RTable>> mapRTable, int testCase) {
		this(particles, epochs, nodes, INERTIA_MAX, INERTIA_MIN, DEFAULT_COGNITIVE, DEFAULT_SOCIAL, currentWorkload,
				rtable, mapRTable, testCase);
	}

	/**
	 * Construct the Swarm with custom values.
	 * 
	 * @param particles  the number of particles to create
	 * @param epochs     the number of generations
	 * @param inertia    the particles resistance to change
	 * @param cognitive  the cognitive component or introversion of the particle
	 * @param social     the social component or extroversion of the particle
	 * @param testCase
	 * @param mapRTable2
	 */
	public PSOSwarm(int particles, int epochs, int nodes, double inertia_max, double inertia_min, double cognitive,
			double social, PSOVector currentWorkload,List<RTable> rtable,
			HashMap<Integer, List<RTable>> mapRTable, int testCase) {

		this.numOfParticles = particles;
		this.epochs = epochs;
		this.nodes = nodes;
		this.inertia_max = inertia_max;
		this.inertia_min = inertia_min;
		this.cognitiveComponent = cognitive;
		this.socialComponent = social;

		this.currentWorkload = currentWorkload;
		this.rtable = rtable;
		this.mapRTable = mapRTable;
		bestPosition = new PSOVector(nodes);
		double[] initialBestPosition = new double[nodes];
		Arrays.fill(initialBestPosition, INFINITY);
		bestPosition.set(initialBestPosition);
		bestEval = INFINITY;
		this.testCase = testCase;

		model = new PSOFunction(nodes, testCase); // model
	}

	/**
	 * Execute the algorithm.
	 */
	public Map<Integer, Double> run(String serviceId) {
		PSOParticle[] particles = initialize();
		List<PSOParticle> okParticles = new ArrayList<PSOParticle>();
		for (PSOParticle p : particles) {
			okParticles.add(p);
		}
		
		
		LogPSO log = LogPSO.getInstance();
		double oldEval = bestEval;
		// System.out.println("--------------------------EXECUTING-------------------------");
		// System.out.println("Global Best Evaluation (Epoch " + 0 + "):\t" + bestEval);
		// log.log("init: " + bestEval + "\n");
		// System.out.println("---------------------------------------------------------------");
		
		List<PSOParticle> updateParticles = new ArrayList<PSOParticle>();

		PSOParticle bestParticle = new PSOParticle(PSOSim.PSOParticle.FunctionType.A, "bestParticle", nodes, mapRTable,
				rtable);
		bestParticle.setPosition(bestPosition);
		Map<Integer, Double> map = mappingRatio(bestParticle);
		
		
		for (int i = 0; i < epochs; i++) {
			
			// log.log(i + "\n");
			// update inertia
			double inertia = inertia_max - (((inertia_max - inertia_min) * (i + 1)) / epochs);

			if (bestEval < oldEval) {
				// System.out.println("---------------------------------------------------------------");
				// System.out.println("Global Best Evaluation (Epoch " + (i + 1) + "):\t" +
				// bestEval);
				oldEval = bestEval;
			}
			
			
			//okParticles = updateParticles;
			
		//	System.out.println("echo: " + i + " listPartice: " + updateParticles.size());
			
			for (PSOParticle p : updateParticles) {
				okParticles.remove(p);
			}
			
		//	System.out.println("echo: " + i + " listPartice: " + okParticles.size());
			
			if(okParticles.size()==0) {
				if (i<=1) {
					System.out.println("ALL PARTICLE FALL");
				}
				
				return map;
			}
			
			for (PSOParticle p : okParticles) {
				// System.out.println(" >>>>>> DEBUG eval: " + i + " " +
				// p.getPosition().toStringOutput());
				// log.log(p.getName() + "\t" + p.getPosition().toStringOutput() + "\n");
				// log.log(p.getName() + "\t" +
				// p.getPosition().getVectorRatio().toStringOutput() + "\n");
				// log.log(p.getName() + "\t" + p.getPosition().toStringOutput());
				double eval = eval(p);
				// log.log("t_ser: " + eval +"\n");
				 if (eval==INFINITY) {
					 updateParticles.add(p);
					 }
				
				if(p.updatePersonalBest(eval)) {
	//				System.out.println("PBEST: " + "echo: " + i + " " + p.getName());
				}

				if (updateGlobalBest(p)) {
					System.out.println("GBEST: " + "echo: " + i + " " + p.getName() + "\n" + p.getBestPosition().toStringOutput());
				}
			}

//			System.out.println("echo: " + i + " best: " + bestPosition.getVectorRatio().toStringOutput());

			for (PSOParticle p : particles) {
				updateVelocity(p, inertia);
				p.updatePosition();
			}

		}

		//bestParticle = new PSOParticle(PSOSim.PSOParticle.FunctionType.A, "bestParticle", nodes, mapRTable,
//				rtable);
		bestParticle.setPosition(bestPosition);
		map = mappingRatio(bestParticle);
		// System.out.println("---------------------------COMPLETE-------------------------");
		// LOGGER.info("Final ratio for service {} : {}",serviceId,
		// bestParticle.getPosition().toStringOutput());
		return map;
	}

	/**
	 * Create a set of particles, each with random starting positions.
	 * 
	 * @return an array of particles
	 */
	private PSOParticle[] initialize() {
		PSOParticle[] particles = new PSOParticle[numOfParticles];
		for (int i = 0; i < numOfParticles; i++) {
			// System.out.println("---------------------init");

			PSOParticle particle = new PSOParticle(PSOSim.PSOParticle.FunctionType.A, "p" + i, nodes, mapRTable,
					rtable);
			// System.out.println("particle: " + particle.getPosition().toStringOutput());
			// double initialEval = model.multiFunction(particle, workLoad,
			// currentWorkload,bestNode,rtable).getSum();
			PSOVector initTime = model.multiFunction(particle, currentWorkload, rtable, mapRTable);
			double initialEval = initTime.getBiggestResult();
			
			
			
			if(particle.updatePersonalBest(initialEval)) {
//				System.out.println(particle.getName() + " echo: " + 0 
//						+ "\n"  + "ratio: " + particle.getPosition().getVectorRatio().toStringOutput() 
//						+ "\n" + "time(p): " + initTime.toStringOutput()
//						+ "\n" + initialEval);

			}

			// Checked sum = 100
			particles[i] = particle;
			// System.out.println("particle: " + particle.getPosition().toStringOutput());
			//updateGlobalBest(particle);

			if (updateGlobalBest(particle)) {
	//			System.out.println("GBEST: " + "e" + 0 + " " + particle.getName() + " is " + particle.getPosition().getVectorRatio().toStringOutput() + "\n" + bestEval);
			}

		}
		return particles;
	}

	private double eval(PSOParticle p) {

		// double eval = model.multiFunction(p, workLoad,
		// currentWorkload,bestNode,rtable).getSum();
		// System.out.println("particle: " + p.getPosition().toStringOutput());
		double eval = model.multiFunction(p, currentWorkload, rtable, mapRTable).getBiggestResult(); // tmax
		// System.out.println("EVAL = " + eval);
		if (PSOFunction.constraintF1(p, mapRTable)) {
			eval = INFINITY;
			System.out.println("Contrains 1");

		}

//    	if(PSOFunction.constraintF2(p)) {
//    		eval = INFINITY;
//    		LogPSO log = LogPSO.getInstance();
//    		//log.log("\tF2\t");
//    		//System.out.println("Contrains 2");
//    	}

//    	if(PSOFunction.constraintF3(p, workLoad , currentWorkload )) {
//    		eval = INFINITY;
//    		System.out.println("Contrains 3");
//    	}
//    		
//    	
//    	if(PSOFunction.constraintF4(p, workLoad))
//    	{
//    		eval = INFINITY;
//    		System.out.println("Contrains 4");
//    	}

		return eval;
	}

	/**
	 * Update the global best solution if a the specified particle has a better
	 * solution
	 * 
	 * @param particle the particle to analyze
	 */
	private boolean updateGlobalBest(PSOParticle particle) {
		if (particle.getBestEval() < bestEval) {
			bestPosition = particle.getBestPosition();
			bestEval = particle.getBestEval();
			return true;
		}
		// System.out.println("==> Global Best at " + particle.getName() + " is: "
		// +bestEval + "\n" + bestPosition.toStringOutput());

		return false;
	}

	/**
	 * Update the velocity of a particle using the velocity update formula
	 * 
	 * @param particle the particle to update
	 */
	private void updateVelocity(PSOParticle particle, double inertia) {
		PSOVector oldVelocity = particle.getVelocity();
		PSOVector pBest = particle.getBestPosition();
		PSOVector gBest = bestPosition.clone();
		PSOVector pos = particle.getPosition();

		Random random = new Random();
		double r1 = random.nextDouble();
		double r2 = random.nextDouble();

		// The first product of the formula.
		PSOVector newVelocity = oldVelocity.clone();
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

	private static Map<Integer, Double> mappingRatio(PSOParticle bestParticle) {
		Map<Integer, Double> workLoadRatio = new HashMap<Integer, Double>();
		PSOVector result_workload = bestParticle.getPosition(); // cloning
		// result_workload.mul(workLoad);
		double[] ratio = result_workload.getVectorCoordinate();

		int sum = 0;
		for (int i = 0; i < ratio.length; i++) { // skip 0 because 0 is for Manager already
			int nImages = (int) Math.round(ratio[i]);
			int workerid = i + 1;
			workLoadRatio.put(i, ratio[i]);
			sum += nImages;
		}

		// workLoadRatio.put("MANAGER", sum+1+"-"+workLoad);
		return workLoadRatio;
	}

}