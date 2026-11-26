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
package apptest

import custom.CustomPage
import goowee.elements.ElementsController
import goowee.elements.components.Form
import goowee.elements.components.Label
import goowee.elements.components.Table
import goowee.elements.components.TableRow
import goowee.security.SecurityService
import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class CustomPageController implements ElementsController {

    SecurityService securityService

    def index() {
        def p = createPage(CustomPage)
        def header = p.content.addComponent(Form, 'headerForm')
        header.with {
            addField(
                class: Label,
                id: 'status',
                html: buildStatusLabel(getCurrentStatus()),
                cols: 12,
                displayLabel: false,
            )
        }

        def table = p.content.addComponent(Table)
        table.with {
            hasHeader = false
            rowHighlight = false
//            filters.with {
//                folded = false
//                addField(
//                        class: Select,
//                        id: 'clientRef',
//                )
//                addField(
//                        class: TextField,
//                        id: 'markplateKey',
//                )
//            }

            actionbar.with {
                actions.group = true
//                addAction(
//                        action: 'onConnect'
//                )
//                addAction(
//                        action: 'onExecute'
//                )
//                addAction(
//                        action: 'onDisconnect'
//                )
//                addAction(
//                        action: 'onTestStatus',
//                )
            }
            sortable = [
                username: 'asc',
            ]
            columns = [
                'username',
                'firstname',
                'lastname',
            ]

            actions.removeTailAction()
            actions.removeDefaultAction()

            body.eachRow { TableRow row, Map values ->
            }
        }

        def filters = table.filterParams
        table.body = securityService.listUser(filters, params)
        table.paginate = securityService.countUser(filters)

        display page: p
    }

    private String buildStatusLabel(Map status) {
        return """
            <div class="${status.color}" style="padding: 10px; margin-top: -18px; text-align: center; color: white;">
            <h1>${status.description}</h1>
            </div>
        """
    }

    private Map getCurrentStatus() {
        String status
        try {
            status = new Random().nextInt(9 - 0 + 1) + 0

        } catch (Exception ignore) {
            status = '0'
        }

        return getStatus(status)
    }

    private Map getStatus(String status) {
        return [
            status     : status,
            description: getStatusDescription(status),
            color      : 'bg-light',
        ]
    }

    private String getStatusDescription(String status) {
        Map descriptions = [
            '0': 'MARKER OFF, PRESS "EMERGENCY RESET"',
            '1': 'MARKER HEATING UP, PLEASE WAIT...',
            '2': 'MARKER OFF, TURN THE KEY',
            '3': 'MARKER IN STANDBY',
            '4': 'MARKER READY, CLOSE THE COVER TO PRINT',
            '5': 'MARKER READY TO PRINT',
            '6': 'MARKER READY, SHUTTER CLOSED',
            '7': 'PRINTING',
            '8': 'PRINTING, SHUTTER CLOSED',
            '9': 'MARKER OFF, REMOVE THE COVER',
            ':': 'SYSTEM ERROR, USE THE MAIN "ON/OFF" SWITCH',
        ]

        return descriptions[status]
    }

}
