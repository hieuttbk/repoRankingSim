package multihop.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import multihop.Constants;
import multihop.Constants.TYPE;
import multihop.request.RequestBase;

public class Node {
	int lvl = Constants.MAXINT; // level
	int id;
	double lat;
	double lng;
	String name;
	double res;
	double gain;
	double WL = 0;
	double cWL = 0;

	double aWL = 0;
	double pWL = 0;

	double[] x;
	double[] y;
	double[] velo;
	double[] phi;
	double[] sign;

	double rank;
	double rankCluster;

	Node parent;

	RequestBase req;

	int RANGE = Constants.RANGE[Constants.TYPE.VEHICLE.ordinal()];

	Vector<Vector<Node>> nodeLK = new Vector<Vector<Node>>();

	Vector<Node> nodeLKRSU = new Vector<Node>();

	Map<Integer, Double> rankMap = new HashMap<Integer, Double>();

	List<RequestBase> listReq = new ArrayList<RequestBase>();

	Queue<RequestBase> qReq = new PriorityQueue<RequestBase>();

	List<RequestBase> doneReq = new ArrayList<RequestBase>();

	public Vector<Node> getNodeLKRSU() {
		return nodeLKRSU;
	}

	public void setNodeLKRSU(Vector<Node> nodeLKRSU) {
		this.nodeLKRSU = nodeLKRSU;
	}

	public int getRANGE() {
		return RANGE;
	}

	public void setRANGE(int rANGE) {
		RANGE = rANGE;
	}

	public Vector<Vector<Node>> getNodeLK() {
		return nodeLK;
	}

	public void setNodeLK(Vector<Vector<Node>> nodeLK) {
		this.nodeLK = nodeLK;
	}

	public double[] getPhi() {
		return phi;
	}

	public void setPhi(double[] phi) {
		this.phi = phi;
	}

	public double[] getSign() {
		return sign;
	}

	public void setSign(double[] sign) {
		this.sign = sign;
	}

	public double[] getX() {
		return x;
	}

	public void setX(double[] x) {
		this.x = x;
	}

	public double[] getY() {
		return y;
	}

	public void setY(double[] y) {
		this.y = y;
	}

	public double getpWL() {
		return pWL;
	}

	public void setpWL(double pWL) {
		this.pWL = pWL;
	}

	public double getaWL() {
		return aWL;
	}

	public void setaWL(double aWL) {
		this.aWL = aWL;
	}

	public List<RequestBase> getDoneReq() {
		return doneReq;
	}

	public void setDoneReq(List<RequestBase> doneReq) {
		this.doneReq = doneReq;
	}

	public Queue<RequestBase> getqReq() {
		return qReq;
	}

	public void setqReq(Queue<RequestBase> qReq) {
		this.qReq = qReq;
	}

	public List<RequestBase> getListReq() {
		return listReq;
	}

	public void setListReq(List<RequestBase> listReq) {
		this.listReq = listReq;
	}

	public RequestBase getReq() {
		return req;
	}

	public void setReq(RequestBase req) {
		this.req = req;
	}

	public double getcWL() {
		return cWL;
	}

	public void setcWL(double cWL) {
		this.cWL = cWL;
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
		Map.Entry<Integer, Double> maxEntry = null;

		for (Map.Entry<Integer, Double> entry : rankMap.entrySet()) {
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
		}
		rank = maxEntry.getValue();
	}

	Node() {
	};

	/**
	 * init WL =0 and RES = Const
	 */
	public Node(int id, String name, double lat, double lng) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.WL = 0;
		this.res = Constants.RES[Constants.TYPE.VEHICLE.ordinal()];
	}

	public String toString() {
		return "Node" + id + " " + lat + " " + lng;

	}

	public Boolean checkLK(Node a, int i) {

//		double k = (this.lat-a.getLat())*(this.lat-a.getLat())+(this.lng-a.getLng())*(this.lng-a.getLng());	
		double k = (this.x[i] - a.getX()[i]) * (this.x[i] - a.getX()[i])
				+ (this.y[i] - a.getY()[i]) * (this.y[i] - a.getY()[i]);
		if (k <= RANGE) {
			return true;
		}
		return false;

	}
	
	
	public Boolean checkLKRSU(Node a, int i) {

//		double k = (this.lat-a.getLat())*(this.lat-a.getLat())+(this.lng-a.getLng())*(this.lng-a.getLng());	
		double k = (this.lat - a.getX()[i]) * (this.lat - a.getX()[i])
				+ (this.lng - a.getY()[i]) * (this.lng - a.getY()[i]);
		if (k <= RANGE) {
			return true;
		}
		return false;

	}

	public Boolean checkLK(Node a) {

		double k = (this.lat - a.getLat()) * (this.lat - a.getLat())
				+ (this.lng - a.getLng()) * (this.lng - a.getLng());
		if (k <= RANGE) {
			return true;
		}
		return false;

	}

	public String getChildString() {
		String child = "";
		for (Node n : nodeLKRSU) {
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

	public Vector<Node> getNodeChild() {
		return nodeLKRSU;
	}

	public void setNodeChild(Vector<Node> nodeChild) {
		this.nodeLKRSU = nodeChild;
	}

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

}
