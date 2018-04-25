/*
 * The PID webservice offers SOAP methods to manage the Handle System(r) resolution technology.
 *
 * Copyright (C) 2010-2018, International Institute of Social History
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

import net.handle.hdllib.HandleException;
import org.socialhistoryservices.pid.rmi.HandleDao;
import org.socialhistoryservices.pid.database.dao.HandleDaoImpl;
import org.socialhistoryservices.pid.database.dao.domain.Handle;
import org.socialhistoryservices.pid.exceptions.PidException;
import org.socialhistoryservices.pid.schema.PidType;
import org.socialhistoryservices.pid.security.NAAuthentication;
import org.socialhistoryservices.pid.util.PidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lucien van Wouw <lwo@iisg.nl>
 * @since 2011-01-01
 */
public class StubPidResourceService implements PidResourceService {

    @Value("#{pidProperties['handle.baseUrl']}")
    private String handleBaseUrl = "";

    @Autowired
    private HandleDao handleDao;

    @Autowired
    private MappingsServiceImp mappingsService;

    private NAAuthentication NAAuthentication;

    final private static byte[] bURL = HandleDaoImpl.URL.getBytes();

    @Override
    public PidType upsertPid(String na, PidType pidType) throws HandleException {

        NAAuthentication.authorize(na);
        String lidCandidate = pidType.getLocalIdentifier();
        if (lidCandidate != null) {
            final List<Handle> h = handleDao.fetchHandleByAttribute(na, lidCandidate, "LID");
            if (h.size() == 0) {
                // As a PID is synonymous to a LID, we allow new LIDs to bind to PIDs
            } else {
                final String pid = h.get(0).getHandle();
                if (pidType.getPid() == null) {
                    pidType.setPid(pid);
                } else if (!pid.equalsIgnoreCase(pidType.getPid())) {
                    throw new PidException("Cannot accept local identifier " + lidCandidate + " because it already is bound to PID " + pid);
                }
            }
        }
        setDefault(na, pidType);
        return mappingsService.convertHandleToPidType(handleDao.upsertHandle(na, pidType));
    }

    @Override
    public PidType createPid(String na, PidType pidType) throws HandleException {

        NAAuthentication.authorize(na);
        setDefault(na, pidType);
        String lidCandidate = pidType.getLocalIdentifier();
        if (lidCandidate != null) {
            final List<Handle> handles = handleDao.fetchHandleByAttribute(na, lidCandidate, "LID");
            if (handles.size() != 0)
                throw new PidException("Cannot create local identifier " + lidCandidate + " because it already is bound to PID " + handles.get(0).getHandle());
        }
        final List<Handle> handles = handleDao.createNewHandle(na, pidType);
        return mappingsService.convertHandleToPidType(handles);
    }

    private void setDefault(String na, PidType pidType) {
        if (pidType.getResolveUrl() == null)
            pidType.setResolveUrl(handleBaseUrl);

        if (pidType.getPid() == null) {
            pidType.setPid(PidGenerator.getPid(na)); // No need to check for existing PIDs. Too small a change.
        } else {
            String pidCandidate = PidGenerator.validatePid(na, pidType.getPid());
            pidType.setPid(pidCandidate);
        }
    }

    @Override
    public PidType updatePid(PidType handle) throws HandleException {

        String na = NAAuthentication.authorize(handle.getPid());
        String lidCandidate = handle.getLocalIdentifier();
        checkAvailability(handle.getPid(), na, lidCandidate);
        return mappingsService.convertHandleToPidType(handleDao.updateHandle(na, handle));
    }

    private void checkAvailability(String pid, String na, String lidCandidate) {
        if (lidCandidate != null) { // Check if we are not used elsewhere...
            final List<Handle> handles = handleDao.fetchHandleByAttribute(na, lidCandidate, "LID");
            for (Handle h : handles) {
                if (h.getHandle().equals(pid)) {
                    return;
                }
                throw new PidException("Cannot create local identifier "
                        + lidCandidate
                        + " because it already is bound to PID " + handles.get(0).getHandle());
            }
        }
    }

    @Override
    public PidType getPid(String pidId) throws HandleException {
        String normalisedPidId = pidId;
        if (pidId.startsWith(handleBaseUrl)) {
            normalisedPidId = pidId.replaceFirst(handleBaseUrl, "");
        }
        NAAuthentication.authorize(normalisedPidId);
        return mappingsService.convertHandleToPidType(handleDao.fetchHandleByPID(normalisedPidId));
    }

    @Override
    public PidType getAnonymousPid(String pidId) throws HandleException {
        String normalisedPidId = pidId;
        if (pidId.startsWith(handleBaseUrl)) {
            normalisedPidId = pidId.replaceFirst(handleBaseUrl, "");
        }
        return mappingsService.convertHandleToPidType(handleDao.fetchHandleByPID(normalisedPidId));
    }

    @Override
    public void getPidByAttribute(List<PidType> ht, String na, String attribute) {

        final java.util.List fetchHandles = handleDao.fetchHandleByAttribute(NAAuthentication.authorize(na), attribute, null);
        List<PidType> handles = mappingsService.convertHandlesToPidType(fetchHandles);
        for (PidType handle : handles) {
            ht.add(handle);
        }
    }

    @Override
    public PidType createHopePid(String na, String lid, String resolveUrl) throws HandleException {

        if (lid == null)
            throw new PidException("The local identifier is not in the request.");
        if (resolveUrl == null)
            resolveUrl = "";
        List<Handle> handles = handleDao.fetchHandleByAttribute(NAAuthentication.authorize(na), lid, "LID");
        if (handles.isEmpty()) {
            PidType pidType = new PidType();
            pidType.setPid(PidGenerator.getPid(na));
            pidType.setResolveUrl(resolveUrl);
            pidType.setLocalIdentifier(lid);
            handles = handleDao.createNewHandle(na, pidType);
        } else {
            for (Handle handle : handles) {
                if (Arrays.equals(handle.getType(), bURL) &&
                        !handle.getDataAsString().equals(resolveUrl)) {
                    PidType pidType = new PidType();
                    pidType.setPid(handle.getHandle());
                    pidType.setResolveUrl(resolveUrl);
                    pidType.setLocalIdentifier(lid);
                    return updatePid(pidType);
                }
            }
        }
        return mappingsService.convertHandleToPidType(handles);
    }

    @Override
    public boolean deletePid(String pid) {
        NAAuthentication.authorize(pid);
        return handleDao.deletePid(pid);
    }

    @Override
    public long deletePids(String na) {

        if (na == null || !na.contains("."))
            throw new PidException("The naming authority is not a test prefix. Test prefixes contain a dot.");
        return handleDao.deletePids(NAAuthentication.authorize(na));
    }




    public void setNAAuthentication(NAAuthentication NAAuthentication) {
        this.NAAuthentication = NAAuthentication;
    }
}