package structure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.server.DDL;

// static class, methods give information about the databases and their tables
public final class DBStructure {
	
	public static String getTablePK(String dbname, String tbname) {
		
		try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File("databases//" + dbname + ".xml"));
			
            document.getDocumentElement().normalize();
            DDL.removeEmptyText(document);
            
            NodeList pk = document.getElementsByTagName("pkAttribute");
            
            for(int i = 0; i < pk.getLength(); i++) {
            	
            	if(pk.item(i).getNodeType() == Node.ELEMENT_NODE) {
            		
            		Element element = (Element) pk.item(i);
            		if(((Element)element.getParentNode().getParentNode()).getAttribute("tableName").equals(tbname)) {
            			
            			return element.getTextContent();
            		}
            	}
            }
            
		} catch(ParserConfigurationException | IOException | SAXException e) {
			
			e.printStackTrace();
		}
		
		return "NO_PK_FOUND";
	}

	// gets all current database names
	public static List<String> getDatabases() {
		
		List<String> databases = new ArrayList<String>();
		
		try(Stream<Path> walk = Files.walk(Paths.get("databases//"))) {
			
			databases = walk.map(x -> x.toString())
					.filter(f -> f.endsWith(".xml"))
					.map(x -> x.substring(0,x.indexOf('.')).substring(x.lastIndexOf('\\') + 1))
					.collect(Collectors.toList());

		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return databases;
	}
	
	// gets all names of the tables currently present in the specified database
	public static List<String> getTables(String dbname) {

		List<String> tables = new ArrayList<String>();
		
		try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File("databases//" + dbname + ".xml"));
			
            document.getDocumentElement().normalize();
            
            NodeList nodeList = document.getElementsByTagName("Table");
            
            int l = nodeList.getLength();
            
            for(int i = 0; i < l; i++) {
            	
            	Element element = (Element)nodeList.item(i);
            	tables.add(element.getAttribute("tableName"));
            }
            
		} catch(ParserConfigurationException | IOException | SAXException e) {
			
			e.printStackTrace();
		}
		
		return tables;
	}
	
	// gets all the databases and their tables names, returns a list containing String lists
	// the 0. element of a list is the database name, the other elements are the table names
	public static List<ArrayList<String>> getAllDBTables() {
		
		List<ArrayList<String>> structure = new ArrayList<ArrayList<String>>();
		
		List<String> databases = getDatabases();
		
		for(String db : databases) {
			
			ArrayList<String> dbt = new ArrayList<String>();
			dbt.add(db);
			dbt.addAll(getTables(db));
			structure.add(dbt);
		}
		
		return structure;
	}
	
	// get all columns of a specific table
	public static List<String> getColumns(String dbname, String tableName) {
		
		List<String> columns = new ArrayList<String>();
		
		try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File("databases//" + dbname + ".xml"));
			
            document.getDocumentElement().normalize();
            
            NodeList nodeList = document.getElementsByTagName("Attribute");
            
            int l = nodeList.getLength();
            
            for(int i = 0; i < l; i++) {
            	
            	Element element = (Element)nodeList.item(i);
            	columns.add(element.getAttribute("attributeName"));
            }
            
		} catch(ParserConfigurationException | IOException | SAXException e) {
			
			e.printStackTrace();
		}
		
		return columns;
	}
	
}
