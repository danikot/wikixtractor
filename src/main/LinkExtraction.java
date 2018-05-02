package main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import org.neo4j.cypher.internal.frontend.v3_0.parser.Strings;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
/**
 * Extrahiertet Kategorien aus Zeile.
 * @author Olvian
 */


public class LinkExtraction {



	/* Bekommt einen String, dieser wird geparsed.
	 * Falls Link-Attribut Kategorien beinhaltet werden
	 * diese zurückgegeben, ansonsten wird leerer String
	 * zurÃ¼ckgegeben.
	 * @author Olvian
	 * */ 
	
	public static Set<String> getCategory(String RecivedString) throws IOException
	{
		Set<String> Categories=new HashSet<String>();
		Document doc = Jsoup.parse(RecivedString);
        Elements links = doc.select("a[href]");
        //String WorkString = "";
        for (Element link : links) {
        	if (link.attr("href").contains("/wiki/Kategorie:")) {
        		Categories.add(link.text().toString());
        		        }}
		return Categories;
        
	} 
	public static Set<String> getLinks(String ReceivedString) throws IOException
	{ Set<String> LinksSet=new HashSet<String>();
	Document doc = Jsoup.parse(ReceivedString);
    Elements linksEl = doc.select("a[href]");
    for (Element link : linksEl) {
    	if (!link.attr("href").contains("/wiki/Kategorie:")) {
    		LinksSet.add(link.text().toString());
    		        	}}
		return LinksSet;
    }
	
	}
