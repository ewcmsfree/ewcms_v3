/**
 * Copyright (c)2010-2011 Enterprise Website Content Management System(EWCMS), All rights reserved.
 * EWCMS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.ewcms.com
 */

package com.ewcms.publication;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ewcms.publication.dao.ArticlePublishDaoable;
import com.ewcms.publication.dao.ChannelPublishDaoable;
import com.ewcms.publication.dao.PublishDaoable;
import com.ewcms.publication.dao.ResourcePublishDaoable;
import com.ewcms.publication.dao.SitePublishDaoable;
import com.ewcms.publication.dao.TemplatePublishDaoable;
import com.ewcms.publication.dao.TemplateSourcePublishDaoable;
import com.ewcms.publication.module.Channel;
import com.ewcms.publication.module.Site;
import com.ewcms.publication.module.Template;
import com.ewcms.publication.module.Template.TemplateType;
import com.ewcms.publication.publish.PublishRunnerable;
import com.ewcms.publication.publish.SimplePublishRunner;
import com.ewcms.publication.task.Taskable;
import com.ewcms.publication.task.generator.ChannelTask;
import com.ewcms.publication.task.generator.DetailChannelTask;
import com.ewcms.publication.task.generator.DetailTask;
import com.ewcms.publication.task.generator.GenTaskBase;
import com.ewcms.publication.task.generator.HomeTask;
import com.ewcms.publication.task.generator.ListTask;
import com.ewcms.publication.task.generator.SiteTask;
import com.ewcms.publication.task.resource.ResourceSiteTask;
import com.ewcms.publication.task.resource.TemplateSourceSiteTask;
import com.ewcms.publication.task.resource.TemplateSourceTask;
import com.ewcms.publication.uri.UriRuleable;
import com.ewcms.publication.uri.UriRules;

import freemarker.template.Configuration;

/**
 * 实现发布服务
 * 
 * @author <a href="hhywangwei@gmail.com">王伟</a>
 */
@Service
public class PublishService implements PublishServiceable, InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(PublishService.class);
    
    @Autowired
    private ChannelPublishDaoable channelPublishDao;
    @Autowired
    private ArticlePublishDaoable articlePublishDao;
    @Autowired
    private TemplatePublishDaoable templatePublishDao;
    @Autowired
    private SitePublishDaoable sitePublishDao;
    @Autowired
    private PublishDaoable publishDao;
    @Autowired
    private Configuration cfg;
    @Autowired
    private ResourcePublishDaoable resourcePublishDao;
    @Autowired
    private TemplateSourcePublishDaoable templateSourcePublishDao;
    
    private String tempRoot;
    private PublishRunnerable pubRunner;
    
	@Override
	public void pubTemplateSource(Long siteId, List<Long> ids)throws PublishException {
		Site site = sitePublishDao.findOne(siteId);
		Taskable task = new TemplateSourceTask(site, tempRoot, ids, templateSourcePublishDao);
		
		task.regTask(pubRunner);
	}

	@Override
	public void pubTemplateSource(Long siteId, boolean again)throws PublishException {
		Site site = sitePublishDao.findOne(siteId);
		Taskable task = new TemplateSourceSiteTask(site, tempRoot, again, templateSourcePublishDao);
		
		task.regTask(pubRunner);
	}

	@Override
	public void pubResource(Long siteId, List<Long> ids)throws PublishException {
		Site site = sitePublishDao.findOne(siteId);
        Taskable task = new TemplateSourceTask(site, tempRoot, ids, templateSourcePublishDao);
		
		task.regTask(pubRunner);
	}

	@Override
	public void pubResource(Long siteId, boolean again)throws PublishException {
		Site site = sitePublishDao.findOne(siteId);
		Taskable task = new ResourceSiteTask(site, again, resourcePublishDao);
		
		task.regTask(pubRunner);
	}

	@Override
	public void pubTemplate(Long siteId ,Long channelId, Long templateId, boolean again)throws PublishException {
		Site site = sitePublishDao.findOne(siteId);
		Channel channel = channelPublishDao.findPublishOne(siteId, channelId);
		Template template = templatePublishDao.findOne(channelId, templateId);
		Taskable task = newTemplateTask(site,channel,template,again);
		
		task.regTask(pubRunner);
	}
	
	private Taskable newTemplateTask(Site site, Channel channel, Template template, boolean again)throws PublishException{
		
		String templatePath = template.getUniquePath();
		
		GenTaskBase task = null;
		if(template.getType() == TemplateType.LIST){
			task = new ListTask(site, channel, cfg, tempRoot, templatePath, resourcePublishDao, templateSourcePublishDao, articlePublishDao);
		}
		if(template.getType() == TemplateType.DETAIL){
			task = new DetailChannelTask(site,channel,cfg,tempRoot,again,templatePath,resourcePublishDao,templateSourcePublishDao,articlePublishDao);
		}
		if(template.getType() == TemplateType.HOME || template.getType() == TemplateType.OTHER){
			task = new HomeTask(site,channel,cfg,tempRoot,templatePath,resourcePublishDao,templateSourcePublishDao);
		}

		if(task == null){
			throw new PublishException(template.getType().name() + " template type not exist");
		}
		
		UriRuleable uriRule = StringUtils.isBlank(template.getUriPattern()) ? 
				null : UriRules.newUriRuleBy(template.getUriPattern());
		task.setUriRule(uriRule);
		
		return task;
	}

	@Override
	public void pubChannel(Long siteId, Long channelId, boolean pubChild, boolean again) throws PublishException {
		
		Site site = getSite(siteId);
		Channel channel = getChannel(siteId, channelId);
		Taskable task = new ChannelTask(site, channel, cfg, tempRoot, again, pubChild,
				resourcePublishDao, templateSourcePublishDao, channelPublishDao, templatePublishDao, articlePublishDao);
		
		task.regTask(pubRunner);
	}

	@Override
	public void pubSite(Long siteId, boolean again)throws PublishException {
		
		Site site = getSite(siteId);
		Taskable task = new SiteTask(site, cfg, tempRoot, again, resourcePublishDao, templateSourcePublishDao, channelPublishDao, templatePublishDao, articlePublishDao);
		
		task.regTask(pubRunner);
	}

	@Override
	public void pubArticle(Long siteId, Long channelId, List<Long> ids) throws PublishException {
		Site site = getSite(siteId);
		Channel channel = getChannel(siteId, channelId);
		List<Template> templates = templatePublishDao.findInChannel(channelId);
		for(Template t : templates){
			if(t.getType() != TemplateType.DETAIL){
				continue;
			}
			
			Taskable task = new DetailTask(site, channel, cfg, tempRoot, t.getUniquePath(),
					ids, resourcePublishDao, templateSourcePublishDao, articlePublishDao);
			task.regTask(pubRunner);
		}
	}
	

	@Override
	public void closeTask(Long siteId, String id)throws PublishException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Taskable> getSiteTask(Long siteId) {
		return null;
	}
	
    /**
     * 得到站点对象
     * </br>
     * 站点不存在抛出异常
     * 
     * @param siteId 站点编号
     * @return
     * @throws PublishException
     */
    private Site getSite(Long siteId)throws PublishException{
        Site site = sitePublishDao.findOne(siteId);
        if(site == null){
            logger.error("Site id's {},but site not exist.",siteId);
            throw new PublishException("sit is null");
        }
        return site;
    }
    
    /**
     * 得到频道
     * 
     * @param siteId    站点编号
     * @param channelId 频道编号
     * @return
     * @throws PublishException
     */
    private Channel getChannel(Long siteId, Long channelId)throws PublishException{
    	Channel channel = channelPublishDao.findPublishOne(siteId, channelId);
    	if(channel == null){
    		logger.error("Site id's {} and channel id's {}", siteId, channelId);
    		throw new PublishException("channel is null");
    	}
    	return channel;
    }
    
	@Override
	public void afterPropertiesSet() throws Exception {
		
		if(tempRoot == null){
			tempRoot = System.getProperty("java.io.tmpdir");
		}
		tempRoot = FilenameUtils.normalize(tempRoot);
		try{
			testTempRoot(tempRoot);	
		}catch(IOException e){
			throw new IOException(tempRoot + " not write please check.", e);
		}
		
		
		if(pubRunner == null){
			pubRunner = new SimplePublishRunner(publishDao);
		}
		pubRunner.start();
	}
	
	private void testTempRoot(String tempRoot)throws IOException{
		File temp = new File(tempRoot);
		
		if(!temp.exists()){
			temp.mkdirs();
		}else{
			File test = new File(tempRoot + File.separator + String.format("%d-test.txt", System.currentTimeMillis()));
			FileUtils.writeStringToFile(test, "HELLO EWCMS");
			FileUtils.deleteQuietly(test);	
		}
	}
	
	@Override
	public void destroy() throws Exception {
		if(pubRunner != null){
			pubRunner.shutdown();
		}
	}
	
	public void setChannelPublishDao(ChannelPublishDaoable channelPublishDao) {
		this.channelPublishDao = channelPublishDao;
	}

	public void setArticlePublishDao(ArticlePublishDaoable articlePublishDao) {
		this.articlePublishDao = articlePublishDao;
	}

	public void setTemplatePublishDao(TemplatePublishDaoable templatePublishDao) {
		this.templatePublishDao = templatePublishDao;
	}

	public void setSitePublishDao(SitePublishDaoable sitePublishDao) {
		this.sitePublishDao = sitePublishDao;
	}

	public void setResourcePublishDao(ResourcePublishDaoable resourcePublishDao) {
		this.resourcePublishDao = resourcePublishDao;
	}

	public void setTemplateSourcePublishDao(TemplateSourcePublishDaoable templateSourcePublishDao) {
		this.templateSourcePublishDao = templateSourcePublishDao;
	}

	public void setConfiguration(Configuration cfg){
        this.cfg = cfg;
    }
	
	public void setPublishRunner(PublishRunnerable runner){
		this.pubRunner = runner;
	}
	
	public void setTempRoot(String tempRoot){
		this.tempRoot = tempRoot;
	}
}
