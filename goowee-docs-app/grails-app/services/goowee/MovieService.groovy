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
package goowee

import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional
import groovy.contracts.Requires
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CurrentTenant
@CompileStatic
class MovieService {

    @CompileDynamic
    private DetachedCriteria<TMovie> buildQuery(Map filterParams, Boolean readOnly = false) {
        def query = TMovie.where {}

        if (filterParams.containsKey('id')) {
            query = query.where { id == filterParams.id }
        }
        if (filterParams.containsKey('dateCreated')) {
            query = query.where { dateCreated == filterParams.dateCreated }
        }
        if (filterParams.containsKey('title')) {
            query = query.where { title == filterParams.title }
        }
        if (filterParams.containsKey('released')) {
            query = query.where { released == filterParams.released }
        }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                title =~ "%${search}%"
            }
        }

        // Add additional filters here

        query.readOnly(readOnly)
        return query
    }

    private Map getFetchAll() {
        // Add any relationship here
        // like references to other DomainObjects or hasMany
        return fetch + [
                actors: 'join',
        ]
    }

    private Map getFetch() {
        // Add only single-sided relationships here
        // like references to other Domain Objects
        // Do not fetch hasMany relationships in paginated lists,
        // they can cause pagination issues.
        return [:]
    }

    TMovie get(Serializable id) {
        return find(id: id)
    }

    TMovie find(Map filterParams) {
        return buildQuery(filterParams).get(fetch: fetchAll)
    }

    List<TMovie> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) {
            fetchParams.sort = [dateCreated: 'asc']
        }
        if (!fetchParams.fetch) {
            fetchParams.fetch = fetch
        }

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
    TMovie create(Map args = [:]) {
        if (args.failOnError == null) {
            args.failOnError = false
        }

        TMovie obj = new TMovie(args)
        obj.save(flush: true, failOnError: args.failOnError)

        return obj
    }

    @Transactional
    @CompileDynamic
    @Requires({ args.id })
    TMovie update(Map args = [:]) {
        if (args.failOnError == null) {
            args.failOnError = false
        }

        TMovie obj = get(args.id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)

        return obj
    }

    @Transactional
    void delete(Serializable id) {
        TMovie obj = get(id)
        if (obj) {
            obj.delete(flush: true)
        }
    }
}
