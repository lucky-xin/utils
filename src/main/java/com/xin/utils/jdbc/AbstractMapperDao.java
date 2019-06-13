//package com.xin.utils.jdbc;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.xin.utils.CollectionUtil;
//import com.xin.utils.ExecutorServiceUtil;
//import com.xin.utils.StringUtil;
//import org.apache.log4j.Logger;
//import org.hibernate.resource.transaction.spi.TransactionStatus;
//
//import javax.script.ScriptException;
//import java.util.*;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
///**
// * @author Luchaoxin
// * @version V1.0
// * @Description: 根据xml映射文件处理抽象Dao
// * @date 2018年6月5日 下午4:07:51
// * @Copyright (C)2018 , Suntektech
// */
//public abstract class AbstractMapperDao {
//
//    protected List<Map<String, String>> mappingInfo;
//
//    private DatabaseTableMapper mapper;
//
//    private String tableName;
//
//    private Map<String, String> uniqueInfo;
//
//    private List<Map<String, String>> primaryKeys;
//
//    private Lock lock = new ReentrantLock();
//
//    protected Logger logger;
//
//    protected TransactionTemplate transactionTemplate;
//
//    protected JdbcTemplate jdbc;
//
//    public AbstractMapperDao(DatabaseTableMapper mapper) {
//        if (null == mapper) {
//            throw new RuntimeException("mapper must not be null.");
//        }
//        if (StringUtil.isEmpty(mapper.getTableName())) {
//            throw new RuntimeException("mapper.getTableName() must not be null.");
//        }
//        if (CollectionUtil.isEmpty(mapper.getMappingInfo())) {
//            throw new RuntimeException("mapper.getMappingInfo() must not be empty.");
//        }
//
//        this.mapper = mapper;
//        this.mappingInfo = mapper.getMappingInfo();
//        this.tableName = mapper.getTableName();
//        init();
//    }
//
//    private StringBuilder insertVSql;
//
//    private StringBuilder endInsertVSqlStr = new StringBuilder(")values(");
//
//    private void init() {
//        insertVSql = new StringBuilder("insert into ").append(tableName).append(" (");
//
//        Map<String, String> values = mapper.getValues();
//
//        uniqueInfo = getMapper().getUniqueInfo();
//        primaryKeys = getMapper().getPrimaryKeys();
//        if (CollectionUtil.isEmpty(values)) {
//            return;
//        }
//        // 1.处理xml配置的映射文件 字段-值
//        Iterator<Map.Entry<String, String>> interator = values.entrySet().iterator();
//        while (interator.hasNext()) {
//            Map.Entry<String, String> entry = interator.next();
//            String key = entry.getKey();
//            String value = entry.getValue();
//            insertVSql.append(key).append(",");
//            endInsertVSqlStr.append("'").append(value).append("'").append(",");
//        }
//    }
//
//    public void batchSave(List<Map<String, Object>> datas) {
//        if (CollectionUtil.isEmpty(datas)) {
//            return;
//        }
//        try {
//            lock.lock();
//            doBatchSave(datas);
//        } catch (Exception e) {
//            logger.error("入库错误：", e);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    /**
//     * 根据传过来的参数映射值，并生成插入或者更新的sql
//     *
//     * @param datas
//     * @throws NoSuchMethodException
//     * @throws ScriptException
//     */
//    private void doBatchSave(List<Map<String, Object>> datas) throws NoSuchMethodException, ScriptException {
//        long startTime = System.currentTimeMillis();
//
//        List<String> sqls = new ArrayList<String>();
//        List<Object[]> params = new ArrayList<Object[]>();
//
//        List<String> otherSqls = new ArrayList<String>();
//        List<Object[]> otherParams = new ArrayList<Object[]>();
//
//        if (CollectionUtil.isEmpty(uniqueInfo)) {
//            throw new RuntimeException("唯一字段，和主键信息map不能为空。");
//        }
//
//        String vnmpUniqueColumn = CollectionUtil.getStringValue(uniqueInfo, DatabaseTableMapper.VNMP_UNIQUE_KEY);
//        String omofUniqueColumn = CollectionUtil.getStringValue(uniqueInfo, DatabaseTableMapper.OMOF_UNIQUE_KEY);
//        String uniqueType = CollectionUtil.getStringValue(uniqueInfo, DatabaseTableMapper.UNIQUE_TYPE_KEY);
//
//        int nextId = getNextPK();
//        int insertCount = 0;
//        int updateCount = 0;
//        Set<String> uniqueValues = Sets.newHashSet();
//        prepareTraversal();
//        for (Map<String, Object> item : datas) {
//
//            StringBuilder insertSql = new StringBuilder(getInsertVSql());
//            StringBuilder endInsertSqlStr = new StringBuilder(getEndInsertVSql());
//            StringBuilder updateSql = new StringBuilder("update ").append(tableName).append(" set ");
//
//            List<Object> param = Lists.newArrayList();
//
//            String uniqueValue = CollectionUtil.getStringValue(item, omofUniqueColumn);
//
//            if ((DatabaseTableMapper.INTEGER_TYPE_NAME.equals(uniqueType) && !StringUtil.isInteger(uniqueValue))
//                    || uniqueValue.isEmpty() || !uniqueValues.add(uniqueValue)) {
//                logger.info("跳过了本次循环,list可能存在重复,或者 xml配置文件所标记的列unique=\"true\",数据不对.");
//                continue;
//            }
//
//            boolean isExist = isExist(item, omofUniqueColumn, vnmpUniqueColumn);
//
//            if (isContinue(isExist, item)) {
//                continue;
//            }
//            preHandle(item);
//            Iterator<Map<String, String>> iterator = mappingInfo.iterator();
//            // 处理xml配置的映射文件 联网字段->一机一档字段
//            while (iterator.hasNext()) {
//                Map<String, String> valueMppingInfo = iterator.next();
//                String column = CollectionUtil.getStringValue(valueMppingInfo, DatabaseTableMapper.VNMP_COLUMN_KEY);
//                String omof = CollectionUtil.getStringValue(valueMppingInfo, DatabaseTableMapper.OMOF_COLUMN_KEY);
//                String type = CollectionUtil.getStringValue(valueMppingInfo, DatabaseTableMapper.VNMP_COLUMN_TYPE_KEY);
//                if (isExist) {
//                    updateSql.append(column).append("=?,");
//                } else {
//                    insertSql.append(column).append(",");
//                    endInsertSqlStr.append("?,");
//                }
//
//                Object value = null;
//                Map<String, String> jsFunctionInfo = getMapper().getJSFunctionInfo(column);
//                if (!CollectionUtil.isEmpty(jsFunctionInfo)) {
//                    value = invokeFun(jsFunctionInfo, item, nextId);
//                } else if (DatabaseTableMapper.INTEGER_TYPE_NAME.equals(type)) {
//                    value = CollectionUtil.getIntegerValue(item, omof);
//                } else {
//                    value = CollectionUtil.getStringValue(item, omof);
//                }
//                param.add(value);
//            }
//            // 更新的条件为xml配置文件中含unique="true"的行
//            if (isExist) {
//                updateSql.deleteCharAt(updateSql.length() - 1).append(" where ").append(vnmpUniqueColumn).append("=?");
//                param.add(uniqueValue);
//                sqls.add(updateSql.toString());
//                updateCount++;
//            } else {
//                insertCount++;
//                nextId++;
//                // 添加所有主键信息
//                Iterator<Map<String, String>> it = primaryKeys.iterator();
//                while (it.hasNext()) {
//
//                    Map<String, String> map = it.next();
//                    String primaryKeyColumn = CollectionUtil.getStringValue(map, DatabaseTableMapper.PRIMARY_KEY_COLUMN_KEY);
//                    String primaryKeyValue = CollectionUtil.getStringValue(map, DatabaseTableMapper.PRIMARY_KEY_VALUE_KEY);
//                    Object value = null;
//                    Map<String, String> funInfo = getMapper().getJSFunctionInfo(primaryKeyColumn);
//                    if (!CollectionUtil.isEmpty(funInfo)) {
//                        value = invokeFun(funInfo, item, nextId);
//                    } else {
//                        value = CollectionUtil.getStringValue(item, primaryKeyValue);
//                    }
//
//                    insertSql.append(primaryKeyColumn).append(",");
//                    endInsertSqlStr.append("?,");
//                    param.add(value);
//                }
//                insertSql.append(vnmpUniqueColumn);
//                endInsertSqlStr.append("?)");
//
//                param.add(uniqueValue);
//                sqls.add(insertSql.append(endInsertSqlStr).toString());
//            }
//            params.add(param.toArray());
//            addTransactionData(item, sqls, params, isExist, nextId);
//            postHandle(item, otherSqls, otherParams, nextId);
//        }
//
//        batchUpdate(sqls.toArray(new String[]{}), params);
//        long endTime = System.currentTimeMillis();
//        logger.info("同步" + tableName + "表信息：插入和更新数据结束->执行时间：" + (endTime - startTime));
//        logger.info("定时查询更新事务,新增" + tableName + "数：" + insertCount + " 更新" + tableName + "数：" + updateCount);
////        increaseId(tableName, insertCount);
//        batchSaveCompleted();
//        ExecutorServiceUtil.execute(() -> {
////            batchUpdate(otherSqls, otherParams);
//            asyncHandleOtherThings();
//        });
//
//    }
//
//    /**
//     * 在进入方法{@link AbstractMapperDao#batchSave(List)} 中执行for循环之前调用此方法
//     */
//    protected void prepareTraversal() {
//
//    }
//
//    /**
//     * 在进入方法{@link AbstractMapperDao#batchSave(List)} 遍历for循环中完成了数据入库之后调用此方法完成
//     * 异步处理其他操作
//     */
//    protected void asyncHandleOtherThings() {
//
//    }
//
//    /**
//     * 已经完成了批量提交
//     */
//    protected void batchSaveCompleted() {
//
//    }
//
//    /**
//     * 在进入方法{@link AbstractMapperDao#batchSave(List)} 中的for循环开始遍历当前元素时，调用此方法
//     *
//     * @param item item为{@link AbstractMapperDao#batchSave(List)} List所遍历的map元素
//     */
//    protected void preHandle(Map<String, Object> item) {
//
//    }
//
//    /**
//     * 在进入方法{@link AbstractMapperDao#batchSave(List)} 中的for循环结束遍历当前元素时，调用此方法
//     * 添加事务数据,需要和for循环生成的sql批量提交
//     *
//     * @param item      item item为{@link AbstractMapperDao#doBatchSave(List)}
//     *                  List所遍历的map元素
//     * @param sqls      sqls 此次批量生成的sql语句
//     * @param params    params 此次批量生成的sql语句对应的参数
//     * @param isExist   当前item是否已经存在
//     * @param currentId 入库自增主键当前int值
//     */
//    protected void addTransactionData(Map<String, Object> item, List<String> sqls, List<Object[]> params,
//                                      boolean isExist, int currentId) {
//
//    }
//
//    /**
//     * 在进入方法{@link AbstractMapperDao#batchSave(List)} 中的for循环结束遍历当前元素时，调用此方法
//     *
//     * @param item      item为{@link AbstractMapperDao#doBatchSave(List)} List所遍历的map元素
//     * @param sqls      此次批量生成的sql语句
//     * @param params    此次批量生成的sql语句对应的参数
//     * @param currentId 入库自增主键当前int值
//     */
//    protected void postHandle(Map<String, Object> item, List<String> sqls, List<Object[]> params, int currentId) {
//
//    }
//
//    /**
//     * 是否跳过当前遍历item，
//     *
//     * @param isExist 当前元素记录是否已经存在
//     * @param item    为{@link AbstractMapperDao#doBatchSave(List)} List所遍历的map元素
//     * @return
//     */
//    protected boolean isContinue(boolean isExist, Map<String, Object> item) {
//        return false;
//    }
//
//    /**
//     * 根据functionInfo判断是否调用js函数
//     *
//     * @param functionInfo
//     * @param item
//     * @param nextId
//     * @return
//     * @throws NoSuchMethodException
//     * @throws ScriptException
//     */
//    private Object invokeFun(Map<String, String> functionInfo, Map<String, Object> item, int nextId)
//            throws NoSuchMethodException, ScriptException {
//        String jsFunName = CollectionUtil.getStringValue(functionInfo, DatabaseTableMapper.JS_FUNCTION_NAME_KEY);
//        String jsFunParams = CollectionUtil.getStringValue(functionInfo, DatabaseTableMapper.JS_FUNCTION_PARAMS_KEY);
//        if (jsFunName.isEmpty()) {
//            return null;
//        }
//
//        if (jsFunParams.isEmpty() && DatabaseTableMapper.NEXT_ID_VALUE.equals(jsFunName)) {
//            return nextId + 1;
//        }
//
//        Object jsResult = null;
//        Object[] params = null;
//        if (jsFunParams.isEmpty()) {
//            params = new Object[]{};
//        } else {
//            String[] funParams = jsFunParams.split(",");
//            List<String> args = Lists.newArrayList();
//            for (String funParam : funParams) {
//                int index = funParam.indexOf(DatabaseTableMapper.JS_FUNCTION_PARAM_REGEX);
//                if (index == 0) {
//                    args.add(CollectionUtil.getStringValue(item, funParam.substring(1)));
//                } else {
//                    args.add(funParam);
//                }
//            }
//            params = args.toArray();
//        }
//
//        jsResult = DatabaseTableMapper.invokeJsFun(jsFunName, params);
//        return jsResult;
//    }
//
//    private int getNextPK() {
//        return 1;
////        return PrimaryKeyTableTool.getInstance().getNextPK(tableName);
//    }
//
//    private DatabaseTableMapper getMapper() {
//        return mapper;
//    }
//
//    private StringBuilder getEndInsertVSql() {
//        return endInsertVSqlStr;
//    }
//
//    private StringBuilder getInsertVSql() {
//        return insertVSql;
//    }
//
//    protected boolean isExist(Map<String, Object> map, String omofColumn, String vnmpColumn) {
//        Object value = CollectionUtil.getStringValue(map, omofColumn);
//        StringBuilder sql = new StringBuilder("select count(1) from ").append(tableName).append(" where ")
//                .append(vnmpColumn).append("=?");
//        return jdbc.queryForObject(sql.toString(), Integer.class, value) > 0;
//    }
//
//    public Map<String, Object> getFirstMap(List<Map<String, Object>> list) {
//        if (CollectionUtil.isEmpty(list)) {
//            return Maps.newHashMap();
//        }
//
//        return list.get(0);
//    }
//
//
//    @SuppressWarnings("unchecked")
//    public int[] batchUpdate(final String[] sqls, final List<Object[]> params) {
//        return (int[]) transactionTemplate.execute(new TransactionCallback() {
//            public Object doInTransaction(TransactionStatus transactionStatus) {
//                try {
//                    int[] result = new int[sqls.length];
//                    for (int i = 0; i < sqls.length; i++) {
////                        logger.debug(sqls[i], params.get(i));
//                        result[i] = getJdbc().update(sqls[i], params.get(i));
//                    }
//                    return result;
//                } catch (Throwable t) {
//                    transactionStatus.setRollbackOnly();
//                    throw new RuntimeException(t);
//                }
//            }
//        });
//    }
//
//    protected abstract JdbcTemplate getJdbc();
//
//
//}
