package com.ewcms.visit.model.clickrate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author 吴智俊
 */
@Embeddable
public class SourceFormPk implements Serializable {

	private static final long serialVersionUID = 4109619510944935912L;

	@Column(name = "site_id", nullable = false)
	private Long siteId;
	@Temporal(TemporalType.DATE)
	@Column(name = "visit_date", nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date visitDate;

	public SourceFormPk(){
	}
	
	public SourceFormPk(Date visitDate){
		super();
		this.visitDate = visitDate;
	}
	
	public SourceFormPk(Date visitDate, Long siteId){
		super();
		this.visitDate = visitDate;
		this.siteId = siteId;
	}
	
	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	@JSONField(format = "yyyy-MM-dd")
	public Date getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(Date visitDate) {
		this.visitDate = visitDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
		result = prime * result
				+ ((visitDate == null) ? 0 : visitDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceFormPk other = (SourceFormPk) obj;
		if (siteId == null) {
			if (other.siteId != null)
				return false;
		} else if (!siteId.equals(other.siteId))
			return false;
		if (visitDate == null) {
			if (other.visitDate != null)
				return false;
		} else if (!visitDate.equals(other.visitDate))
			return false;
		return true;
	}
}
