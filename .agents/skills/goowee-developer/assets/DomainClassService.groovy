package template

import goowee.security.SecurityService
import goowee.tenant.AuditOperation
import goowee.tenant.AuditService
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional
import groovy.contracts.Requires
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

@Slf4j
@CurrentTenant
@CompileStatic
class DomainClassService {

    SecurityService securityService
    AuditService auditService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    @CompileDynamic
    private DetachedCriteria<TDomainClass> buildQuery(Map filterParams, Boolean readOnly = false) {
        def query = TDomainClass.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                true
                    || name =~ "%${search}%"
            }
        }

        // Add additional filters here

        query.readOnly(readOnly)
        return query
    }

    private Map getFetchAll() {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        return fetch + [
            'hasManyRelationship': 'join',
        ]
    }

    private Map getFetch() {
        // Add only single-sided relationships here (Eg. references to other Domain Objects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        return [
            'relationshipName': 'join',
        ]
    }

    TDomainClass get(Serializable id) {
        return find(id: id)
    }

    TDomainClass find(Map filterParams) {
        return buildQuery(filterParams).get(fetch: fetchAll)
    }

    List<TDomainClass> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
    TDomainClass create(Map args = [:]) {
        if (args.usernameCreated == null) args.usernameCreated = securityService.currentUsername
        if (args.failOnError == null) args.failOnError = false

        TDomainClass obj = new TDomainClass(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @CompileDynamic
    @Requires({ args.id })
    TDomainClass update(Map args = [:]) {
        if (args.usernameUpdated == null) args.usernameUpdated = securityService.currentUsername
        if (args.failOnError == null) args.failOnError = false

        TDomainClass obj = get(args.id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    void delete(Serializable id) {
        TDomainClass obj = get(id)
        if (obj) {
            obj.delete(flush: true)
            auditService.log(AuditOperation.DELETE, obj)
        }
    }
}
