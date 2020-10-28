package com.tome25.remotenotifications.network;

import com.tome25.utils.json.JsonObject;

/**
 * A class storing a address with a udp port and a tcp port.
 * 
 * @author ToMe25
 *
 */
public class UDPTCPAddress {

	private String address;
	private int udpPort;
	private int tcpPort;
	private JsonObject json;

	/**
	 * Creates a new address object from the given body, udp port, and tcp port.
	 * 
	 * @param address the body of the address.
	 * @param udpPort the udp port.
	 * @param tcpPort the tcp port.
	 */
	public UDPTCPAddress(String address, int udpPort, int tcpPort) {
		this.address = address;
		this.udpPort = udpPort;
		this.tcpPort = tcpPort;
	}

	/**
	 * Creates a new address object from the given json element.
	 * 
	 * @param json the json to get the properties for this address from.
	 */
	public UDPTCPAddress(JsonObject json) {
		this.address = json.getString("addr");
		this.udpPort = (int) json.get("udp");
		this.tcpPort = (int) json.get("tcp");
	}

	/**
	 * Gets the body of this address.
	 * 
	 * @return the body of this address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Gets the udp port of this address.
	 * 
	 * @return the udp port of this address.
	 */
	public int getUdpPort() {
		return udpPort;
	}

	/**
	 * Gets the tcp port of this address.
	 * 
	 * @return the tcp port of this address.
	 */
	public int getTcpPort() {
		return tcpPort;
	}

	/**
	 * Returns the json representation of this address.
	 * 
	 * @return the json representation of this address.
	 */
	public JsonObject toJson() {
		if (json == null) {
			json = new JsonObject("addr", address);
			json.put("udp", udpPort);
			json.put("tcp", tcpPort);
		}
		return json;
	}

	@Override
	public String toString() {
		return String.format("%s[address=\"%s\", udp-port=%d, tcp-port=%d]", getClass().getName(), address, udpPort,
				tcpPort);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + tcpPort;
		result = prime * result + udpPort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UDPTCPAddress other = (UDPTCPAddress) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (tcpPort != other.tcpPort)
			return false;
		if (udpPort != other.udpPort)
			return false;
		return true;
	}

}
