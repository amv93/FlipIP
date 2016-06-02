package com.flipip.rest;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"ipAddress","usedByName","purpose","inUse"})
public class Record implements Comparable<Record>{

	@Override
	public String toString() {
		return "Record [ipAddress=" + ipAddress + ", usedByName=" + usedByName
				+ ", purpose=" + purpose + ", inUse=" + inUse + "]";
	}
	private String ipAddress;
	public Record(String ipAddress, String usedByName, String purpose,
			String inUse) {
		super();
		this.ipAddress = ipAddress;
		this.usedByName = usedByName;
		this.purpose = purpose;
		this.inUse = inUse;
	}
	private String usedByName;
	private String purpose;
	private String inUse;
	
	public Record() {
	}
	public String getIpAddress() {
		return ipAddress;
	}
	@XmlElement(name="IP-address")
	public void setIpAddress(String ipAddress) {
		if(isIP(ipAddress))
		this.ipAddress = ipAddress;
	}
	private boolean isIP(String ipAddress) {
		 boolean valid = ipAddress.matches("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
		return valid;
	}	
	public String getUsedByName() {
		return usedByName;
	}
	@XmlElement(name="UsedBy-Name")
	public void setUsedByName(String usedByName) {
		this.usedByName = usedByName;
	}	
	public String getPurpose() {
		return purpose;
	}
	
	@XmlElement(name="Purpose")
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}	
	public String getInUse() {
		return inUse;
	}
	@XmlElement(name="In-Use")
	public void setInUse(String inUse) {
		this.inUse = inUse;
	}
	@Override
	public int compareTo(Record record) {
		return this.usedByName.compareToIgnoreCase(record.usedByName);	
	}	
}

