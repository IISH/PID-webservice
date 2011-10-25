
/*
 * The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
 *
 * Copyright (C) 2010-2011, International Institute of Social History
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.socialhistoryservices.pid.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 pidType : Used as a request and response parameter. It contains pid and resolve attributes.
 *             
 * 
 * <p>Java class for pidType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pidType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pid" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="resolveUrl" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="locAtt" type="{http://pid.socialhistoryservices.org/}locAttType"/>
 *         &lt;element name="localIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pidType", namespace = "http://pid.socialhistoryservices.org/", propOrder = {
    "pid",
    "resolveUrl",
    "locAtt",
    "localIdentifier"
})
public class PidType {

    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String pid;
    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String resolveUrl;
    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected LocAttType locAtt;
    @XmlElement(namespace = "http://pid.socialhistoryservices.org/", required = true)
    protected String localIdentifier;

    /**
     * Gets the value of the pid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPid() {
        return pid;
    }

    /**
     * Sets the value of the pid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPid(String value) {
        this.pid = value;
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

    /**
     * Gets the value of the locAtt property.
     * 
     * @return
     *     possible object is
     *     {@link LocAttType }
     *     
     */
    public LocAttType getLocAtt() {
        return locAtt;
    }

    /**
     * Sets the value of the locAtt property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocAttType }
     *     
     */
    public void setLocAtt(LocAttType value) {
        this.locAtt = value;
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

}
