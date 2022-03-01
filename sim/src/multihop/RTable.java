package multihop;

public class RTable {
	int id;
	String des;
	String route;
	int hop;
	double ratio;
	double timeCompute;
	double timeTrans;
	double resource;
	int npath;
	double cWL;
	RequestPSO req;
	double timeSer;
	
	/**
	 * @param des is node destination
	 * @param route is next node 
	 * @param hop is number of hop from des
	 * @pram resource is capacity of node 
	 * */
	public RTable(int id, String des, String route, int hop, double resource ) {
		super();
		this.id = id;
		this.des = des;
		this.route = route;
		this.hop = hop;
		this.resource=resource;
		this.cWL=0;
	}
	
	
	public RTable(int id, String des, String route, int hop, double resource, RequestPSO req) {
		super();
		this.id = id;
		this.des = des;
		this.route = route;
		this.hop = hop;
		this.resource=resource;
		this.cWL=0;
		this.req=req;
	}

	

	public double getTimeSer() {
		return timeSer;
	}


	public void setTimeSer(double timeSer) {
		this.timeSer = timeSer;
	}


	public RequestPSO getReq() {
		return req;
	}


	public void setReq(RequestPSO req) {
		this.req = req;
	}


	public double getcWL() {
		return cWL;
	}


	public void setcWL(double cWL) {
		this.cWL = cWL;
	}


	public int getNpath() {
		return npath;
	}


	public void setNpath(int npath) {
		this.npath = npath;
	}


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
	
	public double getResource() {
		return resource;
	}
	public void setResource(double resource) {
		this.resource = resource;
	}
	public String toString() {
		String rtable = "id: " + id + " req:" + req.getId() + " src:" + req.getSrcNode().getName() + " des:" + des + " route:" + route + " p:" + ratio 
				+ " tComp:" + timeCompute + " tTrans:" + timeTrans + " tSer: " + (timeCompute+timeTrans)
				+ " hop:" + hop + " npath:" + npath 
				+ " cWL:" + cWL;
		
		return rtable;
	}
	
}
