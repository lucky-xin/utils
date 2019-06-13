//package com.xin.jdbc;
//
//public class PageQueryDecorator {
//
//
//    private static int MAXROW = 5000;
//
//    public static String createQuery(String module, String sql, int pageSize,
//                                     int pageNo, String sort) {
//        return createQuery(module, null, sql, pageSize, pageNo, sort);
//    }
//
//    public static String createQuery(String module, String datasourceName,
//                                     String sql, int pageSize, int pageNo, String sort) {
//        int from = (pageNo - 1) * pageSize;
//        int to = pageNo * pageSize;
//        if ((new DaoFactory()).ifUseSqlServer(module, datasourceName))
//            return (new StringBuilder(
//                    "select * from (select row_number() over(order by "))
//                    .append(sort).append(") as rowid, t_t.* from (")
//                    .append(sql).append(") t_t")
//                    .append(") t_t_t where rowid > ").append(from)
//                    .append(" and rowid <= ").append(to).toString();
//        if ((new DaoFactory()).ifUseOracle(module, datasourceName))
//            return (new StringBuilder(
//                    " select * from ( select t_t.*, rownum rn from ("))
//                    .append(sql).append(" order by ").append(sort)
//                    .append(" ) t_t ").append(" where rownum < ").append(to)
//                    .append(" ) where rn >= ").append(from).toString();
//        if ((new DaoFactory()).ifUseMySql(module, datasourceName)) {
//            if (StringUtil.isObjectNull(sort))
//                return (new StringBuilder(" select * from (")).append(sql)
//                        .append(") t_t limit ").append(from).append(", ")
//                        .append(pageSize).toString();
//            else
//                return (new StringBuilder(" select * from (")).append(sql)
//                        .append(") t_t order by ").append(sort)
//                        .append(" limit ").append(from).append(", ")
//                        .append(pageSize).toString();
//        } else {
//            throw new RuntimeException(
//                    "PageQuery not supported of this DBtype!");
//        }
//    }
//
//    static String createQuerySetMaxRow(String module, String datasourceName,
//                                       String sql) {
//        if ((new DaoFactory()).ifUseSqlServer(module, datasourceName))
//            return sql.replaceFirst("select",
//                    (new StringBuilder("select TOP ")).append(MAXROW)
//                            .toString());
//        if ((new DaoFactory()).ifUseOracle(module, datasourceName))
//            return (new StringBuilder(" select * from (")).append(sql)
//                    .append(") t_t").append(" where rownum < ").append(MAXROW)
//                    .toString();
//        if ((new DaoFactory()).ifUseMySql(module, datasourceName))
//            return (new StringBuilder(" select * from (")).append(sql)
//                    .append(") t_t limit 0, ").append(MAXROW).toString();
//        else
//            throw new RuntimeException(
//                    "PageQuery not supported of this DBtype!");
//    }
//
//    public static String createPagedQuery(RequestContext context,
//                                          String datasourceName, String sql) {
//        int pageSize = Integer.parseInt((String) context
//                .getParameter("pageSize"));
//        int pageNo = Integer.parseInt((String) context.getParameter("pageNo"));
//        String sort = (String) context.getParameter("sort");
//        String module = context.getModule();
//        int from = (pageNo - 1) * pageSize;
//        int to = pageNo * pageSize;
//        if ((new DaoFactory()).ifUseSqlServer(module, datasourceName)) {
//            sql = sql
//                    .replaceFirst(" from ", (new StringBuilder(
//                            " , row_number() over(order by ")).append(sort)
//                            .append(") as rowid from ").toString());
//            sql = (new StringBuilder("select * from (")).append(sql)
//                    .append(") t where t.rowid > ").append(from)
//                    .append(" and t.rowid <= ").append(to).toString();
//            return sql;
//        }
//        if ((new DaoFactory()).ifUseOracle(module, datasourceName))
//            return (new StringBuilder(
//                    " select * from ( select t_t.*, rownum rn from ("))
//                    .append(sql).append(" order by ").append(sort)
//                    .append(" ) t_t ").append(" where rownum < ").append(to)
//                    .append(" ) where rn >= ").append(from).toString();
//        if ((new DaoFactory()).ifUseMySql(module, datasourceName))
//            return (new StringBuilder(" select * from (")).append(sql)
//                    .append(") t_t order by ").append(sort).append(" limit ")
//                    .append(from).append(", ").append(pageSize).toString();
//        else
//            throw new RuntimeException(
//                    "PageQuery not supported of this DBtype!");
//    }
//
//    public static String createRownumSql(String module, String datasourceName,
//                                         String sql, String sort) {
//        if ((new DaoFactory()).ifUseSqlServer(module, datasourceName)) {
//            sql = sql
//                    .replaceFirst(" from ", (new StringBuilder(
//                            " , row_number() over(order by ")).append(sort)
//                            .append(") as rowid from ").toString());
//            return sql;
//        }
//        if ((new DaoFactory()).ifUseOracle(module, datasourceName)) {
//            sql = sql.replaceFirst(" from ", " , rownum rowid from ");
//            sql = (new StringBuilder(String.valueOf(sql))).append(" order by ")
//                    .append(sort).toString();
//            return sql;
//        }
//        if ((new DaoFactory()).ifUseMySql(module, datasourceName)) {
//            sql = sql
//                    .replaceFirst(" from ",
//                            " ,(@rownum:=@rownum+1) as rowid from (select @rownum:=0) num, ");
//            sql = (new StringBuilder(String.valueOf(sql))).append(" order by ")
//                    .append(sort).toString();
//            return sql;
//        } else {
//            throw new RuntimeException(
//                    "PageQuery not supported of this DBtype!");
//        }
//    }
//}
