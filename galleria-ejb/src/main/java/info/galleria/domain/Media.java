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
public class Media {
    
    
    @XmlElement(name="creative") public CreativeStart creativeStart;

    public CreativeStart getCreativeStart() {
        return creativeStart;
    }
    
      public static class CreativeStart {
          @XmlElement(name="media_url") public String mediaUrl;

          public String getMediaUrl() {
             return mediaUrl;
          }
       
      }
    
    
}
