package org.ecad.captacao.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;

import com.google.gson.annotations.Expose;

@Entity
public class Document implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3970133048677676996L;

	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Expose
	private Long id;
	
	@Expose
	private Date captureDate;

	@Expose
	private Date normalizeDate;

	@Expose
	@Column(length=3000, nullable=false)
	private String url;
	
	@Expose
	@Column(nullable=false)
	private NormalizationStatus status;
	
	@ManyToOne
	private Robot robot;
	
	@Expose
	@ElementCollection
	@CollectionTable(name="DOCUMENT_FIELDS_VALUES")
	@MapKeyColumn(name="field_key")
	@Column(name="field_value", length=500000)
	private Map<String, String> fieldsValues;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public Map<String, String> getFieldsValues() {
		return fieldsValues;
	}

	public void setFieldsValues(Map<String, String> fieldsValues) {
		this.fieldsValues = fieldsValues;
	}

	public Date getCaptureDate() {
		return captureDate;
	}

	public void setCaptureDate(Date captureDate) {
		this.captureDate = captureDate;
	}

	public Date getNormalizeDate() {
		return normalizeDate;
	}

	public void setNormalizeDate(Date normalizeDate) {
		this.normalizeDate = normalizeDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public NormalizationStatus getStatus() {
		return status;
	}

	public void setStatus(NormalizationStatus status) {
		this.status = status;
	}

	public Robot getRobot() {
		return robot;
	}

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Document other = (Document) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}