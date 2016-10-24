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
		String url = "https://www.cs.purdue.edu";
		Document  doc = Jsoup.connect(url).get();
		String text = doc.body().text();
		//System.out.println(text);
		//tokenizeWebsite(url);
		checkWordExist();
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
