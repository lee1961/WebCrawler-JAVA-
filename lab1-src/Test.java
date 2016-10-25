import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
	static Connection connection;
	public static void main(String[] args) throws Exception {




		//System.out.println("hello world");
		//String drivers = props.getProperty("jdbc.drivers");
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/urldatabase?user=root&password=123");
		// int id = 1;
		// String id_str = "1";
		// String sql = ("SELECT url FROM urls WHERE urlid = " + String.valueOf(id));
		// Statement stat = connection.createStatement();
		// ResultSet rs = stat.executeQuery(sql);
		// if (rs.next()) {
		// 	String result = rs.getString("url");
		// 	System.out.println("the result is " + result);
		// 	//urltoVisit = result;
		// 	//System.out.println("the urltovisit  is " + urltoVisit);
		// }
		// int g = 0;
		// PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `urls` WHERE `urlid` = ?");
		// stmt.setInt(1,g);
		//
		// ResultSet rs = stmt.executeQuery();
		// if(rs.next()) {
		// 	System.out.println("the result is " + rs.getString("url"));
		// }
		//String url = "https://www.cs.purdue.edu";
		String url = "https://www.cs.purdue.edu/";
		Document  doc = Jsoup.connect(url).get();
		String text = doc.body().text();
		Element image = doc.select("img").first();
		if(image != null) {
			String im = image.absUrl("src");
			if( im != null) {
				System.out.println("the url is " + im);
			}
		}


		//System.out.println(text);
		//tokenizeWebsite(url);
		//checkWordExist();
		//String g = "   Gene Spafford  Grace  ";
		String g = "Gene";
		//String za = g.trim();
		//System.out.println("the zstring is \n" + za);
		//g.replaceAll("\\s+$", "");
		//System.out.println(g.trim());
		//g = rtrim(g);
		g = g.trim();
		System.out.println(g);
		String arr[] = g.split(" ");

		//System.out.println(arr[1]);
		StringBuilder strbuilder = new StringBuilder();
		strbuilder.append("SELECT distinct urlid FROM WORDS ");
		//strbuilder.setLength(strbuilder.length() - 7);
		//System.out.println("the strbuilder string is " + strbuilder.toString());

		//select distinct urlid from words where word = 'gene' and urlid in (select distinct urlid from words where word = 'spafford');
		String concat = " AND urlid in (select distinct urlid from words";
		//System.out.println("the arr.length is " + arr.length);

		// SELECT distinct urlid FROM WORDS  Where Word = 'Gene' AND urlid in (select distinct urlid from words Where Word = 'Spafford' AND urlid in (select distinct urlid from words Where Word = 'Grace'));
		String z = "select distinct urlid from words where word = 'gene' and urlid in (select distinct urlid from words where word = 'spafford')";
		int count = arr.length;
		for(int i = 0 ; i < arr.length ; i++) {
			//handle 0 Word
			//handle 1 Word
			//handle multiple words
			// "select distinct urlid from words where word = 'gene' and urlid in (select distinct urlid from words where word = 'spafford')";
			strbuilder.append(" Where Word = " + "\'"  + arr[i] + "\'"  + concat);

		}
		//select distinct urlid from words where word = 'gene' and urlid in (select distinct urlid from words where word = 'spafford');
		// SELECT distinct urlid FROM WORDS  Where Word = 'Gene' AND urlid in (select distinct urlid from words Where Word = 'Spafford';
		strbuilder.setLength(strbuilder.length() - concat.length());
		for(int i = 1 ; i < count ;i++) {
			strbuilder.append(")");
		}
		System.out.println("the strbuilder string is " + strbuilder.toString());
		PreparedStatement stmt = connection.prepareStatement(strbuilder.toString());
		// for(int i = 0; i < arr.length ;i++) {
		// 	stmt.setString(i+1,arr[i]);
		// }
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			System.out.println("the urlid is " + rs.getInt("urlid"));
			//System.out.println("yeah alreadyd exist in the url");
		}
		insertWeirdWord();
	}
	public static void insertWeirdWord() throws Exception {
		//String query = "INSERT INTO WORDS (word,urlid) VALUES (?,?)";
		String query = "insert into WORDS(word,urlid)" + " values (?,?)";
		PreparedStatement stat = connection.prepareStatement(query);

		String word = "Pothen's";
		int urlid = 2001;
		stat.setString(1,word);
		stat.setInt(2,urlid);



		stat.executeUpdate();

	}
	public static void checkWordExist() throws Exception {
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `WORDS` WHERE `Word` = ? AND urlid = ?");

		stmt.setString(1,"Lawson");
		int id = 0;
		stmt.setInt(2,id);
		//stmt.setString(1,temp);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()) {
			System.out.println("the result is " + rs.getString("urlid"));
			//System.out.println("yeah alreadyd exist in the url");
		}
	}
	public static void tokenizeWebsite(String url) {
		try {
			Document  doc = Jsoup.connect(url).get();
			String text = doc.body().text();
			String arr[] = text.split(" ");
			for(int i = 0; i < arr.length ; i++) {
				String temp = arr[i];
				temp = temp.trim();
				//str = str.replaceAll("[^A-Za-z0-9]", "");
				temp = temp.replaceAll("[^A-Za-z0-9]", "");
				if(!temp.equals("")) {
					//checking whether the word is alredy
					PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `urls` WHERE `urlid` = ?");
					int g = 1;
					stmt.setInt(1,g);
					ResultSet rs = stmt.executeQuery();
					if(rs.next()) {
						System.out.println("the result is " + rs.getString("url"));
					}
					System.out.println(temp);
				}
				//System.out.print(temp + " ");
				// temp = temp.toLowerCase();
				//   pot.add(temp.toCharArray(),temp.length());
				//pot.stem();
				//temp = pot.toString();
				//   if(!hashmap.containsKey(temp)) {
				// 	  hashmap.put(temp,1);
				//   } else {
				// 	  hashmap.put(temp,hashmap.get(temp) + 1);
				//   }
			}
		} catch (Exception e) {

		}
	}

}
