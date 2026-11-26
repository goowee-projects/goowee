# Controllers

Controllers are written using a scripting style. Being the highest layer of the application, they focus on readability and expressive, concise logic.

- [ ] DO NOT map any controller in `UrlMappings.groovy`, this is handled by Goowee
- [ ] Define `@Slf4j` and `@Secured`
- [ ] Implement `ElementsController`
- [ ] Services are injected with explicit types.
- [ ] Use `def` to declare variables and methods.
- [ ] To create a controller, a domain class and a service must exist in the project. If they do not exist create them.
- [ ] Use the `DomainClassController.groovy` asset to create a new controller.
- [ ] Maintain the code styling and formatting of the controller template
- [ ] The controller name is not prefixed with `T`.
- [ ] Use `createContent(...)`.
- [ ] Define all required actions.
- [ ] Use `display` for responses.
- [ ] Define forms in `buildForm`.
- [ ] Keep business logic out of controllers.
- [ ] Build UI only with Goowee Elements APIs.
- [ ] Implement the related feature in `BootStrap.groovy`. For the first controller of the application, set the `favourite` parameter to `true`.
- [ ] Implement the controller using the available Goowee components and controls.
- [ ] Update the `messages.properties` file according to the new controller.

## Forbidden Deviations
Generated code MUST NOT:

- Rename `c`, `obj`, or `params`.
- Inline form definitions.
- Skip `with {}` blocks.
- Introduce business logic.
- Bypass `display`.

## Forbidden Practices
- Business logic inside controllers.
- Direct persistence access.
- Bypassing `createContent`.
- Building UI outside Goowee Elements APIs.
- Defining forms outside `buildForm`.
- Heavy logic inside `eachRow`.
