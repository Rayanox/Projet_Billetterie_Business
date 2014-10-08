import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.HTMLReader;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class HTML_Manipulation {

	public static Document ConvertHtmlStringToHtmlDocument(String html) {
		try{
			Document doc = Jsoup.parse(html);
			Elements at = doc.body().children();
			ArrayList<Element> liste = new ArrayList<Element>();
			for (Element attr : liste) {
				System.out.println("Attribut = "+attr.nodeName());
			}
			return doc;
		}catch(Exception e) {
			return null;
		}
	}
	
	
	public static void WriteToFile(String xml) {
		try {
			
			String path = System.getProperty("user.home");
			if(System.getProperty("os.name").contains("indows")) {
				path += "\\Desktop\\Billeterie.html";
			}else {
				path += "/Bureau/Billeterie.html";
			}
			
			FileWriter writer = new FileWriter(new File(path));
			writer.write(xml);
			writer.flush();
			writer.close();
			
			
		}catch(Exception  e) {
			JOptionPane j = new JOptionPane();
			j.showMessageDialog(new JFrame(), "Error when writing into file\n");
		}
	}
	
}
