package multihop.request;

import multihop.node.NodeVehicle;

public class RequestBase implements Comparable<RequestBase> {
	int id;
	double WL;
	NodeVehicle srcNode;
	double timeInit;
	boolean done;



	public RequestBase(int id, double wL, NodeVehicle srcNode, double timeInit, boolean done) {
		super();
		this.id = id;
		WL = wL;
		this.srcNode = srcNode;
		this.timeInit = timeInit;
		this.done = done;
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

	public NodeVehicle getSrcNode() {
		return srcNode;
	}

	public void setSrcNode(NodeVehicle srcNode) {
		this.srcNode = srcNode;
	}

	public double getTimeInit() {
		return timeInit;
	}

	public void setTimeInit(double timeInit) {
		this.timeInit = timeInit;
	}
	
	@Override
	public int compareTo(RequestBase o) {

		if (this.timeInit > o.timeInit) {
			return 1;
		} else if (this.timeInit < o.timeInit) {
			return -1;
		} else {
			if (this.id > o.id) {
				return 1;
			} else if (this.id < o.id) {
				return -1;
			} else {
				return 0;
			}
		}
	}

}
