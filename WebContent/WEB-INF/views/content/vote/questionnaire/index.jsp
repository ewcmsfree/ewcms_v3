<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
	<title>问卷调查</title>	
	<%@ include file="../../../taglibs.jsp" %>
    <script type="text/javascript" src="${ctx}/static/easyui/ext/datagrid-detailview.js"></script>
	<script type="text/javascript" src="${ctx}/static/views/content/vote/questionnaire/index.js"></script>
    <script type="text/javascript">
    	var _questionnaireIndex = new QuestionnaireIndex('#tt');
    	$(function(){
    		_questionnaireIndex.init({
    			queryUrl : '${ctx}/content/vote/questionnaire/query/${channelId}',
    			editUrl : '${ctx}/content/vote/questionnaire/edit/${channelId}',
    			deleteUrl : '${ctx}/content/vote/questionnaire/delete/${channelId}',
    			subjectIndexUrl : '${ctx}/content/vote/subject/index',
    			viewVoteUrl : '${ctx}/view.vote',
				resultVoteUrl : '${ctx}/content/vote/questionnaire/resultVote',
				personUrl : '${ctx}/content/vote/person/index'
    		});
    	});
	</script>
  </head>
  <body class="easyui-layout">
	<div region="center" border="false">  
	  <table id="tt" toolbar="#tb" fit="true"></table>
	  <div id="tb" style="padding:2px;height:auto;">
        <div class="toolbar" style="margin-bottom:2px">
          <a id="tb-add" href="#" class="easyui-linkbutton" plain="true" iconCls="icon-add">新增</a>
		  <a id="tb-edit" href="#" class="easyui-linkbutton" plain="true" iconCls="icon-edit">修改</a>
		  <a id="tb-remove" href="#" class="easyui-linkbutton" plain="true" iconCls="icon-remove">删除</a>
		  <a id="tb-prview" href="#" class="easyui-linkbutton" plain="true" iconCls="icon-voteprivew">预览</a>
		  <a id="tb-result" href="#" class="easyui-linkbutton" plain="true" iconCls="icon-voteresult">结果</a>
		  <a id="tb-person" href="#" class="easyui-linkbutton" plain="true" iconCls="icon-votedetail">投票人员</a>
        </div>
        <div>
          <form id="queryform" style="padding:0;margin:0;">
            <table class="formtable" width="100%">
              <tr>
                <td width="8%">编号：</td>
                <td width="17%"><input type="text" name="EQ_id" style="width:120px;"/></td>
                <td width="8%">问卷名称：</td>
                <td width="17%"><input type="text" name="LIKE_title" style="width:120px;"/></td>
                <td width="8%">投票时间：</td>
                <td width="17%"><input type="text" id="startTime" name="GTED_startTime" class="easyui-datebox" style="width:100px"/>&nbsp;至&nbsp;<input type="text" id="endTime" name="LTED_endTime" class="easyui-datebox" style="width:100px"/></td>
                <td width="25%" colspan="2">
			      <a href="#" id="toolbar-arrows" style="text-decoration:none">更多...</a>
                  <a id="tb-query" href="#" class="easyui-linkbutton" >查询</a>
                  <a id="tb-clear" href="javascript:document.forms[0].reset();" class="easyui-linkbutton">清除</a>
                </td>
              </tr>
              <tr>
                <td>查看方式：</td>
                <td><form:select id="status" name="EQ_status" path="statusMap">
			          <form:option value="" label="------请选择------"/>
			          <form:options items="${statusMap}" itemLabel="description"/>
			        </form:select>
			    </td>
                <td>结束投票：</td>
                <td><input type="checkbox" id="voteEnd" name="EQ_voteEnd"/></td>
                <td>投票人数</td>
                <td><input type="text" id="number" name="GTE_number" style="width:60px;"/>&nbsp;至&nbsp;<input type="text" id="number" name="LTE_number" style="width:60px;"/></td>
                <td width="8%">验证码：</td>
                <td width="17%"><input type="checkbox" id="verifiCode" name="EQ_verifiCode"/></td>
              </tr>
            </table>
          </form>
        </div>
      </div>
    </div>
    <div id="edit-window" class="easyui-window" closed="true" icon="icon-winedit" title="&nbsp;问卷调查">
      <div class="easyui-layout" fit="true">
        <div region="center" border="false">
          <iframe id="editifr"  name="editifr" class="editifr" frameborder="0" scrolling="no"></iframe>
        </div>
      </div>
    </div>
    <div id="person-window" class="easyui-window" icon="icon-votedetail" closed="true">
      <div class="easyui-layout" fit="true">
        <div region="center" border="false">
       	  <iframe id="editifr_person"  name="editifr_person" class="editifr" frameborder="0" width="100%" height="100%" scrolling="no"></iframe>
        </div>
      </div>
    </div>
  </body>
</html>