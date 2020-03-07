package ru.laz.db.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.io.Serializable;

public class PhysicalNamingStrategyImpl extends PhysicalNamingStrategyStandardImpl implements Serializable {

        public static final PhysicalNamingStrategyImpl INSTANCE = new PhysicalNamingStrategyImpl();

        @Override
        public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
            String nameModified = name.getText();
            // Do whatever you want with the name modification
            return new Identifier(nameModified, name.isQuoted());
        }
}
