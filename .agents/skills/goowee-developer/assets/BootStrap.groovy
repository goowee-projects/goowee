package template

import goowee.application.GooweeApplication

class BootStrap {

    GooweeApplication gooweeApplication
    TenantInstallService tenantInstallService

    def init = {
        gooweeApplication.onTenantInstall { Stirng tenantId ->
            tenantInstallService.install()
        }

        gooweeApplication.onInit {
            // Register features, prettyPrinters, credits
        }
    }

}
