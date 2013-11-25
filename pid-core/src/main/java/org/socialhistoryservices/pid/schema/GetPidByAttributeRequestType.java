
package org.socialhistoryservices.pid.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 GetPidByAttributeRequestType (method GetPidByAttribute): a reverse lookup of a Pid using a bound
 *                 pidType.resolveUrl or pidType.localIdentifier as the attribute value. It is possible there are more than
 *                 one pid to include the lookup result.
 *             
 * 
 * <p>Java class for GetPidByAttributeRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetPidByAttributeRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="na" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attribute" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetPidByAttributeRequestType", namespace = "http://pid.socialhistoryservices.org/", propOrder = {
    "na",
    "attribute"
})
public class GetPidByAttributeRequestType {

    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected String na;
    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected String attribute;

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

    /**
     * Gets the value of the attribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Sets the value of the attribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute(String value) {
        this.attribute = value;
    }

}
