package com.xin.utils.elasticsearch;

import com.xin.utils.AssertUtil;
import com.xin.utils.StringUtil;
import com.xin.utils.elasticsearch.exception.ElasticSearchException;
import com.xin.utils.elasticsearch.model.Index;
import com.xin.utils.io.Dom4jUtil;
import com.xin.utils.io.IOUtil;
import com.xin.utils.log.LogFactory;
import com.xin.utils.model.Field;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Luchaoxin
 * @Description: ElasticSearch帮助类, 用户创建索引，字段映射
 * @date 2018-09-02 10:58
 */
public class ElasticSearchHelper {

    private Logger logger = LogFactory.getLogger(ElasticSearch.class.getSimpleName());

    private ConcurrentHashMap<String, Index> indexes = new ConcurrentHashMap<>(256);

    private Client client;

    public ElasticSearchHelper(Client client) {
        this.client = client;
    }

    public Index getIndex(String index, String type) {
        String key = getKey(index, type);
        return indexes.get(key);
    }

    private String getKey(String index, String type) {
        return StringUtil.isEmpty(type) ? index : index + "_" + type;
    }

    /**
     * 加载定义mapping的schema.xml文件
     *
     * @param path
     * @throws ElasticSearchException
     */
    public void loadSchema(String path) throws ElasticSearchException {
        InputStream in = null;
        try {
            in = ElasticSearchHelper.class.getResourceAsStream(path);
            Document document = Dom4jUtil.read(in);
            Element rootElement = document.getRootElement();
            String tableName = "index";
            Index index;
            for (Element element : rootElement.elements(tableName)) {
                index = new Index(element);
                indexes.put(getKey(index.getName(), index.getType()), index);
            }

        } catch (Exception e) {
            logger.error("读取schema.xml异常", e);
            throw new ElasticSearchException("读取schema.xml异常" + e.getMessage());
        } finally {
            IOUtil.close(in);
        }
    }

    /**
     * 根据schema.xml文件之中的定义创建mapping，如果index不存在则先创建index
     * 为某个index手动创建mapping
     *
     * @param index 索引名称
     * @param type  索引类型
     * @throws ElasticSearchException
     */
    public void createMapping(String index, String type) throws ElasticSearchException {
        AssertUtil.checkNotEmpty(index, "创建mapping的索引不能为空！");

        Index indexHolder = getIndex(index, type);

        if (null == indexHolder) {
            return;
        }

        if (!exists(index)) {
            createIndex(indexHolder);
        }

        List<Field> fields = indexHolder.getFields();
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            builder.startObject("properties");

            String fieldType;
            for (Field field : fields) {
                fieldType = resolveType(field.getType().toLowerCase());
                AssertUtil.checkNotEmpty(type, "字段[" + field.getName() + "] 的属性不能为空");
                builder.startObject(field.getName())
                        .field("type", fieldType)
                        .field("index", field.isAnalyze() ? "analyzed" : "not_analyzed")
                        .field("store", field.isStore())
                        .field("include_in_all", field.isIncludeInAll() ? "true" : "false");

                if (field.isIndex()) {
                    builder.field("boost", field.getBoost());
                } else {
                    builder.field("index", "no");
                }

                if (field.isAnalyze()) {
                    builder.field("analyzer", field.getAnalyzer())
                            .field("search_analyzer", field.getSearchAnalyzer());
                }

                if ("date".equalsIgnoreCase(fieldType)) {
                    builder.field("format", field.getFormat());
                }

                if (field.isSorted()) {
                    builder.field("doc_values", true);
                }
                builder.endObject();
            }

            builder.endObject();
            builder.endObject();

            PutMappingRequest mapping = Requests.putMappingRequest(index).type(type).source(builder);

            logger.info("更新mapping:" + mapping.source());
            client.admin().indices().putMapping(mapping).actionGet();
        } catch (Exception e) {
            logger.error("创建索引 index[" + index + "]异常.", e);
            throw new ElasticSearchException("创建索引 index[" + index + "]异常." + e.getMessage());
        }
    }

    public String getMappingInfo(String index, String type) {

        try {
            ImmutableOpenMap<String, MappingMetaData> mappings = client
                    .admin()
                    .cluster()
                    .prepareState()
                    .execute()
                    .actionGet()
                    .getState()
                    .getMetaData()
                    .getIndices()
                    .get(index)
                    .getMappings();
            String mapping = StringUtil.toString(mappings.get(type).source());
            return mapping;
        } catch (Exception e) {
            return "";
        }
    }

    private String resolveType(String type) {

        switch (type) {
            case "int":
            case "integer":
                return "long";
            case "string":
                return "text";
            default:
                break;
        }
        return type;
    }

    /**
     * 创建Index
     */
    public void createIndex(Index index) {

        if (exists(index.getName())) {
            return;
        }

        IndicesAdminClient adminClient = client.admin().indices();

        Settings.Builder builder = Settings.builder()
                .put("index.number_of_shards", index.getShards())
                .put("index.number_of_replicas", index.getReplicas());

        adminClient.prepareCreate(index.getName())
                .setSettings(builder)
                .get();
    }

    public boolean exists(String index) {
        IndicesAdminClient adminClient = client.admin().indices();
        IndicesExistsRequest request = new IndicesExistsRequest(index);
        IndicesExistsResponse response = adminClient.exists(request).actionGet();
        return response.isExists();
    }

}
