package com.flipip.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.flipip.db.DataSource;

@Path("/RecordService")
public class RecordService {
	DataSource recMgr = DataSource.getInstance();
	/*
	 * login - This API logs in the valid user if he is not already logged in.
	 */
	@GET
	@Path("/login/{userName}")	
    public Response login(@PathParam("userName") String userName){	
		String userExists = recMgr.login(userName);
		if(userExists !=null){
			if(userExists.equalsIgnoreCase("valid")){
			   return Response.status(Response.Status.OK).build();
			}
			else{ //i.e.if(userExists.equalsIgnoreCase("duplicate"))
				return Response.status(Response.Status.CONFLICT).build();
			}
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
	
	/*
	 * This API gets all the records that is assigned in the name of the logged-in user.
	 */
	@GET
    @Path("/user/{userName}")    
    public Response getUser(@PathParam("userName") String userName){		
		String userRecords = recMgr.getUserRecords(userName);
         if(userRecords != null) {
        	 return Response.status(Response.Status.OK).entity(userRecords).build(); 
         }
         return Response.status(Response.Status.NO_CONTENT).build();
    }
	/*
	 * This API randomly assigns an IP to the logged-in user and returns the assigned IP
	 * 	
	 */
	@POST
    @Path("/assign/")     
	public Response assignIP(@HeaderParam("purpose") String purpose,String userName){		
		String ip =recMgr.assignIP(userName,purpose);		
	    if(ip !=null){
	    	return Response.status(Response.Status.OK).entity(ip).build();
	    }
	    return Response.status(Response.Status.NO_CONTENT).build();
    }
	/*
	 * This API removes the deletes the requested IP from the logged-in user's record-list 
	 */
	@DELETE
    @Path("/deleteRecord/{userName}")     
	public Response deleteRecord(@PathParam("userName") String userName,String ipAddress){		
		recMgr.deleteRecord(userName,ipAddress);		
	   	return Response.status(Response.Status.OK).build();	   
    }
	/*
	 * This API logs out the user.
	 */
	@PUT
	@Path("/logout/{userName}")
	public Response logOut(@PathParam("userName") String userName){
		recMgr.logOut(userName);
		return Response.status(Response.Status.OK).build();		
	}
}


