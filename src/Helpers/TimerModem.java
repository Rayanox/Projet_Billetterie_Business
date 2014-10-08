package Helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TimerModem extends TimerTask{

	private static String pathConfigTimerFile = IO_File_Logger.pathConfigWifi + IO_File_Logger.slash + "ConfigHeure.txt";
	private int heureDebut= 2, heureFin= 12, minuteDebut = 00, minuteFin = 00;
	private static boolean premiereIteration = true;
	
	@Override
	public void run() {
		File fichier = new File(pathConfigTimerFile);
		initFichier(fichier);
		
		try {
			plannifier(fichier);	
			int heureNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int minuteNow = Calendar.getInstance().get(Calendar.MINUTE);
			//Permet le premier lancement de la tache immediatement par le timer sans lancer l'operation de scrapping si l'heure n'est pas celle definie.
			if((heureDebut == heureNow &&  minuteDebut == minuteNow) || (heureFin == heureNow && minuteFin == minuteNow))
				lancerScrap();
			
		}catch(Exception e) {
			MailService.SendErrorMail("Erreur dans le programme Wifi, abandon...\n\nErreur:\n"+e.getMessage());
		}
			
	}
	
	

	private void lancerScrap() {
		JOptionPane dialog = new JOptionPane();
		Calendar now = Calendar.getInstance();
		dialog.showMessageDialog(null, "Lancement d'une iteration à "+now.get(Calendar.HOUR_OF_DAY)+"h"+now.get(Calendar.MINUTE)+"min");
	}

	private void plannifier(File fichier) {
		try {
			Calendar dateWifiBlocked = GregorianCalendar.getInstance();
			
			BufferedReader reader = new BufferedReader(new FileReader(fichier));
			String [] texteDebut = reader.readLine().split("=")[1].split("h");
			String [] texteFin = reader.readLine().split("=")[1].split("h");
			
			heureDebut = Integer.parseInt(texteDebut[0]);
			minuteDebut = Integer.parseInt(texteDebut[1]);
			heureFin = Integer.parseInt(texteFin[0]);
			minuteFin = Integer.parseInt(texteFin[1]);
			
			//On definit les prochaines heures et minutes
			int heureProchaineIteration, minuteProchaineIteration;
			if(premiereIteration) {
				premiereIteration = false;
				heureProchaineIteration = heureDebut;
				minuteProchaineIteration = 	minuteDebut;
			}else {
				if(heureDebut == dateWifiBlocked.get(Calendar.HOUR_OF_DAY)) {
					heureProchaineIteration = heureFin;
					minuteProchaineIteration = 	minuteFin;											
				}else {
					heureProchaineIteration = heureDebut;
					minuteProchaineIteration = 	minuteDebut;
				}
			}
			
			
						
			//Reglage du jour
			if(dateWifiBlocked.get(Calendar.HOUR_OF_DAY) > heureProchaineIteration || (dateWifiBlocked.get(Calendar.HOUR_OF_DAY) == heureProchaineIteration && dateWifiBlocked.get(Calendar.MINUTE) > minuteProchaineIteration))
				dateWifiBlocked.add(Calendar.DAY_OF_MONTH, 1);
			
			//On assigne les prochaines heures et minutes à l'objet de Calendar.
			dateWifiBlocked.set(Calendar.HOUR_OF_DAY, heureProchaineIteration);
			dateWifiBlocked.set(Calendar.MINUTE, minuteProchaineIteration);
			dateWifiBlocked.set(Calendar.SECOND, 0);
			
			Timer timer = new Timer();
			timer.schedule(new TimerModem(), dateWifiBlocked.getTime());
			System.out.println("Plannification wifi OK -> Prochaine iteration :\n - "+heureProchaineIteration+"h"+minuteProchaineIteration+"min");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MailService.SendErrorMail("Erreur lors de la lecture du fichier de configWifi\n\nErreur:\n"+e.getMessage());
		}
	}

	private void initFichier(File fichier) {
		if(!fichier.exists() || !fichier.isFile()) {
			try {
				initDossier();
				
				fichier.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(fichier));
				writer.write("Debut="+heureDebut+"h"+minuteDebut+"\r\n");
				writer.write("Fin="+heureFin+"h"+minuteFin+"\r\n");
				writer.flush();
				System.out.println("Fichier de config de wifi inexistant => Creation du fichier avec des horaires par defaut.\nSi vous desirez changer les horaires et les appliquer des la premiere iteration, modifier le fichier texte et relancer le programme.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				MailService.SendErrorMail("Erreur lors de la creation du fichier de config wifi");
			}
		}
	}



	private void initDossier() {
		File dossier = new File(IO_File_Logger.pathConfigWifi);
		if(!dossier.exists() || !dossier.isDirectory()) {
			dossier.mkdirs();
		}
	}

}
