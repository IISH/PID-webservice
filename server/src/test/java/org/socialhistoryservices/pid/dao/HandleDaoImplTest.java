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

package org.socialhistoryservices.pid.dao;

import net.handle.hdllib.HandleException;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.socialhistoryservices.pid.database.dao.HandleDao;
import org.socialhistoryservices.pid.database.dao.domain.Handle;
import org.socialhistoryservices.pid.schema.LocAttType;
import org.socialhistoryservices.pid.schema.LocationType;
import org.socialhistoryservices.pid.schema.PidType;
import org.socialhistoryservices.pid.service.MappingsServiceImp;
import org.socialhistoryservices.pid.util.PidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import javax.xml.namespace.QName;
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

/**
 * In these test units we operate the handleDao and it's methods directly.
 *
 * This means we need to supply na, pids and resolveUrl's, which would have been supplied by the
 * StubPidResourceService layer.
 */
//
public class HandleDaoImplTest {

    final private Logger log = Logger.getLogger(this.getClass());

    @Value("#{pidProperties['handle.baseUrl']}")
    private String handleBaseUrl = "";

    @Autowired
    private HandleDao handleDao;

    @Autowired
    private MappingsServiceImp pidResourceService;

    final String na = "00000.0";
    final String resolveUrl = "http://socialhistoryservices.org/";


    @BeforeClass
    public static void setup() throws Exception {

        if (System.getProperty("pid.properties") == null)
            System.setProperty("pid.properties", "server/pid.properties");
    }

    @Test
    public void testUpsertHandle() throws HandleException {

        handleDao.deletePids(na); // Not really a test... but used to remove all pids.
        log.info("Create New Handle with upsert");

        final String[] pids = {PidGenerator.getPid(na), PidGenerator.getPid(na), PidGenerator.getPid(na)};
        {
            for (String pid : pids) {

                log.info("Create New Handle");
                final List<Handle> insertedHandles = handleDao.upsertHandle(na, getPidType(pid, resolveUrl, null, null));
                assertNotNull(insertedHandles);
                assertEquals(2, insertedHandles.size());
                String returnPid = insertedHandles.get(0).getHandle();
                log.info("Created Handle ID: " + returnPid);
                assertEquals("The inserted PID is different from the returned one. Impossible.", pid, returnPid);
            }
            // create a it:
            // Lookup... we should have three documents:
            for (String pid : pids) {
                final List<Handle> retrieven_handles = handleDao.fetchHandleByPID(pid);
                assertNotNull(retrieven_handles);
                assertEquals(2, retrieven_handles.size());
                assertEquals(pid, retrieven_handles.get(0).getHandle());
            }

            // update with new URL
            String url = resolveUrl + PidGenerator.getPid();
            for (String pid : pids) {
                final List<Handle> insertedHandles = handleDao.upsertHandle(na, getPidType(pid, url, null, null));
                assertNotNull(insertedHandles);
                assertEquals(2, insertedHandles.size());
                List<Handle> retrieve_handles = handleDao.fetchHandleByPID(insertedHandles.get(0).getHandle());
                assertEquals(url, retrieve_handles.get(0).getDataAsString());
            }
        }
    }

    @Test
    public void testCreateNewHandle
            () throws Exception {

        handleDao.deletePids(na); // Not really a test... but used to remove all pids.

        log.info("Create New Handle");
        String pid = PidGenerator.getPid(na);
        final List<Handle> insertedHandles = handleDao.createNewHandle(na, getPidType(pid, resolveUrl, null, null));
        assertNotNull(insertedHandles);
        assertEquals(2, insertedHandles.size());
        String returnPid = insertedHandles.get(0).getHandle();
        log.info("Created Handle ID: " + returnPid);
        assertEquals("The inserted PID is different from the returned one. Impossible.", pid, returnPid);
    }

    @Test
    public void testReverseLookup
            () throws Exception {
        String pid = PidGenerator.getPid(na);
        String lid = "I am local id: 12345" + PidGenerator.getPid();
        log.info("Create New Handle with local id " + lid);
        final List<Handle> insertedHandles = handleDao.createNewHandle(na, getPidType(pid, resolveUrl, null, lid));
        assertNotNull(insertedHandles);
        assertEquals("Expected three handles: one for the pid and lid each; and the hs_admin", 3, insertedHandles.size());
        String returnPid = insertedHandles.get(0).getHandle();
        log.info("Created Handle ID: " + returnPid);
        log.info("Attempting reverse lookup with " + lid);
        final List<Handle> retrieveHandles = handleDao.fetchHandleByAttribute(na, lid, "LID");
        assertFalse(retrieveHandles.size() == 0);
        if (retrieveHandles.size() != 2)
            log.warn("More than one handle was found. This is possible with a reverse lookup.");
        for (Handle retrieveHandle : retrieveHandles) {
            log.info(retrieveHandle.getHandle());
        }
    }

    @Test
    public void testCreateNewHandleAllParams
            () throws Exception {

        log.info("Create New Handle");
        String pid = PidGenerator.getPid(na);

        final LocAttType locAttType = new LocAttType();
        final LocationType locationType1 = new LocationType();
        locationType1.setHref(resolveUrl + PidGenerator.getPid());
        locationType1.setId("11");
        locationType1.setCountry("gb");
        locAttType.getLocation().add(locationType1);
        final LocationType locationType2 = new LocationType();
        locationType2.setHref(resolveUrl + PidGenerator.getPid());
        locationType2.setId("21");
        locationType2.setCountry("nl");
        locationType2.getOtherAttributes().put(new QName("a"), "b") ;
        locAttType.getLocation().add(locationType2);

        final List<Handle> insertedHandles = handleDao.createNewHandle(na, getPidType(pid, resolveUrl, locAttType, null));
        assertNotNull(insertedHandles);

        LocAttType locAtt = null;
        while ( insertedHandles.iterator().hasNext() ){
            Handle h = insertedHandles.iterator().next();
            if ( h.getIndex() == 1000 ) {
                locAtt = pidResourceService.getLocations(h);
                break;
            }
        }
        assertNotNull("Expected to see locations", locAtt);
        List<LocationType> locations = locAtt.getLocation();
        assertEquals("When a URL is added plus two locations, we must have three locations returned. The third location is the URL", 3, locations.size());
        boolean hasUrl = false;
        for (LocationType location : locations) {
            if (location.getHref().equals(resolveUrl)) {
                hasUrl = true;
                assertEquals("The resolve URL's value must be identical to the supplied resolve url.", resolveUrl, location.getHref());
            }
        }
        assertEquals("A default location was expected", true, hasUrl);
    }

    @Test
    public void testFetchHandleByPID
            () throws Exception {
        String knownPid = PidGenerator.getPid(PidGenerator.getPid(na));
        final List<Handle> insertedHandles = handleDao.createNewHandle(na, getPidType(knownPid, resolveUrl, null, null));
        assertNotNull(insertedHandles);
        final List<Handle> retrieven_handles = handleDao.fetchHandleByPID(knownPid);
        assertEquals("HandlePid should be the same", knownPid, retrieven_handles.get(0).getHandle());
        final String unknownPid = PidGenerator.getPid();
        final List<Handle> handleList = handleDao.fetchHandleByPID(unknownPid);
        assertEquals("HandlePid ought to be empty when supplied an unknown pid.", 0, handleList.size());
    }

    @Test
    public void testFetchHandleByAttribute
            () throws Exception {

        String pid = PidGenerator.getPid(na);
        String[] testRefUrls = {
                resolveUrl + PidGenerator.getPid(),
                resolveUrl + PidGenerator.getPid(),
                resolveUrl + PidGenerator.getPid()};
        final LocAttType locationAttributes = new LocAttType();
        for (String testRefUrl : testRefUrls) {
            final LocationType location = new LocationType();
            location.setHref(testRefUrl);
            location.setId("41");
            location.setCountry("gb");
            locationAttributes.getLocation().add(location);
        }
        final List<Handle> insertedHandles = handleDao.createNewHandle(na, getPidType(pid, resolveUrl, locationAttributes, null));
        assertNotNull(insertedHandles);

        for (String testRefUrl : testRefUrls) {
            final List<Handle> retrieven_handles = handleDao.fetchHandleByAttribute(na, testRefUrl, null);
            assertNotNull(retrieven_handles);
            final String returnPid = retrieven_handles.get(0).getHandle();
            assertEquals("HandlePid should be the same", pid, returnPid);
        }
    }

    @Test
    public void testUpdateHandleNoLID
            () throws Exception {

        String pid = PidGenerator.getPid(na);
        handleDao.createNewHandle(na, getPidType(pid, resolveUrl, null, null));
        PidType pidType = new PidType();
        pidType.setPid(pid);
        String newResolveUrl = resolveUrl + PidGenerator.getPid();
        pidType.setResolveUrl(newResolveUrl);
        final List<Handle> updatedHandles = handleDao.updateHandle(na, pidType);
        assertNotNull(updatedHandles);
        assertEquals("URL", updatedHandles.get(0).getTypeAsString());
        final String updatedUrl = updatedHandles.get(0).getDataAsString();
        assertEquals("updated URL should be the same", newResolveUrl, updatedUrl);
        assertNotSame("updated URL should be different from the previous one", resolveUrl, updatedUrl);
    }

    @Test
    public void testUpdateHandleWithLID
            () throws Exception {

        String pid = PidGenerator.getPid(na);
        String lid = PidGenerator.getPid();
        handleDao.createNewHandle(na, getPidType(pid, resolveUrl, null, lid));
        PidType update = new PidType();
        update.setPid(pid);
        String lidUpdate = PidGenerator.getPid();
        update.setLocalIdentifier(lidUpdate);
        String updatedResolveUrl = resolveUrl + PidGenerator.getPid();
        update.setResolveUrl(updatedResolveUrl);
        final List<Handle> updatedHandles = handleDao.updateHandle(na, update);
        assertEquals("There should be two handles: for the URL and the LID; and the HS_ADMIN", 3, updatedHandles.size());
        boolean hasLID = false;
        for (Handle handle : updatedHandles) {
            hasLID = (handle.getTypeAsString()).equals("LID");
            if (hasLID) {
                assertEquals("The local identifier ought to be changed during an update that included a new LID.",
                        lidUpdate, handle.getDataAsString());
                break;
            }
        }
        assertTrue("There must be a LID.", hasLID);
    }

    @Test
    public void testUpdateHandleWithChangedLID
            () throws Exception {

        String pid = PidGenerator.getPid(na);
        String lid = PidGenerator.getPid();
        handleDao.createNewHandle(na, getPidType(pid, resolveUrl, null, lid));
        String newLid = PidGenerator.getPid();
        PidType pidType = new PidType();
        pidType.setPid(pid);
        pidType.setLocalIdentifier(newLid);
        String updatedResolveUrl = resolveUrl + PidGenerator.getPid();
        pidType.setResolveUrl(updatedResolveUrl);
        final List<Handle> updatedHandles = handleDao.updateHandle(na, pidType);
        final Handle handle = updatedHandles.get(1);
        assertEquals("LID", handle.getTypeAsString());
        final String updatedLid = handle.getDataAsString();
        assertEquals(newLid, updatedLid);
        assertNotSame("The local identifier ought to be different from the new one.", lid, updatedLid);
    }

    @Test
    public void testUpdateHandleWithLIDAndLocations
            () throws Exception {

        String pid = PidGenerator.getPid(na);
        String lid = PidGenerator.getPid();
        String[] testRefUrls = {
                resolveUrl + PidGenerator.getPid(),
                resolveUrl + PidGenerator.getPid(),
                resolveUrl + PidGenerator.getPid()};
        final LocAttType locationAttributes = new LocAttType();
        for (int i = 0; i < testRefUrls.length; i++) {
            String testRefUrl = testRefUrls[i];
            final LocationType location = new LocationType();
            location.setHref(testRefUrl);
            location.setId("41");
            location.setCountry("gb");
            location.setView("a_view_" + i);
            locationAttributes.getLocation().add(location);
        }
        handleDao.createNewHandle(na, getPidType(pid, resolveUrl, locationAttributes, lid));

        PidType update = new PidType();
        update.setPid(pid);
        update.setLocalIdentifier(lid);
        String updatedResolveUrl = resolveUrl + PidGenerator.getPid();
        update.setResolveUrl(updatedResolveUrl);
        final List<Handle> updatedHandles = handleDao.updateHandle(na, update);
        final Handle handle = updatedHandles.get(1);
        assertEquals("LID", handle.getTypeAsString());
        assertEquals("The lid ought to be same.", lid, handle.getDataAsString());
        for (Handle h : updatedHandles) {
            if (h.getTypeAsString().equals("10320/loc")) {
                throw new AssertionError("The location attributes should have been removed ( was null in update )");
            }
        }

        update.setLocalIdentifier(null);
        final List<Handle> reUpdatedHandles = handleDao.updateHandle(na, update);
        assertEquals("The lid has been removed. There should only be two handles. ", 2, reUpdatedHandles.size());
    }

    @Test
    public void testUpdateHandleNonExistentPid
            () throws HandleException {
        PidType pidType = new PidType();
        pidType.setPid(PidGenerator.getPid(na));

        List<Handle> updatedHandles = null;
        try {
            updatedHandles = handleDao.updateHandle(na, pidType);
            assertEquals("You cannot update a non existent Pid.", 0, updatedHandles.size());
        } catch (HandleException e) {
        }
        assertNull("You cannot update a non existent Pid.", updatedHandles);
    }

    @Test
    public void testDeletePid
            () throws HandleException {

        final String pid = PidGenerator.getPid(na);
        handleDao.createNewHandle(na, getPidType(pid, resolveUrl, null, null));
        boolean deleted = handleDao.deletePid(pid);
        Assert.isTrue(deleted);

        final List<Handle> retrieven_handles = handleDao.fetchHandleByPID(pid);
        assertEquals(0, retrieven_handles.size());

        handleDao.createNewHandle(na, getPidType(pid, resolveUrl, null, null));
        deleted = handleDao.deletePid(pid + "abc");
        Assert.isTrue(!deleted);
    }

    @Test
    public void testCountDeletions
            () throws HandleException {

        handleDao.deletePids(na);
        int t = 5;
        for (int i = 0; i < t; i++) {

            String pid = PidGenerator.getPid(na);
            String lid = PidGenerator.getPid();
            handleDao.createNewHandle(na, getPidType(pid, resolveUrl, null, lid));
        }
        long count = handleDao.deletePids(na);
        assertTrue("We deleted " + t + " PIDs... number should be the same.", t == count);
    }

    private PidType getPidType
            (String
                     pid, String
                    resolveUrl, LocAttType
                    locAttType, String
                    localIdentifier) {

        PidType pidType = new PidType();
        pidType.setPid(pid);
        pidType.setResolveUrl(resolveUrl);
        pidType.setLocAtt(locAttType);
        pidType.setLocalIdentifier(localIdentifier);
        return pidType;
    }
}