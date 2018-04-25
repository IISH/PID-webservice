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

import org.apache.log4j.Logger;

import java.io.*;
import java.text.MessageFormat;
import java.util.Properties;

public final class PidProperties extends Properties {

    private static final long serialVersionUID = 1L;
    private final Logger log = Logger.getLogger(getClass());

    public PidProperties() {
        final String[] expected = new String[]{"log4j.xml", "handle.weight"};
        load("PID_HOME", "pid.properties", expected);
    }

    public void load(String environment, String property, String[] expected) {
        String sorProperties = "";
        InputStream inputStream = null;
        try {
            sorProperties = System.getProperty(property);
            if (sorProperties != null) {
                log.info("Found system property '" + property + "', resolved to " + new File(sorProperties).getCanonicalPath());
            }
            inputStream = getInputFromFile(sorProperties);
            if (inputStream == null) {
                log.info("System property '" + property + "' not found, checking environment for '" + property + "'.");
                sorProperties = System.getenv(environment);
                if (sorProperties != null) {
                    log.info("Found environment property '" + property + "', resolved to " + new File(sorProperties).getCanonicalPath());
                }
                inputStream = getInputFromFile(sorProperties);
            }
        } catch (Exception e) {
            log.fatal("Error in resolving file defined with " + sorProperties);
            System.exit(1);
        }

        if (inputStream == null)
            offerSolution(environment, property);
        loadProperties(inputStream, sorProperties);
        checkExpected(expected);
    }

    private void offerSolution(String environment, String property) {
        log.fatal(
                "Configuration not available!\n" +
                        "Solutions:\n" +
                        "1) Start the JVM with parameter -D" + property + "=/path/to/[filename].properties\n" +
                        "2) Set the environment variable '" + environment + "' to /path/to/filename.properties"
        );
        System.exit(1);
    }

    private void loadProperties(InputStream inputStream, String sorProperties) {

        try {
            load(inputStream);
        } catch (IOException e) {
            log.fatal("Unable to load '" + sorProperties + "'.properties' from input stream!");
            System.exit(1);
        }
    }

    private void checkExpected(String[] expected) {

        boolean complete = true;
        if (expected != null)
            for (String expect : expected) {
                String value = getProperty(expect);
                if (value == null) {
                    log.warn(MessageFormat.format("Missing property ''{0}''", expect));
                    complete = false;
                }
            }

        if (!complete) {
            log.fatal("Configuration properties incomplete. Check log of this class for warnings.");
            System.exit(1);
        }
    }

    private InputStream getInputFromFile(String filePath) {
        if (filePath != null) {
            try {
                log.info("Going to load properties from '" + filePath + "', resolved to " + new File(filePath).getCanonicalPath());
                return new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("No file found: " + filePath, e);
            } catch (IOException e) {
                throw new RuntimeException("IO exception on: " + filePath, e);
            }
        } else {
            return null;
        }
    }
}