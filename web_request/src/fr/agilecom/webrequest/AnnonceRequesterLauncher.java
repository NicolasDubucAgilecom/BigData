package fr.agilecom.webrequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import fr.agilecom.webrequest.lacentrale.LaunchRequestLaCentrale;
import fr.agilecom.webrequest.leboncoin.LeboncoinWebHttpRequester;
import fr.agilecom.webrequester.bean.AnnonceOccasionAuto;
import fr.agilecom.webrequester.core.WebHttpRequester;

import org.apache.log4j.Logger;

public class AnnonceRequesterLauncher {
	
	final static String FLOW_MODE = "flow";
	final static String BATCH_MODE = "batch";
	final static String OUT_FILE_CSV = "annonces.csv.out";
	final static int FLOW_MODE_TEMPO_DEFALUT = 240;
	
	static Logger log = Logger.getLogger(AnnonceRequesterLauncher.class.getName());
	
	public static void main(String[] args) throws Exception {
		
		log.info("AnnonceRequesterLauncher started !");
		
		// Variables initializationds :
		boolean run=false;
		int flow_mode_tempo=FLOW_MODE_TEMPO_DEFALUT;
		String out_file="annonces.out.csv";
		File fRun = new File("run");
		
		if(fRun.exists()){
			try{
				fRun.delete();
				
			} catch (Exception e) {
				log.fatal("File 'run' file cannot be deleted : '" + fRun.getAbsolutePath() + "'");
				log.info("Please delete it manually...");
				throw new Exception("File 'run' file cannot be deleted : '" + fRun.getAbsolutePath() + "'");
			} 
		}

		// properties file :
		if(args.length<1){
			throw new Exception("Bad call !!! need one arguments !!");
		}
		
		Properties props = new Properties();
		File confFile = new File(args[0]);
		if(!confFile.exists() && !confFile.isFile()){
			log.fatal("Bad input file  : " + args[0]);
			throw new Exception("Bad input file  : " + args[0]);
		}
		props.load(new FileInputStream(confFile));
				
		// manage arguments :
		String mode = props.getProperty("mode");
		if(mode!=null && mode.equals(BATCH_MODE) || mode.equals(FLOW_MODE)){
			run=true;
			if(mode.equals(FLOW_MODE)){
				try{
					flow_mode_tempo = Integer.parseInt((String)props.get("flow_mode_tempo"));
				}catch(Exception e){
					log.warn("Flow mode set but 'flow_mode_tempo' parameter not set. Default value is used : '" + FLOW_MODE_TEMPO_DEFALUT + "'");
				}
			}
		}else{
			log.fatal("Bad mode configuration. Mode need 'batch' or ' 'flow' value.");
			throw new Exception("Bad mode configuration. Mode need 'batch' or ' 'flow' value.");
		}
		
		//create thread persistence flag
		if(mode.equals(FLOW_MODE)){
			try{
				if(!fRun.createNewFile()){
					throw new Exception("Flow_mode : 'run' persistence flag cannot be created");
				}
			}catch(Exception e){
				log.fatal("File 'run' file cannot be created : '" + fRun.getAbsolutePath() + ". Please verify folder rights .");
				throw new Exception("File 'run' file cannot be created : '" + fRun.getAbsolutePath() + ". Please verify folder rights .");
			}
		}
		// manages sites argument : 
		String sites = props.getProperty("sites");
		if(! sites.matches("((leboncoin|lacentrale)(,|;)?)*")){
			log.fatal("Bad input for properties file. Bad format for field 'sites'");
			throw new Exception("Bad input for properties file. Bad format for field 'sites'");
		}
		
		int http_request_tempo = Integer.parseInt(props.getProperty("http_request_tempo"));
		
		String[] sites_list = sites.split(",");
		String output_type = props.getProperty("output_type");//TODO : manage all types
		
		
		// Do requests to site. Request all site define bu id in properties file .. 
		WebHttpRequester leboncoin_requeter = null;
		WebHttpRequester lacentrale_requeter = null;
		
		HashMap<String, AnnonceOccasionAuto> annonces = new HashMap<String, AnnonceOccasionAuto>();
		
		try{
			out_file = (String)props.get("out_file");
			if(out_file==null){
				throw new Exception("'out_file' properties is null.");
			}
			
			if(!out_file.equals(OUT_FILE_CSV)){
				log.fatal("Incorrrect 'out_file' properties. Unkwown '" + out_file + "' value.");
				throw new Exception("Incorrrect 'out_file' properties. Unkwown '" + out_file + "' value.");
			}
		}catch(Exception e){
			log.warn("Properties 'out_file' has not been set : use default value : " + OUT_FILE_CSV);
			out_file=OUT_FILE_CSV;
		}
		File fOutFile = new File(out_file);
		boolean printHeader = !fOutFile.exists();
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
						fOutFile, 
						true /* append = true */)
				); 
		
		if(printHeader){
			pw.println(AnnonceOccasionAuto.printCsvHeader());
		}
		
		while(run){
		
			for(String site_id : sites_list){
				if(site_id.equals("leboncoin") && run){
					
					if(leboncoin_requeter==null){
						leboncoin_requeter = new LeboncoinWebHttpRequester();
					}
					try{
						log.info("Requesting leboncoin ...");
						leboncoin_requeter.doRequest(http_request_tempo);
						HashMap<String, AnnonceOccasionAuto> tmp_annonces = leboncoin_requeter.getResult();
						
						log.info("Site leboncoin requested. " + tmp_annonces.size() + " rows collected form leboncoin."); 
						annonces.putAll(tmp_annonces);
					}catch(Exception e){
						e.printStackTrace();
					}
				
				}else if(site_id.equals("lacentrale") && run){
					
					if(lacentrale_requeter==null){
						lacentrale_requeter = new LaunchRequestLaCentrale();
					}
					
					try{
						log.info("Requesting lacentrale ...");
						lacentrale_requeter.doRequest(http_request_tempo);
						HashMap<String, AnnonceOccasionAuto> tmp_annonces = lacentrale_requeter.getResult();
						
						log.info("Site lacentrale requested. " + tmp_annonces.size() + " rows collected form lacentrale.");
						annonces.putAll(tmp_annonces);
					}catch(Exception e){
						e.printStackTrace();
					}
				
				}else{
					System.out.println("Unkwon site identifier : " + site_id);
				}
			}// for sites list
			
			// Manage output 
				//TODO : implements other output ??
			
			
			
			log.info("Writing to output file : "+ out_file);
			Iterator<String> ita = annonces.keySet().iterator();
			AnnonceOccasionAuto annonce;
			
			while(ita.hasNext()){
				annonce = annonces.get(ita.next());
				pw.println(annonce.toCsvFormat());
				pw.flush();
			}
			
			//After write, reset it :
			annonces = new HashMap<String, AnnonceOccasionAuto>();
			
			// batch mode : only one time
			if(mode.equals("batch")){
				break;
			// flow mode
			}else{
				run = isRunning();
			}
			
			if(run){
				for(int i=0; i< flow_mode_tempo ; i++){
					run = isRunning();
					if(!run){
						break;
					}
					Thread.sleep(1000);
				}
			}
		}	// while run
		
		log.info("Output file Writed. Closing it...");
		pw.close();
		log.info("Output file closed");
	}
	
	static boolean isRunning(){
		
		boolean running = false;
		running=(new File("run")).exists();
		
		return running;
	}
}
