
package org.socialhistoryservices.pid.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * UpdatePidRequestType (method UpdatePidRequest):
 *                 re-binds the pidType.pid to the supplied pidType.resolveUrl, pidType.locAtt and pidType.localIdentifier.
 *                 If the pidType.resolveUrl or pidType.locAtt are not supplied they will be un-bound\removed from the pid
 *                 (if there). A pidType.localIdentifier cannot be removed, only changed or added.
 *             
 * 
 * <p>Java class for UpdatePidRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UpdatePidRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
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
@XmlType(name = "UpdatePidRequestType", namespace = "http://pid.socialhistoryservices.org/", propOrder = {
    "handle"
})
public class UpdatePidRequestType {

    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected PidType handle;

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
