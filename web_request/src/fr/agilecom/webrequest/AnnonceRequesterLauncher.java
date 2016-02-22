package fr.agilecom.webrequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import fr.agilecom.webrequest.lacentrale.LaunchRequestLaCentrale;
import fr.agilecom.webrequest.leboncoin.LeboncoinWebHttpRequester;
import fr.agilecom.webrequester.bean.AnnonceOccasionAuto;

public class AnnonceRequesterLauncher {

	public static void main(String[] args) throws Exception {
		
		// properties file :
		if(args.length<1){
			throw new Exception("Bad call !!! need one arguments !!");
		}
		
		Properties props = new Properties();
		File confFile = new File(args[0]);
		if(!confFile.exists() && !confFile.isFile()){
			throw new Exception("Bad input file  : " + args[0]);
		}
		props.load(new FileInputStream(confFile));
		
		// manage arguments :
		String sites = props.getProperty("sites");
		if(! sites.matches("((leboncoin|lacentrale)(,|;)?)*")){
			System.out.println("Bad input for properties file. Bad format for field 'sites'");
			throw new Exception("Bad input for properties file. Bad format for field 'sites'");
		}
		
		int http_request_tempo = Integer.parseInt(props.getProperty("http_request_tempo"));
		
		
		String[] sites_list = sites.split(",");
		String output_type = props.getProperty("output_type");
		
		// Do requests to site. Request all site define bu id in properties file .. 
		WebHttpRequester requeter = null;
		HashMap<String, AnnonceOccasionAuto> annonces = new HashMap<String, AnnonceOccasionAuto>();
		
		for(String site_id : sites_list){
			if(site_id.equals("leboncoin")){
				
				requeter = new LeboncoinWebHttpRequester();
				
				try{
					requeter.doRequest(http_request_tempo);
					HashMap<String, AnnonceOccasionAuto> tmp_annonces = requeter.getResult();
					
					annonces.putAll(tmp_annonces);
				}catch(Exception e){
					e.printStackTrace();
				}
			
			}else if(site_id.equals("lacentrale")){
				
				requeter = new LaunchRequestLaCentrale();
				
				try{
					requeter.doRequest(http_request_tempo);
					HashMap<String, AnnonceOccasionAuto> tmp_annonces = requeter.getResult();
					
					annonces.putAll(tmp_annonces);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				System.out.println("Unkwon site identifier : " + site_id);
			}
		}
		
		// Manage output 
		Iterator<String> ita = annonces.keySet().iterator();
		AnnonceOccasionAuto annonce;
		
		PrintWriter pw = new PrintWriter("annonces.out.csv");
		pw.println(AnnonceOccasionAuto.printCsvHeader());
		
		while(ita.hasNext()){
			annonce = annonces.get(ita.next());
			pw.println(annonce.toCsvFormat());
			pw.flush();
		}
		
		pw.close();
	}
}
