/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.galleria.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author atan
 */
@XmlRootElement
public class TwitterEntity {
    
    @XmlElement(name="hashtags") public List<HashTag> hashtags;
    
    @XmlElement(name="media") public Media media;

    public Media getMedia() {
        return media;
    }
    

    public List<HashTag> getHashtags() {
        return hashtags;
    }
    
    
    
}
