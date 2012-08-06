/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.galleria.domain;


import java.util.List;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author atan
 */
@XmlRootElement
public class Status {
    
    @XmlElement(name="created_at") public String data;
    public String text;
    public User user;
    public String id;



    @XmlElement(name="entities") public List<TwitterEntity> twitterEntity;

    public List<TwitterEntity> getTwitterEntity() {
        return twitterEntity;
    }

    
    
    public String getData() {
        return data;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }
    
    public String getId() {
        return id;
    }
    
    
    
}
