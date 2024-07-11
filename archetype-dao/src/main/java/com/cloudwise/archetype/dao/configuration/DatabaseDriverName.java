package com.cloudwise.archetype.dao.configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.springframework.boot.jdbc.DatabaseDriver;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum DatabaseDriverName {

    MYSQL(DbType.MYSQL, com.mysql.cj.jdbc.Driver.class.getName()),
    MARIADB(DbType.MARIADB, org.mariadb.jdbc.Driver.class.getName()),
    POSTGRE_SQL(DbType.POSTGRE_SQL, org.postgresql.Driver.class.getName()),
    DM(DbType.DM, dm.jdbc.driver.DmDriver.class.getName()),
    XU_GU(DbType.XU_GU, "com.xugu.cloudjdbc.Driver"),
    KINGBASE_ES(DbType.KINGBASE_ES, "com.kingbase8.Driver"),
    PHOENIX(DbType.PHOENIX, DatabaseDriver.PHOENIX.getDriverClassName()),
    GAUSS(DbType.GAUSS, org.opengauss.Driver.class.getName()),
    CLICK_HOUSE(DbType.CLICK_HOUSE, "com.clickhouse.jdbc.ClickHouseDriver"),
    GBASE(DbType.GBASE, "com.gbase.jdbc.Driver"),
    GBASE_8S(DbType.GBASE_8S, "com.gbase.jdbc.Driver"),
    OSCAR(DbType.OSCAR, com.oscar.Driver.class.getName()),
    SYBASE(DbType.SYBASE, "com.sybase.jdbc4.jdbc.SybDriver"),
    OCEAN_BASE(DbType.OCEAN_BASE, com.alipay.oceanbase.jdbc.Driver.class.getName()),
    FIREBIRD(DbType.FIREBIRD, DatabaseDriver.FIREBIRD.getDriverClassName()),
    HIGH_GO(DbType.HIGH_GO, "com.highgo.jdbc.Driver"),
    CUBRID(DbType.CUBRID, "cubrid.jdbc.driver.CUBRIDDriver"),
    IMPALA(DbType.IMPALA, "com.cloudera.impala.jdbc41.Driver"),
    VERTICA(DbType.VERTICA, "com.vertica.jdbc.Driver"),
    XCloud(DbType.XCloud, "com.bonc.xcloud.jdbc.XCloudDriver"),
    OTHER(DbType.OTHER, null);

    /**
     * 数据库类型.
     */
    private final DbType dbType;
    /**
     * 驱动类名.
     */
    private final String driverClassName;

    /**
     * 根据url识别数据库和驱动类型，返回驱动全限定名
     */
    public static Optional<String> getDriverClassNameByUrl(String jdbcUrl) {
        DbType dbType = JdbcUtils.getDbType(jdbcUrl);
        return Optional.ofNullable(Arrays.stream(values()).filter(type -> type.dbType.equals(dbType)).findFirst().orElse(OTHER).getDriverClassName());
    }

    public String getDriverClassName() {
        if (Objects.isNull(this.driverClassName)) {
            return null;
        }
        try {
            //make sure we don't have an old jdbc driver that doesn't have this class
            Class.forName(this.driverClassName);
            return this.driverClassName;
        } catch (ClassNotFoundException e) {
            //
            // Try to load the class again with the current thread classloader
            //
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Class.forName(this.driverClassName, true, cl);
                return this.driverClassName;
            } catch (ClassNotFoundException cnfe) {
                return null;
            }
        }
    }

    private DatabaseDriverName(final DbType dbType, final String driverClassName) {
        this.dbType = dbType;
        this.driverClassName = driverClassName;
    }

}