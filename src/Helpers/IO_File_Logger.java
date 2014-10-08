package Helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class IO_File_Logger {

	public static String slash = (osWindows() ? "\\" : "/");
	public static String pathDossierConfig = System.getProperty("user.home") + slash+".config_Business_Billetterie";
	public static String pathDossierTMP = pathDossierConfig + slash +"TMP";
	public static String pathConfigWifi = pathDossierConfig + slash + "ConfigWifi";
	
	
	private static final String pathLogNewMatchFile = pathDossierConfig + slash +"Log_nouveaux_Matchs.txt";
	private static final String pathLogNewPrixFile = pathDossierConfig + slash +"Log_nouveaux_Prix.txt";
	private static final String pathLogNewStatusFile = pathDossierConfig + slash +"Log_nouveaux_Status.txt";
	
	private static final String pathlastsMatchsTMPFile = pathDossierTMP + slash +".lastsMatchsTMP.rayanox";
	private static final String pathlastConnectionFile = pathDossierTMP + slash +".lastConnection.rayanox";
	
	//retourne faux si l'ecriture s'est mal deroulee
	public static boolean LoggerNouveauxMatchs(ArrayList<Match> nouveauxMatchs) {
		
		if(nouveauxMatchs.isEmpty()) return true;
		File fichierLog = new File(pathLogNewMatchFile);
		if(!fichierLog.exists()) {
			System.out.print("");
		}
			
		
		try {
			BufferedWriter writerNewMatchs = new BufferedWriter(new FileWriter(fichierLog, true));
					
			
			//On log les nouveaux matchs dans le fichier de nouveaux matchs
			String texteNouveauxMatchs = "";			
			writerNewMatchs.write("["+GetDateActuelle()+"]"
					+"\r\n\r\n");
			for (Match match : nouveauxMatchs) {
				texteNouveauxMatchs += "-> Match : "+match.equipe
						+"\r\nDate match : "+ match.getDate()
						+"\r\nEtat billetterie : "+(match.statusComplet ? "Fermee" : "Ouverte")
						+( match.statusComplet ? "" : "\r\nPrix : "+match.prix)
						+"\r\n\r\n";				
			}
			writerNewMatchs.write(texteNouveauxMatchs);
			writerNewMatchs.write("\r\n\r\n\r\n\r\n\r\n\r\n");
			writerNewMatchs.flush();
			
			//On avertit la liste d'utilisateurs par mail.			
			MailService.SendNotificationMail("\nNouveau"+(nouveauxMatchs.size() > 1 ? "x" : "")+" Matchs detectes sur la billeterie du PSG :\n\n" + texteNouveauxMatchs, "(Rayanox) Notif PSG Billetterie : Nouveaux matchs");
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MailService.SendErrorMail(e.getMessage());
		}	
		
		return false;
	}
	

	public static boolean LoggerNouveauxPrix(ArrayList<Match> nouveauxPrix) {
		if(nouveauxPrix.isEmpty()) return true;
		
		File fichierLog = new File(pathLogNewPrixFile);		
		try {
			BufferedWriter writerNewMatchs = new BufferedWriter(new FileWriter(fichierLog, true));
					
			
			//On log les nouveaux matchs dans le fichier de nouveaux matchs
			String texteNouveauxPrix = "";			
			writerNewMatchs.write("["+GetDateActuelle()+"]"
					+"\r\n\r\n");
			for (Match match : nouveauxPrix) {
				texteNouveauxPrix += "-> Match : "+match.equipe
						+"\r\nDate match : "+ match.getDate()
						+"\r\nEtat billetterie : "+(match.statusComplet ? "Fermee" : "Ouverte")
						+( match.statusComplet ? "" : "\r\nAncien prix : "+match.ancienPrix+" ===> Nouveau prix : " + match.prix)
						+"\r\n\r\n";				
			}
			writerNewMatchs.write(texteNouveauxPrix);
			writerNewMatchs.write("\r\n\r\n\r\n\r\n\r\n\r\n");
			writerNewMatchs.flush();
			
			//On avertit la liste d'utilisateurs par mail.			
			MailService.SendNotificationMail("\nNouveau"+(nouveauxPrix.size() > 1 ? "x" : "")+" Variation prix detectes sur la billeterie du PSG :\n\n" + texteNouveauxPrix, "(Rayanox) Notif PSG Billetterie : Nouveaux prix");
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MailService.SendErrorMail(e.getMessage());
		}	
		
		return false;
	}




	public static boolean LoggerNouveauxStatus(ArrayList<Match> nouveauxStatusComplets) {
		if(nouveauxStatusComplets.isEmpty()) return true;
		
		File fichierLog = new File(pathLogNewStatusFile);		
		try {
			BufferedWriter writerNewMatchs = new BufferedWriter(new FileWriter(fichierLog, true));
					
			
			//On log les nouveaux matchs dans le fichier de nouveaux matchs
			String texteNouveauxStatus = "";			
			writerNewMatchs.write("["+GetDateActuelle()+"]"
					+"\r\n\r\n");
			for (Match match : nouveauxStatusComplets) {
				texteNouveauxStatus += "-> Match : "+match.equipe
						+"\r\nDate match : "+ match.getDate()
						+"\r\nEtat billetterie : "+(match.statusComplet ? "Fermee" : "Ouverte")
						+( match.statusComplet ? "" : "\r\nPrix : "+match.prix)
						+"\r\n\r\n";				
			}
			writerNewMatchs.write(texteNouveauxStatus);
			writerNewMatchs.write("\r\n\r\n\r\n\r\n\r\n\r\n");
			writerNewMatchs.flush();
			
			//On avertit la liste d'utilisateurs par mail.			
			MailService.SendNotificationMail("\nNouveau"+(nouveauxStatusComplets.size() > 1 ? "x" : "")+" status detectes sur la billeterie du PSG :\n\n" + texteNouveauxStatus, "(Rayanox) Notif PSG Billetterie : Nouveaux status");
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MailService.SendErrorMail(e.getMessage());
		}	
		
		return false;
	}

	
	public static ArrayList<String> extractMatchsFileTMP() {
		ArrayList<String> listEquipes = new ArrayList<String>();
		
		File fichier = new File(pathlastsMatchsTMPFile);
		if(!fichier.exists()) return new ArrayList<String>();
		
		try {			
			BufferedReader reader = new BufferedReader(new FileReader(fichier));
			String texte = reader.readLine();
			String [] equipes = texte.split("<->");
			for (String string : equipes) {
				listEquipes.add(string);
			}
		} catch (Exception e) {
			return new ArrayList<String>();
		}
		
		return listEquipes;
	}
	


	public static void loggerLastMatchTMP(ArrayList<Match> matchs) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(pathlastsMatchsTMPFile)));
			String texteInsertion = "";
			for (Match m: matchs) {
				texteInsertion += m.equipe.toUpperCase() 
						+ ":" + m.prix
						+ ":" + m.statusComplet
						+ "<->";
			}
		if(texteInsertion.length()>=3) texteInsertion = texteInsertion.substring(0, texteInsertion.length()-3);
		writer.write(texteInsertion);
		writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			MailService.SendErrorMail(e.getMessage());
		}
	}

	public static void loggerLastConnection() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(pathlastConnectionFile)));
			Calendar date = GregorianCalendar.getInstance();
			writer.write("Derniere connexion : \r\n-> "+getJour(date)+" "+ date.get(Calendar.DAY_OF_MONTH)+" "+getMois(date)+" "+date.get(Calendar.YEAR));
			writer.write("\r\n"+date.get(Calendar.HOUR_OF_DAY)+":"+date.get(Calendar.MINUTE)+":"+date.get(Calendar.SECOND));
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MailService.SendErrorMail("Probleme lors du log de la dernierre connexion...");
		}
		
	}





	//------- Helpers ---------
	
	
	
	
	
	
	private static String getMois(Calendar date) {
		int mois = date.get(Calendar.MONTH);
		switch(mois) {
			case 0 :
				return "Janvier";
			case 1 :
				return "Fevrier";
			case 2 :
				return "Mars";
			case 3 :
				return "Avril";
			case 4 :
				return "Mai";
			case 5 :
				return "Juin";
			case 6 :
				return "Juillet";
			case 7 :
				return "Aout";
			case 8 :
				return "Septembre";
			case 9 :
				return "Octobre";
			case 10 :
				return "Novembre";
			case 11 :
				return "Decembre";
		}
		return "Mois inconnu";
	}


	private static String getJour(Calendar date) {
		int jour = date.get(Calendar.DAY_OF_WEEK);
		switch(jour) {
			case 1 :
				return "Dimanche";
			case 2 :
				return "Lundi";
			case 3 :
				return "Mardi";
			case 4 :
				return "Mercredi";
			case 5 :
				return "Jeudi";
			case 6 :
				return "Vendredi";
			case 7 :
				return "Samedi";
		}
		return "Jour inconnu";
	}


	private static String GetDateActuelle() {
		Calendar c = GregorianCalendar.getInstance();
		return c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.MONTH)+"/"+ c.get(Calendar.YEAR)+"  -    "+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND);			
	}


	private static boolean osWindows() {
		return System.getProperty("os.name").toLowerCase().contains("indow");
	}


	

	



	
	
	
}
