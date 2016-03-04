package fr.agilecom.webrequester.core;

import java.util.HashMap;

import org.json.JSONException;

import fr.agilecom.webrequester.bean.AnnonceOccasionAuto;

public interface WebHttpRequester {
	
	public static int DEFAULT_REQUEST_TEMPO = 500;
	public boolean doRequest()  throws JSONException ;
	public boolean doRequest(int tempo)  throws JSONException ;
	public HashMap<String, AnnonceOccasionAuto> getResult();
	
}
