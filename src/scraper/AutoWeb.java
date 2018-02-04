
package scraper;

import io.webfolder.ui4j.api.browser.BrowserEngine;
import io.webfolder.ui4j.api.browser.BrowserFactory;
import io.webfolder.ui4j.api.browser.Page;
import io.webfolder.ui4j.api.dom.Element;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;


public class AutoWeb {
    static BrowserEngine webKit = BrowserFactory.getWebKit();
    Page pageKit;
    public String URL;
    List<Element> parsedhtml;
    
    AutoWeb(String URL) throws InterruptedException, FileNotFoundException, UnsupportedEncodingException{
        this.URL = URL;
        algorithm();
    }
    
    
    public void algorithm () throws InterruptedException, FileNotFoundException, UnsupportedEncodingException{
        pageKit = webKit.navigate(this.URL);
        Thread.sleep(3000);
        pageKit.show();
        while(!((String)pageKit.executeScript("document.readyState")).equals("complete"));
        parsedhtml = parseHtml();
        tapSearch();
        scrollToBottom();
        Thread.sleep(3000);
        saveInFile();
        for (int i = 2; ; i++) {
            parsedhtml = parseHtml();
            if(!nextPage(i)) break;
            saveInFile();
            Thread.sleep(1000);
        }
        
        close();
    }
    
    
    public void scrollToBottom() throws InterruptedException{
        int currentHeight = 0, previousHeight = -1;
        while(previousHeight != currentHeight) {
            previousHeight = currentHeight;
            currentHeight = pageKit.executeScript("document.body.scrollHeight").hashCode();
            pageKit.executeScript("scrollBy(0,"+currentHeight+")");
            Thread.sleep(1000);
        }        
    }
    
    
    public List<Element> parseHtml(){
        return pageKit.getDocument().queryAll("*");
    }
    
    
    public boolean tapSearch(){
        boolean isSearcheble = false;
        for(int i = 0; i < parsedhtml.size(); i++) {
            if(parsedhtml.get(i).getText().toString().toLowerCase().contains("поиск") || parsedhtml.get(i).getText().toString().toLowerCase().contains("найти")
                    || parsedhtml.get(i).getText().toString().toLowerCase().contains("подобрать")) {
                parsedhtml.get(i).click();
                if(!((String)pageKit.executeScript("document.readyState")).equals("complete")) {isSearcheble = true; break;}
            }
        }
        while(!((String)pageKit.executeScript("document.readyState")).equals("complete"));
        return isSearcheble;
    }
    
    public boolean nextPage (int numPage){
        boolean isNext = false;
        int i = 0;
        for( ; i < parsedhtml.size(); i++) {
            if((parsedhtml.get(i).getText().toString().equals("Optional["+numPage+"]") || parsedhtml.get(i).getText().toString().equals("Optional[Следующая]"))
                    && parsedhtml.get(i).getTagName().toString().equals("a")) {
                parsedhtml.get(i).click();
                isNext = true;
                break;
            }
        }
        return isNext;  
    }
    
    public void saveInFile() throws FileNotFoundException, UnsupportedEncodingException{
        File input = new File("C:\\GameinXML\\data2.html");
        PrintWriter writer = new PrintWriter(input,"UTF-8");
        writer.write(pageKit.getDocument().getBody().toString());
    }
    
    public void close(){
        pageKit.close();
        webKit.shutdown();        
    }
}