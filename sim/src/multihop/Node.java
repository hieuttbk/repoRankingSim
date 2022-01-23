package multihop;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Node {
	int lvl=Constants.MAXINT; // level
	int id;
	double lat;
	double lng;
	String name;
	double res;
	double gain;
	double WL=0;
	
	double rank;
	double rankCluster;
	
	Node parent;
	
	int RANGE = Constants.RANGE;
	
	Vector<Node> nodeLK = new Vector<Node>();
	
	Vector<Node> nodeChild = new Vector<Node>();
	
	Vector<Node> nodeCluster = new Vector<Node>();

	Vector<Node> vNode = new Vector<Node>();
	
	Map<Integer,Double> rankMap =  new HashMap<Integer,Double>();
	
	
	
	
	public Vector<Node> getNodeCluster() {
		return nodeCluster;
	}


	public void setNodeCluster(Vector<Node> nodeCluster) {
		this.nodeCluster = nodeCluster;
	}


	public double getRankCluster() {
		return rankCluster;
	}


	public void setRankCluster(double rankCluster) {
		this.rankCluster = rankCluster;
	}


	public double getRank() {
		return rank;
	}


	public void setRank(double rank) {
		this.rank = rank;
	}


	public Map<Integer, Double> getRankMap() {
		return rankMap;
	}


	public void setRankMap(Map<Integer, Double> rankMap) {
		this.rankMap = rankMap;
	}


	private void caclRank() {
		Map.Entry<Integer,Double> maxEntry = null;

		for (Map.Entry<Integer,Double> entry : rankMap.entrySet())
		{
		    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
		    {
		        maxEntry = entry;
		    }
		}
		rank=maxEntry.getValue();
	}
	
	
	Node(){};
	
	/**
	 * init WL =0 and RES = Const
	 * */
	Node (int id, String name, double lat, double lng){
		this.id=id;
		this.name=name;
		this.lat=lat;
		this.lng=lng;
		this.WL=0;
		this.res=Constants.RES;
	}
	
	
	public String toString() {
		return "Node" + id + " " + lat + " " + lng;
		
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

	public double getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public double getLng() {
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


	public Vector<Node> getvNode() {
		return vNode;
	}


	public void setvNode(Vector<Node> vNode) {
		this.vNode = vNode;
	}
	
	
	
}
