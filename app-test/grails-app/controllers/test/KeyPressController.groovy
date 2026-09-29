package test

import goowee.elements.ElementsController
import goowee.security.SecurityService

class KeyPressController implements ElementsController {

    SecurityService securityService

    def onKeyPress() {
        String physicalId = keyPressed
        def user = securityService.getUserByPhysicalId(physicalId)
        if (user) {
            display controller: 'authentication', action: 'logout'
            return
        }

        display
    }
}
