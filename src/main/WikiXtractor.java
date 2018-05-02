package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
//import log4j
import org.apache.log4j.*;
import org.apache.log4j.Logger;

import org.neo4j.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import scala.sys.process.processInternal;



//import java.io.*;

//Für Javadoc:  http://www.java-forum.org/thema/javadoc-mit-eclipse-erstellen.135963/



public class WikiXtractor {

	private static Logger logger = Logger.getRootLogger();

	
	/**
	 * Unser Main zu Milestone 2.
	 * @author Christopher - logging, kommentierungen, code-design, code-entwurf
	 * @author Daniel - reset, catlinks, articlelinks, pageinfo
	 * @param args[0] name of file read
	 * @param args[1] name of output file
	 * @throws IOException 
	 * */
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
	
		
		

		try {
			SimpleLayout layout = new SimpleLayout();
		    ConsoleAppender consoleAppender = new ConsoleAppender( layout );
		    logger.addAppender( consoleAppender );
		    
		    //durch das false wird die log-Datei immer wieder geleert. Bei true bleiben alte logs erhalten
		    FileAppender fileAppender = new FileAppender( layout, "logs/run.log", false );
		    logger.addAppender( fileAppender );
		      // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
		      // Teste Logger-Meldungen mit Meldestufe "Warn"
		      logger.setLevel( Level.WARN );
		    }// /try
		catch( Exception e ) {
		      logger.error("Exeption in run/Main: ", e);      		     
		}// /catch
		

	    logger.debug( "Meine Debug-Test-Meldung" );
	    logger.info(  "Meine Info-Test-Meldung"  );
	    logger.warn(  "Meine Warn-Test-Meldung"  );
	    logger.error( "Meine Error-Test-Meldung" );
	    logger.fatal( "Meine Fatal-Test-Meldung" );
	    
	    

	    
	    logger.setLevel(Level.INFO);
	    

		
	    /*
	     * // Declare directory of database File 
	     * dbDir = new File("db"); 
	     * // Delete old database should it exist 
	     * if (dbDir.exists()) FileUtils.deleteRecursively(dbDir); 
	     * // Create or reopen database 
	     * db = new GraphDatabaseFactory().newEmbeddedDatabase(dbDir); 
	     * // Perform operations on database now...
	     * 
	     * 
	     * Wenn ein Link keine Kategorie ist, dann ist die NamespaceID = 0
	     * Eine Kategorie hat die NamespaceID 14
	     * 
	     * CREATE UNIQUE (:Page{ArtikelID: "ÜbergabeID", NamespaceID: "Übergabe...", Titel: "Übergabe Titel", Inhalt: "Inhalt"})
	     * Verbindung Varibalen zuweisen (a,b) :    MATCH (a: aktuelleNote), (b:neue Node{Titel,NamespaceID})
	     * Verbindung erstellen:     				CREATE (a)-[:Link]->(b)
	     * 
	   */
	    
		
		switch(args[0]){
		
		//WikiXtractor importhtml
		
					case "importhtml":
		
						//zeigt ordner wo das Programm läuft und ob es die eingegebene Datei gibt.
						//		System.out.print(System.getProperty("user.dir"));
						//		System.out.print("/n");
						//		File myWikiText=new File(args[1]);	
						//		System.out.print(myWikiText.exists());

						logger.info( "WikiExtractor ImportHTML startet" );
						logger.info( "Alle Seiten werden eingelesen" );
	    
						PageFactory KrasnyjOktiabr = new PageFactory();
						KrasnyjOktiabr.AlleSeitenEinlesen(args[2],args[1]);
		
						//logger.info( "Alle Seiten wurden eingelesen und in einem Set zusammengefasst" );
						//logger.info( "Erstellen des Outputs in einer XML-Datei" );
						//PageExport Export=new PageExport();
						//Export.ExportPages(KrasnyjOktiabr.getEingeleseneSeiten(), args[1]);
		
						logger.info( "Alle Wiki-Seiten wurden in die Datenbank eingefügt" );
						logger.info( "ImportHTML beendet" );
						
						break;
	    
	    
	    //WikiExtractor reset
	    
					case "reset":
						
						logger.info( "WikiExtractor Reset wird ausgeführt" );
						
						//Es wird mithilfe der FileUtils-Bibliothek der Ordner mit der Datenbank geloescht.
						File moribund=new File(args[1]);
						try {FileUtils.deleteDirectory(moribund);} catch (IOException e) {"Exeption in run/Main -> case 'reset' - Löschen der Datenbank: ", e}
			
			
						GraphDatabaseService newGraph = new GraphDatabaseFactory().newEmbeddedDatabase( moribund );
						newGraph.shutdown();
						
						logger.info( "Alte Datenbank gelöscht, neue (leere) Datenbank wurde angelegt" );
			
						break;
						
					
		//WikiExtractor categorylinks
			
					case "categorylinks": 
						
						logger.info( "WikiExtractor Categorylinks wird ausgeführt" );
						
						//es dauert auf meinem Computer ~6 Stunden bis die Kategoriengraphen fertig sind.
						File DB_FILE=new File(args[1]);
						GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_FILE );
						LinkExtraction extractor=new LinkExtraction();
						
						//TEMPresult wird alle Nodes aus der Graphendatenbank (zwei Zeilen weiter, durch cypher) bekommen.
						Result TEMPresult = null;
						Transaction tx= graphDb.beginTx();

					      try{   TEMPresult = graphDb.execute("MATCH (n) RETURN n;");
					      graphDb.execute("CREATE INDEX ON:Page(title);");
					      graphDb.execute("CREATE INDEX ON:Page(namespace);");}
					      finally{tx.close();};
					      
					      //begin und close um die Rezeption aller Knoten im Graphen herum und nicht um alles,...
					      //weil bei close geschrieben wird. Wir wollen nicht 6 Stunden lang die Cat-Graphen im RAM nur behalten
					      //das ist ein counter um den Nutzer während darzustellen wie weit das Programm ist.

					      int intEresting=1;
					      
					      //diese Schleife läuft alle Knoten, also alle in der Datenbank enthaltenen Wikipediaseiten, durch.
					      while(TEMPresult.hasNext()){
					    	  tx=graphDb.beginTx();
					    	  try{
					    		  Node node=(Node) TEMPresult.next().get("n");
	
					      			
					    		  //wegkommentieren, wenn's stört.
					    		  System.out.println(intEresting + "- currently doing " + node.getProperties("title"));
					    		  intEresting++;
					      			
					      			
					    		//extractor ist Objekt der Klasse Linkextraction. 
					    		//Katset ist ein Set mit den Titeln der im Artikel verlinkten Kategorien.
					    		Set<String> Katset=extractor.getCategory((String) node.getProperty("wikitext"));
					      				
					    		for (String string1: Katset){
					      				
					      					//WENN es die Kategorie schon in der Datenbank gibt,...
					      					if(graphDb.execute("MATCH (n{title:\'"+ string1 +"\',namespace:14}) RETURN n;").hasNext()){
					      						
					      						//...wird sie mit Kantenlable 'CATEGORY' verlinkt. Die Kante ist vom aktuellen Knoten zu der Kategorie gerichtet.
					      						graphDb.execute("MATCH (i{title:\'" + node.getProperty("title") + "\',namespace:"+node.getProperty("namespace")+"}) CREATE UNIQUE (i)-[:CATEGORY]-> (j:PAGE{title:\'" + string1 +"\',namespace:14})");
					      					
					      					} // /if
					      				
					      				//Erst wenn die Transaktion geschlossen ist, wird in die Graphendatenbank geschrieben. 
					      				//Bei uns: nachdem alle Kategorienkanten aus einem Wikipediaseitenknoten gelesen wurden.					      				
					      				} // /for
					      				
					      				tx.success();
					      			}  // /try 
					      			finally{tx.close();}					      			
					    } // /while
					      			
						graphDb.shutdown();
						
						logger.info( "WikiExtractor Categorylinks beendet" );
						
						break;
						
						
		//WikiExtractor articlelinks						
					
					case "articlelinks": 

						//analog zu oben mit Unterschied im Cyphercode.
						//natürlich ließe beides abstrahieren.
						//für's debugging erschien es mir aber praktischer, zwei leicht verschiedene varianten nebeneinander zu halten. 
						
						File DB_FAIL=new File(args[1]);
						GraphDatabaseService graphDB = new GraphDatabaseFactory().newEmbeddedDatabase( DB_FAIL );
						
						LinkExtraction eXtractor=new LinkExtraction();
						Result TEMPresulT = null;
						Transaction tX= graphDB.beginTx();
						
						try{ TEMPresulT = graphDB.execute("MATCH (n) RETURN n;");
					      graphDB.execute("CREATE INDEX ON:Page(title);");
					      graphDB.execute("CREATE INDEX ON:Page(namespace);");}
						finally{tX.close();}
						int out_of_sheer_interest=1;
						
							while(TEMPresulT.hasNext()){
								tX=graphDB.beginTx();
								try{
									Node node=(Node) TEMPresulT.next().get("n");
					      			
					      			System.out.println(out_of_sheer_interest + "- currently doing " + node.getProperties("title"));
					      			out_of_sheer_interest++;
					      			
					      			//System.out.println(node.getProperty("wikitext"));
					      			Set<String> Artset=eXtractor.getLinks((String) node.getProperty("wikitext"));
					      			
					      			for (String string2: Artset){
					      				
					      				string2 = string2.replace("'", "\\'");
					      				if( graphDB.execute("MATCH (n{title:\'"+ string2 +"\',namespace:0}) RETURN n;").hasNext()){
					      					graphDB.execute("MATCH (i{title:\'" + node.getProperty("title") + "\',namespace:"+node.getProperty("namespace")+"}) CREATE UNIQUE (i)-[:LINKS]-> (j{title:\'" + string2 +"\',namespace:0})");
					      				}// /if
					      				
					      			}// /for
					      			tX.success(); 
					      		}// /try
					      			finally{tX.close();}
							}// /while
					      			
						graphDB.shutdown();
						
						break;

						
		//WikiExtractor pageinfo						
					
					case "pageinfo":

						//was weiter kommt ist self-explanatory: transaktion starten, 
						//das gefragte durch cypher MATCHen 
						//und in einer Schleife an das CLI übergeben.
						
						File DB_FAYIL=new File(args[1]);
						GraphDatabaseService GraphDB = new GraphDatabaseFactory().newEmbeddedDatabase( DB_FAYIL );
						Transaction TX= GraphDB.beginTx();
						
						try{   
							Result TEMP = GraphDB.execute("MATCH (:Page{namespace:"+args[2]+",title:\'"+args[3]+"\'})-[:CATEGORY]->(m) RETURN m;");
							System.out.println("\n The Categories " +(String) args[2]+" " + (String) args[3] + " belongs to directly are:");		
							//if(TEMP.hasNext()){System.out.print("HAS NEXT");}
								
							while(TEMP.hasNext()){
								Node node=(Node) TEMP.next().get("m");
									System.out.print(node.getProperty("title") + ", ");
							}
					   
							TEMP = GraphDB.execute("MATCH (:Page{namespace:"+args[2]+",title:\'"+args[3]+"\'})-[:CATEGORY*]->(m) RETURN m;");
							System.out.println("\n The Categories " +(String) args[2]+" " + (String) args[3] + " belongs to directly and indirectly are:");
								
							while(TEMP.hasNext()){
								Node node=(Node) TEMP.next().get("m");
								System.out.print(node.getProperty("title") + ", ");
								//if(!TEMP.hasNext()){break;}
				      		}
					      
							TEMP = GraphDB.execute("MATCH (:Page{namespace:"+args[2]+",title:\'"+args[3]+"\'})-[:LINKS]->(m) RETURN m;"); 
							System.out.println("\n The Pages " +(String) args[2]+" " + (String) args[3] + " links to directly are:");
								
							while(TEMP.hasNext()){
								Node node=(Node) TEMP.next().get("m");
								System.out.print(node.getProperty("title") + ", ");
									//if(!TEMP.hasNext()){break;}
							}
					     
							TEMP = GraphDB.execute("MATCH (n:Page{namespace:"+args[2]+",title:\'"+args[3]+"\'})<-[:LINKS]-(m) RETURN m;");
							System.out.println("\n The Pages that link to " +(String) args[2]+" " + (String) args[3] + " directly are:");	
								
							while(TEMP.hasNext()){
								Node node=(Node) TEMP.next().get("m");
								System.out.print(node.getProperty("title") +", ");
									//if(!TEMP.hasNext()){System.out.print(".");break;}
				      		}
					      
							TX.success();
						}// /try
						finally{TX.close();}	
						
						GraphDB.shutdown();
						
						break;

						
		}// /switch
		}// /main
	}// /class
