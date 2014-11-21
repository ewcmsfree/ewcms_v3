/**
 * Copyright (c)2010-2011 Enterprise Website Content Management System(EWCMS), All rights reserved.
 * EWCMS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.ewcms.com
 */
package com.ewcms.content.particular.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ewcms.common.model.BaseSequenceEntity;

/**
 * 审批备案机关
 * 
 * <ul>
 * <li>code:组织机构代码</li>
 * <li>name:机关单位名称</li>
 * </ul>
 * 
 * @author wuzhijun
 */

@Entity
@Table(name = "particular_approval_record")
@SequenceGenerator(name = "seq", sequenceName = "seq_particular_approval_record_id", allocationSize = 1)
public class ApprovalRecord extends BaseSequenceEntity<Long> {

	private static final long serialVersionUID = 1607960165639563582L;

	@Column(name = "code", length = 9, nullable = false, unique = true)
	private String code;
	@Column(name = "name", length = 200, nullable = false)
	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
