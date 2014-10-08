import java.net.URL;


public class AdressesWebs {

	
	public static URL AdresseBilletteriePSG() {
		try {
			return new URL("http://billetterie.psg.fr/fr/matchs");
		}catch (Exception e) {
			return null;
		}
		
	}
}
