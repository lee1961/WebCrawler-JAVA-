// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\QueryServlet.java".
import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.*;

@WebServlet("/searchQuery")
public class QueryServlet extends HttpServlet {  // JDK 6 and above only
    Connection conn = null;
    // The doGet() runs once per HTTP GET request to this servlet.
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Set the MIME type for the response message
        response.setContentType("text/html");
        // Get a output writer to write the response message into the network socket
        PrintWriter out = response.getWriter();


        Statement stmt = null;
        PreparedStatement st = null;
        try {
            // Step 1: Allocate a database Connection object
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/urldatabase?user=myuser&password=xxxx"); // <== Check!

            // database-URL(hostname, port, default database), username, password

            // Step 2: Allocate a Statement object within the Connection
            stmt = conn.createStatement();
            String g = request.getParameter("query");
            int urlid[] = returnUrlid(g);



            // Step 3: Execute a SQL SELECT query
            // String sqlStr = "select * from books where author = "
            // + "'" + request.getParameter("author") + "'"
            // + " and qty > 0 order by price desc";

            // Print an HTML page as the output of the query
            out.println("<html><head><title>Query Response</title></head><body>");
            // to search again
            out.println("<h3>Search again</h3>");
            out.println("<form method=\"get\" action=\"http://localhost:9999/hello/searchQuery\">");
            out.println("<input style=\"height:30px;font-size:20pt;width:1000px;\" input type=\"text\" name=\"query\" ><br><br>");
            String temp = "<input type=\"submit\" value=\"Search\">";
            out.println(temp);
            out.println("</form>");


            out.println("<h3>the requestparameter is " + request.getParameter("query") + "</h3>");
            out.println("<h3>Thank you for your query.</h3>");





            //out.println("<p>You query is: " + sqlStr + "</p>"); // Echo for debugging
            //ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the server

            // Step 4: Process the query result set
            int count = 0;
            // while(rs.next()) {
            //     // Print a paragraph <p>...</p> for each record
            //     out.println("<p>" + rs.getInt("urlid")
            //     +  "</p>");
            //     count++;
            // }
            // // this prints the urlid 
            // while(count < urlid.length) {
            //     out.println("<p>" + urlid[count]
            //     +  "</p>");
            //     count++;
            // }


            out.println("<p>==== " + count + " records found =====</p>");
            String resultURL[] = new String[urlid.length];
            for(int i = 0 ; i < urlid.length ; i++) {
                	//PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `WORDS` WHERE `Word` = ? AND urlid = ?");
                    System.out.println("yeah i am here");
                    PreparedStatement statement = conn.prepareStatement("SELECT * FROM URLS WHERE urlid = ?");
                    statement.setInt(1,urlid[i]);
                    ResultSet rs = statement.executeQuery();
                    if(rs.next()) {
                        // out.println("<p>" + rs.getString("url")
                        // +  "</p>");
                        //<a href="http://www.w3schools.com/html/">Visit our HTML tutorial</a> Try it Yourself »
                        out.println("<a style=\"height:30px;font-size:20pt;width:1000px;\" href=" +  "\"" + rs.getString("url") + "\">" + rs.getString("description") + "</a>" + "<br>");

                        resultURL[i] = rs.getString("url");
                    }
            }
            // count = 0;
            // while(count < resultURL.length) {
            //     out.println("<p>" + resultURL[count]
            //     +  "</p>");
            //     count++;
            // }


            out.println("</body></html>");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            out.close();  // Close the output writer
            try {
                // Step 5: Close the resources
                if (stmt != null) st.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    // this will return the list of the urls that contain the words typed by the user
    public int[] returnUrlid(String g)  {
        StringBuilder strbuilder = new StringBuilder();
        strbuilder.append("SELECT distinct urlid FROM WORDS ");
        g = g.trim();
        String arr[] = g.split(" ");
        int count = arr.length;
        String concat = " AND urlid in (select distinct urlid from words";
        for(int i = 0 ; i < arr.length ; i++) {
            //handle 0 Word
            //handle 1 Word
            //handle multiple words
            // "select distinct urlid from words where word = 'gene' and urlid in (select distinct urlid from words where word = 'spafford')";
            strbuilder.append(" Where Word = " + "\'"  + arr[i] + "\'"  + concat);
        }
        strbuilder.setLength(strbuilder.length() - concat.length());
        for(int i = 1 ; i < count ;i++) {
            strbuilder.append(")");
        }
        try {
            PreparedStatement stmt = conn.prepareStatement(strbuilder.toString());
            ResultSet rs = stmt.executeQuery();
            ArrayList<Integer> ar = new ArrayList <Integer>();
            while(rs.next()) {
                int id = rs.getInt("urlid");
                ar.add(id);
                System.out.println("the urlid is " + id);
                //System.out.println("yeah alreadyd exist in the url");
            }
            int [] result = new int[ar.size()];
            for(int i = 0 ; i < ar.size()  ; i++) {
                result[i] = ar.get(i);
            }
            return result;
        } catch (Exception e) {

        }
        return null;

    }
}