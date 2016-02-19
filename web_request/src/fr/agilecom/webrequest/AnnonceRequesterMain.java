package fr.agilecom.webrequest;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class AnnonceRequesterMain {

	public static void main(String[] args) throws Exception {
		
		// properties file :
		String conf_file = args[0];
		Properties props = new Properties();
		props.load(new FileInputStream(new File(conf_file)));
		
		String sites_list = props.getProperty("sites");
		int http_request_tempo = Integer.parseInt(props.getProperty("sites"));
		
	}
}
