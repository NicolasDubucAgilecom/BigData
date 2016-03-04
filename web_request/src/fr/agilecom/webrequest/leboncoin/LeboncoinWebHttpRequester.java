package fr.agilecom.webrequest.leboncoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import fr.agilecom.webrequest.AnnonceRequesterLauncher;
import fr.agilecom.webrequester.bean.AnnonceOccasionAuto;
import fr.agilecom.webrequester.core.HttpUrlConnectionReader;
import fr.agilecom.webrequester.core.WebHttpRequester;

public class LeboncoinWebHttpRequester implements WebHttpRequester {
	
	HashMap<String, AnnonceOccasionAuto> annouces = null;
	HashMap<String, AnnonceOccasionAuto> annouces_old = new HashMap<String, AnnonceOccasionAuto>();
	static Logger log = Logger.getLogger(AnnonceRequesterLauncher.class.getName());
	String first_id = null;
	
	@Override
	public boolean doRequest(int tempo) throws JSONException {
		
		annouces = new HashMap<String, AnnonceOccasionAuto>();
		
		// Recuperation de la liste des url des annonces leboncoin
		LeboncoinUrlConnectionReader jhcr = new LeboncoinUrlConnectionReader(
					"http://www.leboncoin.fr/voitures/offres/ile_de_france/?f=a&th=0",
					".*<a href=\"..(.*voitures.*).ca.*title=.*>.*",
					null,
					1
				);
		
		// Lectures des fiches d'annonces
		ArrayList<String> result = jhcr.read();
		Iterator<String> it = result.iterator();
		
		HttpUrlConnectionReader simpleHttp = new HttpUrlConnectionReader();
		String htmlpage = null;
		
		boolean found_old_annouce=false;
		
		// Pour chaque annonce
		
		while(it.hasNext()){
			
			String announce_url = it.next();
			AnnonceOccasionAuto annonce = new AnnonceOccasionAuto();
						
			// Anti DOS security :
			try {
				Thread.sleep(tempo);
			} catch (InterruptedException e) {}
			
			htmlpage=simpleHttp.read("http://"+announce_url, false);
//			System.out.println(htmlpage);
			// Récupération des blocks contenant les informations :
		    Pattern pattern = Pattern.compile(".*var utag_data = (\\{.*\\}).* </script>.*"
		    		+ "(<section class=\"properties lineNegative\".*properties_description.*</div>.*</section>).*"
		    		+ "<footer .*adview_footer mts\">.*"
		    		+ "" );
		    Matcher matcher = pattern.matcher(htmlpage);
		    boolean matches = matcher.matches();
		    if(matches){
		    	
		    	JSONObject json = null;
		    	
		    	// details
		    	String utag_data = matcher.group(1);
		    	json = new JSONObject(utag_data);
		    	
		    	String prix="price_not_found";
		    	String departement = "departement_not_found";
		    	String cp = "cp_not_found";
		    	String ville = "town_not_found";
		    	String carburant = "carburant_not_found";
		    	String kilometrage = "kilometrage_not_found";
		    	String typeboite = "typeboite_not_found";
		    	String brand = "brand_not_found";
		    	String modele = "modele_not_found";
		    	String releaseDate = "annee_not_found";
		    	
		    	try{
		    		prix = json.getString("prix");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		departement = json.getString("departement");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		cp = json.getString("cp");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		ville = json.getString("city");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : '" + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		carburant = json.getString("nrj");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		kilometrage = json.getString("km");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		typeboite = json.getString("vitesse");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		brand = json.getString("marque");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		modele = json.getString("modele");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	try{
		    		releaseDate = json.getString("annee");
		    	}catch(JSONException je){
		    		log.error("Error in parsing XML HTML page : " + 
		    					je.getMessage() + " / " +
		    					((je.getCause()!=null)?je.getCause().getMessage():" ")  +"'."
		    					);
		    	}
		    	
//		    	String section = matcher.group(2);		    	
//		    	String fields[] = section.split("itemprop=");
		    	
		    	if(!matches){
			    	annonce.setZz_status("Format XML (itemprop) non attendu. URL : " + announce_url);
		    		continue;
		    	}
		    	
		    	//String description = fields[8].substring(fields[8].indexOf(">") + ">".length(), fields[8].indexOf("</div>")).trim();
		    	
		    	annonce = new AnnonceOccasionAuto();
		    	
				annonce.setAnneeMiseEnCirculation(releaseDate);
				annonce.setCaburant(carburant);
				annonce.setCodePostal(cp);
				annonce.setCouleur_exterieure("");
				annonce.setCouleur_interieure("");
				annonce.setDepartement(departement);
//				annonce.setDescription(description);
				annonce.setKilometrage(kilometrage);
				annonce.setMarque(brand);
				annonce.setModele(modele);
				annonce.setNombre_porte("");
				annonce.setPremiere_main("");
				annonce.setPrix(prix);
				annonce.setPuissance_dynamique("");
				annonce.setPuissance_fiscale("");
				annonce.setTypeboite(typeboite);
				annonce.setVendeur("");
				annonce.setVille(ville);
				
   	
		    }else{
		    	annonce.setZz_status("Format XML (global) non attendu. URL : " + announce_url);
	    		log.error("Format XML (global) non attendu. URL : " + announce_url);
	    		
	    		continue;
		    }
		    String id = announce_url.substring(announce_url.lastIndexOf('/')+"/".length(), announce_url.indexOf(".htm"));

		    // Stop at the first
		    if(first_id==null)
		    	first_id=id;
		    
		    if(annouces_old.containsKey("leboncoin_"+id)){
		    	found_old_annouce=true;
		    	log.info("Stopping criteraia matches ! Annouce id has already found in previous main loop ! (" + id + ")");
		    	break;
		    }
		    
		    annonce.setZz_identifiant(id);
		    annonce.setZz_provider("leboncoin");
		    annonce.setZz_status("OK");
		    annouces.put("leboncoin_"+id, annonce);
		    
		    log.debug("Annonce : " + id + " => " +  annonce.toCsvFormat());
		    
		}//while
		
		// Clear old annouce_old (saved annouce), if:
			// If old annouce has not been found => mean that we can reset old with current checked annouces
			// OR if annouces_old is to big we need to clear it
		if(found_old_annouce || annouces_old.size() > 500){
			// if there are current annouces, if not old_annouces can be empty :
			if(annouces.size() > 0){
				annouces_old=new HashMap<String, AnnonceOccasionAuto>();
				annouces_old.putAll(annouces);
			}
		}else{
			// simply put current annouces to old annouces :
			annouces_old.putAll(annouces);
		}
		return true;
	}

	@Override
	public boolean doRequest() throws JSONException {
		return doRequest(WebHttpRequester.DEFAULT_REQUEST_TEMPO);
	}

	@Override
	public HashMap<String, AnnonceOccasionAuto> getResult() {
		
		return annouces;
		
	}

	
	public String getFirst_id(){
		return first_id;
	}
}
