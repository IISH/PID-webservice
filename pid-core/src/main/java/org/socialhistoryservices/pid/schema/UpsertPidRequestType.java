
package org.socialhistoryservices.pid.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 UpsertPidRequestType (method UpsertPidRequest): inserts a Pid and binds it to the supplied resolve
 *                 attributes as defined in the pidType IF it does not exist. If the pid or lid
 *                 already persist in the database, the method will update the pid data.
 *             
 * 
 * <p>Java class for UpsertPidRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpsertPidRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="na" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="handle" type="{http://pid.socialhistoryservices.org/}pidType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpsertPidRequestType", namespace = "http://pid.socialhistoryservices.org/", propOrder = {
    "na",
    "handle"
})
public class UpsertPidRequestType {

    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected String na;
    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected PidType handle;

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
     * Gets the value of the handle property.
     * 
     * @return
     *     possible object is
     *     {@link PidType }
     *     
     */
    public PidType getHandle() {
        return handle;
    }

    /**
     * Sets the value of the handle property.
     * 
     * @param value
     *     allowed object is
     *     {@link PidType }
     *     
     */
    public void setHandle(PidType value) {
        this.handle = value;
    }

}
