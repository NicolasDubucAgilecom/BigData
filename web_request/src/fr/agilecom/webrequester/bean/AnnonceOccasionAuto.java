package fr.agilecom.webrequester.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

/**
 * 
 * @author buboc
 *
 */
public class AnnonceOccasionAuto {
	// TODO add insert date and announce date...
	String anneeMiseEnCirculation = null;
	String caburant = null; // type motorisation
	String codePostal = null;
	String couleur_interieure = null;
	String couleur_exterieure = null;
	String departement = null;
	String description = "";
	String insert_date = null;
	String kilometrage = null;
	String marque = null;
	String modele = null;
	String nombre_porte = null;
	String premiere_main = null;
	String prix = null;
	String puissance_fiscale = null;
	String puissance_dynamique = null;
	String vendeur = null;
	String typeboite = null;
	String ville = null;
	String zz_identifiant = null;
	String zz_provider = null;
	String zz_status = null;
	
	public String getAnneeMiseEnCirculation() {
		return anneeMiseEnCirculation;
	}
	public void setAnneeMiseEnCirculation(String anneeMiseEnCirculation) {
		this.anneeMiseEnCirculation = anneeMiseEnCirculation;
	}
	public String getCaburant() {
		return caburant;
	}
	public void setCaburant(String caburant) {
		this.caburant = caburant;
	}
	public String getCodePostal() {
		return codePostal;
	}
	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}
	public String getCouleur_interieure() {
		return couleur_interieure;
	}
	public void setCouleur_interieure(String couleur_interieure) {
		this.couleur_interieure = couleur_interieure;
	}
	public String getCouleur_exterieure() {
		return couleur_exterieure;
	}
	public void setCouleur_exterieure(String couleur_exterieure) {
		this.couleur_exterieure = couleur_exterieure;
	}
	public String getDepartement() {
		return departement;
	}
	public void setDepartement(String departement) {
		this.departement = departement;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getInsert_date() {
		Date date = new Date();
		String date_str = date.toString();
		return (date_str);
	}
	public String getKilometrage() {
		return kilometrage;
	}
	public void setKilometrage(String kilometrage) {
		this.kilometrage = kilometrage;
	}
	public String getMarque() {
		return marque;
	}
	public void setMarque(String marque) {
		this.marque = marque;
	}
	public String getModele() {
		return modele;
	}
	public void setModele(String modele) {
		this.modele = modele;
	}
	public String getNombre_porte() {
		return nombre_porte;
	}
	public void setNombre_porte(String nombre_porte) {
		this.nombre_porte = nombre_porte;
	}
	public String getPremiere_main() {
		return premiere_main;
	}
	public void setPremiere_main(String premiere_main) {
		this.premiere_main = premiere_main;
	}
	public String getPrix() {
		return prix;
	}
	public void setPrix(String prix) {
		this.prix = prix;
	}
	public String getPuissance_fiscale() {
		return puissance_fiscale;
	}
	public void setPuissance_fiscale(String puissance_fiscale) {
		this.puissance_fiscale = puissance_fiscale;
	}
	public String getPuissance_dynamique() {
		return puissance_dynamique;
	}
	public void setPuissance_dynamique(String puissance_dynamique) {
		this.puissance_dynamique = puissance_dynamique;
	}
	public String getVendeur() {
		return vendeur;
	}
	public void setVendeur(String vendeur) {
		this.vendeur = vendeur;
	}
	public String getTypeboite() {
		return typeboite;
	}
	public void setTypeboite(String typeboite) {
		this.typeboite = typeboite;
	}
	public String getVille() {
		return ville;
	}
	public void setVille(String ville) {
		this.ville = ville;
	}
	
	public String getZz_identifiant() {
		return zz_identifiant;
	}
	public void setZz_identifiant(String zz_identifiant) {
		this.zz_identifiant = zz_identifiant;
	}
	public String getZz_provider() {
		return zz_provider;
	}
	public void setZz_provider(String zz_provider) {
		this.zz_provider = zz_provider;
	}
	public String getZz_status() {
		return zz_status;
	}
	public void setZz_status(String zz_status) {
		this.zz_status = zz_status;
	}
	public static String printCsvHeader(){

		String return_str = null;
		
		Class<?> clz;
		try {
			AnnonceOccasionAuto aoa = new AnnonceOccasionAuto();
			clz = Class.forName(aoa.getClass().getName());

			for(Field field : clz.getDeclaredFields()){
				
				if(return_str==null){
					return_str=field.getName();
				}else{
					return_str=return_str.concat(";"+field.getName());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return return_str;
	}
	
	public String toCsvFormat() {
		
		String return_str = null;
		
		Class<?> clz;
		try {
			clz = Class.forName(this.getClass().getName());

			for(Field field : clz.getDeclaredFields()){
				
				String method = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
				Method mth = clz.getMethod(method);
				
				String result = (String)mth.invoke(this);
				if(return_str==null){
					return_str=result;
				}else{
					return_str=return_str.concat(";"+result);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return_str = StringEscapeUtils.unescapeHtml4(return_str);
		
		return return_str;
	}
	
	public JSONObject toJSONObject(){
		
		JSONObject json = new JSONObject();
		Class<?> clz;
		
		String key = null;
		String value = null;
		
		try {
			clz = Class.forName(this.getClass().getName());

			for(Field field : clz.getDeclaredFields()){
				
				key = field.getName();
				String method = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
				Method mth = clz.getMethod(method);
				
				value = (String)mth.invoke(this);

				json.put(key, value);
			}
		} catch (Exception e) {
			return null;
		}
		
		return json;
	}
}
