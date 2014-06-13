/**
 * Copyright (c)2010-2011 Enterprise Website Content Management System(EWCMS), All rights reserved.
 * EWCMS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.ewcms.com
 */

package com.ewcms.publication.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ewcms.publication.freemarker.FreemarkerUtil;
import com.ewcms.publication.freemarker.GlobalVariable;
import com.ewcms.publication.service.ChannelPublishService;
import com.ewcms.site.model.Channel;
import com.ewcms.site.model.Site;
import com.ewcms.util.EmptyUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 频道列表标签
 * <br>
 * 频道标签主要得到系统的频道，显示频道到前台功能。可以和文章列表组合使用，实现文章的灵活显示。
 * 
 * @author wangwei
 */
public class ChannelListDirective implements TemplateDirectiveModel {
    private static final Logger logger = LoggerFactory.getLogger(ChannelListDirective.class);
    
    private static final int DEFAULT_ROW = -1;
    
    private static final String CHANNEL_PARAM_NAME = "channel";
    private static final String ROW_PARAM_NAME = "row";
    private static final String NAME_PARAM_NAME = "name";
    private static final String CHILD_PARAM_NAME = "child";
    private static final String PARENT_PARAM_NAME = "parent";

    private String channelParam = CHANNEL_PARAM_NAME;
    private String rowParam = ROW_PARAM_NAME;
    private String nameParam = NAME_PARAM_NAME;
    private String childParam = CHILD_PARAM_NAME;
    private String parentParam = PARENT_PARAM_NAME;
    
    private ChannelPublishService channelPublishService;
    
    public ChannelListDirective(ChannelPublishService channelPublishService){
        this.channelPublishService = channelPublishService;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
            boolean child = getChildValue(params);
            boolean parent = getParentValue(params);
            int row = getRowValue(params);
            Long siteId = getSiteIdValue(env);
            
            List<Channel> channels = getChannelList(env, params, siteId, parent, child);
            
            row = (row == DEFAULT_ROW) ? channels.size() : row;
            if(row < channels.size()){
                channels = channels.subList(0, row);
            }
            
            if (EmptyUtil.isArrayNotEmpty(loopVars)) {
                loopVars[0] = env.getObjectWrapper().wrap(channels);
                if(EmptyUtil.isNull(body)){
                    logger.warn("Body is null");
                }else{
                    body.render(env.getOut());    
                }
            } else {
                String name = getNameValue(params);
                Writer writer = env.getOut();
                for (int i = 0 ; i < channels.size(); i++) {
                    Channel channel = channels.get(i);
                    FreemarkerUtil.setVariable(env, name, channel);
                    FreemarkerUtil.setVariable(env, GlobalVariable.INDEX.toString(), i + 1);
                    body.render(writer);
                    FreemarkerUtil.removeVariable(env, GlobalVariable.INDEX.toString());
                    FreemarkerUtil.removeVariable(env, name);
                }
                writer.flush();
            }
    }

    /**
     * 得到当前站点
     *
     * @param env Environment
     * @return
     * @throws TemplateException
     */
    private Long getSiteIdValue(Environment env) throws TemplateException {
        String current = GlobalVariable.SITE.toString();
        Site site = (Site) FreemarkerUtil.getBean(env, current);
        if(EmptyUtil.isNull(site)){
            logger.error("Site is null in freemarker variable");
            throw new TemplateModelException("Site is null in freemarker variable");
        }
        logger.debug("Site is {}",site);
        return site.getId();
    }
    
    /**
     * 得到显示频道集合
     * 
     * @param env
     *          Freemarker环境
     * @param params
     *          标签参数集合
     * @param name   
     *          标签参数名称
     * @param parent
     *          显示父频道
     * @param child 
     *          显示子频道
     * @return
     * @throws TemplateModelException
     */
    @SuppressWarnings("rawtypes")
    private List<Channel> getChannelList(Environment env, Map params, Long siteId, boolean parent, boolean child)throws TemplateException{
        final String name = channelParam;
        boolean debug = FreemarkerUtil.isDebug(env);
        
        Channel channel = (Channel) FreemarkerUtil.getBean(params, name);
        if (EmptyUtil.isNotNull(channel)) {
            return loadingChannel(siteId, channel.getId(), parent, child, debug);
        }

        Long channelId = FreemarkerUtil.getLong(params, name);
        if (EmptyUtil.isNotNull(channelId)) {
            return loadingChannel(siteId, channelId, parent, child, debug);
        }

        String value = FreemarkerUtil.getString(params, name);
        if (EmptyUtil.isStringNotEmpty(value)) {
            return loadingChannelInVariable(env, siteId, value, parent, child, debug);
        }

        List<?> values = FreemarkerUtil.getSequence(params, name);
        if (EmptyUtil.isCollectionNotEmpty(values)) {
            return loadingChannelByList(env, siteId, values, parent, child, debug);
        }

        channel = (Channel) FreemarkerUtil.getBean(env, GlobalVariable.CHANNEL.toString());
        if(EmptyUtil.isNotNull(channel)){
            return loadingChannel(siteId, channel.getId(), parent, child, debug);    
        }
        return new ArrayList<Channel>();
    }
    
    /**
     * 通过频道编号得到显示的频道列表
     * 
     * @param siteId 站点编号
     * @param channelId 频道编号
     * @param child 显示子站点
     * @param parent 显示父频道
     * @param debug 调试模式 （debug为true,没有发布频道也显示）
     * @return
     * @throws TemplateException
     */
    List<Channel> loadingChannel(Long siteId, Long channelId, boolean parent, boolean child, boolean debug)throws TemplateException{
        
        Channel channel = channelPublishService.getChannel(siteId,channelId);
        if (EmptyUtil.isNull(channel)) {
            logger.error("Channel's id is {},it's not exist in site's({}).",channelId,siteId);
            throw new TemplateModelException("Channl's id is " + channelId + ",it's not exist.");
        }
        
        if(!debug && !channel.getPublicenable()){
            return new ArrayList<Channel>();
        }
        
        List<Channel> list = new ArrayList<Channel>();
        if(child){
        	if (parent){
        		Channel parentChannel = channelPublishService.getChannelParent(channelId);
        		if (parentChannel != null) channelId = parentChannel.getId();
        	}
        	
            List<Channel> children = channelPublishService.getChannelChildren(channelId);
            if(debug){
                list.addAll(children);
            }else{
                for(Channel c: children){
                    if(c.getPublicenable()){
                        list.add(c);
                    }
                }
            }
        }else{
        	if (parent){
        		list.add(channelPublishService.getChannelParent(channelId));
        	}else{
        		list.add(channelPublishService.getChannel(siteId,channelId));
        	}
        }
        
        return list;
    }
    
    /**
     * 通过频道url或变量名得到显示频道列表
     * 
     * @param env 
     *          Freemarker环境
     * @param siteId 
     *          站点编号
     * @param value 
     *          url或变量名称
     * @param parent 显示父频道
     * @param child 
     *          显示子站点
     * @return
     * @throws TemplateException
     */
    private List<Channel> loadingChannelInVariable(Environment env, Long siteId,String value, boolean parent, boolean child,boolean debug)throws TemplateException{
        Channel channel = (Channel)FreemarkerUtil.getBean(env, value);
        if(EmptyUtil.isNotNull(channel)){
            logger.debug("Channel is {}",channel.toString());
            return loadingChannel(siteId, channel.getId(), parent, child, debug);
        }
        String path = UriFormat.formatChannelPath(value);
        channel = channelPublishService.getChannelByUrlOrPath(siteId, path);
        if(EmptyUtil.isNotNull(channel)){
            logger.debug("Channel is {}",channel.toString());
            return loadingChannel(siteId, channel.getId(), parent, child, debug);
        }
        throw new TemplateModelException("Url or variable \"" + value + "\" had not exist");
    }
    
    /**
     * 通过频道集合得到显示频道列表
     * <br>
     * 数组内可以是频道编号，频道地址，变量名称。
     * 
     * @param env Freemarker环境
     * @param siteId 站点编号
     * @param values 频道集合
     * @param parent 显示父频道
     * @param child 显示子站点
     * @return
     * @throws TemplateException
     */
    private List<Channel> loadingChannelByList(Environment env,Long siteId,List<?> values,boolean parent, boolean child,boolean debug)throws TemplateException{
        List<Channel> channels = new ArrayList<Channel>();
        for (Object value : values) {
            if (value instanceof Number) {
                logger.debug("Channel's id is {} ",value);
                channels.addAll(loadingChannel(siteId,((Number) value).longValue(), parent, child,debug));
            }else if (value instanceof String) {
                logger.debug("Channel's url or variable is {}",value);
                channels.addAll(loadingChannelInVariable(env,siteId,(String)value, parent, child,debug));
            }else{
                logger.debug("Channel's value type is {}",value.getClass().getName());
                throw new TemplateModelException("Channel array are only int and string");
            }
        }
        return channels;
    }
    
    @SuppressWarnings("rawtypes")
    private int getRowValue(Map params) throws TemplateException {
        Integer rows = FreemarkerUtil.getInteger(params, rowParam);
        return rows == null ? DEFAULT_ROW : rows;
    }

    @SuppressWarnings("rawtypes")
    private String getNameValue(Map params) throws TemplateException {
        String value = FreemarkerUtil.getString(params, nameParam);
        return value == null ? GlobalVariable.CHANNEL.toString() : value;
    }

    @SuppressWarnings("rawtypes")
    private boolean getChildValue(Map params) throws TemplateException {
        Boolean value = FreemarkerUtil.getBoolean(params, childParam);
        return value == null ? false : value;
    }
    
    @SuppressWarnings("rawtypes")
    private boolean getParentValue(Map params) throws TemplateException {
    	Boolean value = FreemarkerUtil.getBoolean(params, parentParam);
    	return value == null ? false : value;
    }
     
    /**
     * 设置标签频道参数名
     * 
     * @param paramName 参数名
     */
    public void setChannelParam(String paramName) {
        this.channelParam = paramName;
    }

    /**
     * 设置标签显示行参数名
     * 
     * @param paramName 参数名
     */
    public void setRowParam(String paramName) {
        this.rowParam = paramName;
    }

    /**
     * 设置标签输出变量名参数名
     * 
     * @param paramName 参数名称
     */
    public void setNameParam(String paramName) {
        this.nameParam = paramName;
    }

    /**
     * 设置标签是否是子频道参数名
     * 
     * @param paramName 参数名称
     */
    public void setChildParam(String childParam) {
        this.childParam = childParam;
    }
    
    /**
     * 设置标签是否显示父频道参数
     * 
     * @param paramName 参数名称
     */
    public void setParentParam(String parentParam){
    	this.parentParam = parentParam;
    }
}