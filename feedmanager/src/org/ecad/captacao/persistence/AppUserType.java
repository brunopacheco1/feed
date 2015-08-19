package org.ecad.captacao.persistence;

public enum AppUserType {

	NORMAL(30), SYSTEM(null);
	
	private AppUserType(Integer duration) {
		this.duration = duration;
	}
	
	private Integer duration;
	
	public Integer getDuration() {
		return duration;
	}
}