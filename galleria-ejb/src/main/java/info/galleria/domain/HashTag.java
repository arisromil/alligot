/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.galleria.domain;


import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author atan
 */
public class HashTag {
    @XmlElement(name="hashtag") public HashTagText tag;

    public HashTagText getTag() {
        return tag;
    }
    

         public static class HashTagText {
            @XmlElement(name="text") public String text;

            public String getText() {
                return text;
            }
       
        }    
    
    
}
