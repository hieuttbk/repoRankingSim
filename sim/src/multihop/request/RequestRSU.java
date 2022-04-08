package multihop.request;

import multihop.node.NodeRSU;
import multihop.node.NodeVehicle;

public class RequestRSU extends RequestVehicle {
	NodeRSU srcNodeRSU;
	int desRSU;
	double wlRSU;
	double timeVR; // t_trans from Vehicle to RSU

	public RequestRSU(int id, double wL, NodeVehicle srcNode, double timeInit, boolean done, int des, String route,
			double start, double end) {
		super(id, wL, srcNode, timeInit, done, des, route, start, end);
	}

	public RequestRSU(RequestVehicle rv) {
		//dWL = rv.getMovedData();
		super(rv.getId(), rv.getMovedData(),rv.getSrcNode(),rv.getTimeInit(),rv.isDone(),rv.getDes(),rv.getRoute(),rv.getStart(),rv.getEnd());
		//this.wlRSU = rv.getMovedData();
	}
	
	public String toString() {
		if(this.srcNodeRSU!=null) {
			return "req"+ this.id + " (p=" + this.ratio + " " +  this.srcNodeRSU.getName() + "-" + this.srcNode.getName() + ")";
		}else {
			return "req"+ this.id + " (" + this.ratio + " " + this.srcNode.getName() + ")";

		}
	}

	public double getTimeVR() {
		return timeVR;
	}

	public void setTimeVR(double timeVR) {
		this.timeVR = timeVR;
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
	

	@Override
	public int compareTo(RequestBase o) {
		
		if (this.start >= ((RequestRSU)o).start) {
			return 1;
		} else if (this.start < ((RequestRSU)o).start) {
			return -1;
		} else {
			return 0;
		}
	}
	
	
}
