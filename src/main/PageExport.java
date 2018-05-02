package main;

import java.util.Set;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;


/*
 * Idee: 
 * Ich bin im Internet auf den Begriff "PrintWriter" gestoßen. Mit diesem wird
 * eine neue Datei erstellt in Form von 
 * PrintWriter printWriter = new printWriter ("output.xml", "UTF-8")
 * Sobald dann zum ersten Mal etwas in die Datei mit printWriter.println(...) geschrieben 
 * wird, wird diese auch erstellt. Der erste Eintrag ist dann immer:
 * <?xml version="1.0" encoding="UTF-8"?>   gefolgt von einem Absatz und <pages>
 * 
 * Einen Tab erstellt man mit "/t", wenn ich das richtig verstanden habe.
 * 
 * Wie bekomme ich die Daten?
 * Mit einer for-Schleife geht man über das Ergebnis (Set) der PageFactory und die 
 * dazugehörigen Kategorien eingeleitet durch <categories> bei jeder Page.
 * 
 * Beispiel:
 * <page pageID="1234" namespaceID="0" title="Blabla">
 * 	/t <categories>
 * 		/t/t <category name ="Kategroie 1">
 * 		   .....
 * 
 * 
 * 
 */

/**
 * Schreibt XML-Datei aus dem Set aller Pages. Bekommt Set von PageFactory via run.
 * @author Christopher
 */ 
public class PageExport {

	private static Logger logger = Logger.getLogger( PageExport.class );
	public PageExport() {	  }
	public void ExportPages(Set<Page> SetOfPages, String OutputFilename){
		

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(OutputFilename, "UTF-8");
		} catch (Exception e) { logger.error("Exeption in PageExport: ", e);}
		/*
		// Ich würde gerne folgendes Problem abfangen: Wenn berreits eine Output.xml besteht, soll eine zweite erstellt werden.
		// z.B. Output(2).xml oder soetwas in der Art
		*/
		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<pages>\n");
		
		
		for (Page p : SetOfPages) {
			//System.out.print("page printed\n");
			//System.out.print(p.getTitle() + "\n");
			//System.out.print(p.getCategories().toString());
			pw.print("\t<page pageID=\"" + p.getID());
			pw.print("\" namespaceID=\"" + p.getNamespace());
			pw.print("\" title=\"" + p.getTitle());
			pw.print("\">\n");
		/*	
			//  die Geschichte mit dem \"   (Backslash und Quote) ist dafür, dass im String am Schluss auch die Quotes angegeben werden
			//  wie im Beispiel:  <page pageID="17724" namespaceID="0" title="Amiga 500">
			//  \t erstellt einen Tab, damit das alles am Schluss schön eingerückt ist
			// pro \t wird einmal eingerückt
			*/

			
			// categories werden in der XML-Datei nur gesetzt, wenn auch wirklich categories existieren
			// Allerdings werden die Klammern dennoch gesetzt
			pw.print("\t\t<categories>\n");	
			
			
			if(!p.getCategories().isEmpty()){
			
			
			for (String Categorie : p.getCategories()){
				//System.out.print("kategorien genommen");
				//System.out.print(Categorie);
				pw.print("\t\t\t<category name=\"" + Categorie);
				pw.print("\"/>\n");
				
			
			} // end inner for-loop			
		
			
			} // end if
			
			pw.print("\t\t</categories>\n");			
			pw.print("\t</page>\n");
			
			// Test, ob der Logger auch hier funktioniert  (tut er!)
			//logger.info("Folgende Artikel ID wurde in XML angelegt: " + p.getID());
		
		} //end outer for-loop
		
		pw.print("</pages>");
		pw.close();
		
		
		
		
	}// /void
}// /class
