import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import com.sun.org.apache.bcel.internal.generic.NEW;


public class Echangeur {
	
		
	public static String Request(URL url, Protocol protocol) {
			
		String reponse;
		String reqBilleterie;
		String cookie = "";
		int nbRequest = 0;
		
		do {
			if(nbRequest>15) return null;
			
			reqBilleterie = prepareRequest(url, cookie);
			reponse = transactionEnvoi(reqBilleterie, url.getHost(), protocol);
			cookie = findCookieInHTTP(reponse);
			nbRequest++;
		}while(!reponse.contains("DOCTYPE"));
		
		System.out.println("HTML recu !");
		return reponse;
	}

	private static String findCookieInHTTP(String reponse) {

		String cookie = "Cookie: ";
		try {
			String dixPremieresLignes = getDixPremieresLignes(reponse);
			return cookie += dixPremieresLignes.substring(dixPremieresLignes.indexOf("VarnishQueue"), reponse.indexOf(";")) + "\n";
		}catch(Exception e) {
			return "";
		}
		
		
	}

	private static String getDixPremieresLignes(String reponse) {
		
		int sauts = 0;
		int i = 0;
		for(; i+1 <reponse.length(); i++) {
			if(reponse.charAt(i) == '\n') {
				sauts++;
			}
			if(sauts >= 10) return reponse.substring(0, i);
		}
		return reponse.substring(0, i);
	}

	private static String transactionEnvoi(String reqBilleterie, String url, Protocol protocol) {
		try {
			
			Socket soc = new Socket(InetAddress.getByName(url), protocol == protocol.HTTP ? 80 : 443);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream(), "UTF-8"));
			BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream(), "UTF-8"));

			String tmp, response = "";
			
				writer.write(reqBilleterie);
				writer.flush();
				
				while((tmp = reader.readLine()) != null){
					//System.out.println(tmp);
					response += tmp+"\n";
					
				}			
			
			
			return response;
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return null;
		
	}

	private static String prepareRequest(URL url, String cookie) {
		
		return requestPage(url, cookie);
	}

	private static String requestPage(URL url, String cookie) {
		String req ="";
				
		req += "GET "+url.getPath()+" HTTP/1.1\n"
				+ "Host: "+url.getHost()+"\n"
				+ cookie+"\n\n";
		
		return req;
	}
	
}
