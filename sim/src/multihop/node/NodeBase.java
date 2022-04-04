package multihop.node;

public abstract class NodeBase {
	int id;
	String name;
	
	double lat;
	double lng;
	int RANGE;
	
	int lvl;
	
	double res; 

	public NodeBase(int id, String name, double lat, double lng, int range, double res) {
		super();
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.RANGE = range;
		this.res = res;
	}
//	public abstract Boolean checkLK(NodeBase a, int i);
//	public abstract Boolean checkLK(NodeBase a);
//	

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public int getRange() {
		return RANGE;
	}
	public void setRange(int range) {
		this.RANGE = range;
	}
	public int getLvl() {
		return lvl;
	}
	public void setLvl(int lvl) {
		this.lvl = lvl;
	}
	public double getRes() {
		return res;
	}
	public void setRes(double res) {
		this.res = res;
	}

}
