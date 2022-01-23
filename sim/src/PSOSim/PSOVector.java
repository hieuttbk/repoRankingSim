package PSOSim;

import java.util.Arrays;

/**
 * Can represent a position as well as a velocity.
 */

public class PSOVector {
	
	private int dim; // dimension, number of propagations
	private double[] p; // propagations 
    private double limit = Double.MAX_VALUE;

    // constructor for p[dim] = 0
    public PSOVector (int dim) {
    	this.dim = dim;
    	this.p = new double[dim];
    	Arrays.fill(this.p, 0);
    }
    
    // constructor for p[] = v[]
    public PSOVector (double[] v){
    	this.dim = v.length;
    	this.p = new double[v.length];
    	for(int i=0; i<v.length; i++){
        	this.p[i] = v[i];
        }
    	
    }

    // get p value by index
    double getById (int id) {
        return this.p[id];
    }


    // set value to p
    public void set (double[] v) {
    	this.p = new double[v.length];
        for(int i=0; i<p.length; i++){
        	this.p[i] = v[i];
        }
    }
    
    // set p[] = [v v v v]
    void setSingleValue (double v){
    	for(int i=0; i<p.length; i++){
        	this.p[i] = v;
        }
    }

    // get p value by index
    public void setById (int id, double value){
    	this.p[id] = value; 
    }

    // operator p +-*/ v
    void add (double[] v) {
    	for(int i=0; i<p.length; i++){
    		this.p[i] += v[i];
    	}
        limit();
    }

    void sub (double[] v) {
        for(int i=0; i<p.length; i++){
        	this.p[i] -= v[i];
        }
        limit();
    }

    void mul (double s) {
        for(int i=0; i<p.length; i++){
        	this.p[i] *= s;
        }
        limit();
    }
    
    void mulVector(PSOVector v){
    	for(int i = 0; i<p.length; i++){
    		this.p[i] *= v.getById(i);
    	}
    	limit();
    }

    void div (double s) {
    	for(int i=0; i<p.length; i++){
    		this.p[i] /= s;
    	}
        limit();
    }

    void normalize () {
        double m = mag();
        if (m > 0) {
        	for(int i=0; i<p.length; i++){
        		this.p[i] /= m;
        	}
        }
    }
    
    
    /*
    private double mag(){
    	double sum=0;
    	for (int i=0; i<p.length; i++){
    		sum += p[i];
    	}
    	return sum;
    }
    
    private void limit(){
    	double m = mag();
    	if(m > 100){
    		for(int i=0; i<p.length; i++){
    			this.p[i] /= m;
    		}
    	}
    }
     * 
     */
    private double mag () {
    	double sum=0;
    	for (int i=0; i<p.length; i++){
    		sum += p[i]*p[i];
    	}
    	return Math.sqrt(sum);
    }
    void limit (double l) {
    	limit = l;
    	limit();
    }
    
    
    private void limit () {
    	double m = mag();
    	if (m > limit) {
    		double ratio = m / limit;
    		for (int i=0; i<p.length; i++){
    			this.p[i] /= ratio ;
    		}
    	}
    }


    public PSOVector clone () {
    	PSOVector clone = new PSOVector(dim);
    	clone.set(this.p);
        return clone;
    }


    public String toStringOutput () {
    	String output = "";
    	for(int i=0; i<p.length; i++){
    		output += "p"+i+" : " + p[i] + "\t";
    	}
        return output;
    }
    
    
    public double[] getVectorCoordinate(){
    	return p;
    }
    
    // t-max
    public double getBiggestResult(){
    	double max = p[0];
    	for(int i=1; i<p.length; i++){
    		if(max <= p[i])
    			max = p[i];		
    	}
    	return max;
    }
    
    public double getSum(){
    	double sum = 0;
    	for(int i=0; i<p.length; i++){
    		sum += p[i];
    	}
    	return sum;
    }
    
    public PSOVector getVectorRatio(){
        double[] w = new double[dim];
        for(int i = 0; i<w.length; i++){
            w[i] = this.p[i];
        }
        //System.arraycopy(this.p, 1, w, 1, w.length - 1);
        return new PSOVector(w);
    }
}

