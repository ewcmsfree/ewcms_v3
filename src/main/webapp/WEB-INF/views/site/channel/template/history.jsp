<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/jspf/taglibs.jspf" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
	<title>历史内容</title>
	<%@ include file="/WEB-INF/views/jspf/import-css.jspf" %>
  </head>
  <body class="easyui-layout">
	<div region="center" style="padding: 2px;" border="false">
	  <table id="tt" toolbar="#tb" fit="true"></table>
	  <div id="tb" style="padding:5px;height:auto;">
        <div class="toolbar" style="margin-bottom:2px">
		  <a id="td-restore" href="javascript:void(0);" class="easyui-linkbutton" plain="true" iconCls="icon-resume">还原</a>
		  <a id="td-cancel" href="javascript:void(0);" class="easyui-linkbutton" plain="true" iconCls="icon-cancel">关闭</a>
		</div>
        <div  style="padding-left:5px;">
          <form id="queryform"  style="padding: 0;margin: 0;">
                     时间：<input type="text" id="startDate" name="GTED_createDate" class="easyui-datebox" style="width:120px"/>至<input type="text" id="endDate" name="LTED_createDate" class="easyui-datebox" style="width:120px"/>&nbsp;
            <a id="tb-query" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',toggle:true">查询</a>
            <a id="tb-clear" href="javascript:document.forms[0].reset();" class="easyui-linkbutton" data-options="iconCls:'icon-clear'">清除</a>
          </form>
        </div>
      </div>
	</div>
	<%@ include file="/WEB-INF/views/jspf/import-js.jspf" %>
	<script type="text/javascript" src="${ctx}/static/views/site/channel/template/history.js"></script>
	<script type="text/javascript">
		var _historyTemplate = new HistoryTemplate('#tt');
		$(function(){
			_historyTemplate.init({
				queryUrl : '${ctx}/site/channel/template/history/query/${templateId}',
				restoreUrl : '${ctx}/site/channel/template/history/restore/${templateId}'
			});
		});
	</script>
  </body>
</html>