package multihop;

public class RTable {
	int id;
	String des;
	String route;
	int hop;
	double ratio;
	double timeCompute;
	double timeTrans;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
	}
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public int getHop() {
		return hop;
	}
	public void setHop(int hop) {
		this.hop = hop;
	}
	public double getRatio() {
		return ratio;
	}
	public void setRatio(double ratio) {
		this.ratio = ratio;
	}
	public double getTimeCompute() {
		return timeCompute;
	}
	public void setTimeCompute(double timeCompute) {
		this.timeCompute = timeCompute;
	}
	public double getTimeTrans() {
		return timeTrans;
	}
	public void setTimeTrans(double timeTrans) {
		this.timeTrans = timeTrans;
	}
	
	
}
