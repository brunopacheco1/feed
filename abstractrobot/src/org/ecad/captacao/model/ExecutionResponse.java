package org.ecad.captacao.model;

public class ExecutionResponse extends GenericResponse {

	private ExecutionStatus status;

	public ExecutionStatus getStatus() {
		return status;
	}

	public void setStatus(ExecutionStatus status) {
		this.status = status;
		this.setMessage(status.getMsg());
	}
}