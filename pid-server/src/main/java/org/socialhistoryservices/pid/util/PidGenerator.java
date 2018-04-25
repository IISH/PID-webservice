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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for the generation of a unique key for each handle. It must take care to never generate
 * two identical keys even under high concurrency. Therefore proven techniques like UUIDs are good candidates.
 *
 * @author Lucien van Wouw
 */

public class PidGenerator {

    private static String pattern = "[\\sa-zA-Z0-9-:" + escapeMetacharacters("._()[]{@$}=\\") + "]{1,240}";

    public static String getPid() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    public static String getPid(String na) {
        return na + "/" + getPid();
    }

    /**
     * Only characters are allowed that do not need a URL encoding. Maximum size of the pid is 240.
     *
     * @param pidCandidate
     * @return
     */
    public static String validatePid(String na, String pidCandidate) {

        final String expression = "^" + escapeMetacharacters(na) + "/" + pattern + "$";
        if (pidCandidate == null)
            throw new PidException("Pid syntax is invalid. The pid must match the expression: " + expression);
        Pattern p = Pattern.compile(expression);
        Matcher m = p.matcher(pidCandidate);
        if (!m.find(0))
            throw new PidException("Pid syntax is invalid. The pid must match the expression: " + expression);
        return pidCandidate;
    }

    /**
     * Escapes the regular expression's special characters.
     *
     * @param text
     * @return
     */
    private static String escapeMetacharacters(String text) {

        final Character[] metaCharacters = {'[', ']', '{', '}', '^', '$', '.', '|', '?', '*', '+', '(', ')', '\\'};
        ArrayList<Character> list = new ArrayList(Arrays.asList(metaCharacters));

        final StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (list.contains(c))
                sb.append("\\"+Character.toString(c));
            else
                sb.append(c);
        }
        return sb.toString();
    }
}