package org.ecad.captacao.model;

public enum ExecutionStatus {

	NOT_RUNNING("Crawler e normalizador aguardando execução"),
	CRAWLER_SCHEDULED("O crawler será executado em instantes"),
	NORMALIZER_SCHEDULED("O normalizador será executado em instantes"),
	CRAWLING("Crawler em execução"),
	NORMALIZING("Normalizador em execução");
	
	private ExecutionStatus(String msg) {
		this.msg = msg;
	}
	
	private String msg;

	public String getMsg() {
		return msg;
	}
}