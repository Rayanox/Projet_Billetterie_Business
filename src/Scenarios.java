import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import Helpers.MailService;
import Helpers.Match;
import Helpers.IO_File_Logger;


public class Scenarios {

	public static void AnalyseBilletteriePSG() {
		
		Thread t = new Thread() {
			public void run() {
				while(true) {

					URL urlBilletteriePSG = AdressesWebs.AdresseBilletteriePSG();
					String pageBilleterie = Echangeur.Request(urlBilletteriePSG, Protocol.HTTP);
					if(pageBilleterie != null) {
						String pageBilleterieSansHTTP = pageBilleterie.substring(pageBilleterie.indexOf("<!DOCTYPE"));
						Document docBilletterie = HTML_Manipulation.ConvertHtmlStringToHtmlDocument(pageBilleterieSansHTTP);

						
						Element noeudPereMatchs = docBilletterie.getElementById("manif-0"); //Noeud qui contient tous les matchs proposés
						ArrayList<Element> noeudsMatchs = extraireElementsMatchs(noeudPereMatchs);
						ArrayList<Match> listeDesMatchs = extrairesMatchsInfosFromElementsToMatchObjects(noeudsMatchs);
						

						ArrayList<String> listMatchsFileTMP = IO_File_Logger.extractMatchsFileTMP();
						ArrayList<Match> nouveauxMatchs = TrouverNouveauxMatchs(listeDesMatchs, listMatchsFileTMP);
						ArrayList<Match> nouveauxPrix = TrouverNouveauxPrix(listeDesMatchs, listMatchsFileTMP);
						ArrayList<Match> nouveauxStatusComplets = TrouverNouveauxStatus(listeDesMatchs, listMatchsFileTMP);
						
						//On log sur fichier (et automatiquement notifié par mail)
						IO_File_Logger.LoggerNouveauxMatchs(nouveauxMatchs);
						IO_File_Logger.LoggerNouveauxPrix(nouveauxPrix);
						IO_File_Logger.LoggerNouveauxStatus(nouveauxStatusComplets);
						
						//On log tous les matchs dans le fichier TMP
						IO_File_Logger.loggerLastMatchTMP(listeDesMatchs);
						IO_File_Logger.loggerLastConnection();

					}else {
						MailService.SendErrorMail("/!\\ Impossible de contacter le site du psg durant 15 requetes /!\\\nAttente de 1h avant de recommencer les opérations normalement");
						Attente(3600, this);
					}
					
					//Attente
					Attente(30, this);
				}
			}
		};
		
		t.start();
				
	}
	
	private static int compteurWifiDesactivation = 0;
	
	public static void ExtinctionWifiOnNigth() {
		
		initDossierConfigWifi();
		
		Thread t = new Thread(){
			int heureBloquage = 23, heureDebloquage = 12, minuteBloquage = 0;
			Timer timer = new Timer();
			
			public void run() {
				try {		
					
					Calendar dateWifiBlocked = GregorianCalendar.getInstance();
					if(dateWifiBlocked.get(Calendar.HOUR_OF_DAY) >heureBloquage && dateWifiBlocked.get(Calendar.MINUTE) > minuteBloquage)
						dateWifiBlocked.add(Calendar.DAY_OF_MONTH, 1);
					
					final TimerTask task = new TimerTask(){
						
						public void run() {
							int heure;
							if(compteurWifiDesactivation%2 == 0 ) {
								heure = heureBloquage;
							} else {
								heure = heureDebloquage;
							}
							compteurWifiDesactivation ++;
							Calendar dateWifiBlocked = GregorianCalendar.getInstance();
							if(dateWifiBlocked.get(Calendar.HOUR_OF_DAY) >heureBloquage && dateWifiBlocked.get(Calendar.MINUTE) > minuteBloquage)
								dateWifiBlocked.add(Calendar.DAY_OF_MONTH, 1);
							//timer.schedule(task, dateWifiBlocked.getTime());
							
							//Lancement du fichier par process
							
						}
					};
					
					
					TimerTask task_timer = new TimerTask(){
						
						public void run() {
							int heure;
							if(compteurWifiDesactivation%2 == 0 ) {
								heure = heureBloquage;
							} else {
								heure = heureDebloquage;
							}
							compteurWifiDesactivation ++;
							Calendar dateWifiBlocked = GregorianCalendar.getInstance();
							if(dateWifiBlocked.get(Calendar.HOUR_OF_DAY) >heureBloquage && dateWifiBlocked.get(Calendar.MINUTE) > minuteBloquage)
								dateWifiBlocked.add(Calendar.DAY_OF_MONTH, 1);
							timer.schedule(task, dateWifiBlocked.getTime());
							
						}
					};
						
					
					
					
				} catch(Exception e) {
					MailService.SendErrorMail("Une erreur inattendue est survenue sur le programme ExtinctionWifiOnNight :\n\nErreur :\n"+e.getMessage());
				}
			}
		};
		
	}
	
	
	
	
	
	
	
	
	
	
	


















	// ---- Helpers ------
	
	
	private static void initDossierConfigWifi() {
		File f = new File(IO_File_Logger.pathConfigWifi);
		if(!f.exists() || !f.isDirectory()) {
			f.mkdirs();
		}
	}
	
	private static ArrayList<Match> TrouverNouveauxStatus(ArrayList<Match> listeDesMatchs, ArrayList<String> listMatchsFileTMP) {
		ArrayList<Match> nouveauxMatchsPrix = new ArrayList<Match>();			
		for (Match m: listeDesMatchs) {
			Match ancienMatchTrouve = extractMatchEquipe(listMatchsFileTMP, m);
			if(ancienMatchTrouve != null) {
				if(m.prix != ancienMatchTrouve.prix)
				nouveauxMatchsPrix.add(m);
			}
		}
		
		return nouveauxMatchsPrix;
	}



























	private static ArrayList<Match> TrouverNouveauxPrix(ArrayList<Match> listeDesMatchs, ArrayList<String> listMatchsFileTMP) {
		ArrayList<Match> nouveauxMatchsPrix = new ArrayList<Match>();			
		for (Match m: listeDesMatchs) {
			Match ancienMatchTrouve = extractMatchEquipe(listMatchsFileTMP, m);
			if(ancienMatchTrouve != null) {
				if(m.prix != ancienMatchTrouve.prix && m.statusComplet == ancienMatchTrouve.statusComplet){
					m.ancienPrix = ancienMatchTrouve.prix;
					nouveauxMatchsPrix.add(m);
				}
				
			}
		}
				
		return nouveauxMatchsPrix;
	}













	private static void Attente(int i, Thread t) {
		try {
			t.sleep(i*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private static ArrayList<Match> TrouverNouveauxMatchs(ArrayList<Match> listeDesMatchs, ArrayList<String> listMatchsFileTMP) {
		ArrayList<Match> nouveauxMatchs = new ArrayList<Match>();			
		for (Match m: listeDesMatchs) {
			if(!possedeMatchEquipe(listMatchsFileTMP, m)) {
				nouveauxMatchs.add(m);
			}
		}
		
		
		
		return nouveauxMatchs;
	}













	private static boolean possedeMatchEquipe(ArrayList<String> listMatchsFileTMP, Match m) {
		for (String equipe : listMatchsFileTMP) {
			if(equipe.trim().toLowerCase().contains(m.equipe.trim().toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	private static Match extractMatchEquipe(ArrayList<String> listMatchsFileTMP, Match m) {
		Match matchTrouve = null;
		for (String equipe : listMatchsFileTMP) {
			if(equipe.trim().toLowerCase().contains(m.equipe.trim().toLowerCase())) {
				String [] parts = equipe.split(":");
				String nomEquipe = parts[0];
				int prix = Integer.parseInt(parts[1]);
				boolean complet = (parts[2] == "true" ? true : false);
				return new Match(nomEquipe, prix, complet);
			}
		}
		return matchTrouve;
	}



























	private static ArrayList<Match> extrairesMatchsInfosFromElementsToMatchObjects(
			ArrayList<Element> noeudsMatchs) {
		ArrayList<Match> results = new ArrayList<Match>();
		for (Element element : noeudsMatchs) {
			Match m = new Match();
			m.equipe = extraireEquipe(element);
			m.date = extraireDate(element);
			m.statusComplet = extraireComplet(element);
			m.prix = m.statusComplet ? -1 : extrairePrix(element);
			results.add(m);
		}
			
		return results;
	}


	private static int extrairePrix(Element element) {
		Element noeudPrix = null;
		for (Element el : element.children()) {
			if(el.attr("class").equals("manif_bottom")) {
				
				for (Element el1 : el.children()) {
					if(el1.attr("class").equals("manif_infos4")) {
						noeudPrix = el1;
						break;
					}
				}
				
			}
		}
		String prixTexte = noeudPrix.child(0).child(1).ownText();
		int prix = Integer.parseInt(prixTexte.substring(0, prixTexte.length()-1));
		return prix;
	}













	private static boolean extraireComplet(Element element) {
		Element noeudComplet = null;
		for (Element el : element.children()) {
			if(el.attr("class").equals("manif_bottom")) {
				
				for (Element el1 : el.children()) {
					if(el1.attr("class").equals("manif_infos5")) {
						noeudComplet = el1;
						break;
					}
				}
				
			}
		}
		String complet = noeudComplet.child(0).child(0).ownText();
		boolean isComplet = complet.toLowerCase().contains("complet");		
		return isComplet;
	}













	private static GregorianCalendar extraireDate(Element element) {
		
		Element noeudDate = null;
		for (Element el : element.children()) {
			if(el.attr("class").equals("manif_infos3")) {
				noeudDate = el;
				break;
			}
		}
		String texte = noeudDate.child(1).childNode(0).outerHtml();
		String [] dateTab = texte.split("-")[2].trim().split("/");
		int jour = Integer.parseInt(dateTab[0]), mois = Integer.parseInt(dateTab[1]), annee = Integer.parseInt(dateTab[2]);
		GregorianCalendar date = new GregorianCalendar(annee, mois, jour);
		return date;
	}










	













	private static String extraireEquipe(Element element) {
		Element noeudEquipe = null;
		for (Element el : element.children()) {
			if(el.attr("class").equals("manif_infos3")) {
				noeudEquipe = el;
				break;
			}
		}
		String annonce = noeudEquipe.child(0).childNode(0).outerHtml();
		String [] texte = annonce.split("- ");
		String equipe = texte[texte.length-1];
		return equipe;
	}













	private static ArrayList<Element> extraireElementsMatchs(Element pere) {
		ArrayList<Element> liste = new ArrayList<Element>();
		for (Element element : pere.children()) {
			if(element.attr("data-compet").equals("offres/unite")) {
				liste.add(element.child(0));
			}
		}
		return liste;
	}
	
	
	
}


