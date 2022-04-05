package multihop.request;

import multihop.node.NodeRSU;
import multihop.node.NodeVehicle;

public class RequestRSU extends RequestVehicle {
	NodeRSU srcNodeRSU;
	int desRSU;
	double wlRSU;

	public RequestRSU(int id, double wL, NodeVehicle srcNode, double timeInit, boolean done, int des, String route,
			double start, double end) {
		super(id, wL, srcNode, timeInit, done, des, route, start, end);
	}

	public RequestRSU(RequestVehicle rv) {
		//dWL = rv.getMovedData();
		super(rv.getId(), rv.getMovedData(),rv.getSrcNode(),rv.getTimeInit(),rv.isDone(),rv.getDes(),rv.getRoute(),rv.getStart(),rv.getEnd());
		//this.wlRSU = rv.getMovedData();
	}

	public NodeRSU getSrcNodeRSU() {
		return srcNodeRSU;
	}

	public void setSrcNodeRSU(NodeRSU srcNodeRSU) {
		this.srcNodeRSU = srcNodeRSU;
	}

	public double getWlRSU() {
		return wlRSU;
	}

	public void setWlRSU(double wlRSU) {
		this.wlRSU = wlRSU;
	}

	
	
}
