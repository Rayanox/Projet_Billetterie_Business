package Helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Match {

	public String equipe;
	public int prix;
	public int ancienPrix = -1;
	public boolean statusComplet;
	public GregorianCalendar date; 
	
	
	public Match() {
	}
	
	public Match(String equipe, int prix, boolean complet, GregorianCalendar date) {
		this.equipe = equipe; 
		this.prix = prix;
		this.statusComplet = complet;
		this.date = date;
	}
	
	public Match(String equipe, int prix, boolean complet) {
		this.equipe = equipe; 
		this.prix = prix;
		this.statusComplet = complet;
		this.date = null;
	}
	
	public String getDate() {
		return date.get(Calendar.DAY_OF_MONTH)+"/"+ date.get(Calendar.MONTH)+"/"+ date.get(Calendar.YEAR);
	}
}
