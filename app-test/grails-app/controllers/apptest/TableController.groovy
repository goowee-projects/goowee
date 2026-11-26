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

import goowee.application.GooweeApplication
import goowee.elements.ElementsController
import goowee.elements.components.Table
import goowee.elements.components.TableRow
import goowee.elements.controls.ProgressBar
import goowee.elements.controls.Select
import goowee.elements.controls.TextField
import goowee.elements.style.TextAlign
import goowee.elements.style.TextStyle
import goowee.types.Money
import jakarta.annotation.PostConstruct

class TableController implements ElementsController {

    GooweeApplication gooweeApplication
    PersonService personService

    @PostConstruct
    void init() {
        gooweeApplication.registerPrettyPrinter(TCompany, 'Employee at \'${it.name}\'')
        gooweeApplication.registerPrettyPrinter('COOL_NAME', '${it}, you are awesome!')

        gooweeApplication.registerTransformer('UPPERCASE') { value ->
            return value.toString().toUpperCase()
        }
        gooweeApplication.registerTransformer('MULTIPLY') { value ->
            println 'BEFORE ' + value
            Object result = value ? value * 100 : new Money(1000)
            println 'AFTER ' + result
            return result
        }
    }

    def index() {
        def c = createTable()
        println params
        display content: c
    }

    private createTable() {
        def c = createContent()
        def table = c.addComponent(Table)
        table.with {
            actionbar.addAction(
                action: 'action1',
                //confirmMessage: 'Confermare?',
            )

            filters.with {
                addField(
                    class: Select,
                    id: 'user1',
                    optionsFromRecordset: personService.list(),
                    keys: ['id'],
                )
                addField(
                    class: TextField,
                    id: 'textfield',
                )
            }

            rowStriped = true
            columns = [
                'name',
                'postcode',
                'address',
                'empty',
                'company',
                'salary',
                'active',
            ]
            sortable = [
                name: 'asc',
            ]
            labels = [
                email : 'Runtime generated label EMAIL',
                active: '', // Hide the label
                empty : '',
            ]
            transformers = [
                address: 'UPPERCASE',
                salary : 'MULTIPLY',
            ]
            prettyPrinters = [
                name: 'COOL_NAME',
            ]
            prettyPrinterProperties = [
                salary  : [decimals: 0, symbolicCurrency: false, prefixedUnit: true],
                postcode: [renderZero: '-'],
            ]

            actions.addAction(action: 'commonAction')

            groupActions.addAction(action: 'groupAction1')
            groupActions.addAction(action: 'groupAction2')

            max = 4
            header.eachRow { TableRow row ->
                row.cells.postcode.backgroundColor = '#000077'
                row.cells.postcode.textColor = '#ffffff'
                row.cells.postcode.textAlign = TextAlign.END
                row.cells.name.textAlign = TextAlign.END
                row.cells.name.prettyPrinterProperties.textArgs = [3]
            }

            body.eachRow { row ->
                processMyRow(row)
            }

            // Assignment of "header", "body" and "footer" data must be done after ".eachRow" setup
            body = personService.list(params)
            footer = [
                [actions: 'Subtotal', name: '10.000', postcode: '200'],
                [actions: 'Total', name: '10.000', postcode: '200'],
            ]
            paginate = personService.count()
        }

        /*page.addComponent(class: Table, id: 'table2').with {
            removeDefaultAction()
        }

        page.table2.body = [['':'']]*/

        return c
    }

    private processMyRow(TableRow row) {
        // Color the column
        row.cells.name.backgroundColor = '#00dd00'

        row.first?.actions.removeAllActions()
        row.first?.with {
            backgroundColor = 'rgba(204, 204, 0, 0.3)'
        }

        row.last?.with {
            actions.removeTailAction()
            actions.removeAction(action: 'commonAction')
            removeSelection()

            textColor = 'red'
            textStyle = [TextStyle.LINE_THROUGH, TextStyle.BOLD]

            actions.defaultAction?.text = 'Click me!'
            actions.defaultAction?.infoMessage = 'Hello, my friend, how are you?'
            actions.defaultAction?.on(event: 'click', action: 'onActionClick')

            // Cell features
            cells.postcode.textAlign = TextAlign.CENTER
            cells.postcode.icon = 'fa-envelope'
            cells.postcode.url = 'mailto:g.sartori@gmail.com'

            cells.name.textAlign = TextAlign.CENTER
            //cells.username.html = '<a href="http://www.google.it">' + asset.image(src: "elements/brand/logo.png") + '</a>'

            // Color the row
            backgroundColor = 'rgba(204, 0, 0, 0.3)'

            // Color the cell
            cells.address.backgroundColor = '#000077'
            cells.address.textColor = '#ffffff'

            // Remove the cell color
            //row.cells.firstname.cssClass = ''
        }

        if (row.values.name == 'admin') {
            row.cells.name.prettyPrinterProperties.prettyPrinter = null

            row.actions.removeDefaultAction()
            row.actions.removeAction(action: 'commonAction')

            //row.values.lastname = LocalTime.now()
            /*row.cells.lastname.control = [
                    class: TimeField,
                    id: 'TimeField1',
            ]*/
            row.cells.address.setComponent(
                class: ProgressBar,
                id   : 'progress1',
                max  : 100,
                value: 20,
            )
            row.cells.company.setComponent(
                class: ProgressBar,
                id   : 'progress2',
            )
        }

        if (row.values.name == 'user1') {
            //row.hasSelection = false
            row.values.address = 'runtime-overriden@email.com'

            row.actions.addSeparator()
            row.actions.addAction(action: 'rowAction')
            row.cells.postcode.setComponent(
                class: TextField,
                id   : 'TextField1',
            )
            row.cells.postcode.colspan = 2
        }

        if (row.values.name == 'user2') {
            row.actions.removeDefaultAction()
            row.actions.removeTailAction()
        }

        if (row.actions.hasMenuActions()) {
            row.actions.addSeparator('test.separator.label')
            row.actions.addAction(controller: 'myAdminController', action: 'rowActionAdmin', params: [x: 1, y: 2])
        }
    }

    def onActionClick() {
        def t = createTable()

        t.table.body.eachRow { row ->

            processMyRow(row)

            if (row.values.username == 'user2') {
                // Color the row
                row.backgroundColor = '#aa0000'
            }
        }

        def newActionButton = t.table.addControl(
            class: Button,
            id: 'newAction',
            isDefaultAction: true,
        )
        newActionButton.addAction(action: 'action2')

        t.replace(component: 'table.dataset', with: t.table.dataset)
        t.replace(component: 'table.actionbar.action1', with: newActionButton)

        display transition: t
    }
}
