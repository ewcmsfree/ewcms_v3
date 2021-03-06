<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/views/jspf/taglibs.jspf" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
	<title>文档编辑</title>	
	<%@ include file="/WEB-INF/views/jspf/import-css.jspf" %>
  </head>
  <body class="easyui-layout">
	<div region="west" title='<label id="treeload-button" style="cursor:pointer;"><img src="${ctx}/static/image/refresh.png" style="vertical-align: middle;">专栏库</label>' split="true" style="width:180px;">
	  <ul id="tt2"></ul>
	</div>
	<div id="tt_related" region="center" style="padding:2px;" border="false">
	  <table id="tt" toolbar="#tb" fit="true"></table>
      <div id="tb" style="padding:5px;height:auto;">
        <div class="toolbar" style="margin-bottom:2px">
		</div>
        <div  style="padding-left:5px;">
          <form id="queryform"  style="padding: 0;margin: 0;">
                      编号：<input type="text" name="EQ_id" style="width:120px;"/>&nbsp;
                      标题：<input type="text" name="LIKE_title" style="width:120px;"/>&nbsp;
            <a id="tb-query" href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',toggle:true">查询</a>
            <a id="tb-clear" href="javascript:document.forms[0].reset();" class="easyui-linkbutton" data-options="iconCls:'icon-clear'">清除</a>
          </form>
        </div>
      </div>
    </div>
    <div id="edit-window" class="easyui-window" closed="true" icon="icon-winedit" title="&nbsp;文档编辑" style="display:none;">
      <div class="easyui-layout" fit="true">
        <div region="center" border="false">
		  <iframe id="editifr"  name="editifr" class="editifr" frameborder="0" scrolling="no"></iframe>
        </div>
      </div>
    </div>
    <%@ include file="/WEB-INF/views/jspf/import-js.jspf" %>
	<script type="text/javascript" src="${ctx}/static/views/content/document/refer/article.js"></script>
	<script type="text/javascript">
		var _referArticle = new ReferArticle('#tt', '#tt2');
		$(function(){
			_referArticle.init({
				queryUrl : '${ctx}/content/document/article/query',
				treeUrl : '${ctx}/site/channel/tree'
			});
		});
			
			//重载站点专栏目录树
			function channelTreeLoad(){
				$('#tt2').tree('reload');
			}

			function getRelationRows(){
				var rows = $('#tt').datagrid('getSelections');
				return rows;
			}
			
			function initOperateQuery(){
				var url='<s:url namespace="/document/article" action="query"/>';
				url = url + "?channelId=" + channelId;
				$("#tt").datagrid({
	            	pageNumber:1,
	                url:url
	            });
			}
	</script>	
  </body>
</html>