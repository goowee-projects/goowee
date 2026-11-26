# Application Initialization

- [ ] If the project is not a Grails project, stop here.
- [ ] Verify that the following dependency is present in `build.gradle`: `implementation "org.apache.grails:grails-core"`. If it is missing, tell the user this is not a Grails project and stop here.
- [ ] Add the following dependency to `build.gradle`: `implementation "org.goowee:goowee-core:2.0.0-RC1"`.
- [ ] If you cannot create a Goowee project stop here.
- [ ] Replace `BootStrap.groovy` with the `BootSatrp.groovy` asset and create the `TenantInstallService` with an `install()` method.
- [ ] Append the EXACT UNEDITED `application.yml` asset content into the application `application.yml`. Replace `${project-name}` with the value of Gradle `rootProject.name`.
- [ ] Copy the `messages.properties` asset into. `grails-app/i18n/messages.properties`.

- [ ] Create the following files:
  - [ ] `grails-app/assets/javascripts/goowee.js`
  - [ ] `grails-app/assets/stylesheets/goowee.css`
  - [ ] DO NOT include them in application.js or application.css

- [ ] DO NOT add code to:
  - [ ] `UrlMappings.groovy` (IMPORTANT)
  - [ ] `application.css`
  - [ ] `application.js`

- [ ] Set the H2 database URL to `jdbc:h2:./${info.app.name}/${info.app.name};LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE`.
- [ ] Set the `dbCreate` property to `update` for the development environment.

- [ ] Add `/project-name/` to the root `.gitignore`.
