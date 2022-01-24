package multihop;

public class RequestPSO {
	int id;
	double WL;
	Node srcNode;
	
	
	
	
	public RequestPSO(int id, double wL, Node srcNode) {
		super();
		this.id = id;
		WL = wL;
		this.srcNode = srcNode;
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
	
}
