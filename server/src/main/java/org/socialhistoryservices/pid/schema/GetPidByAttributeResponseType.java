
package org.socialhistoryservices.pid.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetPidByAttributeResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPidByAttributeResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="handle" type="{http://pid.socialhistoryservices.org/}pidType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPidByAttributeResponseType", namespace = "http://pid.socialhistoryservices.org/", propOrder = {
    "handle"
})
public class GetPidByAttributeResponseType {

    @XmlElement(namespace = "http://pid.socialhistoryservices.org/")
    protected List<PidType> handle;

    /**
     * Gets the value of the handle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the handle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHandle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PidType }
     * 
     * 
     */
    public List<PidType> getHandle() {
        if (handle == null) {
            handle = new ArrayList<PidType>();
        }
        return this.handle;
    }

}
