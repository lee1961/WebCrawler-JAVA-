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
    static int maxCount = 0;
    public Properties props;

    int maxUrlInDatabase = 0;


    Crawler() {
        urlID = 0;
        currentUrlCount = 0 ;
        maxCount = 0;
        //maxCount = 1000;
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
        stat.executeUpdate("CREATE TABLE URLS (urlid INT, url VARCHAR(512), description VARCHAR(200),picture VARCHAR(300))");
        //stat.executeUpdate("CREATE TABLE URLS(urlid INT, url VARCHAR(512), description VARCHAR(200)");
        stat.executeUpdate("CREATE TABLE WORDS(Word VARCHAR(512), URLID INT)");
    }



    public boolean urlInDB(String urlFound) throws SQLException, IOException {
        Statement stat = connection.createStatement();
        ResultSet result = stat.executeQuery( "SELECT * FROM urls WHERE url LIKE '"+urlFound+"'");

        if (result.next()) {
            //System.out.println("URL "+urlFound+" already in DB");
            return true;
        }
        // System.out.println("URL "+urlFound+" not yet in DB");
        return false;
    }

    public void insertURLInDB( String url,String description,String picture) throws SQLException, IOException {
        //Statement stat = connection.createStatement();

        String g = " ";
        //String query = "INSERT INTO URLS VALUES ('"+urlID+"','"+url+"','"+description+"','')";
        String query = "INSERT INTO URLS (urlid,url,description,picture) VALUES (?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1,urlID);
        stmt.setString(2,url);
        stmt.setString(3,description);
        stmt.setString(4,picture);
        stmt.executeUpdate();
        stmt.close();

        // String query= "INSERT INTO DBUSER"
        // + "(urlID,url, description,picture) VALUES"
        // + "(?,?,?,?)";
        //String query = "INSERT INTO URLS VALUES ('"+urlID+"','"+url+"','')";
        //System.out.println("Executing "+query);
        //stat.executeUpdate( query );
        urlID++;
        maxUrlInDatabase = urlID - 1;
    }
    public boolean wordInCurrentUrl(String word) throws SQLException, IOException {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `WORDS` WHERE `Word` = ? AND urlid = ?");
        stmt.setString(1,word);
        stmt.setInt(2,urlID);
        ResultSet rs = stmt.executeQuery();
        if(rs.next()) {
            //System.out.println("the result is " + rs.getString("url"));
            return true;
        }
        return false;
    }
    //inserting words into the word table
    public void tokenizeWebsite(String url) {
        try {
            //HashTable<String,String> hash = new HashTable<String,String>();
            HashSet<String> hash_set = new HashSet<String>();

            Document  doc = Jsoup.connect(url).data("query", "Java")
            .userAgent("Mozilla")
            .cookie("auth", "token")
            .timeout(3000)
            .post();
            String text = doc.body().text();
            String arr[] = text.split(" ");
            for(int i = 0; i < arr.length ; i++) {
                String temp = arr[i];
                temp = temp.trim();

                temp = temp.replaceAll("[^A-Za-z0-9]", "");
                if(!temp.equals("")) {
                    //hash.put(temp,url);

                    //if the word doesnt exist in the current urlID then only insert
                    if(!hash_set.contains(temp)) {
                        hash_set.add(temp);
                        //Statement stat = connection.createStatement();
                        //String query = "INSERT INTO WORDS VALUES ('"+temp+"','"+urlID+"')";
                        //stat.executeUpdate( query );
                        String query = "insert into WORDS(word,urlid)" + " values (?,?)";
                        PreparedStatement stat = connection.prepareStatement(query);
                        stat.setString(1,temp);
                        stat.setInt(2,urlID);
                        stat.executeUpdate();
                    }
                }
            }

        } catch (Exception e) {

        }

    }


    public  static String  getDescription(String url) {
        try {

            // first get the title
            // then get the headers
            //if not get the stuff
            Document  doc = Jsoup.connect(url).data("query", "Java")
            .userAgent("Mozilla")
            .cookie("auth", "token")
            .timeout(3000)
            .post();
            Elements links = doc.select("a[href]");

            String title = doc.title();

            title = title.replaceAll("\\p{Punct}+", "");
            StringBuilder strRead = new StringBuilder();


            strRead.append(title);

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


            String s = strRead.toString();
            //	System.out.println("the s is " + s );
            String description = s.substring(0,Math.min(s.length(),100));
            //System.out.println("the description length is " + description.length());
            return description;


        } catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }

    }
    public void fetchURL(String urlScanned) {
        //insert some links first

        HashSet<String> hash_url = new HashSet<String>();
        int flag = 0;

        int refUrl = urlID;
        try {

            int count = 0;
            int i  = 0;
            System.out.println("sadasdsadsa");
            System.out.println("the urlid is " + urlID);
            //crawl starts
            System.out.println("the max count is " + maxCount);
            String urltoVisit = urlScanned;

            while(count < maxCount) {
                //System.out.println("assdasd");
                //System.out.println("urltoVisit is " + urltoVisit);
                Document  doc = Jsoup.connect(urltoVisit).data("query", "Java")
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(3000)
                .post();
                Elements links = doc.select("a[href]");
                String description = getDescription(urltoVisit);
                String picture = getPicture(urltoVisit);
                tokenizeWebsite(urltoVisit);
                insertURLInDB(urltoVisit,description,picture);
                hash_url.add(urltoVisit);
                count++;

                for (Element link : links) {
                    String website = link.attr("abs:href");
                    if (!hash_url.contains(website) ) {
                        //still need to check whether its a valid html m8!
                        try {
                            if(count > maxCount) {
                                break;
                            }
                            Document  d = Jsoup.connect(website).data("query", "Java")
                            .userAgent("Mozilla")
                            .cookie("auth", "token")
                            .timeout(3000)
                            .post();

                            String desc = getDescription(website);
                            String pic = getPicture(website);

                            //inserting the words into the word table
                            tokenizeWebsite(website);
                            //inserting the link into the url table
                            insertURLInDB(website,desc,pic);
                            hash_url.add(website);


                            count++;

                        } catch (Exception e) {

                        }

                    } else {
                        //System.out.println("already have the ur3l inside the database" + g);
                    }

                }
                // bfs for that link is Done
                Statement st = connection.createStatement();
                refUrl++;

                String sql = ("SELECT url FROM urls WHERE urlid = " + String.valueOf(refUrl));

                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `urls` WHERE `urlid` = ?");
                stmt.setInt(1,refUrl);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String result = rs.getString("url");
                    //System.out.println("the result is " + result);
                    urltoVisit = result;
                    //System.out.println("the urltovisit  is " + urltoVisit);
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void fetchURL_with_domain(String urlScanned,String domain) {
        //insert some links first

        HashSet<String> hash_url = new HashSet<String>();
        int flag = 0;

        int refUrl = urlID;
        try {

            int count = 0;
            int i  = 0;
            System.out.println("sadasdsadsa");
            System.out.println("the urlid is " + urlID);
            //crawl starts
            System.out.println("the max count is " + maxCount);
            String urltoVisit = urlScanned;

            while(count < maxCount) {
                //System.out.println("assdasd");
                //System.out.println("urltoVisit is " + urltoVisit);
                if(urltoVisit.contains(domain)) {
                    Document  doc = Jsoup.connect(urltoVisit).data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .post();
                    Elements links = doc.select("a[href]");
                    String description = getDescription(urltoVisit);
                    String picture = getPicture(urltoVisit);
                    tokenizeWebsite(urltoVisit);
                    insertURLInDB(urltoVisit,description,picture);
                    hash_url.add(urltoVisit);
                    count++;


                    for (Element link : links) {
                        String website = link.attr("abs:href");
                        if (!hash_url.contains(website) && website.contains(domain)) {
                            //still need to check whether its a valid html m8!
                            try {
                                if(count > maxCount) {
                                    break;
                                }
                                Document  d = Jsoup.connect(website).data("query", "Java")
                                .userAgent("Mozilla")
                                .cookie("auth", "token")
                                .timeout(3000)
                                .post();

                                String desc = getDescription(website);
                                String pic = getPicture(website);

                                //inserting the words into the word table
                                tokenizeWebsite(website);
                                //inserting the link into the url table
                                insertURLInDB(website,desc,pic);
                                hash_url.add(website);


                                count++;

                            } catch (Exception e) {

                            }

                        } else {
                            //System.out.println("already have the ur3l inside the database" + g);
                        }

                    }
                }
                // bfs for that link is Done
                Statement st = connection.createStatement();
                refUrl++;

                String sql = ("SELECT url FROM urls WHERE urlid = " + String.valueOf(refUrl));

                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `urls` WHERE `urlid` = ?");
                stmt.setInt(1,refUrl);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String result = rs.getString("url");
                    //System.out.println("the result is " + result);
                    urltoVisit = result;
                    //System.out.println("the urltovisit  is " + urltoVisit);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public static String getPicture (String url ){
        try {
            Document  doc = Jsoup.connect(url).data("query", "Java")
            .userAgent("Mozilla")
            .cookie("auth", "token")
            .timeout(3000)
            .post();
            String text = doc.body().text();
            //    Element image = doc.select("img[src$=.jpg]").first();
            //img[src~=(?i)\.(png|jpe?g)]
            //Element image = doc.select("img[src~=(?i).(png|jpe?g)]").first();
            Element image = doc.select("img[src~=]").first();

            if(image != null) {
                String im = image.absUrl("src");

                if( im != null) {
                    //System.out.println("the image url is " + im);
                    return im;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        //return null;
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
            int max = Integer.parseInt(crawler.props.getProperty("crawler.maxurls"));
            String domain = crawler.props.getProperty("crawler.domain");
            maxCount = max - 1; // minus 1 because starts from zero
            System.out.println("the maax is " + maxCount);


            crawler.createDB();
            //System.out.println("the root is " + root);
            //crawler.fetchURL(root);
            if(domain == null) {
                crawler.fetchURL(root);
            } else {
                crawler.fetchURL_with_domain(root,domain);
            }

        }
        catch( Exception e) {
            e.printStackTrace();
        }
    }




}
