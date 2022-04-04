package multihop.node;

import java.util.Vector;

public class NodeRSU extends NodeBase {

	public NodeRSU(int id, String name, double lat, double lng, int range, double res) {
		super(id, name, lat, lng, range, res);
		// TODO Auto-generated constructor stub
	}

	Vector<NodeRSU> nodeNeigbour = new Vector<NodeRSU>();
	Vector<Vector<NodeVehicle>> nodeChild = new Vector<Vector<NodeVehicle>>();

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

}
