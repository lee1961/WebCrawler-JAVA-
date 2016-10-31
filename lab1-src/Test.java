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
	public static void main(String[] args)  {

		try {
			//System.out.println("hello world");
			//String drivers = props.getProperty("jdbc.drivers");

			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/urldatabase?user=root&password=123");
			//String secondLink = "https://www.cs.purdue.edu/homes/cs390lang/java/";
			String secondLink = "https://jsoup.org";
			System.out.println("The description of the link is " + getDescription(secondLink));
			//getDescription(secondLink);
		} catch (Exception e) {
			System.out.println("asd");
			//System.out.println(e.printStackTrace());
		}



	}
	public  static String  getDescription(String url) {
		try {

			// first get the title
			// then get the headers
			//if not get the stuff
			Document  doc = Jsoup.connect(url).get();
			Elements links = doc.select("a[href]");

			String title = doc.title();
			//	System.out.println("the title is " + title);
			title = title.replaceAll("\\p{Punct}+", "");
			StringBuilder strRead = new StringBuilder();
			//System.out.println("the title is " + title);

			strRead.append(title);
			// Elements hTags = doc.select("h1, h2, h3, h4, h5, h6");
			// //Element htags =

			Element link = doc.select("h1").first();
			if(link != null && strRead.length() < 100) {
				strRead.append(" ");
				strRead.append(link.text());
				strRead.append(" ");
				link = doc.select("h2").first();
				if(link != null && strRead.length() < 100) {
						strRead.append(link.text());
						strRead.append(" ");
						link = doc.select("h3").first();
						if(link != null && strRead.length() < 100) {
							strRead.append(link.text());
							strRead.append(" ");
							link = doc.select("h4").first();
							if(link != null && strRead.length() < 100 ) {
								strRead.append(link.text());
								strRead.append(" ");
								link = doc.select("h5").first();
								if(link != null && strRead.length() < 100) {
									strRead.append(link.text());
									strRead.append(" ");
									link = doc.select("h6").first();
									if(link != null && strRead.length() < 100) {
										strRead.append(link.text());
										//strRead.append(" ")
									}
								}
							}
						}
				}

			}

		//	String text = doc.select("body").text();
		//	System.out.println("the length of the text is "+ text.length());
			// if(text.length() != 0) {
			// 	//System.out.println("nothing inside here");
			// 	text = text.replaceAll("\\p{Punct}+", "");
			// 	strRead.append(text);
			// }



			String s = strRead.toString();
			//	System.out.println("the s is " + s );
			String description = s.substring(0,Math.min(s.length(),100));
			System.out.println("the description length is " + description.length());
			return description;

			// Document doc = Jsoup.connect(url).get();
			// 	// for(Element meta : doc.select("meta")) {
			// 	//     System.out.println("Name: " + meta.attr("p") + " - Content: " + meta.attr("content"));
			// 	// }
			// 	//System.out.println(doc.title());
			//
			// 	String title = doc.title();
			// 	//tmp = tmp.replaceAll("\\p{Punct}+", "");
			// 	title = title.replaceAll("\\p{Punct}+", "");
			// 	System.out.println(title);
			// 	System.out.println("the length is " + title.length());
			// 	return "";
			//insertURLInDB(urlScanned	,description);
		} catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}

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
