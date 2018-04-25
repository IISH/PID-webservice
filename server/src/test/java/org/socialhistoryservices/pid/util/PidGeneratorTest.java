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

package org.socialhistoryservices.pid.util;

import org.socialhistoryservices.pid.exceptions.PidException;
import org.socialhistoryservices.pid.util.PidGenerator;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * Test the PID syntax validation.
 */
public class PidGeneratorTest {

    final String na = "00000.0";

    @Test
    public void goodPids() {

        String pid1 = null, pid2 = PidGenerator.getPid(na);
        try {
            pid1 = PidGenerator.validatePid(na, pid2);
        } catch (PidException e) {
        }
        assertNotNull(pid1);
        assertEquals(pid1, pid2);

        pid1=null;
        pid2 = na + "/123$e4-ed3djKaAJkj(ed)ed[ed]ed{eded}eded                 _eded=edede\\ed5";
        try {
            pid1 = PidGenerator.validatePid(na, pid2);
        } catch (PidException e) {
        }
        assertNotNull(pid1);
        assertEquals(pid1, pid2);
    }

    @Test
    public void badPids() {

        String pid = null;
        char c = 240;
        try {
            pid = PidGenerator.validatePid(na, na + "/1234rffr5" + c);// High level character
        } catch (PidException e) {
        }
        assertNull("PID syntax should not have been accepted.", pid);

        try {
            pid = PidGenerator.validatePid(na, na + "/1234/rffr5" + c + "dfghj");// High level character
        } catch (PidException e) {
        }
        assertNull("PID syntax should not have been accepted.", pid);

        // Illegal character forward slash
        try {
            pid = PidGenerator.validatePid(na, na + "/1234/rr5");
        } catch (PidException e) {
        }
        assertNull("PID syntax should not have been accepted.", pid);

        // Too long a string
        try {
            String pidCandidate = PidGenerator.getPid(na) + PidGenerator.getPid() + PidGenerator.getPid() +
                    PidGenerator.getPid() + PidGenerator.getPid() + PidGenerator.getPid() +
                    PidGenerator.getPid() + PidGenerator.getPid();
            pid = PidGenerator.validatePid(na, pidCandidate);

        } catch (PidException e) {
        }
        assertNull("PID should not have been accepted, because it has too many characters.", pid);


        // Na not matching... allthough authorization is not handled by this class.
        try {
            pid = PidGenerator.validatePid(na, "12345" + "/123");
        } catch (PidException e) {
        }
        assertNull("PID syntax should not have been accepted.", pid);

        try {
            pid = PidGenerator.validatePid(na, null);
        } catch (PidException e) {
        }
        assertNull("A null for a pid value should not have been accepted.", pid);
    }
}