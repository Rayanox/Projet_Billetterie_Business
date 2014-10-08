package Helpers;

import rayanox.mailAPI.Mail;

public class MailService {
	
	private static String [] Utilisateurs_Destinataires = new String [] {"rayane.benhmidane32@hotmail.fr"};

	public static void SendErrorMail(String message, Exception e) {
		SendMail("", "Error_Scrap_Program...", "Une erreur a etee detectee dans l'execution du programme de Serveur Scrap.\n\n\tMessage d'erreur :\n"+message+( e != null ? "\n\nErreur : \n"+e.getMessage() : ""));
	}
	
	public static void SendErrorMail(String message) {
		SendMail("", "Error_Scrap_Program...", "Une erreur a etee detectee dans l'execution du programme de Serveur Scrap.\n\n\tMessage d'erreur :\n"+message);
	}
	
	public static void SendNotificationMail(String message, String subject) {
		SendMail("", subject, message);
	}
	
	
	
	
	
	
	
	
	private static void SendMail(String From, String Subject, String Message) {
		Mail mail = new Mail("rayanox92@gmail.com", "9230039392");
		mail.SendMail(From, Utilisateurs_Destinataires, Subject, Message);
	}
	
	
	
	
}

