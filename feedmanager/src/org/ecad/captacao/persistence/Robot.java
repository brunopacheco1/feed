package org.ecad.captacao.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import com.google.gson.annotations.Expose;

@Entity
public class Robot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8269904635290961631L;
	
	@Id  @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Expose
	private Long id;
	@Expose
	@Column(nullable=false)
	private String name;
	@Expose
	@Column(nullable=false)
	private String robotUrl;
	@Expose
	@Column(nullable=false)
	private String seedUrl;
	@Expose
	@Column(nullable=false, length=1000)
	private String documentRegex;
	@Expose
	@Column(nullable=false, length=1000)
	private String seedRegex;
	@Expose
	@Column(nullable=false)
	private Integer endDepth = 1;
	@Expose
	@Column(nullable=false)
	private Integer connectionTimeout = 10000;
	@Expose
	@Column(nullable=false)
	private Long delay = 0l;
	
	@Expose
	@ManyToOne
	private RobotGroup robotGroup;
	
	@Expose
	@ElementCollection
	@CollectionTable(name="DOCUMENT_FIELDS")
	@MapKeyColumn(name="field_key")
	private Map<String, String> fields;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="robot")
	private List<Document> documents = new ArrayList<>();
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="robot")
	private List<AppToken> tokens = new ArrayList<>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public RobotGroup getRobotGroup() {
		return robotGroup;
	}

	public void setRobotGroup(RobotGroup robotGroup) {
		this.robotGroup = robotGroup;
	}

	public String getRobotUrl() {
		return robotUrl;
	}

	public void setRobotUrl(String robotUrl) {
		this.robotUrl = robotUrl;
	}

	public String getSeedUrl() {
		return seedUrl;
	}

	public void setSeedUrl(String seedUrl) {
		this.seedUrl = seedUrl;
	}

	public Integer getEndDepth() {
		return endDepth;
	}

	public void setEndDepth(Integer endDepth) {
		this.endDepth = endDepth;
	}

	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public Long getDelay() {
		return delay;
	}

	public void setDelay(Long delay) {
		this.delay = delay;
	}
	
	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public String getDocumentRegex() {
		return documentRegex;
	}

	public void setDocumentRegex(String documentRegex) {
		this.documentRegex = documentRegex;
	}

	public String getSeedRegex() {
		return seedRegex;
	}

	public void setSeedRegex(String seedRegex) {
		this.seedRegex = seedRegex;
	}

	public Map<String, String> getFields() {
		return fields;
	}

	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}

	public List<AppToken> getTokens() {
		return tokens;
	}

	public void setTokens(List<AppToken> tokens) {
		this.tokens = tokens;
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
		Robot other = (Robot) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}