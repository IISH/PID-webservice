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

package org.socialhistoryservices.pid.security;

/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.TokenServicesUserApprovalHandler;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Dave Syer
 *
 */
public class UserApprovalHandler extends TokenServicesUserApprovalHandler {

	private Collection<String> autoApproveClients = new HashSet<String>();

	private boolean useTokenServices = true;

	/**
	 * @param useTokenServices the useTokenServices to set
	 */
	public void setUseTokenServices(boolean useTokenServices) {
		this.useTokenServices = useTokenServices;
	}

	/**
	 * @param autoApproveClients the auto approve clients to set
	 */
	public void setAutoApproveClients(Collection<String> autoApproveClients) {
		this.autoApproveClients = autoApproveClients;
	}


	/**
	 * Allows automatic approval for a white list of clients in the implicit grant case.
	 *
	 * @param authorizationRequest The authorization request.
	 * @param userAuthentication the current user authentication
	 *
	 * @return Whether the specified request has been approved by the current user.
	 */
	public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
		if (useTokenServices && super.isApproved(authorizationRequest, userAuthentication)) {
			return true;
		}
		if (!userAuthentication.isAuthenticated()) {
			return false;
		}
		return authorizationRequest.isApproved()
				|| (authorizationRequest.getResponseTypes().contains("token") && autoApproveClients
						.contains(authorizationRequest.getClientId()));
	}

}