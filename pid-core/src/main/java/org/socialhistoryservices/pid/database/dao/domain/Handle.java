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

package org.socialhistoryservices.pid.database.dao.domain;

import net.handle.hdllib.HandleValue;
import net.handle.hdllib.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain class for the webservice. It extends the existing Handle System
 * HandleValue class and adds utility methods.
 */
public class Handle extends HandleValue {

    private String handle = null;
    private ArrayList<String> locations = null;

    public Handle() {
    }

    public Handle(String handle) {
        this.handle = handle;
    }

    public String getHandle() {
        return handle;
    }

    public void setType(String type) {
        setType(Util.encodeString(type));
    }

    public void setData(String data) {
        if (data != null)
            setData(Util.encodeString(data));
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public List<String> getLocations() {
        if (locations == null) {
            locations = new ArrayList<String>();
        }
        return locations;
    }

    public void setLocation(String location) {
        if (location != null && !location.isEmpty() && !getLocations().contains(location))
            locations.add(location);
    }

    public static Handle cast(String pid, HandleValue value) {

        final Handle handle = new Handle(pid);
        handle.setIndex(value.getIndex());
        handle.setData(value.getData());
        handle.setType(value.getType());
        handle.setAdminCanRead(value.getAdminCanRead());
        handle.setAdminCanWrite(value.getAdminCanWrite());
        handle.setAnyoneCanRead(value.getAnyoneCanRead());
        handle.setAdminCanWrite(value.getAnyoneCanWrite());
        handle.setReferences(value.getReferences());
        handle.setTimestamp(value.getTimestamp());
        handle.setTTL(value.getTTL());
        handle.setTTLType(value.getTTLType());
        return handle;
    }
}
