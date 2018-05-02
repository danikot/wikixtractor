package main;
import java.util.HashSet;
import java.util.Set;

/**
 * Pageklasse mit Getter- und Setter- Methoden.
 * @author daniil
 */
public class Page {
	
	
	private int namespace;
	private String title;
	private Set<String> categories=new HashSet();
	private int ID;
	private String Artikeltext;
	
	
	public void setNamespace(int receivedNamespace) {namespace=receivedNamespace;}
	public void setTitle(String receivedTitle){title=receivedTitle;}
	public void setCategories(Set<String> receivedCategories){
		
		//System.out.print("\n ...");
		if(!receivedCategories.isEmpty()){
		//categories=receivedCategories;
		for(String x:receivedCategories){categories.add(x);}
		}}
	public void setID(int receivedID){ID = receivedID;}
	
	public Page(int a, int b, String c/*,Set<String> d*/){namespace=b;ID=a;title=c;/*categories=d;*/}
	
	
	public int getNamespace(){return namespace;}
	public int getID(){return ID;}
	public String getTitle(){return title;}
	public Set<String> getCategories(){return categories;}
	
	//Ueberreiter wie aufgabe verlangt
	
	public int hashCode(){String pseudoHASH;
	                      pseudoHASH = title + Integer.toString(namespace) + Integer.toString(ID);
	                      return pseudoHASH.hashCode();}
	public boolean equals(Page pageComparedTo){
		if(namespace==pageComparedTo.getNamespace() && title==pageComparedTo.getTitle() && ID==pageComparedTo.getID())
		{return true;}
		else{return false;}}//es deucht mir if, else und return kann hier weg -D.
	public String getArtikeltext() {
		return Artikeltext;
	}
	public void setArtikeltext(String artikeltext) {
		Artikeltext = artikeltext;
	}
	
	
	}


// Test