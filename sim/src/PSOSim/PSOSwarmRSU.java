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
public class PSOSwarmRSU {


	private int numOfParticles, epochs;
	private double inertia_max, inertia_min, cognitiveComponent, socialComponent;
	private int dim;
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
	public PSOSwarmRSU(int particles, int epochs,int dim, List<RTable> rtable,
			HashMap<Integer, List<RTable>> mapRTable, int testCase) {
		this(particles, epochs,dim, INERTIA_MAX, INERTIA_MIN, DEFAULT_COGNITIVE, DEFAULT_SOCIAL,
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
	public PSOSwarmRSU(int particles, int epochs,int dim, double inertia_max, double inertia_min, double cognitive,
			double social,List<RTable> rtable,
			HashMap<Integer, List<RTable>> mapRTable, int testCase) {

		this.numOfParticles = particles;
		this.epochs = epochs;
		this.dim = dim;
		this.inertia_max = inertia_max;
		this.inertia_min = inertia_min;
		this.cognitiveComponent = cognitive;
		this.socialComponent = social;

		this.rtable = rtable;
		this.mapRTable = mapRTable;
		bestPosition = new PSOVector(dim);
		double[] initialBestPosition = new double[dim];
		Arrays.fill(initialBestPosition, INFINITY);
		bestPosition.set(initialBestPosition);
		bestEval = INFINITY;
		this.testCase = testCase;

		model = new PSOFunction(dim, testCase); // model
	}



	/**
	 * Execute the algorithm.
	 */
	public Map<Integer, Double> run(String serviceId) {
		PSOParticleRSU[] particles = initialize();
		List<PSOParticleRSU> okParticles = new ArrayList<PSOParticleRSU>();
		for (PSOParticleRSU p : particles) {
			okParticles.add(p);
		}
		
		
		LogPSO log = LogPSO.getInstance();
		double oldEval = bestEval;
		// System.out.println("--------------------------EXECUTING-------------------------");
		// System.out.println("Global Best Evaluation (Epoch " + 0 + "):\t" + bestEval);
		// log.log("init: " + bestEval + "\n");
		// System.out.println("---------------------------------------------------------------");
		
		List<PSOParticleRSU> updateParticles = new ArrayList<PSOParticleRSU>();

		PSOParticleRSU bestParticle = new PSOParticleRSU(PSOSim.PSOParticleRSU.FunctionType.A, "bestParticle", dim, mapRTable,
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
			
			for (PSOParticleRSU p : updateParticles) {
				okParticles.remove(p);
			}
			
		//	System.out.println("echo: " + i + " listPartice: " + okParticles.size());
			
			if(okParticles.size()==0) {
				if (i<=1) {
					System.out.println("ALL PARTICLE FALL");
				}
				
				return map;
			}
			
			for (PSOParticleRSU p : okParticles) {

				double eval = eval(p);
				// log.log("t_ser: " + eval +"\n");
				 if (eval==INFINITY) {
					 updateParticles.add(p);
					 }
				
				if(p.updatePersonalBest(eval)) {
	//				System.out.println("PBEST: " + "echo: " + i + " " + p.getName());
				}

				if (updateGlobalBest(p)) {
				//	System.out.println("GBEST: " + "echo: " + i + " " + p.getName() + "\n" + p.getBestPosition().toStringOutput());
				}
			}

//			System.out.println("echo: " + i + " best: " + bestPosition.getVectorRatio().toStringOutput());

			for (PSOParticleRSU p : particles) {
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
	private PSOParticleRSU[] initialize() {

		PSOParticleRSU[] particles = new PSOParticleRSU[numOfParticles];
		for (int i = 0; i < numOfParticles; i++) {
		//	System.out.println("---------------------init");

			PSOParticleRSU particle = new PSOParticleRSU(PSOParticleRSU.FunctionType.A, "p" + i, dim, mapRTable, rtable);
		//	System.out.println("particle: " + particle.getPosition().toStringOutput());
			// double initialEval = model.multiFunction(particle, workLoad,
			// currentWorkload,bestNode,rtable).getSum();
			PSOVector initTime = model.multiFunctionRSU(particle, rtable, mapRTable);
			double initialEval = initTime.getBiggestResult();
			
			
			
			if(particle.updatePersonalBest(initialEval)) {


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

	private double eval(PSOParticleRSU p) {
		double eval = model.multiFunctionRSU(p, rtable, mapRTable).getBiggestResult(); // tmax
		if (PSOFunction.constraintF1(p, mapRTable)) {
			eval = INFINITY;
			System.out.println("Contrains 1");
		}

		return eval;
	}

	/**
	 * Update the global best solution if a the specified particle has a better
	 * solution
	 * 
	 * @param particle the particle to analyze
	 */
	private boolean updateGlobalBest(PSOParticleRSU particle) {
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
	private void updateVelocity(PSOParticleRSU particle, double inertia) {
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

	private static Map<Integer, Double> mappingRatio(PSOParticleRSU bestParticle) {
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