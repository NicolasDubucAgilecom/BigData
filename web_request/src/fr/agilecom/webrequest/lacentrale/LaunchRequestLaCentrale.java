package fr.agilecom.webrequest.lacentrale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONException;

import fr.agilecom.webrequest.AnnonceRequesterLauncher;
import fr.agilecom.webrequester.bean.AnnonceOccasionAuto;
import fr.agilecom.webrequester.core.HttpUrlConnectionReader;
import fr.agilecom.webrequester.core.WebHttpRequester;

public class LaunchRequestLaCentrale implements WebHttpRequester {

	HashMap<String, AnnonceOccasionAuto> annouces = new HashMap<String, AnnonceOccasionAuto>();
	HashMap<String, AnnonceOccasionAuto> annouces_old = new HashMap<String, AnnonceOccasionAuto>();
	static Logger log = Logger.getLogger(AnnonceRequesterLauncher.class.getName());
	
	@Override
	public boolean doRequest(int tempo) throws JSONException {
		
		String htmlpage=null;
		annouces = new HashMap<String, AnnonceOccasionAuto>();
		String[] pages = new String[3];
		//String[] pages = new String[6];
		pages[0]="http://www.lacentrale.fr/listing_auto.php?SS_CATEGORIE=40%2C41%2C42%2C43%2C44%2C45%2C46%2C47%2C48%2C49&marque=&modele=&prix_maxi=&energie=&cp=&num=1";
		pages[1]="http://www.lacentrale.fr/listing_auto.php?SS_CATEGORIE=40%2C41%2C42%2C43%2C44%2C45%2C46%2C47%2C48%2C49&marque=&modele=&prix_maxi=&energie=&cp=&num=2";
		pages[2]="http://www.lacentrale.fr/listing_auto.php?SS_CATEGORIE=40%2C41%2C42%2C43%2C44%2C45%2C46%2C47%2C48%2C49&marque=&modele=&prix_maxi=&energie=&cp=&num=3";
//		pages[3]="http://www.lacentrale.fr/listing_auto.php?SS_CATEGORIE=40%2C41%2C42%2C43%2C44%2C45%2C46%2C47%2C48%2C49&marque=&modele=&prix_maxi=&energie=&cp=&num=4";
//		pages[4]="http://www.lacentrale.fr/listing_auto.php?SS_CATEGORIE=40%2C41%2C42%2C43%2C44%2C45%2C46%2C47%2C48%2C49&marque=&modele=&prix_maxi=&energie=&cp=&num=5";
//		pages[5]="http://www.lacentrale.fr/listing_auto.php?SS_CATEGORIE=40%2C41%2C42%2C43%2C44%2C45%2C46%2C47%2C48%2C49&marque=&modele=&prix_maxi=&energie=&cp=&num=6";
		
		// Recuperation de la liste des url des annonces lacentrale
		HttpUrlConnectionReader simpleHttp = new HttpUrlConnectionReader();;
		ArrayList<String> urls_annouce=new ArrayList<String>();
		for(String page : pages){

			htmlpage=simpleHttp.read(page,false);
			
			if(htmlpage.contains("Error 42")){
				log.error("Error 42 detected !!!!!!!!!!!!! We have been ban from lacentrale");
				return false;
			}
			String[] heap_annouce = htmlpage.split("<a href=.\\/auto-occasion-annonce-");
			
			for(int i=1; i < heap_annouce.length ; i++){
				urls_annouce.add("http://www.lacentrale.fr/auto-occasion-annonce-"
							+heap_annouce[i].substring(0, heap_annouce[i].indexOf(".html")+".html".length())
									);
				
			}
		}
		
		boolean found_old_annouce=false;
		
		// Parse all annouces :
		Iterator<String> it_urls = urls_annouce.iterator();
		String htmlAnnouce = null;
		Pattern pattern = null;
		Matcher matcher = null;
		
		while(it_urls.hasNext()){
			
			// Anti DOS FW security :
			try {
				Thread.sleep(tempo*5);
			} catch (InterruptedException e1) {	}
			
			AnnonceOccasionAuto annonce = new AnnonceOccasionAuto();
			String announce_url = it_urls.next();
			String general_info=null;
			String credit_info=null;
			String vendor_info=null;
			
			htmlAnnouce = simpleHttp.read(announce_url, false);
			
			// recuperation Marque / modele : via l'appel a credit
			try{
				credit_info=htmlAnnouce.substring(
						htmlAnnouce.indexOf("href=\"credit.php?"),
						htmlAnnouce.indexOf("<p class=\"dptDetail\">")
						).trim();
				
			}catch(Exception e){
				annonce.setZz_status("Format XML (general_info) non attendu. URL : " + announce_url);
				continue;
			}			
			String marque = credit_info.substring(
					credit_info.indexOf("MARQUE=") + "MARQUE=".length(),
					credit_info.indexOf("&amp;MODELE")
					);
			
			String modele = credit_info.substring(
					credit_info.indexOf("MODELE=") + "MODELE=".length(),
					credit_info.indexOf("&amp;VERSION")
					);
			
			String prix = credit_info.substring(
					credit_info.indexOf("PRICE=") + "PRICE=".length(),
					credit_info.indexOf("&amp;CARBURANT")
					);
			
			// recuperation Vendeur / Dept :
			try{				

				vendor_info=htmlAnnouce.substring(
						htmlAnnouce.indexOf("div class=\"box boxContact mT20\" xtcz=\"contacter_le_vendeur\""),
						htmlAnnouce.indexOf("div class=\"box boxContact mT20\" xtcz=\"contacter_le_vendeur\"")+500
								
						);
			}catch(Exception e){
				annonce.setZz_status("Format XML (general_info) non attendu. URL : " + announce_url);
				continue;
			}				
			pattern = Pattern.compile(".*<span class=\"f12\">(.*)</span>.*"
					+ "<span class=\"mB10\">Dpt.(.*)</span>.*");
			matcher = pattern.matcher(vendor_info);
			
			String vendor = "not_found"; 
			String departement = "not_found";
					
			if(matcher.matches()){
				vendor = matcher.group(1).trim();
				departement = matcher.group(2).trim();
			}
			// Recuperation iinformations generales :
			try{
				general_info=htmlAnnouce.substring(
						htmlAnnouce.indexOf("<div class=\"box infosGen\" xtcz=\"informations_generales\""),
						htmlAnnouce.indexOf("<div class=\"box boxOptions\" xtcz=\"options_equipements\"")
						).trim();
				
			}catch(Exception e){
				annonce.setZz_status("Format XML (general_info) non attendu. URL : " + announce_url);
				continue;
			}
						
			pattern = Pattern.compile(".*hiddenOverflow.*"
					+ "<h4>Ann.*</h4>.*<p>(.*)</p>.*"								//1
					+ "<h4>Kilom.*</h4>.*<p>(.*)</p>.*"								//2
					+ "<h4>Nombre de portes.*</h4>.*<p>(.*)</p>.*"					//3
					+ "<h4>Puissance fiscale.*</h4>.*<p>(.*)</p>.*"					//4
					+ "<h4>Puissance din.*</h4>.*<p>(.*)</p>.*"						//5
					+ "<h4>.*te de vitesse.*</h4>.*<p>(.*)</p>.*"					//6
					+ "<h4>.*Eacute;nergie.*</h4>.*<p>(.*)</p>.*"					//7
					+ "<h4>.*Mise en circulation.*</h4>.*<p>(.*)</p>.*"				//8
					+ "<h4>.*Couleur int.*</h4>.*<p>(.*)</p>.*"						//9
					+ "<h4>.*Couleur ext.*</h4>.*<p>(.*)</p>.*"						//10
					+ "<h4>.*Premi&egrave;re main.*</h4>.*<p>(.*)</p>.*"			//11
					+ ".*</div>.*</div>.*");
			matcher = pattern.matcher(general_info);
			
			if(matcher.matches()){
				
				annonce.setAnneeMiseEnCirculation(matcher.group(1).trim());
				annonce.setKilometrage( matcher.group(2).trim());
				annonce.setCaburant(matcher.group(7).trim());
				annonce.setCodePostal("");
				annonce.setCouleur_exterieure(matcher.group(10).trim());
				annonce.setCouleur_interieure(matcher.group(9).trim());
				annonce.setDepartement(departement);
				annonce.setDescription("TODO");
				annonce.setKilometrage(matcher.group(2).trim());
				annonce.setMarque(marque);
				annonce.setModele(modele);
				annonce.setNombre_porte(matcher.group(3).trim());
				annonce.setPremiere_main(matcher.group(11).trim());
				annonce.setPrix(prix);
				annonce.setPuissance_dynamique(matcher.group(5).trim());
				annonce.setPuissance_fiscale(matcher.group(4).trim());
				annonce.setTypeboite(matcher.group(6).trim());
				annonce.setVendeur(vendor);
				annonce.setVille("");
								
			}else{
				annonce.setZz_status("Format XML (general_info) non attendu. URL : " + announce_url);
				continue;
			}
			
		    String id = announce_url.substring(announce_url.lastIndexOf("auto-occasion-annonce-")+"auto-occasion-annonce-".length(), announce_url.indexOf(".htm"));
		    
		    if(annouces_old.containsKey("lacentrale_"+id)){
		    	found_old_annouce=true;
		    	log.info("Stopping criteraia matches ! Annouce id has already found in previous main loop ! (" + id + ")");
		    	break;
		    }
		    
		    annonce.setZz_identifiant(id);
		    annonce.setZz_provider("lacentrale");
		    annonce.setZz_status("OK");
		    annouces.put("lacentrale_"+id, annonce);
		    
		    log.debug("Annonce : " + id + " => " +  annonce.toCsvFormat());
		}// while 
		
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
		// TODO Auto-generated method stub
		return annouces;
	}
}
