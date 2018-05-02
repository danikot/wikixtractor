package main;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
//import org.neo4j.*;
import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

//import scala.*;

/**
 * Hat zahlreiche Schleifen um einen Set mit allen Seiten zu kreieren. Bedient sich der LinkExtraction und der Page.
 * @author Daniel
 */ 

public class PageFactory {

	private static Logger logger = Logger.getLogger( PageFactory.class );

	
//	private Set<Page> SetDerEingelesenenSeiten=new HashSet<Page>();
//	public Set<Page> getEingeleseneSeiten()
//	{return SetDerEingelesenenSeiten;}

	
//	@SuppressWarnings("null")
	public void AlleSeitenEinlesen(String Filename, String DB_PATH){
   
		try {
		BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Filename)),Charset.forName("UTF-8")));
		String Zeile=null;
		Page TEMPvariante=null;
		Set<String> TEMPkategorien=new HashSet<String>();
		String Wikitext=new String();
			//solange es im text was bleibt
		int articlesREAD = 0;
		int articlesREAD1000=0;
	     File DB_FILE=new File(DB_PATH);
	     GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_FILE );

//DEL	     Driver driver = GraphDatabase.driver( DB_PATH, AuthTokens.basic( "neo4j", "neo4j" ) );
//DEL	     Session session = driver.session();
	      Transaction tx= graphDb.beginTx();
	      try{
		while((Zeile=r.readLine()) != null) 
		     { 
		 
		          //head of an article
			      if(Zeile.indexOf("¤	") != -1)
			        {   //System.out.print("ANFANG \n");

			    	    int IndexInZeile = 2;
			        	String pageNamespace=Character.toString(Zeile.charAt(IndexInZeile));
			           while(Zeile.charAt(IndexInZeile) != '	')
			            {pageNamespace += Zeile.charAt(IndexInZeile);
			                  	IndexInZeile++;}
			                    IndexInZeile++; 
			                    String pageID=Character.toString(Zeile.charAt(IndexInZeile));
			                    IndexInZeile++;
			           while(Zeile.charAt(IndexInZeile) != '	')
			             	{pageID = pageID + Zeile.charAt(IndexInZeile);
			             	    IndexInZeile++;}
			            String PageTitle=Zeile.substring(IndexInZeile + 1, Zeile.length());
			        	 
			             TEMPvariante=new Page(
			        			 Integer.parseInt(pageNamespace),
			        			 Integer.parseInt(pageID),PageTitle/*,TEMPkategorien*/);
			           /*  System.out.print(pageNamespace);
			             System.out.print(" " + pageID + " ");
			             System.out.print(PageTitle);
			             System.out.print(TEMPvariante);*/
			             
			            }
			      //end of an article
			      else if(Zeile.indexOf('¤') > -1){ 
			    	  Wikitext=Wikitext.replace("\\", "\\\\");
			    	  Wikitext=Wikitext.replace("'", "\\'");
			    	  articlesREAD++;
			      TEMPvariante.setArtikeltext(Wikitext);

			     
			      if((articlesREAD%1000)==0){articlesREAD=0;tx.close(); graphDb.shutdown();tx=graphDb.beginTx();}
			      if((articlesREAD%5000)==0){graphDb.execute("CREATE INDEX ON:Page(id);");}
//			      Transaction tx= graphDb.beginTx();
//			      try{   
			    	  Result TEMPresult = graphDb.execute("MATCH (n {id:"+TEMPvariante.getID()+"}) RETURN n;");
			      		if(TEMPresult.hasNext()){
			      		System.out.print("ja - " + TEMPresult.next());}
			      		else{graphDb.execute("CREATE (:Page{id:"+TEMPvariante.getID()+",namespace:"+TEMPvariante.getNamespace()+",title:\'"+TEMPvariante.getTitle()+"\',wikitext:\'"+TEMPvariante.getArtikeltext()+"\'})");}
			            tx.success();
//			            }finally{tx.close();}
			      		Wikitext = "";}
			      else{ 
			    	  Wikitext=Wikitext + Zeile + "\n";
			    	  }
			     }  
		r.close();
		graphDb.shutdown();
	//DEL	session.close();
		//System.out.print(SetDerEingelesenenSeiten);
		}finally{tx.close();}}
		
		
		catch (Exception e){
		// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error("Exeption in PageFactory: ", e);
			}
		}
	}
