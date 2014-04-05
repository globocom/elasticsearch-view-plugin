package org.elasticsearch.view.exception;


import org.elasticsearch.ElasticSearchException;

public class ElasticSearchViewNotFoundException extends ElasticSearchException {

	private static final long serialVersionUID = -4987040366070142351L;

	public ElasticSearchViewNotFoundException(String msg) {
        super(msg);
    }

    public ElasticSearchViewNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
