package fr.agilecom.webrequest;

import java.util.HashMap;

import org.json.JSONException;

import fr.agilecom.webrequester.bean.AnnonceOccasionAuto;

public interface WebHttpRequester {
	
	public static int DEFAULT_REQUEST_TEMPO = 500;
	public void doRequest()  throws JSONException ;
	public void doRequest(int tempo)  throws JSONException ;
	public HashMap<String, AnnonceOccasionAuto> getResult();
	
}
