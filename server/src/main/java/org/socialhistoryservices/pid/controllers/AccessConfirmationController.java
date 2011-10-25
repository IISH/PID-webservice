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

package org.socialhistoryservices.pid.controllers;

import org.springframework.security.oauth2.provider.ClientAuthenticationToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.verification.ClientAuthenticationCache;
import org.springframework.security.oauth2.provider.verification.DefaultClientAuthenticationCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.TreeMap;

/**
 * Confirmation controller, where the enduser has to decide if the consumer Oauth client is allowed to receive a key
 * to access the enduser's resources using the enduser's Oauth credentials.
 *
 * @author Lucien van Wouw <lwo@iisg.nl>
 */
@Controller
public class AccessConfirmationController {

    private final ClientAuthenticationCache authenticationCache = new DefaultClientAuthenticationCache();

    @SuppressWarnings({"SpringJavaAutowiringInspection"}) // The bean is only available\instantiated at runtime.
    @Resource
    private ClientDetailsService clientDetailsService;

    @RequestMapping("/confirm_access")
    public ModelAndView authRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ClientAuthenticationToken clientAuth = authenticationCache.getAuthentication(request, response);
        if (clientAuth == null) {
            throw new IllegalStateException("No client authentication request to authorize.");
        }
        ClientDetails client = clientDetailsService.loadClientByClientId(clientAuth.getClientId());
        TreeMap<String, Object> model = new TreeMap<String, Object>();
        model.put("auth_request", clientAuth);
        model.put("client", client);
        return new ModelAndView("access_confirmation", model);
    }
}
