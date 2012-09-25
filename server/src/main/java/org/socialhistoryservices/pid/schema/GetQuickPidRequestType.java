
package org.socialhistoryservices.pid.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * GetQuickPidRequestType ( method: GetQuickPidRequest ): use for creation, binding and
 *                 reverse lookup of a Pid. Specifically:
 *                 1. Pid creation: when the localIdentifier is not bound to a known pid, the webservice creates a pid and
 *                 then binds it to the resolveUrl and localIdentifier.
 *                 2. Pid lookup: when the localIdentifier is bound to an existing Pid, the method will echo back all data
 *                 bound to the pid.
 *                 3. Pid update: when the localIdentifier is bound to an existing Pid and the supplied resolveUrl is
 *                 different to the bound resolveUrl, a rebind will be made.
 *             
 * 
 * <p>Java class for GetQuickPidRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetQuickPidRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="na" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="localIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resolveUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetQuickPidRequestType", namespace = "http://pid.socialhistoryservices.org/", propOrder = {
    "na",
    "localIdentifier",
    "resolveUrl"
})
public class GetQuickPidRequestType {

    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected String na;
    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected String localIdentifier;
    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String resolveUrl;

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
     * Gets the value of the localIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalIdentifier() {
        return localIdentifier;
    }

    /**
     * Sets the value of the localIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalIdentifier(String value) {
        this.localIdentifier = value;
    }

    /**
     * Gets the value of the resolveUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResolveUrl() {
        return resolveUrl;
    }

    /**
     * Sets the value of the resolveUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResolveUrl(String value) {
        this.resolveUrl = value;
    }

}
