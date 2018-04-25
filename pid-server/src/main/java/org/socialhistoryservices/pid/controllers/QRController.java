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

package org.socialhistoryservices.pid.controllers;

import net.handle.hdllib.HandleException;
import org.socialhistoryservices.pid.schema.LocationType;
import org.socialhistoryservices.pid.schema.PidType;
import org.socialhistoryservices.pid.service.QRService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class QRController {

    private QRService qrService;

    /**
     * metadata
     * <p/>
     * Show the PID and it's local URLs
     * <p/>
     * Return model:
     * type | handle | resolveUrl
     *
     * @param na
     * @param id
     * @param handleResolverBaseUrl
     * @param model
     * @return
     * @throws HandleException
     */
    @RequestMapping("/metadata/{na}/{id:.*}")
    public String metadata(@PathVariable("na") String na,
                           @PathVariable("id") String id,
                           @RequestParam(value = "r", required = false, defaultValue = "http://hdl.handle.net/") String handleResolverBaseUrl,
                           Model model, HttpServletResponse response) throws HandleException {
        final String pid = na + "/" + id;
        final PidType pidType = qrService.getPid(pid);
        if (pidType == null) {
            model.addAttribute("pid", pid);
            response.setStatus(404);
            return "metadata404";
        }

        if (!handleResolverBaseUrl.endsWith("/")) handleResolverBaseUrl += "/";

        final List<String[]> handles = new ArrayList<String[]>();
        if (pidType.getResolveUrl() == null) {
            for (LocationType location : pidType.getLocAtt().getLocation()) {
                if (location.getOtherAttributes().isEmpty()) {
                    if (location.getView() == null)
                        handles.add(new String[]{"LOC",
                                handleResolverBaseUrl + pid,
                                location.getHref(),
                                pid});
                    else
                        handles.add(new String[]{"LOC",
                                handleResolverBaseUrl + pid + "?locatt=view:" + location.getView(),
                                location.getHref(),
                                pid + "?locatt=view:" + location.getView()});
                } else
                    for (QName key : location.getOtherAttributes().keySet()) {
                        handles.add(new String[]{"LOC",
                                handleResolverBaseUrl + pid + "?locatt=" + key.getLocalPart() + ":" + location.getOtherAttributes(),
                                location.getHref(),
                                pid + "?locatt=" + key.getLocalPart() + ":" + location.getOtherAttributes()});
                    }
            }
        } else {
            handles.add(new String[]{"URL",
                    handleResolverBaseUrl + pid,
                    pidType.getResolveUrl(),
                    pid});
        }

        model.addAttribute("pid", pid);
        model.addAttribute("handles", handles);
        return "metadata";
    }

    /**
     * Create a QR image for the PID
     *
     * @param na
     * @param id
     * @param locAtt
     * @param handleResolverBaseUrl
     * @param width
     * @param height
     * @param response
     * @throws Exception
     */
    @RequestMapping("/{na}/{id:.*}")
    public void encodeimage(@PathVariable("na") String na,
                            @PathVariable("id") String id,
                            @RequestParam(value = "locatt", required = false) String locAtt,
                            @RequestParam(value = "r", required = false, defaultValue = "http://hdl.handle.net/") String handleResolverBaseUrl,
                            @RequestParam(value = "width", required = false, defaultValue = "0") int width,
                            @RequestParam(value = "height", required = false, defaultValue = "0") int height,
                            HttpServletResponse response) throws Exception {

        byte[] image;
        if (!handleResolverBaseUrl.endsWith("/")) handleResolverBaseUrl += "/";
        image = qrService.encode(handleResolverBaseUrl, na + "/" + id, locAtt, width, height);
        if (image == null) {
            image = qrService.qr404image();
            response.setStatus(404);
        }
        response.setContentType("image/png");
        response.setContentLength(image.length);
        response.getOutputStream().write(image);
    }

    @RequestMapping(value = "/qr", method = RequestMethod.POST)
    public String handleFormUpload(@RequestParam("image") MultipartFile file, HttpServletResponse response) throws IOException {

        String url = null;
        if (!file.isEmpty()) {
            try {
                url = qrService.decode(file.getInputStream());
            } catch (Exception e) {
                // Do not do a thing
            }
        }
        if (url == null) {
            response.setStatus(404);
            return "fnf400";
        } else {
            response.setStatus(301);
            response.setHeader("Location", url);
            response.setHeader("Connection", "close");
            return null;
        }
    }


    public void setQrService(QRService qrService) {
        this.qrService = qrService;
    }

}
