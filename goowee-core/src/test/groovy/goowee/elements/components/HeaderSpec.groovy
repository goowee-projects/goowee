/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package goowee.elements.components

import grails.core.DefaultGrailsApplication
import grails.util.GrailsWebMockUtil
import grails.util.Holders
import org.grails.core.support.GrailsApplicationDiscoveryStrategy
import org.springframework.context.support.GenericApplicationContext
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

class HeaderSpec extends Specification {

    GenericApplicationContext applicationContext

    def setup() {
        def webRequest = GrailsWebMockUtil.bindMockWebRequest()
        webRequest.controllerName = 'books'
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

    def cleanup() {
        RequestContextHolder.resetRequestAttributes()
        Holders.clear()
        applicationContext.close()
    }

    void 'uses text and textArgs for the header title'() {
        when:
        Header header = new Header(id: 'header', text: 'books.index.header.title', textArgs: ['Goowee'])

        then:
        header.text == 'books.index.header.title'
        header.textArgs == ['Goowee']
    }

    void 'allows the title to be updated after construction'() {
        when:
        Header header = new Header(id: 'header')
        header.text = 'books.index.header.updated'
        header.textArgs = ['Elements']

        then:
        header.text == 'books.index.header.updated'
        header.textArgs == ['Elements']
    }
}
