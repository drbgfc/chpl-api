package gov.healthit.chpl.dto.surveillance.report;

import gov.healthit.chpl.dto.SurveillanceBasicDTO;
import gov.healthit.chpl.entity.surveillance.SurveillanceBasicEntity;
import gov.healthit.chpl.entity.surveillance.report.PrivilegedSurveillanceEntity;
import gov.healthit.chpl.entity.surveillance.report.QuarterlyReportSurveillanceMapEntity;

public class QuarterlyReportSurveillanceMapDTO extends SurveillanceBasicDTO {
    private static final long serialVersionUID = 849149508008111347L;

    private Long mappingId;
    private QuarterlyReportDTO quarterlyReport;
    private SurveillanceOutcomeDTO surveillanceOutcome;
    private SurveillanceProcessTypeDTO surveillanceProcessType;
    private Boolean k1Reviewed;
    private String groundsForInitiating;
    private String nonconformityCauses;
    private String nonconformityNature;
    private String stepsToSurveil;
    private String stepsToEngage;
    private String additionalCostsEvaluation;
    private String limitationsEvaluation;
    private String nondisclosureEvaluation;
    private String directionDeveloperResolution;
    private String completedCapVerification;

    public QuarterlyReportSurveillanceMapDTO() {
        super();
    }

    public QuarterlyReportSurveillanceMapDTO(final SurveillanceBasicEntity entity) {
        super(entity);
    }

    public QuarterlyReportSurveillanceMapDTO(final PrivilegedSurveillanceEntity entity) {
        super(entity);

        this.k1Reviewed = entity.getK1Reviewed();
        this.groundsForInitiating = entity.getGroundsForInitiating();
        this.nonconformityCauses = entity.getNonconformityCauses();
        this.nonconformityNature = entity.getNonconformityNature();
        this.stepsToSurveil = entity.getStepsToSurveil();
        this.stepsToEngage = entity.getStepsToEngage();
        this.additionalCostsEvaluation = entity.getAdditionalCostsEvaluation();
        this.limitationsEvaluation = entity.getLimitationsEvaluation();
        this.nondisclosureEvaluation = entity.getNondisclosureEvaluation();
        this.directionDeveloperResolution = entity.getDirectionDeveloperResolution();
        this.completedCapVerification = entity.getCompletedCapVerification();

        if (entity.getQuarterlyReport() != null) {
            this.quarterlyReport = new QuarterlyReportDTO(entity.getQuarterlyReport());
        } else {
            this.quarterlyReport = new QuarterlyReportDTO();
            this.quarterlyReport.setId(entity.getQuarterlyReportId());
        }

        if (entity.getSurveillanceOutcome() != null) {
            this.surveillanceOutcome = new SurveillanceOutcomeDTO(entity.getSurveillanceOutcome());
        } else {
            this.surveillanceOutcome = new SurveillanceOutcomeDTO();
            this.surveillanceOutcome.setId(entity.getSurveillanceOutcomeId());
        }

        if (entity.getSurveillanceProcessType() != null) {
            this.surveillanceProcessType = new SurveillanceProcessTypeDTO(entity.getSurveillanceProcessType());
        } else {
            this.surveillanceProcessType = new SurveillanceProcessTypeDTO();
            this.surveillanceProcessType.setId(entity.getSurveillanceProcessTypeId());
        }
    }

    public QuarterlyReportSurveillanceMapDTO(final QuarterlyReportSurveillanceMapEntity entity) {
        super(entity.getSurveillance());

        this.mappingId = entity.getId();
        this.k1Reviewed = entity.getK1Reviewed();
        this.groundsForInitiating = entity.getGroundsForInitiating();
        this.nonconformityCauses = entity.getNonconformityCauses();
        this.nonconformityNature = entity.getNonconformityNature();
        this.stepsToSurveil = entity.getStepsToSurveil();
        this.stepsToEngage = entity.getStepsToEngage();
        this.additionalCostsEvaluation = entity.getAdditionalCostsEvaluation();
        this.limitationsEvaluation = entity.getLimitationsEvaluation();
        this.nondisclosureEvaluation = entity.getNondisclosureEvaluation();
        this.directionDeveloperResolution = entity.getDirectionDeveloperResolution();
        this.completedCapVerification = entity.getCompletedCapVerification();

        if (entity.getQuarterlyReport() != null) {
            this.quarterlyReport = new QuarterlyReportDTO(entity.getQuarterlyReport());
        } else {
            this.quarterlyReport = new QuarterlyReportDTO();
            this.quarterlyReport.setId(entity.getQuarterlyReportId());
        }

        if (entity.getSurveillanceOutcome() != null) {
            this.surveillanceOutcome = new SurveillanceOutcomeDTO(entity.getSurveillanceOutcome());
        } else {
            this.surveillanceOutcome = new SurveillanceOutcomeDTO();
            this.surveillanceOutcome.setId(entity.getSurveillanceOutcomeId());
        }

        if (entity.getSurveillanceProcessType() != null) {
            this.surveillanceProcessType = new SurveillanceProcessTypeDTO(entity.getSurveillanceProcessType());
        } else {
            this.surveillanceProcessType = new SurveillanceProcessTypeDTO();
            this.surveillanceProcessType.setId(entity.getSurveillanceProcessTypeId());
        }
    }

    public Long getMappingId() {
        return mappingId;
    }

    public void setMappingId(final Long mappingId) {
        this.mappingId = mappingId;
    }

    public QuarterlyReportDTO getQuarterlyReport() {
        return quarterlyReport;
    }

    public void setQuarterlyReport(final QuarterlyReportDTO quarterlyReport) {
        this.quarterlyReport = quarterlyReport;
    }

    public SurveillanceOutcomeDTO getSurveillanceOutcome() {
        return surveillanceOutcome;
    }

    public void setSurveillanceOutcome(final SurveillanceOutcomeDTO surveillanceOutcome) {
        this.surveillanceOutcome = surveillanceOutcome;
    }

    public SurveillanceProcessTypeDTO getSurveillanceProcessType() {
        return surveillanceProcessType;
    }

    public void setSurveillanceProcessType(final SurveillanceProcessTypeDTO surveillanceProcessType) {
        this.surveillanceProcessType = surveillanceProcessType;
    }

    public Boolean getK1Reviewed() {
        return k1Reviewed;
    }

    public void setK1Reviewed(final Boolean k1Reviewed) {
        this.k1Reviewed = k1Reviewed;
    }

    public String getGroundsForInitiating() {
        return groundsForInitiating;
    }

    public void setGroundsForInitiating(final String groundsForInitiating) {
        this.groundsForInitiating = groundsForInitiating;
    }

    public String getNonconformityCauses() {
        return nonconformityCauses;
    }

    public void setNonconformityCauses(final String nonconformityCauses) {
        this.nonconformityCauses = nonconformityCauses;
    }

    public String getNonconformityNature() {
        return nonconformityNature;
    }

    public void setNonconformityNature(final String nonconformityNature) {
        this.nonconformityNature = nonconformityNature;
    }

    public String getStepsToSurveil() {
        return stepsToSurveil;
    }

    public void setStepsToSurveil(final String stepsToSurveil) {
        this.stepsToSurveil = stepsToSurveil;
    }

    public String getStepsToEngage() {
        return stepsToEngage;
    }

    public void setStepsToEngage(final String stepsToEngage) {
        this.stepsToEngage = stepsToEngage;
    }

    public String getAdditionalCostsEvaluation() {
        return additionalCostsEvaluation;
    }

    public void setAdditionalCostsEvaluation(final String additionalCostsEvaluation) {
        this.additionalCostsEvaluation = additionalCostsEvaluation;
    }

    public String getLimitationsEvaluation() {
        return limitationsEvaluation;
    }

    public void setLimitationsEvaluation(final String limitationsEvaluation) {
        this.limitationsEvaluation = limitationsEvaluation;
    }

    public String getNondisclosureEvaluation() {
        return nondisclosureEvaluation;
    }

    public void setNondisclosureEvaluation(final String nondisclosureEvaluation) {
        this.nondisclosureEvaluation = nondisclosureEvaluation;
    }

    public String getDirectionDeveloperResolution() {
        return directionDeveloperResolution;
    }

    public void setDirectionDeveloperResolution(final String directionDeveloperResolution) {
        this.directionDeveloperResolution = directionDeveloperResolution;
    }

    public String getCompletedCapVerification() {
        return completedCapVerification;
    }

    public void setCompletedCapVerification(final String completedCapVerification) {
        this.completedCapVerification = completedCapVerification;
    }
}
