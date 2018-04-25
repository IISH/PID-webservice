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
import org.socialhistoryservices.pid.schema.PidType;

import java.util.List;

/**
 * @author Lucien van Wouw <lwo@iisg.nl>
 */
public interface PidResourceService {

    public PidType upsertPid(String na, PidType normalize) throws HandleException;

    public PidType createPid(String na, PidType pidType) throws HandleException;

    public PidType updatePid(PidType pidType) throws HandleException;

    public PidType getPid(String pidId) throws HandleException;

    public PidType getAnonymousPid(String pidId) throws HandleException;

    public void getPidByAttribute(List<PidType> handles, String na, String href);

    public PidType createHopePid(String na, String lid, String resolveUrl) throws HandleException;

    long deletePids(String na);

    boolean deletePid(String pid);
}
