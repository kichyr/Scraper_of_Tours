package scraper;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import io.webfolder.ui4j.api.browser.BrowserEngine;
import io.webfolder.ui4j.api.browser.BrowserFactory;
import io.webfolder.ui4j.api.browser.Page;
import io.webfolder.ui4j.api.dom.Element;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.http.HttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import static scraper.AutoWeb.webKit;


/**
 *
 * @author User
 */
public class AnaliseMainPage {
        static BrowserEngine webKit = BrowserFactory.getWebKit();
    Page pageKit;
    public String URL;
    List<Element> parsedhtml;
    
    AnaliseMainPage(String URL) throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException, InterruptedException{
        this.URL = URL;
        algorithm();
    }
    
    
    public void algorithm () throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException, InterruptedException{
        pageKit = webKit.navigate(this.URL);
                pageKit.show();
        Thread.sleep(5000);
        parsedhtml = getAllForms();
        analizeForm();
        saveInFile();
        
        close();
    }
    
    
    public List<Element> getAllForms(){
        return pageKit.getDocument().queryAll("form");
    }
    
    
    public Element analizeForm() throws SAXException, IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException{
        File xmlFile = new File("info.xml");
        
        DocumentBuilderFactory d = new DocumentBuilderFactoryImpl();
        DocumentBuilder documentBilder  = d.newDocumentBuilder();
        
        Document document =  documentBilder.newDocument();
        
        org.w3c.dom.Element element = document.createElement("FormList");
        document.appendChild(element);
        
        
        for(int i = 0 ; i < parsedhtml.size(); i++) {

            
            List<Element> children= parsedhtml.get(i).queryAll("input");
            children.addAll(parsedhtml.get(i).queryAll("select"));
            
            if(!children.isEmpty()) {
                org.w3c.dom.Element form = document.createElement("form");
                form.setAttribute("formName", parsedhtml.get(i).getAttribute("name").orElse(""));
                form.setAttribute("method", parsedhtml.get(i).getAttribute("method").orElse(""));
                form.setAttribute("action", parsedhtml.get(i).getAttribute("action").orElse(""));
                form.setAttribute("id", parsedhtml.get(i).getAttribute("id").orElse(""));
                
                element.appendChild(form);

                
                for(int j = 0; j < children.size(); j++) {
                    
                    org.w3c.dom.Element childeOfForm = document.createElement(children.get(j).getTagName());
                    childeOfForm.setAttribute("name", children.get(j).getAttribute("name").orElse(""));
                    childeOfForm.setAttribute("method",children.get(j).getAttribute("method").orElse(""));
                    childeOfForm.setAttribute("action", children.get(j).getAttribute("action").orElse(""));
                    childeOfForm.setAttribute("id", children.get(j).getAttribute("id") .orElse(""));
                    form.appendChild(childeOfForm);

                    
                }
            }
        }
        
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source =  new DOMSource(document);

        StreamResult result = new StreamResult(new File("forms.xml"));
        
        transformer.transform(source, result);
            
        return null;
    }
    
    public void saveInFile() throws FileNotFoundException, UnsupportedEncodingException {
        File input = new File("htmlMainPage.html");
        PrintWriter writer = new PrintWriter(input,"UTF-8");
        writer.write(pageKit.getDocument().getBody().toString());
        writer.flush();
    }
    
    
   public void doGet(){
       CloseableHttpClient client = HttpClients.createDefault();
     //  CloseableHttpClient responce = client.execute()
               
       
   }
    
    public void close(){
        pageKit.close();
        webKit.shutdown();      
    }
}
