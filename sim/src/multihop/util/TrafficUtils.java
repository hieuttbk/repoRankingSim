package multihop.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import multihop.Constants;
import multihop.node.NodeVehicle;
import multihop.request.RequestBase;
import multihop.request.RequestVehicle;

public class TrafficUtils {

	public static Queue<RequestBase> createReqList(List<NodeVehicle> topo) throws IOException {
		FileWriter traff;
		traff = new FileWriter("traffic_module.txt");
		traff.write("\n List REQ: \n" + "id\t" + "srcNode\t" + "timeArrival\t" + "workLoad\t" + "\n");

		Queue<RequestBase> reqPiority = new PriorityQueue<RequestBase>(); // store req by time and id

		int[] data = { 24, 30, 60, 200 };
		int DATA = 0;
		int[] fixedNode = { 6, 0, 5 };

		int idReq = 1;
		for (int n = 0; n < fixedNode.length; n++) {
			int idNode = fixedNode[n];
			double reqTime = 0;
			for (int i = 1; i <= Constants.NUM_REQ; i++) {
				int wl = data[DATA];
				if (idNode == 6) {
					wl = data[3];
				} else {
					wl = data[3];
				}
				reqPiority.add(new RequestBase(idReq, wl, topo.get(idNode), reqTime, false));
				traff.write(idReq + "\t" + idNode + "\t" + reqTime + "\t" + wl + "\n");
				reqTime += 1;
				idReq++;
			}
		}
		traff.close();
		return reqPiority;
	}

}
