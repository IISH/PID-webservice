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

package org.socialhistoryservices.pid.service;

import net.handle.hdllib.HandleException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.socialhistoryservices.pid.database.domain.Handle;
import org.socialhistoryservices.pid.exceptions.PidException;
import org.socialhistoryservices.pid.schema.LocAttType;
import org.socialhistoryservices.pid.schema.PidType;
import org.socialhistoryservices.pid.util.PidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.AuthorizedClientAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Lucien van Wouw <lwo@iisg.nl>
 * @since 2011-01-01
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/application-context.xml"
})

//
public class StubPidResourceServiceTest {

    @Autowired
    private PidResourceService pidResourceService;

    @Value("#{pidProperties['handle.baseUrl']}")
    private String handleBaseUrl = "";

    final static String na = "00000.0";

    @Value("#{pidProperties['clientDetails.clientId']}")
    private String resolveUrl;

    @BeforeClass
    public static void setup() throws Exception {

        if (System.getProperty("pid.properties") == null) // -Dhandle.properties=pid.properties
            System.setProperty("pid.properties", "server/pid.properties");

        class Grant implements GrantedAuthority {

            private String authority;

            public Grant(String authority) {
                this.authority = authority;
            }

            @Override
            public String getAuthority() {
                return authority;
            }
        }
        final Collection<GrantedAuthority> authorities = new ArrayList(4);
        authorities.add(new Grant("ROLE_USER"));
        authorities.add(new Grant("ROLE_NA_00000.1"));
        authorities.add(new Grant("ROLE_NA_" + na));
        authorities.add(new Grant("ROLE_NA_00000.2"));
        AuthorizedClientAuthenticationToken clientAuthentication = new AuthorizedClientAuthenticationToken("socialhistoryservices.org", null, null, null);
        UsernamePasswordAuthenticationToken usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(null, null, authorities);
        OAuth2Authentication auth2Authentication = new OAuth2Authentication(clientAuthentication, usernamePasswordAuthentication);
        final SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(auth2Authentication);
    }

    @Test
    public void testAutoGenerate() throws HandleException {

        pidResourceService.deletePids(na);


        PidType pidType = pidResourceService.createPid(na, getPidType(null, null, null, null));
        assertNotNull(pidType);
        assertNotNull(pidType.getPid());
        assertNotNull(pidType.getResolveUrl());
    }

    @Test
    public void testCustom() throws HandleException {

        String pid = PidGenerator.getPid(na);
        String customResolveUrl = getResolveUrl();
        PidType pidType = pidResourceService.createPid(na, getPidType(pid, customResolveUrl, null, null));
        assertEquals(pid, pidType.getPid());
        assertEquals(customResolveUrl, pidType.getResolveUrl());

        PidType samePid = null;
        try {
            samePid = pidResourceService.createPid(na, getPidType(pid, customResolveUrl, null, null));
        } catch (Exception e) {
            // Pid already exists;
        }
        assertNull("Pid already exists which is not possible using a create method.", samePid);
    }

    @Test
    public void testLookupResolveUrlForPidWithLid() throws Exception {

        String pid = PidGenerator.getPid(na);
        String customResolveUrl = getResolveUrl();
        String lid = PidGenerator.getPid();
        pidResourceService.createPid(na, getPidType(pid, customResolveUrl, null, lid));

        List<PidType> handles = new ArrayList<PidType>();
        pidResourceService.getPidByAttribute(handles, na, customResolveUrl);
        String retrievedLid = handles.get(0).getLocalIdentifier();
        assertNotNull(retrievedLid);
        assertEquals(lid, retrievedLid);
    }

    @Test
    public void testCreateNewHandleWithWrongNas() throws Exception {

        String pid = PidGenerator.getPid(na);
        PidType insertedHandles = null;
        try {
            insertedHandles = pidResourceService.createPid(null, getPidType(pid, getResolveUrl(), null, null));
        } catch (SecurityException e) {
        }
        assertNull("A naming authority must be present", insertedHandles);
        try {
            insertedHandles = pidResourceService.createPid(na + na, getPidType(pid, getResolveUrl(), null, null));
        } catch (SecurityException e) {
        }
        assertNull("A naming authority must match the PID", insertedHandles);
    }

    @Test
    public void testUpdateWithEmptyPid() throws HandleException {

        PidType insertedHandles = null;
        try {
            insertedHandles = pidResourceService.updatePid(getPidType(null, getResolveUrl(), null, null));
        } catch (SecurityException e) {
        }
        assertNull("A pid has to be supplied in order to derive the na ( and for the lookup itself ) .", insertedHandles);
    }

    @Test
    public void testCreatePidWithExistingPid() throws HandleException {

        String pid = PidGenerator.getPid(na);
        pidResourceService.createPid(na, getPidType(pid, getResolveUrl(), null, null));
        PidType insertedHandles = null;
        try {
            insertedHandles = pidResourceService.createPid(na, getPidType(pid, getResolveUrl(), null, null));
        } catch (HandleException e) {
        }
        assertNull("The same pid was offered twice and accepted. But PIDs must be unique!.", insertedHandles);
    }

    @Test
    public void testCreateLidWithExistingLid() throws HandleException {

        String lid = PidGenerator.getPid();
        pidResourceService.createPid(na, getPidType(null, getResolveUrl(), null, lid));
        PidType insertedHandles = null;
        try {
            insertedHandles = pidResourceService.createPid(na, getPidType(null, getResolveUrl(), null, lid));
        } catch (PidException e) {
        }
        assertNull("The same lid was offered twice and accepted. But lids must be unique!.", insertedHandles);
    }

    @Test
    public void testUpdatePidWithAlternatingLids() {

        PidType pidType = getPidType(null, getResolveUrl(), null, PidGenerator.getPid(na));
        try {
            pidResourceService.createPid(na, pidType);
        } catch (HandleException e) {
        }

        for (int i = 0; i < 3; i++) {
            String lid = PidGenerator.getPid();
            pidType.setLocalIdentifier(lid);
            PidType insertedHandles = null;
            try {
                insertedHandles = pidResourceService.updatePid(pidType);
            } catch (HandleException e) {
            }
            assertNotNull("Different LIDs ought to have been accepted.", insertedHandles);
            assertEquals(lid, insertedHandles.getLocalIdentifier());
        }
    }

    @Test
    public void testUpdatePidWithExistingLid() throws HandleException {

        String lid = PidGenerator.getPid();
        PidType pidType = getPidType(null, getResolveUrl(), null, lid);
        pidResourceService.createPid(na, pidType);
        PidType insertedHandles = null;
        try {
            insertedHandles = pidResourceService.updatePid(pidType);
        } catch (Exception e) {
        }
        assertNotNull("A lid can be re-declared in an update, but it was rejected.", insertedHandles);

        // Just add a new PID with a different lid
        String newLid = PidGenerator.getPid();
        pidType.setLocalIdentifier(newLid);
        pidType.setPid(null);
        pidResourceService.createPid(na, pidType);

        // Now see if we cannot bind the lid
        insertedHandles = null;
        pidType.setLocalIdentifier(lid); // Pid, with existing lid from other pid.
        try {
            insertedHandles = pidResourceService.updatePid(pidType);
        } catch (PidException e) {
        }
        assertNull("A lid bound to a PID was offered to bind to a different PID and this was accepted.", insertedHandles);
    }

    @Test
    public void testHopePidCreate() throws HandleException {

        String lid = null;
        PidType pidType = null;
        try {
            pidResourceService.createHopePid(na, lid, getResolveUrl());
        } catch (PidException e) {
        }
        assertNull(pidType);

        lid = PidGenerator.getPid();
        pidType = pidResourceService.createHopePid(na, lid, getResolveUrl());
        String pid = pidType.getPid();
        assertNotNull(pid);
        assertNotNull(lid);
        assertNotNull(pidType.getResolveUrl());

        PidType retrievePid = pidResourceService.getPid(pid);
        assertEquals(pid, retrievePid.getPid());

        List<PidType> handles = new ArrayList();
        pidResourceService.getPidByAttribute(handles, na, lid);
        final String pid2 = handles.get(0).getPid();
        assertEquals(pid, pid2);
    }

    @Test
    public void testHopePidUpdateResolveUrl() throws HandleException {

        String lid = PidGenerator.getPid();
        String resolveUrl = getResolveUrl();
        PidType pidType = pidResourceService.createHopePid(na, lid, resolveUrl);
        String newResolveUrl = getResolveUrl();
        pidResourceService.createHopePid(na, lid, newResolveUrl);

        List<PidType> handles = new ArrayList();
        pidResourceService.getPidByAttribute(handles, na, lid);
        PidType updatedPidType = handles.get(0);
        final String localIdentifier = updatedPidType.getLocalIdentifier();
        assertEquals("The local identifier should never change.", lid, localIdentifier);
        assertEquals("The PID should be the same after a resolveUrl update.", pidType.getPid(), updatedPidType.getPid());
        assertFalse("The old and new resolve URLs should not be the same.", resolveUrl.equals(updatedPidType.getResolveUrl()));
    }

    @Test
    public void testHopePidUpdateResolveUrlWithEmptyUrl() throws HandleException {

        String lid = PidGenerator.getPid();
        PidType pidType = pidResourceService.createHopePid(na, lid, null);
        String newResolveUrl = getResolveUrl();
        pidResourceService.createHopePid(na, lid, newResolveUrl);

        List<PidType> handles = new ArrayList();
        pidResourceService.getPidByAttribute(handles, na, lid);
        PidType updatedPidType = handles.get(0);
        final String localIdentifier = updatedPidType.getLocalIdentifier();
        assertEquals("The local identifier should never change.", lid, localIdentifier);
        assertEquals("The PID should be the same after a resolveUrl update.", pidType.getPid(), updatedPidType.getPid());
    }

    @Test
    public void testUpsertPidCreate() throws HandleException {

        // update with LID.... first should succeed... second and third fail...
        final String pid = PidGenerator.getPid(na);
        String url = getResolveUrl();

        String lid = PidGenerator.getPid();
        PidType pidType = pidResourceService.upsertPid(na, getPidType(pid, url, null, lid));
        assertNotNull(pidType);

        PidType retrievePid = pidResourceService.getPid(pid);
        assertEquals(pid, retrievePid.getPid());

        // Update
        String new_Url = getResolveUrl();
        PidType update = pidResourceService.upsertPid(na, getPidType(pid, new_Url, null, lid));
        assertNotNull(pidType);
        assertEquals(pidType.getPid(), pid);
        assertEquals(pidType.getLocalIdentifier(), lid);

        // Should fail
        PidType newPidType = null;
        final String newPid = PidGenerator.getPid(na);
        try {
            newPidType = pidResourceService.upsertPid(na, getPidType(newPid, url, null, lid));
        } catch (PidException e) {
        }
        assertNull(newPidType);
    }
    @Test
    public void testUpsertPidCreateNoPid() throws HandleException {

        // update with LID.... first should succeed... second and third fail...
        String url = getResolveUrl();
        String lid = PidGenerator.getPid();
        PidType pidType = pidResourceService.upsertPid(na, getPidType(null, url, null, lid));
        assertNotNull(pidType);

        PidType retrievePid = pidResourceService.getPid(pidType.getPid());
        assertEquals(pidType.getPid(), retrievePid.getPid());

        // Update
        String new_Url = getResolveUrl();
        pidResourceService.upsertPid(na, getPidType(null, new_Url, null, lid));

        retrievePid = pidResourceService.getPid(pidType.getPid());
        assertEquals(retrievePid.getLocalIdentifier(), lid);
        assertEquals(retrievePid.getResolveUrl(), new_Url);

        // Should fail
        PidType newPidType = null;
        final String newPid = PidGenerator.getPid(na);
        try {
            newPidType = pidResourceService.upsertPid(na, getPidType(newPid, url, null, lid));
        } catch (PidException e) {
        }
        assertNull(newPidType);
    }

    private PidType getPidType(String pid, String resolveUrl, LocAttType locAttType, String localIdentifier) {

        PidType pidType = new PidType();
        pidType.setPid(pid);
        pidType.setResolveUrl(resolveUrl);
        pidType.setLocAtt(locAttType);
        pidType.setLocalIdentifier(localIdentifier);
        return pidType;
    }

    private String getResolveUrl() {

        return "http://" + resolveUrl + "/" + PidGenerator.getPid();
    }
}
