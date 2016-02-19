package fr.agilecom.webrequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * A complete Java class that shows how to open a URL, then read data (text) from that URL,
 * HttpURLConnection class (in combination with an InputStreamReader and BufferedReader).
 *
 * @author alvin alexander, devdaily.com.
 *
 */
public class HttpUrlConnectionReader
{
	
	public String read(String desiredUrl, boolean markLF){
	  
	  String results=null;
	  
	    try
	    {
	    	// if your url can contain weird characters you will want to 
	    	// encode it here, something like this:
	    	// myUrl = URLEncoder.encode(myUrl, "UTF-8");
	    	results = doHttpUrlConnectionAction(desiredUrl,markLF);
	      
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
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
	  private String doHttpUrlConnectionAction(String desiredUrl, boolean markLF)
	  throws Exception
	  {
	  
		 URL url = null;
	    BufferedReader reader = null;
	    StringBuilder stringBuilder;
	 
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

	    	// Cookies management :
	    	
	    	// read the output from the server
	      reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	      stringBuilder = new StringBuilder();
	      
	      String line = null;
	      while ((line = reader.readLine()) != null)
	      {
	    	  if(markLF){
	    	
	    		  stringBuilder.append(line + "\n");
	    	  }else{
	    		  stringBuilder.append(line);
	    	  }
	      }
	      //stringBuilder.append(line + "\n");
	      return stringBuilder.toString();
	      //return list;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally{
			// close the reader; this can throw an exception too, so
			// wrap it in another try/catch block.
			if (reader != null){
		        try{
		        	reader.close();
		        }catch (IOException ioe){
		        	ioe.printStackTrace();
		        }
			}//if
		}
	  }

}