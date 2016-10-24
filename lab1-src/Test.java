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

	public static void main(String[] args) throws SQLException, IOException {
		//System.out.println("hello world");
		//String drivers = props.getProperty("jdbc.drivers");
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/urldatabase?user=root&password=123");
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
		tokenizeWebsite(url);
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
