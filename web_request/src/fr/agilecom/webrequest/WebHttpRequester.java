package fr.agilecom.webrequest;

import org.json.JSONException;

public interface WebHttpRequester {

	public void doRequest()  throws JSONException ;
	public void getResult();
}
