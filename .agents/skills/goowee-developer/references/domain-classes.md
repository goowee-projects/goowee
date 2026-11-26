# Domain Classes

Goowee domain classes have the following characteristics:

- [ ] Are located in `grails-app/domain/`
- [ ] Are prefixed with the letter `T` (for example, `TCompany`).
- [ ] Always implement a corresponding service class in `grails-app/services/`. If it does not exist, create one for the domain class. See the `services` reference
- [ ] Register a pretty printer for the class in the `gooweeApplication.onInit` method.
- [ ] Always implement `GormEntity` and `MultiTenant<>`
- [ ] Always define the `constraints` block
- [ ] Decide which properties are `nullable: false` depending on the domain and business logic
- [ ] Always implement the following classes and fields:

```groovy
import java.time.LocalDateTime

@GrailsCompileStatic
class TCompany implements GormEntity, MultiTenant<TCompany> {

    Long id
    LocalDateTime dateCreated
    LocalDateTime lastUpdated

}
```
