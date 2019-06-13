package com.xin.utils.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SqlUtil {

    public static String getSqlInParams(String str) {
        String params[] = str.split(",");
        return getSqlInParams(((Object[]) (params)));
    }

    public static String getSqlInParams(Object params[]) {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("(");
        for (int i = 0; i < params.length; i++)
            strbuf.append("?,");

        strbuf.deleteCharAt(strbuf.length() - 1);
        strbuf.append(")");
        return strbuf.toString();
    }

    /**
     * 修饰分页查询的sql
     *
     * @param sql      原sql
     * @param from     查询起始位置
     * @param pageSize 页面大小
     * @return 具有分页功能的查询sql
     */
    public static String decoratePageSql(Connection connection, String sql, int from, int pageSize) {
        int to = from + pageSize;
        if (ifUseSqlServer(connection)) {
            return "select * from ("
                    + "select row_number() as rowid, t_t.* from ("
                    + sql
                    + ") t_t"
                    + ") t_t_t where rowid > "
                    + from
                    + " and rowid <= "
                    + to;
        } else if (ifUseOracle(connection)) {
            return " select * from (" + " select t_t.*, rownum rn from (" + sql + " ) t_t " + " where rownum < " + to
                    + " ) where rn >= " + from;
        } else if (ifUseMySql(connection)) {
            return " select * from (" + sql + ") t_t limit " + from + ", " + pageSize;
        }
        throw new RuntimeException("PageQuery not supported of this DBtype!");
    }

    public static boolean ifUseSqlServer(Connection connection) {
        try {
            return connection.getMetaData().getDriverName().contains("SQL Server");
        } catch (SQLException e) {
        }
        return false;
    }

    public static boolean ifUseOracle(Connection connection) {
        try {
            return connection.getMetaData().getDriverName().contains("SQL Server");
        } catch (SQLException e) {
        }
        return false;
    }

    public static boolean ifUseMySql(Connection connection) {
        try {
            return connection.getMetaData().getDriverName().contains("SQL Server");
        } catch (SQLException e) {
        }
        return false;
    }
}
