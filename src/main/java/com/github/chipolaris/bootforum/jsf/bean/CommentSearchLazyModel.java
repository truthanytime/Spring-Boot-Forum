package com.github.chipolaris.bootforum.jsf.bean;

import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.chipolaris.bootforum.domain.Comment;
import com.github.chipolaris.bootforum.service.IndexService;
import com.github.chipolaris.bootforum.service.SearchCommentResult;

public class CommentSearchLazyModel extends LazyDataModel<Comment> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LoggerFactory.getLogger(CommentSearchLazyModel.class);
	
	private IndexService indexService;
	
	private String keywords;
	
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public CommentSearchLazyModel(IndexService indexService, String keywords) {	
		
		logger.debug("CommentSearchLazyModel constructor");
		
		this.keywords = keywords;
		
		this.indexService = indexService;
		// this.searchCommentResult = new SearchCommentResult();
	}
	
	@Override
	public int count(Map<String, FilterMeta> filterBy) {
		// return 0 here because we don't want to execute another Lucene search
		// 	for the count. The setRowCount in the load() method below will take care of this
		// see https://github.com/primefaces/primefaces/issues/1921
		return 0;
	}
	
	@Override
	public List<Comment> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
		
		logger.info("first is " + first + ", pageSize is " + pageSize);
		
		SearchCommentResult searchCommentResult = indexService.searchCommentByKeywords(
				keywords, first, pageSize).getDataObject();
		
		// see https://github.com/primefaces/primefaces/issues/1921
		this.setRowCount(searchCommentResult.getTotalHits().intValue());
		
		return searchCommentResult.getComments();
	}
	
	/*
	 * Need this method for the rowExpansion feature 
	 */
	@Override
	public String getRowKey(final Comment comment) {
		return comment.getId().toString();
	}
}
