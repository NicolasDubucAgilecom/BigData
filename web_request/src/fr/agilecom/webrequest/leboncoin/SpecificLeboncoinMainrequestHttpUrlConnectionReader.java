package fr.agilecom.webrequest.leboncoin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 


/**
 *
 * A complete Java class that shows how to open a URL, then read data (text) from that URL,
 * HttpURLConnection class (in combination with an InputStreamReader and BufferedReader).
 *
 * @author alvin alexander, devdaily.com.
 *
 */
public class SpecificLeboncoinMainrequestHttpUrlConnectionReader
{

	private String url = null;
	private String filterregex = null;
	private String startAfter = null;
	private int returnGroupItem=0;
	
  public SpecificLeboncoinMainrequestHttpUrlConnectionReader(String url, String filterregex, String startAfter, int returnGroupItem)
  {
    try
    {
        // if your url can contain weird characters you will want to 
      // encode it here, something like this:
      // myUrl = URLEncoder.encode(myUrl, "UTF-8");
 
    	this.url = url;
    	this.filterregex = filterregex;
    	this.startAfter = startAfter;
    	this.returnGroupItem = returnGroupItem;
    }
    catch (Exception e)
    {
      // deal with the exception in your "controller"
    }
  }
 
  public SpecificLeboncoinMainrequestHttpUrlConnectionReader(String url, String filterregex, String startAfter)
  {
    try
    {
        // if your url can contain weird characters you will want to 
      // encode it here, something like this:
      // myUrl = URLEncoder.encode(myUrl, "UTF-8");
 
    	this.url = url;
    	this.filterregex = filterregex;
    	this.startAfter = startAfter;
    }
    catch (Exception e)
    {
      // deal with the exception in your "controller"
    }
  }
  
  public SpecificLeboncoinMainrequestHttpUrlConnectionReader(String url, String filterregex)
  {
    try
    {
        // if your url can contain weird characters you will want to 
      // encode it here, something like this:
      // myUrl = URLEncoder.encode(myUrl, "UTF-8");
 
    	this.url = url;
    	this.filterregex = filterregex;
    }
    catch (Exception e)
    {
      // deal with the exception in your "controller"
    }
  }
  
  public ArrayList<String> read(){
	  
	  ArrayList<String> results=null;
	  
	    try
	    {
	    	// if your url can contain weird characters you will want to 
	    	// encode it here, something like this:
	    	// myUrl = URLEncoder.encode(myUrl, "UTF-8");
	 
	    	results = doHttpUrlConnectionAction(url,filterregex, startAfter,returnGroupItem);
	      
	    }
	    catch (Exception e)
	    {
	      // deal with the exception in your "controller"
	    }
	    
	    return results;
  }
  
  /**
   * Returns the output from the given URL.
   * 
   * I tried to hide some of the ugliness of the exception-handling
   * in this method, and just return a high level Exception from here.
   * Modify this behavior as desired.
   * 
   * @param desiredUrl
   * @return
   * @throws Exception
   */
  private ArrayList<String> doHttpUrlConnectionAction(String desiredUrl, String filterregex, String startAfter, int returnGroupItem)
  throws Exception
  {
	  
	ArrayList<String> list = new ArrayList<String>();  
    URL url = null;
    BufferedReader reader = null;
 
    try
    {
      // create the HttpURLConnection
      url = new URL(desiredUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
       
      // just want to do an HTTP GET here
      connection.setRequestMethod("GET");
       
      // uncomment this if you want to write output to this url
      //connection.setDoOutput(true);
       
      // give it 15 seconds to respond
      connection.setReadTimeout(15*1000);
      connection.connect();
 
      // read the output from the server
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
     // stringBuilder = new StringBuilder();
 
      boolean startAfterMatch=false;
         	        
      String line = null;
      while ((line = reader.readLine()) != null)
      {
    	  // If startAfter if 
    	  if(startAfter!=null && !startAfter.isEmpty()) {
	    	  if(line.contains(startAfter)){
	    		  startAfterMatch=true;
	    	  }
    	  }
    	  
    	  if(startAfter==null || startAfterMatch){
    		  if(line.matches(filterregex)){
    			  if(returnGroupItem!=0){
    				    Pattern pattern = Pattern.compile(filterregex);

    				    Matcher matcher = pattern.matcher(line);
    				    @SuppressWarnings("unused")
						boolean matches = matcher.matches();
    				    //stringBuilder.append(matcher.group(returnGroupItem) + "\n");
    				    list.add(matcher.group(returnGroupItem));
    			  }else{
    				  //stringBuilder.append(line + "\n");
    				  list.add(line);
    			  }
    		  }
    	  }
    	  
    	 // stringBuilder.append(line + "\n");
      }
      //return stringBuilder.toString();
      connection.disconnect();
      return list;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
    finally
    {
      // close the reader; this can throw an exception too, so
      // wrap it in another try/catch block.
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (IOException ioe)
        {
          ioe.printStackTrace();
        }
      }
    }
  }
}