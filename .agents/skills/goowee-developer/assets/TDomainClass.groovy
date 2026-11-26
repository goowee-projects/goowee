package template

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TDomainClassName implements GormEntity, MultiTenant<TDomainClassName> {

    Long id
    LocalDateTime dateCreated
    LocalDateTime lastUpdated
    String usernameCreated
    String usernameUpdated

    String name

    static constraints = {
        name nullable: false
    }

}