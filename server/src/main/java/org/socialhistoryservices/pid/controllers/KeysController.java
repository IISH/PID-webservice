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

import org.socialhistoryservices.pid.util.NamingAuthority;
import org.socialhistoryservices.security.MongoOAuth2ProviderTokenServices;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class KeysController {

    @Resource
    private MongoOAuth2ProviderTokenServices tokenServices;

    @RequestMapping("/keys")
    public ModelAndView list(
            @RequestParam(value = "token", required = false) String refresh_token) {

        ModelAndView mav = new ModelAndView("keys");
        final SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        List<String> nas = NamingAuthority.getNaRole(authentication);
        if (refresh_token != null){
            tokenServices.recreateRefreshAccessToken(refresh_token);
        }
        OAuth2AccessToken token = tokenServices.selectKeys(authentication.getName());
        if (token == null)
            token = tokenServices.createToken( authentication);
        mav.addObject("token", token);
        mav.addObject("nas", nas); // ToDo: when authorities are changed, the ones stored in the oauth table are not updated.
        return mav;
    }
}
