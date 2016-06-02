/**
 * 
 */

 var xmlhttp;
 var serviceURL ="http://localhost:8080/IPFlip/rest/RecordService/";
 var userName,_isloggedIn=false;
 xmlhttp = new XMLHttpRequest();
 var table = document.getElementById("userRecords")
 tbody = table.getElementsByTagName('tbody')[0],
 clone = tbody.removeChild(tbody.rows[0]);	
 var info =  document.getElementById("infoDiv");
 
 document.getElementById("userName").onkeydown = function(event){
	    if(!event)
	    event = event || window.event;
	    var keycode = event.which || event.keyCode || 0;
	    if(keycode === 13){
	       validateUser();
	    }
	}
 
 function validateUser() {
     userName = document.getElementById("userName").value;
     userName = userName.toLowerCase();
     var errorDisplay = document.getElementById("errorDisplayDiv");
    if(userName == null || userName.toString().trim().length == 0){
    	errorDisplay.innerHTML = "Username cannot be empty."
         return;
    }
    errorDisplay.innerHTML ="";
    var url = null ; 
    url =serviceURL+ "login/" + userName;     
     xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4) {
                 if(xmlhttp.status == 200) {                                      
                	    errorDisplay.innerHTML ="";
                    	getUserRecordList(); 
                    	_isloggedIn=true;
                 }else if(xmlhttp.status == 404){
                	 	errorDisplay.innerHTML = "Please enter valid user name";	
                 }else if(xmlhttp.status == 409){
                	 	errorDisplay.innerHTML = "User is already logged in.";	
                 } else	{                	 	
                	 	console.log(xmlhttp.responseText);
                }
            }
     }; 
     xmlhttp.open('GET',url,true);
     xmlhttp.send(null);   
 }
     
  function getUserRecordList(){
	  var url = serviceURL+ "user/" + userName.toString();	  
	  xmlhttp.onreadystatechange = function() {
		    if (xmlhttp.readyState == 4 ){
		    	if(xmlhttp.status == 200) {
		    		var userRecordList = JSON.parse(xmlhttp.responseText);
		    		displayHomePage(userRecordList);
		    	}else if(xmlhttp.status == 204){		    		
		    		displayHomePage();
		    		info.style.color= "#5a5656";
		    		info.style.display= "block";
		    		info.innerHTML ="No Records found." ;
		    	}
		    	else{
		    		console.log(xmlhttp.responseText);
		    	}
		   	}
	  };
	  xmlhttp.open('GET',url,true);	
	  xmlhttp.send();	
  }

  function displayHomePage(userRecordList){	 
	  document.getElementById("loginDiv").style.display="none";
	  document.getElementById("displayUser").innerHTML=userName;
	  document.getElementById("homePageDiv").style.display="block";
	  document.getElementById("logoutBtn").style.display = "block";
	  document.getElementById("userRecords").tBodies.item(0).innerHTML="";	   
	  var assignIPDiv = document.getElementById("assignIPDiv");
	  var thead = table.getElementsByTagName('thead')[0];
	  assignIPDiv.style.display = "block" ;
	  if(userRecordList==null){
		  table.style.display = "none";		  
		  return; 
	  }
	  table.style.display = "table";
	  if(userName == "admin"){		  
		  assignIPDiv.style.display = "none" ; 
		  thead.rows[0].cells[4].innerHTML = "User";
		  for(var i = 0; i < userRecordList.length; i++) {		   
			    var newRow = clone.cloneNode(true);
			    newRow.cells[0].innerHTML = i+1;
			    newRow.cells[1].innerHTML = userRecordList[i].ipAddress ;
			    newRow.cells[2].innerHTML = userRecordList[i].purpose ;
			    newRow.cells[3].innerHTML = userRecordList[i].inUse ;
			    newRow.cells[4].innerHTML = userRecordList[i].usedByName;
			    tbody.appendChild(newRow);
		    }
		  document.getElementById("topOfPage").style.display = "block"; 
	  }
	  else{
		  thead.rows[0].cells[4].innerHTML = "";
		  for(var i = 0; i < userRecordList.length; i++) {		   
			  var newRow = clone.cloneNode(true);
			  newRow.cells[0].innerHTML = i+1;
			  newRow.cells[1].innerHTML = userRecordList[i].ipAddress ;
			  newRow.cells[2].innerHTML = userRecordList[i].purpose ;
			  newRow.cells[3].innerHTML = userRecordList[i].inUse ;
			  tbody.appendChild(newRow);
		  }
	  }	
  }
  function deleteRow(row) {	    
	    var index = row.parentNode.parentNode.rowIndex;		      
	    var ipAddress = table.rows[index].cells[1].innerHTML;
	    table.deleteRow(index);	    
	    if(tbody.rows.length == 0){
	    	table.style.display = "none";
	    }
	    for(var i = 0;i<tbody.rows.length;i++){
	    	tbody.rows[i].cells[0].innerHTML= i+1;
	    }
	    var url = null ; 
	    url =serviceURL+ "deleteRecord/" + userName; 	   
	    xmlhttp.onreadystatechange = function() {
	          if (xmlhttp.readyState == 4) {
	                 if(xmlhttp.status == 200) { 
	                	 info.style.color = "green";
	            		 info.style.display ="block";
	            		 info.innerHTML = ipAddress.toString() + " has been deleted";
	                 }
	                 else{
	                	 console.log(xmlhttp.responseText);
	                 }
	          }
	    };
	    xmlhttp.open('DELETE',url,true);	  
	    xmlhttp.send(ipAddress.toString());	    
 }
  
 function convertHTMLObjToString(htmlObj){
	 	var txt;
	 	var element = document.createElement("div");
	 	element.appendChild(htmlObj.cloneNode(false));
	 	txt = element.innerHTML;
	 	element = null;
	 	return txt;
 }
  
  function insertRow(row,slNum,ipAddress,purpose,inUse) {
	  	row.cells[0].innerHTML=slNum+1;
	    row.cells[1].innerHTML=ipAddress;
	    row.cells[2].innerHTML=purpose;
	    row.cells[3].innerHTML=inUse;
	    return row;
  }
  
  function assignIP(){
	  var url = null;
	  var purpose = document.getElementById("purpose").value;	 	  
	  if(!purpose || 0 === purpose.length || !purpose.trim()){		
		 info.style.color = "red";
		 info.style.display ="block";
		 info.innerHTML = "Enter your purpose.";
		 return;
	  }
	  url =serviceURL+ "assign/" ;
	  xmlhttp.onreadystatechange = function() {
          if (xmlhttp.readyState == 4) {
        	 info.style.display ="block";
             if(xmlhttp.status == 200) {
                    var ip = xmlhttp.responseText;                                
                 	info.style.color ="green";
                 	info.innerHTML = "You have been assigned : " + ip ;  
                 	var newRow = insertRow(clone.cloneNode(true),table.tBodies[0].rows.length,ip,purpose,"Used");
                 	tbody.appendChild(newRow);
                 	table.style.display="table";               	                 	
             }else if(xmlhttp.status == 204){
            	 	info.style.color ="red";
            	 	info.innerHTML = "IP address could not be assigned as all IPs are in use." ;
             }else{
            	    console.log(xmlhttp.responseText);
             }
             document.getElementById("purpose").value="";     
          }
      };
   xmlhttp.open('POST',url,true);
   xmlhttp.setRequestHeader("purpose",purpose.toString());
   xmlhttp.send(userName);	
}
 
  function logOut(){
	  var url = null ; 
	  url =serviceURL+ "logout/" + userName;  
	  xmlhttp.onreadystatechange = function() {
		  if (xmlhttp.readyState == 4){
			  if(xmlhttp.status == 200) {	  
				  _isloggedIn=false;
				  document.getElementById("userRecords").tBodies.item(0).innerHTML="";
				  document.getElementById("infoDiv").style.display="none";
				  document.getElementById("homePageDiv").style.display="none";	  
				  document.getElementById("logoutBtn").style.display = "none";
				  document.getElementById("loginDiv").style.display="block";	 
				  document.getElementById("userName").value = "";	
				  document.getElementById("topOfPage").style.display = "none"; 
			  }else{
				  console.log(xmlhttp.responseText);
			  }		  
		  }
	  };
	  xmlhttp.open('PUT',url,true);
	  xmlhttp.send(null);  
  }
  
  var Event = window.attachEvent || window.addEventListener;
  var chkevent = window.attachEvent ? 'onbeforeunload' : 'beforeunload'; /// make IE7, IE8 compitable
  Event(chkevent, function(e) { // For >=IE7, Chrome, Firefox	  	
	  e.preventDefault();
	  if(_isloggedIn){
		  if(_isloggedIn){
    		  var url = null ; 
    		  url =serviceURL+ "logout/" + userName;
    		  xmlhttp.onreadystatechange = function() {
    			  if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {	  
    					  _isloggedIn=false;
    			  }
    		  };
    		  xmlhttp.open('PUT',url,true);
    		  xmlhttp.send(null); 
    	  }    
	  } 
}); 
  
  