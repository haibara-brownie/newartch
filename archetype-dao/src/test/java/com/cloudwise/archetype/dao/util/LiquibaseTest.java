package com.cloudwise.archetype.dao.util;

import org.apache.commons.lang3.ArrayUtils;

public class LiquibaseTest {

    public static void main(String[] args) {
        liquibase.integration.commandline.LiquibaseCommandLine.main(ArrayUtils.toArray(
                "--classpath=liquibase-core-4.17.0.jar:picocli-4.6.3.jar:mysql-connector-java-8.0.27.jar:snakeyaml-1.33.jar",
                "--driver='com.mysql.cj.jdbc.Driver'",
                "--changeLogFile=config/dbchangelogs/changelog.xml",
                "--url=jdbc:mysql://10.0.12.212:18103/cw_archetype?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true",
                "--username=Rootmaster",
                "--password=Rootmaster@777",
                "update",
                "--log-level=debug"));
    }

}
