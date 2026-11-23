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
package goowee.application

import goowee.elements.PageService
import goowee.elements.pages.ShellService
import goowee.security.AuthenticationProviderService
import goowee.security.CryptoService
import goowee.security.SecurityService
import goowee.tenant.TenantPropertyService
import goowee.types.Money
import goowee.types.Quantity
import goowee.types.Types
import groovy.transform.CompileStatic

/**
 * @author Gianluca Sartori
 */

@CompileStatic
class BootStrap {

//    GroovyPagesTemplateEngine groovyPagesTemplateEngine
    GooweeApplication gooweeApplication
    SecurityService securityService
    AuthenticationProviderService authenticationProviderService
    CryptoService cryptoService
    TenantPropertyService tenantPropertyService
    PageService pageService
    ShellService shellService

    def init = {

        gooweeApplication.onPluginInstall {
            applicationPropertyService.install()
            tenantService.install()
            securityService.install()
        }

        gooweeApplication.onPluginTenantInstall { String tenantId ->
            authenticationProviderService.tenantInstall(tenantId)
            securityService.tenantInstall()
            cryptoService.tenantInstall()
            tenantPropertyService.tenantInstall()
            pageService.tenantInstall()
            shellService.tenantInstall()
        }

        gooweeApplication.beforeInit {
            securityService.init()

            // It works only from the 'app-test' within this project
            // not working as application dependency when compiled as plugin
//            groovyPagesTemplateEngine.groovyPageSourceDecorators = [new PageWhitespacesStripper() as GroovyPageSourceDecorator]

            Types.register(Money)
            Types.register(Quantity)
        }

        gooweeApplication.afterInit {
            applicationPropertyService.validateAll()
            securityService.registerFeatures()
        }

        gooweeApplication.beforeTenantInit { String tenantId ->
            cryptoService.tenantInit()
        }

        gooweeApplication.afterTenantInit { String tenantId ->
            tenantPropertyService.validateAll()
        }

    }

    def destroy = {
        //no-op
    }

}
