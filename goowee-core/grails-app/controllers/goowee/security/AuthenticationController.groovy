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
package goowee.security


import goowee.elements.ElementsController
import goowee.elements.pages.Login
import goowee.properties.TenantPropertyService
import goowee.tenants.TenantService
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

/**
 * Goowee Security
 *
 * @author Gianluca Sartori
 * @author Francesco Piceghello
 */
@Secured(['permitAll'])
class AuthenticationController implements ElementsController {

    TenantService tenantService
    SecurityService securityService
    TenantPropertyService tenantPropertyService

    def login() {
        if (securityService.isLoggedIn()) {
            redirect uri: securityService.loginLandingPage
            return
        }

        def hostTenant = tenantService.getByHost(requestHeader.host)
        def tenantId =  hostTenant?.tenantId ?: tenantService.defaultTenantId

        tenantService.withTenant(tenantId) {
            def loginArgs = [
                    backgroundImage    : tenantPropertyService.getString('LOGIN_BACKGROUND_IMAGE', true),
                    logoImage          : tenantPropertyService.getString('LOGIN_LOGO', true),
                    autocomplete       : tenantPropertyService.getBoolean('LOGIN_AUTOCOMPLETE', true),
                    copy               : tenantPropertyService.getString('LOGIN_COPY', true),
                    registerUrl        : tenantPropertyService.getString('LOGIN_REGISTRATION_URL', true),
                    passwordRecoveryUrl: tenantPropertyService.getString('LOGIN_PASSWORD_RECOVERY_URL', true),
            ]
            display page: createPage(Login, loginArgs)
        }
    }

    @Secured(['ROLE_USER'])
    def afterLogin() {
        securityService.executeAfterLogin()
        if (securityService.isLoginDenied()) {
            forward action: 'logout'
            return
        }

        ///////////////////////////////////////
        // IF YOU'RE HERE THE USER LOGGED IN //
        ///////////////////////////////////////

        // Getting the user preferences
        TUser user = securityService.currentUser
        decimalFormat = user.decimalFormat
        prefixedUnit = user.prefixedUnit
        symbolicCurrency = user.symbolicCurrency
        symbolicQuantity = user.symbolicQuantity
        invertedMonth = user.invertedMonth
        twelveHours = user.twelveHours
        firstDaySunday = user.firstDaySunday
        fontSize = user.fontSize
        animations = user.animations

        // We redirect the user to the configured location
        if (params.ajax) { // Default login
            def message = [
                    login   : true,
                    success : true,
                    redirect: securityService.loginLandingPage,
            ]
            render message as JSON

        } else { // Legacy, keep it just in case
            redirect uri: securityService.loginLandingPage
        }
    }

    def logout() {
        securityService.executeAfterLogout()
        redirect uri: securityService.logoutLandingPage
    }

    def afterLogout() {
        // Serve a gestire il DENY_AUTHORIZATION_MESSAGE ovvero l'avviso da mostrare nel caso l'utente non sia autorizzato
        // ad accedere per motivi legati all'applicazione
        if (session[SecurityService.DENY_AUTHORIZATION_MESSAGE]) {
            def message = [customError: session[SecurityService.DENY_AUTHORIZATION_MESSAGE]]
            render message as JSON

        } else {
            redirect uri: securityService.logoutLandingPage
        }
    }

    def err403() {
        withFormat {
            json {
                render status: 403
            }
            '*' {
                render view: '/goowee/authentication/403'
            }
        }
    }

}
