/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

/**
 *
 * @author User
 */
public class Argument {
    public String type;
    public String whatFind;
    public String value;

    public Argument(String type, String whatFind, String value) {
        this.type = type;
        this.value = value;
        this.whatFind = whatFind;
    }
    
}
