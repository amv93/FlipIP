package com.flipip.rest;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RecordList")
public class RecordList {
	private ArrayList<Record> recordList = null ;
	
	private RecordList() {
	}
	
	public ArrayList<Record> getRecordList() {
		return recordList;
	}

	
	@XmlElement(name = "record")
	public void setRecordList(ArrayList<Record> recordList) {
		this.recordList = recordList;
	}	
}
