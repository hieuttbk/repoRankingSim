package ManagerApplication;

public class Container {
	private String containerName;
	private String containerID;

	public Container(String containerName, String containerID) {
		this.containerName = containerName;
		this.containerID = containerID;
	}

	public String getContainerID() {
		return containerID;
	}

	public String getContainerName() {
		return containerName;
	}
}
