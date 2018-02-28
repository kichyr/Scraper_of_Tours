package scraper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public class Main {

    public static void main(String[] argv) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException, TransformerException {
        String currentUrl = "https://pegast.ru/";
        //AnaliseMainPage scrap = new AnaliseMainPage(currentUrl);
        
        ArrayList<Argument> args = new ArrayList<Argument>();
        args.add(new Argument("name", "HotelName", "Moscow"));
        //MakeRequest rec = new MakeRequest(currentUrl);
        //rec.MakeRequest(args);
        
        AutoWeb a = new AutoWeb(currentUrl);
        //System.out.println(rec.RequestGet());
    }
    
}
