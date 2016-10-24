import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;
public class Test {

	public static void main(String[] args) throws SQLException, IOException {
		System.out.println("hello world");
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
		int g = 0;
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `urls` WHERE `urlid` = ?");
		stmt.setInt(1,g);

		ResultSet rs = stmt.executeQuery();
		if(rs.next()) {
			System.out.println("the result is " + rs.getString("url"));
		}

	}

}
