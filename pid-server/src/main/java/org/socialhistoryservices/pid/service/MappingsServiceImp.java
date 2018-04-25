/*
 * The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
 *
 * Copyright (C) 2010-2012, International Institute of Social History
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

package org.socialhistoryservices.pid.service;

import org.apache.log4j.Logger;
import org.socialhistoryservices.pid.database.dao.HandleDaoImpl;
import org.socialhistoryservices.pid.database.dao.domain.Handle;
import org.socialhistoryservices.pid.rmi.MappingService;
import org.socialhistoryservices.pid.schema.LocAttType;
import org.socialhistoryservices.pid.schema.PidType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MappingsServiceImp implements MappingService {

    Jaxb2Marshaller marshaller;

    private Templates templates;

    private final Logger log = Logger.getLogger(getClass());

    public MappingsServiceImp() {
        try {
            templates = TransformerFactory.newInstance().newTemplates(
                    new StreamSource(MappingsServiceImp.class.getResourceAsStream("/locations.xsl"))
            );
        } catch (TransformerConfigurationException e) {
            log.fatal(e);
            System.exit(-1);
        }
    }

    public PidType convertHandleToPidType(List<Handle> handles) {

        return (handles.size() == 0)
                ? null
                : convertHandlesToPidType(handles).get(0);
    }

    /**
     * A handle in the Handle System logic is a single identifier with a type.
     * As there can be several types ( URL, LID, 10320/loc ) these are added to the
     * PidType as one entity.
     * <p/>
     * In a reverse lookup, there may be several PidTypes also. Here we group a
     * PidType by pid.
     *
     * @param handles The Pid records from the database
     * @return The PID type records for the endpoint
     */
    public List<PidType> convertHandlesToPidType(List<Handle> handles) {

        final List<PidType> pids = new ArrayList(handles.size());
        for (Handle handle : handles) {

            PidType pid = getHandle(handle.getHandle(), pids);
            String type = handle.getTypeAsString();
            if (type.equals(HandleDaoImpl.URL)) {
                pid.setResolveUrl(handle.getDataAsString());
            } else if (type.equals(HandleDaoImpl.LOC)) {
                pid.setLocAtt(getLocations(handle));
            } else if (type.equals(HandleDaoImpl.LID))
                pid.setLocalIdentifier(handle.getDataAsString());
        }

        return pids;
    }


    public LocAttType getLocations(Handle handle) {

        final StreamSource xmlSource = new StreamSource(new ByteArrayInputStream(handle.getData()));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final Result result = new StreamResult(os);

        try {
            templates.newTransformer().transform(xmlSource, result);
        } catch (TransformerException e) {
            log.error("Handle: " + handle.getHandle());
            log.error(e);
            return null;
        }

        final StreamSource source = new StreamSource(new ByteArrayInputStream(os.toByteArray()));
        final JAXBElement element = (JAXBElement) marshaller.unmarshal(source);
        return (LocAttType) element.getValue();
    }

    private PidType getHandle(String pid, List<PidType> handles) {
        for (PidType pidType : handles) {
            if (pidType.getPid().equals(pid))
                return pidType;
        }
        PidType handle = new PidType();
        handle.setPid(pid);
        handles.add(handle);
        return handle;
    }

    public void setMarshaller(Jaxb2Marshaller marshaller) {
        this.marshaller = marshaller;
    }
}
