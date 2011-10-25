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

package org.socialhistoryservices.pid.database.dao;

import net.handle.hdllib.HandleException;
import org.socialhistoryservices.pid.database.domain.Handle;
import org.socialhistoryservices.pid.schema.PidType;

import java.util.List;

/**
 * @author Lucien van Wouw <lwo@iisg.nl>
 * @since 2011-01-01
 */
public interface HandleDao {

    List<Handle> upsertHandle(String na, PidType pidType) throws HandleException;

    List<Handle> createNewHandle(String na, PidType pidType) throws HandleException;

    List<Handle> updateHandle(String na, PidType pidType) throws HandleException;

    List<Handle> fetchHandleByPID(String na, String pid) throws HandleException;

    List<Handle> fetchHandleByAttribute(String na, String href, String type);

    long deletePids(String authorize);

    boolean deletePid(String pid);
}
