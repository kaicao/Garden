package com.kaicao.mariadb4j;

import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;

/**
 * Created by kaicao on 03/11/15.
 */
public class MariaDB4JTest {
    public static void main(String[] args) throws Exception {
        DBConfigurationBuilder builder = new CustomDBConfigurationBuilder()
                .setDataDir("mariadb4j")
                .setPort(4099);
        DB db = DB.newEmbeddedDB(builder.build());

        db.start();
        db.createDB("TEST");
        db.run("CREATE TABLE A (id int primary key, t text);", null, null, "TEST");
    }

    public static class CustomDBConfigurationBuilder extends DBConfigurationBuilder {
        public CustomDBConfigurationBuilder() {
        }

        @Override
        protected String getBinariesClassPathLocation() {
            return "mariadb10_0_13";
        }
    }
}
