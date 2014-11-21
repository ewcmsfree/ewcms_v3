/**
 * Copyright (c)2010-2011 Enterprise Website Content Management System(EWCMS), All rights reserved.
 * EWCMS PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.ewcms.com
 */

package com.ewcms.content.document.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.ewcms.common.model.BaseSequenceEntity;

/**
 * 审核用户
 * 
 * @author wu_zhijun
 * 
 */
@Entity
@Table(name = "content_review_user")
@SequenceGenerator(name = "seq", sequenceName = "seq_content_review_user_id", allocationSize = 1)
public class ReviewUser extends BaseSequenceEntity<Long> {

	private static final long serialVersionUID = 5703561985089566858L;

	@Column(name = "user_name")
	private String userName;
	@Column(name= "real_name")
	private String realName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}
}
