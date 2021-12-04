package RPL;

import java.util.Vector;

public class Node {
	int lvl=Constants.MAXINT; // level
	int id;
	int lat;
	int lng;
	String name;
	double res;
	double gain;
	double WL=0;
	
	Node parent;
	
	int RANGE = Constants.RANGE;
	
	Vector<Node> nodeLK = new Vector<Node>();
	
	Vector<Node> nodeChild = new Vector<Node>();
	
	Node(){};
	
	Node (int id, String name, int lat, int lng){
		this.id=id;
		this.name=name;
		this.lat=lat;
		this.lng=lng;
	}
	
	Boolean checkLK(Node a) {
		double k = (this.lat-a.getLat())*(this.lat-a.getLat())+(this.lng-a.getLng())*(this.lng-a.getLng());	
//		System.out.println("checking node: " + this.name + " and " + a.getName() + " kq:  " + (k - RANGE) );
		if (k<=RANGE) {
	//		System.out.println("checking node: " + this.name + " and " + a.getName());
			return true;
		}
		return false;
		
	}
	
	public String getChildString() {
		String child="";
		for (Node n:nodeChild) {
			child = child + " " + n.getName();
		}
	return child;
	}

	public double getWL() {
		return WL;
	}

	public void setWL(double wL) {
		WL = wL;
	}

	public int getLvl() {
		return lvl;
	}

	public void setLvl(int lvl) {
		this.lvl = lvl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getLng() {
		return lng;
	}

	public void setLng(int lng) {
		this.lng = lng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRes() {
		return res;
	}

	public void setRes(double res) {
		this.res = res;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Vector<Node> getNodeLK() {
		return nodeLK;
	}

	public void setNodeLK(Vector<Node> nodeLK) {
		this.nodeLK = nodeLK;
	}

	public Vector<Node> getNodeChild() {
		return nodeChild;
	}

	public void setNodeChild(Vector<Node> nodeChild) {
		this.nodeChild = nodeChild;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}
	
	
	
}
