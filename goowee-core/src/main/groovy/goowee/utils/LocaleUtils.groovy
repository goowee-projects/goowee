/*
 * Copyright 2021 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package goowee.utils

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * Utility class for handling localized properties in objects.
 * <p>
 * Provides methods to set, get, and check localized properties
 * on objects using language-specific suffixes (e.g., '_en', '_it').
 * </p>
 *
 * Author: Gianluca Sartori
 */
@Slf4j
@CompileStatic
class LocaleUtils {

    static String getFlagCode(String languageCode) {
        // Per riferimento:
        // - Java Locale docs:                     https://docs.oracle.com/javase/7/docs/api/java/util/Locale.html
        // - Icon library:                         https://www.flaticon.com/packs/countrys-flags
        // - Locale uses ISO 639 Alpha 2 codes:    https://www.loc.gov/standards/iso639-2/php/English_list.php
        // - Flags uses ISO 3166-1 Alpha 2 codes:  https://www.iso.org/obp/ui/
        Map localeToFlag = [
                en   : 'gb',    // defaults to UK (cause we're european ;-)
                en_gb: 'gb',
                en_us: 'us',
                pt_pt: 'pt',
                pt_br: 'br',
                zh_cn: 'cn',
                cs   : 'cs_cz',
                da   : 'dk',
                ja   : 'jp',
                nb   : 'no',
        ]
        String lang = languageCode.toLowerCase()
        String langFlag = localeToFlag[lang] ?: lang
        return langFlag
    }

    /**
     * Sets a localized property value on an object.
     * <p>
     * If no language is provided, the default English ('_en') property is set.
     * If the object does not have the specified localized property, logs an error.
     * </p>
     *
     * @param obj the object on which to set the property
     * @param propertyName the base name of the property
     * @param value the value to assign
     * @param language the language code (optional, e.g., 'en', 'it')
     */
    static void setLocalizedProperty(Object obj, String propertyName, String value, String language = null) {
        if (!language) {
            obj[propertyName + '_en'] = value
        }

        Boolean prop = hasProperty(propertyName + '_' + language)
        if (prop) {
            obj[propertyName + '_' + language] = value
        } else {
            log.error "NOT IMPLEMENTED: Cannot set '${propertyName}' with locale '${language}', please contact the developers."
        }
    }

    /**
     * Gets the localized value of a property from an object.
     * <p>
     * Returns the value for the specified language if it exists;
     * otherwise, returns the default English ('_en') value.
     * </p>
     *
     * @param obj the object from which to get the property
     * @param propertyName the base name of the property
     * @param language the language code to retrieve
     * @return the localized value if available, otherwise the default value
     */
    static Object getLocalizedProperty(Object obj, String propertyName, String language) {
        def hasProperty = hasLocalizedProperty(obj, propertyName, language)
        def defaultValue = obj[propertyName + '_en']
        def localizedValue = null

        if (hasProperty) {
            localizedValue = obj[propertyName + '_' + language]
        }

        return localizedValue ?: defaultValue
    }

    /**
     * Checks if an object has a localized property for a given language.
     *
     * @param obj the object to check
     * @param propertyName the base name of the property
     * @param language the language code to check
     * @return true if the localized property exists, false otherwise
     */
    static Boolean hasLocalizedProperty(Object obj, String propertyName, String language) {
        return obj.hasProperty(propertyName + '_' + language)
    }

}
