
package scraper;

import io.webfolder.ui4j.api.browser.BrowserEngine;
import io.webfolder.ui4j.api.browser.BrowserFactory;
import io.webfolder.ui4j.api.browser.Page;
import io.webfolder.ui4j.api.dom.Element;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class AutoWeb {
    static BrowserEngine webKit = BrowserFactory.getWebKit();
    Page pageKit;
    public String URL;
    List<Element> parsedhtml;
    List<Element> parsedMain;
    Element contryElem;
    List<Element> hrefsonMainPage;
    List<Element> hrefs;
    AutoWeb(String URL) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException{
        this.URL = URL;
       
        algorithm();
    }
    
    public void algorithm () throws InterruptedException, FileNotFoundException, UnsupportedEncodingException{
        pageKit = webKit.navigate(this.URL);
        pageKit.show();
        while(!((String)pageKit.executeScript("document.readyState")).equals("complete"));
        parsedhtml = parseHtml();
        parsedMain = parseHtml();
       // System.err.println(parsedhtml);
        hrefsonMainPage = pageKit.getDocument().queryAll("a");
        //parsedMainPage = parsedhtml;
        //findSearchform().setValue("Россия");
        tapSearch();
        Thread.sleep(2000);
        //scrollToBottom();
        hrefs = pageKit.getDocument().queryAll("a");
        System.err.println(hrefs);
        ///saveInFile(parsedhtml);
        for (int i = 2; ; i++) {
            parsedhtml = parseHtml();
            loadHotels();
            hrefs = pageKit.getDocument().queryAll("a");
            hrefs.removeAll(hrefsonMainPage);
            if(!nextPage(i)) break;
            Thread.sleep(2000);
        }
        
        //close();
    }
    
    
    public void scrollToBottom() throws InterruptedException{
        int currentHeight = 0, previousHeight = -1;
        while(previousHeight != currentHeight) {
            previousHeight = currentHeight;
            currentHeight = pageKit.executeScript("document.body.scrollHeight").hashCode();
            pageKit.executeScript("scrollBy(0,"+currentHeight+")");
            Thread.sleep(1000);
            System.err.println("ok");
        }        
    }
    
    
    public List<Element> parseHtml(){
        return pageKit.getDocument().queryAll("*");
    }
    
    
    public Element findSearchform(){
        List<Element> inputslist = pageKit.getDocument().queryAll("input");
        for(int i = 0; i < inputslist.size(); i++) {
            if(inputslist.get(i)!= null && inputslist.get(i).getAttribute("class") != null) {
                String classString = inputslist.get(i).getAttribute("class").toString().toLowerCase();
            
                if(classString.contains("country")) {
                    return inputslist.get(i);
                }
            }
        }
        return null;
    }
    
    
    public boolean tapSearch(){
boolean isSearcheble = false;
        for(int i = 0; i < parsedhtml.size(); i++) {
            String content = parsedhtml.get(i).getInnerHTML().toLowerCase();
            if(parsedhtml.get(i).getChildren().isEmpty() && (content.contains("поиск") || content.contains("найти")
                    || content.contains("подобрать"))) {
                parsedhtml.get(i).click();
                System.err.println(parsedhtml.get(i).getInnerHTML());
                System.err.println(parsedhtml.get(i).getValue());
                if(!((String)pageKit.executeScript("document.readyState")).equals("complete")) {isSearcheble = true; break;}
                
            }
        }
        while(!((String)pageKit.executeScript("document.readyState")).equals("complete"));
        return isSearcheble;
    }
    
    public boolean nextPage (int numPage) throws InterruptedException{
       boolean isNext = false;
        int i = 0;
        for( ; i < hrefs.size(); i++) {

            if((hrefs.get(i).getText().toString().equals("Optional["+numPage+"]") || hrefs.get(i).getText().orElse("").toLowerCase().equals("следующая"))) {
                System.out.println(hrefs.get(i).getAttribute("href").orElse(""));
                System.err.println(numPage);
                hrefs.get(i).click();
                Thread.sleep(1000);
                isNext = true;
                i = hrefs.size();
            }
        }
        return isNext;  
    }
    
    public void saveInFile(List<Element> list) throws FileNotFoundException, UnsupportedEncodingException{
        File input = new File("C:\\GameinXML\\data2.html");
        PrintWriter writer = new PrintWriter(input,"UTF-8");
        if(list != null)
            writer.write(pageKit.getDocument().queryAll("*").toString());
        writer.flush();
        writer.close();
    }
    
    public void loadHotels() throws FileNotFoundException, UnsupportedEncodingException, InterruptedException{
        String previous = "";
        for(int i = 0; i < hrefs.size(); i++){
            if(((hrefs.get(i).getAttribute("class") != null && hrefs.get(i).getAttribute("class").toString().toLowerCase().contains("hotel")) ||
                    (hrefs.get(i).getAttribute("name") != null && hrefs.get(i).getAttribute("name").toString().toLowerCase().contains("hotel")) ||
                        (hrefs.get(i).getAttribute("id") != null && hrefs.get(i).getAttribute("id").toString().toLowerCase().contains("hotel")) ||
                            (hrefs.get(i).getAttribute("href") != null && hrefs.get(i).getAttribute("id").toString().toLowerCase().contains("hotel")) ||
                                (hrefs.get(i).getInnerHTML() != null && hrefs.get(i).getInnerHTML().toString().toLowerCase().contains("hotel")) ||
                                    (hrefs.get(i).getInnerHTML() != null && hrefs.get(i).getInnerHTML().toString().toLowerCase().contains("отель")))
                    &&(hrefs.get(i).getAttribute("href") == null || (!hrefs.get(i).getAttribute("href").orElse("").equals(previous) &&
                    !hrefs.get(i).getAttribute("href").orElse("").split("\\?")[0].contains("search") && (hrefs.get(i).getAttribute("href").orElse("").contains(URL)
                    || !hrefs.get(i).getAttribute("href").orElse("").contains("www"))))
                    //&& !hrefs.get(i).getAttribute("href").toString().toLowerCase().contains("/xf.,vx.search") 
                    /*  &&
                    !((hrefs.get(i).getAttribute("class") == null || !hrefs.get(i).getAttribute("class").toString().toLowerCase().contains("search")) &&
                    (hrefs.get(i).getAttribute("name") == null || !hrefs.get(i).getAttribute("name").toString().toLowerCase().contains("search")) &&
                        (hrefs.get(i).getAttribute("id") == null || !hrefs.get(i).getAttribute("id").toString().toLowerCase().contains("search")) &&
                            (hrefs.get(i).getInnerHTML() == null || !hrefs.get(i).getInnerHTML().toString().toLowerCase().contains("search")) &&
                                (hrefs.get(i).getInnerHTML() == null || !hrefs.get(i).getInnerHTML().toString().toLowerCase().contains("Найти")))*/
                    
                    )
                    {
                        previous = hrefs.get(i).getAttribute("href").orElse("");
                        //System.out.println(URL+previous);
                        System.out.println(hrefs.get(i).getAttribute("href").orElse("").split("\\?")[0]);
                        hrefs.get(i).click();
                        Thread.sleep(2000);
                        saveOutput(hrefs.get(i).getAttribute("href").orElse(""));
                        pageKit.executeScript("window.history.go(-1)");
                        Thread.sleep(2000);
                        webKit.clearCookies();
                        hrefs = pageKit.getDocument().queryAll("a");
                        hrefs.removeAll(hrefsonMainPage);
                        
                        
                        
                    }
        }
    }
    
    public void saveOutput(String result) throws FileNotFoundException, UnsupportedEncodingException{
        new File("\\outputHtmls\\"+URL).mkdirs();
        System.err.println("dsd");
        File input = new File("outputHtmls/"+result.hashCode()+".html");
        PrintWriter writer = new PrintWriter(input,"UTF-8");
        writer.write(pageKit.getDocument().getBody().toString());
        writer.flush();
        writer.close();
    }

    
    public void close(){
        pageKit.close();
        webKit.shutdown();        
    }
}