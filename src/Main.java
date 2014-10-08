import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jsoup.nodes.Document;

import Helpers.IO_File_Logger;
import Helpers.MailService;
import Helpers.TimerModem;


public class Main {

	
	// Ajouter logs de nouveaux prix et de status complets
	
	
	// + Migrer le programme en générique => Match ne sera plus Match mais Evenement<T> et on aura alors des attributs communs (Prix, date, etc) et 
	// des attributs en plus (AdditionnalField qui seront à ajouter en plus spécifiques à chaque programme) ====> Pas sur, à réflechir et experimenter.
	
	
	public static void main(String[] args) {
		System.out.println("Debut du programme ecrit par Rayanox");
		
		
		//Lancement();
		tests();
		
		
		System.out.println("Fin Debut du programme ecrit par Rayanox");
	}

	private static void Lancement() {
		//Initialisation du dossier de config
		InitialisationDossiers();
		
		//Lancement du programme de billeterie PSG
		Scenarios.AnalyseBilletteriePSG();
		
		
		
		
		//Programme d'analyse viaGoGo (à faire)		
		//Programme d'anaylse FNAC (à faire)	
		
	}

	
	
	private static void ShowPropertyList() {
		for (Object key : System.getProperties().keySet()) {
			System.out.println(key + " => " + System.getProperty((String)key));
		}
	}
	
	
	private static void tests() {
		
		TimerModem modemWork = new TimerModem();
		Timer timer = new Timer();
		timer.schedule(modemWork, Calendar.getInstance().getTime());
	}
	
	
	
	
	private static void InitialisationDossiers() {
		File dossierConfig = new File(IO_File_Logger.pathDossierConfig);
		if(!dossierConfig.exists() && !dossierConfig.isDirectory() && !dossierConfig.isHidden()) {
			try {
				dossierConfig.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
				MailService.SendErrorMail("Dossier de config non cree : \n"+e.getMessage());
			}
		}
		File dossierTMP = new File(IO_File_Logger.pathDossierTMP);
		if(!dossierTMP.exists() && !dossierTMP.isDirectory()) {
			dossierTMP.mkdirs();
		}
		
	}
	
	

	
}
