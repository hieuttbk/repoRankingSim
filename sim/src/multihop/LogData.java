package multihop;

public class LogData {
	double WL;
	double[] p;
	double[] timeTrans;
	double[] timeCompute;
	
	
	
	
	public LogData(double wL) {
		super();
		WL = wL;
	}


	public LogData(double wL, double[] p, double[] timeTrans, double[] timeCompute) {
		super();
		WL = wL;
		this.p = p;
		this.timeTrans = timeTrans;
		this.timeCompute = timeCompute;
	}
	
	
	public double getWL() {
		return WL;
	}
	public void setWL(double wL) {
		WL = wL;
	}
	public double[] getP() {
		return p;
	}
	public void setP(double[] p) {
		this.p = p;
	}
	public double[] getTimeTrans() {
		return timeTrans;
	}
	public void setTimeTrans(double[] timeTrans) {
		this.timeTrans = timeTrans;
	}
	public double[] getTimeCompute() {
		return timeCompute;
	}
	public void setTimeCompute(double[] timeCompute) {
		this.timeCompute = timeCompute;
	}

}
