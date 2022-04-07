package multihop.node;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import multihop.request.RequestRSU;

public class NodeRSU extends NodeBase {
	double CWL;
	double aWL;
//	double cWL;

	public NodeRSU(int id, String name, double lat, double lng, int range, double res) {
		super(id, name, lat, lng, range, res);
		// TODO Auto-generated constructor stub
	}

	Vector<NodeRSU> nodeNeigbour = new Vector<NodeRSU>();
	Vector<Vector<NodeVehicle>> nodeChild = new Vector<Vector<NodeVehicle>>();
	
	Queue<RequestRSU> qReq = new PriorityQueue<RequestRSU>();
	List<RequestRSU> doneReq = new ArrayList<RequestRSU>();
	Queue<RequestRSU> qReqV = new PriorityQueue<RequestRSU>();

	

	public Boolean checkLK(NodeVehicle a, int i) {
		double ax = a.getX()[i];
		double ay = a.getY()[i];
		double k = (this.lat - ax) * (this.lat - ax) + (this.lng - ay) * (this.lng - ay);
		if (k <= RANGE) {
			return true;
		}
		return false;
	}

	public Boolean checkLK(NodeRSU a) {
		double k = (this.lat - a.getLat()) * (this.lat - a.getLat())
				+ (this.lng - a.getLng()) * (this.lng - a.getLng());
		if (k <= RANGE) {
			return true;
		}
		return false;
	}
	
	
	public String toString() {
		
		return name + ": " + lat + " , " + lng;
		
	}

	
	
	public Queue<RequestRSU> getqReqV() {
		return qReqV;
	}

	public void setqReqV(Queue<RequestRSU> qReqV) {
		this.qReqV = qReqV;
	}

	public Queue<RequestRSU> getqReq() {
		return qReq;
	}

	public void setqReq(Queue<RequestRSU> qReq) {
		this.qReq = qReq;
	}

	public List<RequestRSU> getDoneReq() {
		return doneReq;
	}

	public void setDoneReq(List<RequestRSU> doneReq) {
		this.doneReq = doneReq;
	}

	public Vector<NodeRSU> getNodeNeigbour() {
		return nodeNeigbour;
	}

	public void setNodeNeigbour(Vector<NodeRSU> nodeNeigbour) {
		this.nodeNeigbour = nodeNeigbour;
	}

	public Vector<Vector<NodeVehicle>> getNodeChild() {
		return nodeChild;
	}

	public void setNodeChild(Vector<Vector<NodeVehicle>> nodeChild) {
		this.nodeChild = nodeChild;
	}

	public double getCWL() {
		return CWL;
	}

	public void setCWL(double cWL) {
		CWL = cWL;
	}

	public double getaWL() {
		return aWL;
	}

	public void setaWL(double aWL) {
		this.aWL = aWL;
	}



}
