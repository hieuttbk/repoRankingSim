package multihop;

import java.util.ArrayList;
import java.util.List;

public class RequestPSO implements Comparable<RequestPSO>{
	int id;
	double WL;
	Node srcNode;
	
	int des;
	String route;
	double timeArrival;
	double timeProcess;
	double ratio;
	double timeTrans;
	boolean done;
	
	double start;
	double end;
	double timeSer;
	List<Node> listNodeReq = new ArrayList<Node>();
	
	
	
	
	
	public RequestPSO(int id, double wL, Node srcNode, double start, double timeArrival ) {
		super();
		this.id = id;
		WL = wL;
		this.srcNode = srcNode;
		this.start = start;
		this.timeArrival = timeArrival;
	}




	public RequestPSO(int id, double wL, int des, String route, double timeArrival, double timeProcess, boolean done) {
		super();
		this.id = id;
		WL = wL;
		this.des = des;
		this.route = route;
		this.timeArrival = timeArrival;
		this.timeProcess = timeProcess;
		this.done = done;
	}

	
	
	
	public RequestPSO(int id, double wL, int des, String route, double timeArrival, double timeProcess, boolean done,
			double start, double end) {
		super();
		this.id = id;
		WL = wL;
		this.des = des;
		this.route = route;
		this.timeArrival = timeArrival;
		this.timeProcess = timeProcess;
		this.done = done;
		this.start = start;
		this.end = end;
	}

	public RequestPSO(int id, double wL, int des, String route, double timeArrival, double timeProcess, boolean done,
			double start, double end, Node srcNode, double ratio, double timeTrans, double timeSer) {
		super();
		this.id = id;
		WL = wL;
		this.des = des;
		this.route = route;
		this.timeArrival = timeArrival;
		this.timeProcess = timeProcess;
		this.done = done;
		this.start = start;
		this.end = end;
		this.srcNode = srcNode;
		this.ratio=ratio;
		this.timeTrans=timeTrans;
		this.timeSer=timeSer;

	}


	public double getTimeSer() {
		return timeSer;
	}




	public void setTimeSer(double timeSer) {
		this.timeSer = timeSer;
	}




	public double getRatio() {
		return ratio;
	}




	public void setRatio(double ratio) {
		this.ratio = ratio;
	}




	public double getTimeTrans() {
		return timeTrans;
	}




	public void setTimeTrans(double timeTrans) {
		this.timeTrans = timeTrans;
	}




	public List<Node> getListNodeReq() {
		return listNodeReq;
	}




	public void setListNodeReq(List<Node> listNodeReq) {
		this.listNodeReq = listNodeReq;
	}




	public RequestPSO(int id, double wL, Node srcNode) {
		super();
		this.id = id;
		WL = wL;
		this.srcNode = srcNode;
	}
	
	
	
	public double getStart() {
		return start;
	}

	public void setStart(double start) {
		this.start = start;
	}

	public double getEnd() {
		return end;
	}

	public void setEnd(double end) {
		this.end = end;
	}

	public int getDes() {
		return des;
	}

	public void setDes(int des) {
		this.des = des;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public double getTimeArrival() {
		return timeArrival;
	}

	public void setTimeArrival(double timeArrival) {
		this.timeArrival = timeArrival;
	}

	public double getTimeProcess() {
		return timeProcess;
	}

	public void setTimeProcess(double timeProcess) {
		this.timeProcess = timeProcess;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getWL() {
		return WL;
	}
	public void setWL(double wL) {
		WL = wL;
	}
	public Node getSrcNode() {
		return srcNode;
	}
	public void setSrcNode(Node srcNode) {
		this.srcNode = srcNode;
	}

	@Override
	public int compareTo(RequestPSO o) {
		
		   if (this.start >= o.start) {
	            return 1;
	        } else if (this.start < o.start) {
	            return -1;
	        } else {
	            return 0;
	        }
	}
	
}
