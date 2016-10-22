import java.io.*;
import java.net.*;
import java.util.regex.*;
import java.sql.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler
{
	Connection connection;
	int urlID;
	int currentUrlCount;
	int maxCount = 0;
	public Properties props;

	Crawler() {
		urlID = 0;
		currentUrlCount = 0 ;
		maxCount = 0;
	}

	public void readProperties() throws IOException {
		props = new Properties();
		FileInputStream in = new FileInputStream("database.properties");
		props.load(in);
		in.close();
	}

	public void openConnection() throws SQLException, IOException
	{
		String drivers = props.getProperty("jdbc.drivers");
		if (drivers != null) System.setProperty("jdbc.drivers", drivers);

		String url = props.getProperty("jdbc.url");
		String username = props.getProperty("myuser");
		String password = props.getProperty("123");

		//connection = DriverManager.getConnection( url, username, password);
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/urldatabase?user=root&password=123");
	}

	public void createDB() throws SQLException, IOException {
		openConnection();

		Statement stat = connection.createStatement();

		// Delete the table first if any
		try {
			stat.executeUpdate("DROP TABLE URLS");
			stat.executeUpdate("DROP TABLE WORDS");
		}
		catch (Exception e) {
		}

		// Create the table
		System.out.println("i will be creating the table");
		stat.executeUpdate("CREATE TABLE URLS (urlid INT, url VARCHAR(512), description VARCHAR(200))");
		stat.executeUpdate("CREATE TABLE WORDS(Word VARCHAR(512), URLID INT)");
	}

	public boolean urlInDB(String urlFound) throws SQLException, IOException {
		Statement stat = connection.createStatement();
		ResultSet result = stat.executeQuery( "SELECT * FROM urls WHERE url LIKE '"+urlFound+"'");

		if (result.next()) {
			System.out.println("URL "+urlFound+" already in DB");
			return true;
		}
		// System.out.println("URL "+urlFound+" not yet in DB");
		return false;
	}

	public void insertURLInDB( String url,String description) throws SQLException, IOException {
		Statement stat = connection.createStatement();
		String query = "INSERT INTO URLS VALUES ('"+urlID+"','"+url+"','"+description+"')";
		//String query = "INSERT INTO URLS VALUES ('"+urlID+"','"+url+"','')";
		//System.out.println("Executing "+query);
		stat.executeUpdate( query );
		urlID++;
	}


public  String  getDescription(String url) {
	try {
		Document  doc = Jsoup.connect(url).get();
		Elements links = doc.select("a[href]");
		String title = doc.title();
		StringBuilder strRead = new StringBuilder(doc.title());
		strRead.append(doc.body().text());
		String s = strRead.toString();
		String description = s.substring(0,Math.min(s.length(),100));
		return description;
		//insertURLInDB(urlScanned	,description);
	} catch (Exception e)
	{
		e.printStackTrace();
	}
	return null;
}

public void fetchURL(String urlScanned) {
	try {


		Document  doc = Jsoup.connect(urlScanned).get();
		Elements links = doc.select("a[href]");



		System.out.println("the title is " + doc.title());
		System.out.println("the doc text is " + doc.body().text());
		String title = doc.title();
		StringBuilder strRead = new StringBuilder(doc.title());
		strRead.append(doc.body().text());
		String s = strRead.toString();


		String description = s.substring(0,Math.min(s.length(),100));

		//insertURLInDB(urlScanned	,description);
		insertURLInDB(urlScanned,description);


		int count = 0;
		int i  = 0;

		while(count < maxCount) {
			//System.out.println("assdasd");


			for (Element link : links) {
				String g = link.attr("abs:href");

				if (!urlInDB(g)) {

					insertURLInDB(g,"");
					count++;

				} else {
					//System.out.println("already have the ur3l inside the database" + g);
				}


			}

		}



		// while (matcher.find()) {
		// 	int start = matcher.start();
		// 	int end = matcher.end();
		// 	String match = input.substring(start, end);
		// 	String urlFound = matcher.group(1);
		//
		// 	// Check if it is already in the database
		// 	if (!urlInDB(urlFound)) {
		// 		insertURLInDB(urlFound);
		// 	} else {
		// 		//System.out.println("there already exsists a url");
		// 	}
		//
		// 	//System.out.println(match);
		// }

	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
}


public void startCrawl() {
	//need to cleaer the database;
	int NextURLID = 0;
	int NEXTIRLIDScanned  =  0;




}

public static void main(String[] args)
{
	Crawler crawler = new Crawler();

	try {
		crawler.readProperties();
		String root = crawler.props.getProperty("crawler.root");
		crawler.createDB();
		//System.out.println("the root is " + root);
		//crawler.fetchURL(root);
		crawler.fetchURL("https://www.cs.purdue.edu");
	}
	catch( Exception e) {
		e.printStackTrace();
	}
}
}
