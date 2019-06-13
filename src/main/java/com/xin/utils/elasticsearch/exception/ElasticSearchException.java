package com.xin.utils.elasticsearch.exception;

/**
 * @author Luchaoxin
 * @Description: ElasticSearch异常
 * @date 2018-08-24
 */
public class ElasticSearchException extends Exception {

    public ElasticSearchException(String error) {
        super(error);
    }

}
