package gov.healthit.chpl.dto;

import gov.healthit.chpl.entity.PendingCertificationResultTestTaskEntity;

public class PendingCertificationResultTestTaskDTO {
	private Long id;
	private Long pendingCertificationResultId;
	private String uniqueId;
	private String description;
	private Float taskSuccessAverage;
	private Float taskSuccessStddev;
	private Integer taskPathDeviationObserved;
	private Integer taskPathDeviationOptimal;
	private Long taskTimeAvg;
	private Integer taskTimeStddev;
	private Integer taskTimeDeviationObservedAvg;
	private Integer taskTimeDeviationOptimalAvg;
	private Float taskErrors;
	private Float taskErrorsStddev;
	private String taskRatingScale;
	private Float taskRating;
	
	public PendingCertificationResultTestTaskDTO() {}
	
	public PendingCertificationResultTestTaskDTO(PendingCertificationResultTestTaskEntity entity) {
		this.setId(entity.getId());
		this.setPendingCertificationResultId(entity.getPendingCertificationResultId());
		this.uniqueId = entity.getUniqueId();
		this.description = entity.getDescription();
		this.taskSuccessAverage = entity.getTaskSuccessAverage();
		this.taskSuccessStddev = entity.getTaskSuccessStddev();
		this.taskPathDeviationObserved = entity.getTaskPathDeviationObserved();
		this.taskPathDeviationOptimal = entity.getTaskPathDeviationOptimal();
		this.taskTimeAvg = entity.getTaskTimeAvg();
		this.taskTimeStddev = entity.getTaskTimeStddev();
		this.taskTimeDeviationObservedAvg = entity.getTaskTimeDeviationObservedAvg();
		this.taskTimeDeviationOptimalAvg = entity.getTaskTimeDeviationOptimalAvg();
		this.taskErrors = entity.getTaskErrors();
		this.taskErrorsStddev = entity.getTaskErrorsStddev();
		this.taskRatingScale = entity.getTaskRatingScale();
		this.taskRating = entity.getTaskRating();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getPendingCertificationResultId() {
		return pendingCertificationResultId;
	}

	public void setPendingCertificationResultId(Long pendingCertificationResultId) {
		this.pendingCertificationResultId = pendingCertificationResultId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getTaskSuccessAverage() {
		return taskSuccessAverage;
	}

	public void setTaskSuccessAverage(Float taskSuccessAverage) {
		this.taskSuccessAverage = taskSuccessAverage;
	}

	public Float getTaskSuccessStddev() {
		return taskSuccessStddev;
	}

	public void setTaskSuccessStddev(Float taskSuccessStddev) {
		this.taskSuccessStddev = taskSuccessStddev;
	}

	public Integer getTaskPathDeviationObserved() {
		return taskPathDeviationObserved;
	}

	public void setTaskPathDeviationObserved(Integer taskPathDeviationObserved) {
		this.taskPathDeviationObserved = taskPathDeviationObserved;
	}

	public Integer getTaskPathDeviationOptimal() {
		return taskPathDeviationOptimal;
	}

	public void setTaskPathDeviationOptimal(Integer taskPathDeviationOptimal) {
		this.taskPathDeviationOptimal = taskPathDeviationOptimal;
	}

	public Long getTaskTimeAvg() {
		return taskTimeAvg;
	}

	public void setTaskTimeAvg(Long taskTimeAvg) {
		this.taskTimeAvg = taskTimeAvg;
	}

	public Integer getTaskTimeStddev() {
		return taskTimeStddev;
	}

	public void setTaskTimeStddev(Integer taskTimeStddev) {
		this.taskTimeStddev = taskTimeStddev;
	}

	public Integer getTaskTimeDeviationObservedAvg() {
		return taskTimeDeviationObservedAvg;
	}

	public void setTaskTimeDeviationObservedAvg(Integer taskTimeDeviationObservedAvg) {
		this.taskTimeDeviationObservedAvg = taskTimeDeviationObservedAvg;
	}

	public Integer getTaskTimeDeviationOptimalAvg() {
		return taskTimeDeviationOptimalAvg;
	}

	public void setTaskTimeDeviationOptimalAvg(Integer taskTimeDeviationOptimalAvg) {
		this.taskTimeDeviationOptimalAvg = taskTimeDeviationOptimalAvg;
	}

	public Float getTaskErrors() {
		return taskErrors;
	}

	public void setTaskErrors(Float taskErrors) {
		this.taskErrors = taskErrors;
	}

	public Float getTaskErrorsStddev() {
		return taskErrorsStddev;
	}

	public void setTaskErrorsStddev(Float taskErrorsStddev) {
		this.taskErrorsStddev = taskErrorsStddev;
	}

	public String getTaskRatingScale() {
		return taskRatingScale;
	}

	public void setTaskRatingScale(String taskRatingScale) {
		this.taskRatingScale = taskRatingScale;
	}

	public Float getTaskRating() {
		return taskRating;
	}

	public void setTaskRating(Float taskRating) {
		this.taskRating = taskRating;
	}
}