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
package goowee.database

import groovy.transform.CompileStatic
import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment

/**
 * Table names start with "t_" to avoid conflicting with database keywords
 *
 * @author Gianluca Sartori
 */

@CompileStatic
class TNamingStrategy implements PhysicalNamingStrategy {

    @Override
    Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        String className = logicalName.text
        String tableName = className.startsWith('T')
                ? className.drop(1)
                : className
        return Identifier.toIdentifier(tableName.toLowerCase())

    }

    @Override
    Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment environment) {
        Identifier.toIdentifier(name.text.toLowerCase())
    }

    @Override
    Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment environment) {
        return name
    }

    @Override
    Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment environment) {
        return name
    }

    @Override
    Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment environment) {
        return name
    }

}