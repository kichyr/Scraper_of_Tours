package scraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jsoup.helper.HttpConnection;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MakeRequest {
    public String autoUrl;
    Map<String,String> sendingArgs;
    
    public MakeRequest(String url) {
        autoUrl = url;
        sendingArgs = new LinkedHashMap<String,String>();
    }
    
    public void MakeRequest(ArrayList<Argument> args) throws SAXException, IOException, ParserConfigurationException{
        File xmlFile = new File("forms.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlFile);
        
        NodeList listElems = document.getElementsByTagName("*");
        
        for( int j = 0; j < args.size(); j++){
            for (int i = 0 ; i < listElems.getLength(); i++) {
                if(listElems.item(i).getAttributes().getNamedItem(args.get(j).type) != null &&
                        listElems.item(i).getAttributes().getNamedItem(args.get(j).type).toString().contains(args.get(j).whatFind)){
                    
                    System.err.println(listElems.item(i).getAttributes().getNamedItem(args.get(j).type));
                    if(listElems.item(i).getParentNode().getAttributes().getNamedItem("method") == null || 
                            (listElems.item(i).getParentNode().getAttributes().getNamedItem("method") != null))
                        
                        //printf
                        System.out.println(autoUrl + listElems.item(i).getParentNode().getAttributes().getNamedItem("action").getNodeValue()+ "?" 
                                +listElems.item(i).getAttributes().getNamedItem(args.get(j).type).getNodeValue()+"=" + args.get(j).value);
                        //
                        writeInFile(RequestGet(autoUrl + listElems.item(i).getParentNode().getAttributes().getNamedItem("action").getNodeValue()+ "?" 
                                +listElems.item(i).getAttributes().getNamedItem(args.get(j).type).getNodeValue()+"=" + args.get(j).value));
                        
                }
        }
        }
    }
    
    
    public String RequestGet(String stringURL) {
      URL url;
      HttpURLConnection conn;
      BufferedReader rd;
      String line;
      String result = "";
      try {
         url = new URL(stringURL);
         conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         while ((line = rd.readLine()) != null) {
            result += line;
         }
         rd.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return result;
    }
    
    public String RequestPost(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
		//con.setRequestProperty("User-Agent", USER_AGENT);

		// For POST only - START
		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		//os.write(POST_PARAMS.getBytes());
		os.flush();
		os.close();
		// For POST only - END

		int responseCode = con.getResponseCode();
		System.out.println("POST Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// print result
			System.out.println(response.toString());
		} else {
			System.out.println("POST request not worked");
		}
                return null;
	}
    
    public void writeInFile(String result) throws FileNotFoundException, UnsupportedEncodingException{
        File input = new File("respounce.html");
        PrintWriter writer = new PrintWriter(input,"UTF-8");
        writer.write(result);
        writer.flush();
    }
    }

