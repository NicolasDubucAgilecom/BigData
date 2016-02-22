package fr.agilecom.webrequest.leboncoin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import fr.agilecom.webrequest.HttpUrlConnectionReader;
import fr.agilecom.webrequest.WebHttpRequester;
import fr.agilecom.webrequester.bean.AnnonceOccasionAuto;

public class LeboncoinWebHttpRequester implements WebHttpRequester {
	
	HashMap<String, AnnonceOccasionAuto> annouces = new HashMap<String, AnnonceOccasionAuto>();

	@Override
	public void doRequest(int tempo) throws JSONException {
		
		// Recuperation de la liste des url des annonces leboncoin
		LeboncoinUrlConnectionReader jhcr = new LeboncoinUrlConnectionReader(
					"http://www.leboncoin.fr/voitures/offres/ile_de_france/?f=a&th=0",
					".*<a href=\"..(.*).ca.*title=.*>",
					"content-border",
					1
				);
		
		// Lectures des fiches d'annonces
		ArrayList<String> result = jhcr.read();
		Iterator<String> it = result.iterator();
		
		HttpUrlConnectionReader simpleHttp = new HttpUrlConnectionReader();
		String htmlpage = null;
		
		// Creation du fichier de Sortie
//		PrintWriter writer = new PrintWriter(output_file);
//		writer.print(AnnonceOccasionAuto.printCsvHeader());
		
		// Pour chaque annonce
		while(it.hasNext()){
			
			String announce_url = it.next();
			AnnonceOccasionAuto annonce = new AnnonceOccasionAuto();
						
			// Anti DOS security :
			try {
				Thread.sleep(tempo);
			} catch (InterruptedException e) {}
			
			htmlpage=simpleHttp.read("http://"+announce_url, false);
			
			// Récupération des blocks contenant les informations :
		    Pattern pattern = Pattern.compile(".*var utag_data = (\\{.*\\})</script>.*<div class=\"lbcParams criterias\">.*(<table>.*"
		    		+ "</table>).*");
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
		    	
		    	try{
		    		prix = json.getString("prix");
		    	}catch(JSONException je){
		    	}
		    	try{
		    		departement = json.getString("departement");
		    	}catch(JSONException je){
		    	}
		    	try{
		    		cp = json.getString("cp");
		    	}catch(JSONException je){
		    	}
		    	try{
		    		ville = json.getString("city");
		    	}catch(JSONException je){
		    	}
		    	
		    	String table = matcher.group(2);
		    	pattern = Pattern.compile("<table>.*<td itemprop=.brand.>(.*)"
		    			+ "</td>.*<th>Modèle.*<td itemprop=.model.>(.*)</td>.*"
		    			+ "<td itemprop=.releaseDate.>(.*)</td>.*<th>Kilom.*"
		    			+ "<td>(.*)</td>.*"
		    			+ "<th>Carburant.*</th>.*<td>(.*)</td>.*"
		    			+ "<th>Bo.te de vitesse.*<td>(.*)</td>.*"
		    			+ "</table>");
		    	
		    	matcher = pattern.matcher(table);
		    	matches = matcher.matches();
		    	if(!matches){
			    	annonce.setZz_status("Format XML (itemprop) non attendu. URL : " + announce_url);
		    		continue;
		    	}
		    	String brand = matcher.group(1).trim();
		    	String modele = matcher.group(2).trim();
		    	String releaseDate = matcher.group(3).trim();
		    	String kilometres = matcher.group(4).trim();
		    	String caburant = matcher.group(5).trim();
		    	String typeboite = matcher.group(6).trim();
		    	
		    	annonce = new AnnonceOccasionAuto();
		    	
				annonce.setAnneeMiseEnCirculation(releaseDate);
				annonce.setKilometrage(kilometres);
				annonce.setCaburant(caburant);
				annonce.setCodePostal(cp);
				annonce.setCouleur_exterieure("");
				annonce.setCouleur_interieure("");
				annonce.setDepartement(departement);
				annonce.setDescription("");
				annonce.setKilometrage(kilometres);
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
		    }
		    String id = announce_url.substring(announce_url.lastIndexOf('/')+"/".length(), announce_url.indexOf(".htm"));
		    annonce.setZz_identifiant(id);
		    annonce.setZz_provider("leboncoin");
		    annonce.setZz_status("OK");
		    annouces.put("leboncoin_"+id, annonce);
		}//while
		
		
		
//		writer.flush();
//		writer.close();
	}

	@Override
	public void doRequest() throws JSONException {
		doRequest(WebHttpRequester.DEFAULT_REQUEST_TEMPO);
	}

	@Override
	public HashMap<String, AnnonceOccasionAuto> getResult() {
		
		return annouces;
		
	}
}
