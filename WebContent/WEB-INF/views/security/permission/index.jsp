<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
	<title>权限组管理</title>	
	<%@ include file="../../taglibs.jsp" %>
	<script type="text/javascript" src="${ctx}/static/views/security/permission/index.js"></script>
	<script type="text/javascript">
		var _permissionIndex = new PermissionIndex();
		$(function(){
			_permissionIndex.init({
		    	queryUrl:"${ctx}/security/permission/query"
		    });
		})
	</script>		
  </head>
  <body class="easyui-layout">
	<div region="center" style="padding:2px;" border="false">
	  <table id="tt" toolbar="#tb" fit="true"></table>
      <div id="tb" style="padding:5px;height:auto;">
        <div style="padding-left:5px;">
          <form id="queryform"  style="padding: 0;margin: 0;">
    		名称：<input type="text" name="LIKE_name" style="width:80px"/>&nbsp;
      		说明：<input type="text" name="LIKE_caption" style="width:120px"/>&nbsp;
            <a href="#" id="tb-query" class="easyui-linkbutton" iconCls="icon-search">查询</a>
            <a href="#" id="tb-clear" class="easyui-linkbutton" data-options="iconCls:'icon-clear'" onclick="javascript:document.forms[0].reset();">清除</a>
          </form>
        </div>
      </div>	
	</div>
    <div id="edit-window" class="easyui-window" closed="true" icon="icon-winedit" title="" style="display:none;">
      <div class="easyui-layout" fit="true">
        <div region="center" border="false">
          <iframe id="editifr"  name="editifr" class="editifr" frameborder="0" scrolling="no"></iframe>
        </div>
      </div>
    </div>	
  </body>
</html>