package goowee.application

import goowee.elements.ElementsController

class MonitoringController implements ElementsController {

    def index() {
        redirect uri: '/monitoring'
    }

}
