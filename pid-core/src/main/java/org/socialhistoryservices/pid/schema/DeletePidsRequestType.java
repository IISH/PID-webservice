
package org.socialhistoryservices.pid.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * na : the naming authority used for testing. This method will only accept naming authority
 *                 prefixes used for testing. This method will remove all PIDs bound to the NA.
 *             
 * 
 * <p>Java class for DeletePidsRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeletePidsRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="na" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeletePidsRequestType", namespace = "http://pid.socialhistoryservices.org/", propOrder = {
    "na"
})
public class DeletePidsRequestType {

    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected String na;

    /**
     * Gets the value of the na property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNa() {
        return na;
    }

    /**
     * Sets the value of the na property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNa(String value) {
        this.na = value;
    }

}
