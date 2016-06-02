package com.flipip.db;

public interface IDataSource {
public String login(String userName);
public String getUserRecords(String userName);	
public String assignIP(String userName,String purpose);
public void deleteRecord(String userName, String ipAddress);
public void logOut(String userName);
}

