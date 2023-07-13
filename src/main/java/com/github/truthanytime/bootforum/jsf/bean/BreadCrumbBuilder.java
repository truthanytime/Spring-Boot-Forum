package com.github.chipolaris.bootforum.jsf.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.github.chipolaris.bootforum.CachingConfig;
import com.github.chipolaris.bootforum.domain.Comment;
import com.github.chipolaris.bootforum.domain.Discussion;
import com.github.chipolaris.bootforum.domain.Forum;
import com.github.chipolaris.bootforum.domain.ForumGroup;
import com.github.chipolaris.bootforum.event.ForumGroupUpdateEvent;
import com.github.chipolaris.bootforum.event.ForumUpdateEvent;

@Component //@Scope("application")
public class BreadCrumbBuilder {

	private static final Logger logger = LoggerFactory.getLogger(BreadCrumbBuilder.class);
	
	public MenuModel buildBreadCrumbModel(Comment comment) {
		
		logger.info(String.format("===> build breadcrumb for Comment id %d <===", comment.getId()));
		
		MenuModel model = buildBreadCrumbModel(comment.getDiscussion());
		
		DefaultMenuItem discussionItem = DefaultMenuItem.builder().value(comment.getTitle()).outcome("/commentThread")
				.params(Collections.singletonMap("id", Arrays.asList(comment.getId().toString()))).build();
		model.getElements().add(discussionItem);
		
		return model;
	}
	
	@Cacheable(value=CachingConfig.DISCUSSION_BREAD_CRUMB, key="#discussion.id")
	public MenuModel buildBreadCrumbModel(Discussion discussion) {
		
		logger.info(String.format("===> build breadcrumb for Discussion id %d <===", discussion.getId()));
		
		MenuModel model = buildBreadCrumbModel(discussion.getForum());
		
		DefaultMenuItem discussionItem = DefaultMenuItem.builder().value(discussion.getTitle()).outcome("/discussion")
				.params(Collections.singletonMap("id", Arrays.asList(discussion.getId().toString()))).build();
		
		model.getElements().add(discussionItem);
		
		return model;
	}
	
	@Cacheable(value=CachingConfig.FORUM_BREAD_CRUMB, key="#forum.id")
	public MenuModel buildBreadCrumbModel(Forum forum) {
		
		DefaultMenuModel model = new DefaultMenuModel();
		// add 'Home', 'ForumGroups', then the Forum
		DefaultMenuItem homeItem = DefaultMenuItem.builder().value("Home").outcome("/index").build();
		model.getElements().add(homeItem);
		
		if(forum != null) {
			logger.info(String.format("===> build breadcrumb for Forum id %d <===", forum.getId()));
			
			// traverse back the forum group tree and put in temporary list
			List<MenuItem> menuItems = new ArrayList<>();
			ForumGroup forumGroup = forum.getForumGroup();
			
			while(forumGroup != null) {
				
				DefaultMenuItem forumGroupItem = DefaultMenuItem.builder().value(forumGroup.getTitle()).outcome("/forums")
						.params(Collections.singletonMap("id", Arrays.asList(forumGroup.getId().toString()))).build();
				menuItems.add(0, forumGroupItem);
				
				forumGroup = forumGroup.getParent();
			}
			
			Iterator<MenuItem> iter = menuItems.iterator();
	
			while(iter.hasNext()) {
				model.getElements().add(iter.next());
			}
			
			DefaultMenuItem forumItem = DefaultMenuItem.builder().value(forum.getTitle()).outcome("/forum")
					.params(Collections.singletonMap("id", Arrays.asList(forum.getId().toString()))).build();
			model.getElements().add(forumItem);
		}
		
		return model;
	}
	
	@Resource 
	private CacheManager cacheManager;
	
	@EventListener
	public void handleForumUpdateEvent(ForumUpdateEvent forumUpdateEvent) {
		
		logger.info("Handle Forum Update");
		
		// clear the breadcrumb caches since the update might change the label of the forum 
		// TODO: perhaps just clear/evict the cache entries that were affected?
		cacheManager.getCache(CachingConfig.FORUM_BREAD_CRUMB).clear();
		cacheManager.getCache(CachingConfig.DISCUSSION_BREAD_CRUMB).clear();
	}
	
	@EventListener
	public void handleForumGroupUpdatedEvent(ForumGroupUpdateEvent forumGroupUpdateEvent) {
		
		logger.info("Handle Forum Group Update");
		
		// clear the breadcrumb caches since the update might change the label of the forum 
		// TODO: perhaps just clear/evict the cache entries that were affected?
		cacheManager.getCache(CachingConfig.FORUM_BREAD_CRUMB).clear();
		cacheManager.getCache(CachingConfig.DISCUSSION_BREAD_CRUMB).clear();
	}
}
