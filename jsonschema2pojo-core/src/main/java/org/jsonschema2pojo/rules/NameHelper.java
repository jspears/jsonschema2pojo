/**
 * Copyright © 2010-2013 Nokia
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

package org.jsonschema2pojo.rules;

import static java.lang.Character.*;
import static org.apache.commons.lang.StringUtils.*;

import org.apache.commons.lang.WordUtils;

import org.jsonschema2pojo.GenerationConfig;

public class NameHelper {

    private static final String ILLEGAL_CHARACTER_REGEX = "[^0-9a-zA-Z_$]";

    private final GenerationConfig generationConfig;

    public NameHelper(GenerationConfig generationConfig) {
        this.generationConfig = generationConfig;
    }

    public String replaceIllegalCharacters(String name) {
        return name.replaceAll(ILLEGAL_CHARACTER_REGEX, "_");
    }

    public String normalizeName(String name) {
        name = capitalizeTrailingWords(name);

        if (isDigit(name.charAt(0))) {
            name = "_" + name;
        }

        return name;
    }

    public String capitalizeTrailingWords(String name) {
        char[] wordDelimiters = generationConfig.getPropertyWordDelimiters();
       

        if (containsAny(name, wordDelimiters)) {
        	 String capitalizedNodeName = WordUtils. capitalizeFully(name, wordDelimiters);
            name = name.charAt(0) + capitalizedNodeName.substring(1);

            for (char c : wordDelimiters) {
                name = remove(name, c);
            }
        }else if (name.equals(name.toUpperCase())){
        	name = WordUtils. capitalizeFully(name);
        }

        return name;
    }
}
