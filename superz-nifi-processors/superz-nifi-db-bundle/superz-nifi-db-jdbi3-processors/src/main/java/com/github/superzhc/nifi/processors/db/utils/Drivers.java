package com.github.superzhc.nifi.processors.db.utils;

public enum Drivers {

    MySQL8("com.mysql.cj.jdbc.Driver"), MySQL5("com.mysql.jdbc.Driver"), Postgres("org.postgresql.Driver"), SQLite("org.sqlite.JDBC"), Oracle("oracle.jdbc.driver.OracleDriver"), SQLServer("com.microsoft.sqlserver.jdbc.SQLServerDriver"), SQLServer_v2("com.microsoft.jdbc.sqlserver.SQLServerDriver"), DB2("com.ibm.db2.jdbc.app.DB2.Driver"), Sysbase("com.sybase.jdbc.SybDriver"), ODBC("sun.jdbc.odbc.JdbcOdbcDriver");

    private String driver;

    private Drivers(String driver) {
        this.driver = driver;
    }

    public String getDriver() {
        return this.driver;
    }
}
