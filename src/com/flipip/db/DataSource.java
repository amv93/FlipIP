package com.flipip.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.flipip.rest.Record;
import com.flipip.rest.RecordList;


public class DataSource implements IDataSource {
	private static DataSource instance = null;
	private RecordList recordList = null;
	List<String> availableIPRecList = new ArrayList<String>();
	/*List<Record> availableIPRecList =new ArrayList<Record>();*/
	String RECORDSFILE = "",ADMIN="admin";		
	List<String> validUserList = null;
	List<String> loggedInUserList = null;
	private Object lock = new Object();
	/*
	 * This function is called when the server boots and application is hosted.
	 */
	public void initialize() {
		System.out.println("initialize");
		initializeUserList();
		loggedInUserList = new ArrayList<String>();
		RECORDSFILE = System.getProperty("catalina.home");		
		RECORDSFILE += "/conf/BLRSMBIPaddress.xml";
		RecordList recList = getAllRecords(); 
		if(recList != null) {
			recordList = recList;
			getAvailableIPRecords();
		}		
	}
	/*
	 * This function stores the valid users in a list that contains global handles.
	 */
	private void initializeUserList() {
	    String fileName = System.getProperty("catalina.home");
	    fileName += "/conf/BLRSMBGlobalHandle.txt" ; 
	    BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(fileName));		
			validUserList = new ArrayList<String>();
	    	String user;
	    	while((user = br.readLine()) != null) {
	   				validUserList.add(user);	   				
	    		}
	   		br.close();   
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	/*
	 * This function returns assigned IP records obtained from the Recordsfile
	 */
	public RecordList getAllRecords() {
		RecordList tempRecListObj = null;	
		File file = new File(RECORDSFILE);
		if(file.exists()){
			try {			
				JAXBContext jaxbContext = JAXBContext.newInstance(RecordList.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();				
				tempRecListObj = (RecordList)jaxbUnmarshaller.unmarshal(file);							
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		 }		
		return tempRecListObj;
	}
    /*
     * This function stores the unused IP records in the available IP records list
     */
	public void getAvailableIPRecords(){		
		if(recordList!=null){						
			for(Record record : recordList.getRecordList()){
				if(record.getInUse().equalsIgnoreCase("notused") && record.getUsedByName().equalsIgnoreCase("available"))
				{
					availableIPRecList.add(record.getIpAddress());
					/*availableIPRecList.add(record);*/
				}
			}			
		}	
	}
	/*
	 * This function logs in the user if he is a valid user and he is not already logged-in
	 */
	@Override
	public String login(String userName) {
		if(userName.equalsIgnoreCase(ADMIN)){
			return "valid";
		}
		for(String user : validUserList){
			if(user.equalsIgnoreCase(userName)){				
				if(!isloggedIn(userName)){
					loggedInUserList.add(user);
					return "valid";
				}
				else{
					return "duplicate";
				}
			}				
		}
		return null;
	}
	
	private boolean isloggedIn(String userName) {
		for(String user: loggedInUserList){
			if(user.equalsIgnoreCase(userName)){
				return true;
			}				
		}
		return false;
	}
	
	@Override
	public String getUserRecords(String userName) {
		String resp = null;
		synchronized (lock) {
			List<Record> selectedUserList =new ArrayList<Record>();
			if(!recordList.getRecordList().isEmpty()){
					if(ADMIN.equalsIgnoreCase(userName)){
					//	selectedUserList.addAll(recordList.getRecordList());
						List<Record> availableList =new ArrayList<Record>();
						for(Record user: recordList.getRecordList()){	
							if(user.getUsedByName().equalsIgnoreCase("available") ){
								availableList.add(user);								
							}else{
								selectedUserList.add(user);	
							}							
						}
						Collections.sort(selectedUserList);
						selectedUserList.addAll(availableList);
						//selectedUserList.addAll(availableIPRecList);
					}														
					else{
						for(Record user: recordList.getRecordList()){
								if(user.getUsedByName().equalsIgnoreCase(userName) ){
									selectedUserList.add(user);								
								}
						}	
					}
					if(!selectedUserList.isEmpty()){	
						resp =  ObjectToJsonStringConverter(selectedUserList);
					}
			}
			return resp;	
		}
	}
    
	/*
	 * This function assigns a random IP to the user and removes the reocrd from unsed record list
	 * 
	 */
	@Override
	public String assignIP(String userName,String purpose) {		
		synchronized (lock) {
			String assignedIP = null;	
			if(!availableIPRecList.isEmpty())
			{
				int index = new Random().nextInt(availableIPRecList.size());
				assignedIP = availableIPRecList.get(index);
				/*assignedIP = availableIPRecList.get(index).getIpAddress();*/
				for(Record record: recordList.getRecordList()){
					if(record.getIpAddress().equals(assignedIP)){
						record.setInUse("Used");
						record.setPurpose(purpose);
						record.setUsedByName(userName);
						availableIPRecList.remove(index);
						setRecords(recordList);
					}
				}
			}	
			return assignedIP ;	
		}
			
	}
	
	
	/*
	 * This function updates the records file
	 */
	private void setRecords(RecordList users) {
		File file = new File(RECORDSFILE);
		if(file.exists()){
		try{
			JAXBContext jaxbContext = JAXBContext.newInstance(RecordList.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(users, file);
			//jaxbMarshaller.marshal(users, System.out);
		    }catch (JAXBException e) {
			e.printStackTrace();
		    }
		}
	}

	private String ObjectToJsonStringConverter(List<Record> recordList){
		String recordListInJsonString = null ; 
		ObjectMapper mapper = new ObjectMapper();		
		try{			
			recordListInJsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(recordList);			
	    }catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return recordListInJsonString;
		
	}	
    /*
     * This function deletes the requested record and places the record in to unused record list
     */
	public void deleteRecord(String userName, String ipAddress) {	
		for(Record record: recordList.getRecordList()){
			if (ipAddress.equalsIgnoreCase(record.getIpAddress()) && userName.equalsIgnoreCase(record.getUsedByName())) {	
				record.setUsedByName("available");
				record.setPurpose(" ");
				record.setInUse("NotUsed");
				/*availableIPRecList.add(record);*/
				break;
			}
		}
		availableIPRecList.add(ipAddress);
		setRecords(recordList);				
	}
	
	public void logOut(String userName) {
		for (Iterator<String> iterator = loggedInUserList.iterator(); iterator.hasNext();) {
		    String user = iterator.next();
		    if (userName.equalsIgnoreCase(user)) {		       
		        iterator.remove();
		        return ;
		    }
		}
	   return ;
	}
	
	public static DataSource getInstance() {
	      if(instance == null) {
	         instance = new DataSource();
	      }
	      return instance;
	   }
}

