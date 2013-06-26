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

package org.socialhistoryservices.pid.ws;

import net.handle.hdllib.HandleException;
import org.socialhistoryservices.pid.schema.*;
import org.socialhistoryservices.pid.service.PidResourceService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.List;

/**
 * @author Lucien van Wouw <lwo@iisg.nl>
 * @since 2011-01-01
 */

@Endpoint("/secure")
public class PidServiceEndPoint {

    public static final String namespace = "http://pid.socialhistoryservices.org/";

    private ObjectFactory objectFactory;
    private PidResourceService pidResourceService;

    @PayloadRoot(localPart = "UpsertPidRequest", namespace = namespace)
    @ResponsePayload
    public JAXBElement<UpsertPidResponseType> upsertPid(@RequestPayload JAXBElement<CreatePidRequestType> requestElement) throws IOException, HandleException {
        final String na = normalize(requestElement.getValue().getNa(), true);
        final PidType handle = normalize(requestElement.getValue().getHandle());
        final PidType pidType = pidResourceService.upsertPid(na, normalize(handle));
        final UpsertPidResponseType response = objectFactory.createUpsertPidResponseType();
        response.setHandle(pidType);
        return objectFactory.createUpsertPidResponse(response);
    }

    @PayloadRoot(localPart = "CreatePidRequest", namespace = namespace)
    @ResponsePayload
    public JAXBElement<CreatePidResponseType> createPid(@RequestPayload JAXBElement<CreatePidRequestType> requestElement) throws IOException, HandleException {
        final String na = normalize(requestElement.getValue().getNa(), true);
        final PidType handle = normalize(requestElement.getValue().getHandle());
        final PidType pidType = pidResourceService.createPid(na, normalize(handle));
        final CreatePidResponseType response = objectFactory.createCreatePidResponseType();
        response.setHandle(pidType);
        return objectFactory.createCreatePidResponse(response);
    }

    @PayloadRoot(localPart = "UpdatePidRequest", namespace = namespace)
    @ResponsePayload
    public JAXBElement<UpdatePidResponseType> updatePid(@RequestPayload JAXBElement<UpdatePidRequestType> requestElement) throws IOException, HandleException {
        final PidType handle = normalize(requestElement.getValue().getHandle());
        final PidType pidType = pidResourceService.updatePid(handle);
        final UpdatePidResponseType response = objectFactory.createUpdatePidResponseType();
        response.setHandle(pidType);
        return objectFactory.createUpdatePidResponse(response);
    }

    @PayloadRoot(localPart = "GetPidRequest", namespace = namespace)
    @ResponsePayload
    public JAXBElement<GetPidResponseType> getPid(@RequestPayload JAXBElement<GetPidRequestType> requestElement) throws IOException, HandleException {
        final String pid = normalize(requestElement.getValue().getPid(), true);
        final PidType handle = pidResourceService.getPid(pid);
        final GetPidResponseType response = objectFactory.createGetPidResponseType();
        response.setHandle(handle);
        return objectFactory.createGetPidResponse(response);
    }

    @PayloadRoot(localPart = "GetPidByAttributeRequest", namespace = namespace)
    @ResponsePayload
    public JAXBElement<GetPidByAttributeResponseType> getPidByAttribute(@RequestPayload JAXBElement<GetPidByAttributeRequestType> requestElement) throws IOException {
        final String na = normalize(requestElement.getValue().getNa(), true);
        final String attribute = normalize(requestElement.getValue().getAttribute(), true);
        final GetPidByAttributeResponseType response = objectFactory.createGetPidByAttributeResponseType();
        pidResourceService.getPidByAttribute(response.getHandle(), na, attribute);
        return objectFactory.createGetPidByAttributeResponse(response);
    }


    @PayloadRoot(localPart = "GetQuickPidRequest", namespace = namespace)
    @ResponsePayload
    public JAXBElement<GetQuickPidResponseType> quickPid(@RequestPayload JAXBElement<GetQuickPidRequestType> requestElement) throws IOException, HandleException {
        final String na = normalize(requestElement.getValue().getNa(), true);
        final String lid = normalize(requestElement.getValue().getLocalIdentifier(), true);
        final String resolveUrl = normalize(requestElement.getValue().getResolveUrl(), false);
        final PidType handle = pidResourceService.createHopePid(na, lid, resolveUrl);
        final GetQuickPidResponseType response = objectFactory.createGetQuickPidResponseType();
        response.setHandle(handle);
        return objectFactory.createGetQuickPidResponse(response);
    }

    @PayloadRoot(localPart = "DeletePidRequest", namespace = namespace)
    @ResponsePayload
    public JAXBElement<DeletePidResponseType> deletePid(@RequestPayload JAXBElement<DeletePidRequestType> requestElement) {
        final String pid = normalize(requestElement.getValue().getPid(), true);
        final boolean deleted = pidResourceService.deletePid(pid);
        final DeletePidResponseType response = objectFactory.createDeletePidResponseType();
        response.setDeleted(deleted);
        return objectFactory.createDeletePidResponse(response);
    }

    @PayloadRoot(localPart = "DeletePidsRequest", namespace = namespace)
    @ResponsePayload
    public JAXBElement<DeletePidsResponseType> deletePids(@RequestPayload JAXBElement<DeletePidsRequestType> requestElement) {
        final String na = normalize(requestElement.getValue().getNa(), true);
        final long count = pidResourceService.deletePids(na);
        final DeletePidsResponseType response = objectFactory.createDeletePidsResponseType();
        response.setCount(count);
        return objectFactory.createDeletePidsResponse(response);
    }

    /**
     * normalize
     * <p/>
     * Empty values will be replaced by null values.
     * otherAttributes that have the same key values as 'href' will set set. This is a legacy issue.
     *
     * @param pidType
     * @return
     */
    private PidType normalize(PidType pidType) {

        final PidType handle = new PidType();
        if (pidType.getLocAtt() != null) {
            List<LocationType> locations = pidType.getLocAtt().getLocation();
            if (!locations.isEmpty()) {
                for (int i = locations.size() - 1; i != -1; i--) {
                    LocationType location = locations.get(i);
                    if (location.getHref() == null) {
                        final QName qName = new QName("href");
                        String href = location.getOtherAttributes().get(qName);
                        if (href == null || href.isEmpty()) {
                            locations.remove(i);
                        } else
                            location.getOtherAttributes().remove(qName);
                        location.setHref(href);
                    } else {
                        if (location.getHref().isEmpty()) locations.remove(i);
                    }
                }
                if (!locations.isEmpty())
                    handle.setLocAtt(pidType.getLocAtt());
            }
        }
        handle.setLocalIdentifier(normalize(pidType.getLocalIdentifier(), true));
        handle.setPid(normalize(pidType.getPid(), true));
        handle.setResolveUrl(pidType.getResolveUrl());
        return handle;
    }

    /**
     * Normalization: empty or null values, are returned as NULL.
     * In addition, we follow the Handle System protocol at http://www.rfc-ref.org/RFC-TEXTS/3652/chapter2.html
     * where we manage case insensitive handles. We uppercase all in the database.
     *
     * @param text       The parameter
     * @param ignoreCase Uppercase or not the text
     * @return A normalized bit of text
     */
    private String normalize(String text, boolean ignoreCase) {

        if (text == null || text.trim().isEmpty())
            return null;
        return (ignoreCase)
                ? text.trim().toUpperCase()
                : text.trim();
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public void setPidResourceService(PidResourceService pidResourceService) {
        this.pidResourceService = pidResourceService;
    }
}