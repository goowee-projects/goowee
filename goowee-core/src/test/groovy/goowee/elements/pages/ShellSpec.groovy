/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package goowee.elements.pages

import goowee.elements.core.Feature
import grails.core.DefaultGrailsApplication
import grails.util.GrailsWebMockUtil
import grails.util.Holders
import org.grails.core.support.GrailsApplicationDiscoveryStrategy
import org.springframework.context.support.GenericApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

class ShellSpec extends Specification {

    GenericApplicationContext applicationContext

    void setup() {
        def webRequest = GrailsWebMockUtil.bindMockWebRequest()
        webRequest.controllerName = 'shell'
        webRequest.actionName = 'index'

        applicationContext = new GenericApplicationContext()
        applicationContext.refresh()
        DefaultGrailsApplication application = new DefaultGrailsApplication()
        application.mainContext = applicationContext
        Holders.addApplicationDiscoveryStrategy([
            findGrailsApplication: { application },
            findApplicationContext: { applicationContext },
        ] as GrailsApplicationDiscoveryStrategy)
    }

    void cleanup() {
        RequestContextHolder.resetRequestAttributes()
        Holders.clear()
        applicationContext.close()
    }

    void 'uses the feature i18n prefix for main and user menu entries'() {
        given:
        ShellConfig config = new ShellConfig()
        config.features.main = new Feature([:])
        config.features.main.add(controller: 'crud')
        config.features.user = new Feature([:])
        config.features.user.add(namespace: 'security', controller: 'user')

        when:
        Shell shell = new Shell(id: 'shell', config: config)

        then:
        shell.menu.items*.textPrefix == ['feature']
        shell.userMenu.items*.textPrefix == ['feature']
    }
}
