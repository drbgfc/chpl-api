package gov.healthit.chpl.validation.surveillance.reviewer;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import gov.healthit.chpl.dao.CertificationResultDetailsDAO;
import gov.healthit.chpl.dao.surveillance.SurveillanceDAO;
import gov.healthit.chpl.domain.CertificationCriterion;
import gov.healthit.chpl.domain.concept.RequirementTypeEnum;
import gov.healthit.chpl.domain.surveillance.Surveillance;
import gov.healthit.chpl.domain.surveillance.SurveillanceRequirement;
import gov.healthit.chpl.domain.surveillance.SurveillanceRequirementType;
import gov.healthit.chpl.domain.surveillance.SurveillanceResultType;
import gov.healthit.chpl.dto.CertificationResultDetailsDTO;
import gov.healthit.chpl.service.CertificationCriterionService;
import gov.healthit.chpl.util.ErrorMessageUtil;

@Component
public class SurveillanceRequirementReviewer implements Reviewer {
    private CertificationResultDetailsDAO certResultDetailsDao;
    private SurveillanceDAO survDao;
    private ErrorMessageUtil msgUtil;
    private CertificationCriterionService criterionService;

    @Autowired
    public SurveillanceRequirementReviewer(SurveillanceDAO survDao, CertificationResultDetailsDAO certResultDetailsDao,
            ErrorMessageUtil msgUtil, CertificationCriterionService criterionService) {
        this.survDao = survDao;
        this.certResultDetailsDao = certResultDetailsDao;
        this.msgUtil = msgUtil;
        this.criterionService = criterionService;
    }

    @Override
    public void review(Surveillance surv) {
        if (surv.getRequirements() == null || surv.getRequirements().size() == 0) {
            surv.getErrorMessages().add(msgUtil.getMessage("surveillance.requirementIsRequiredForProduct",
                    surv.getCertifiedProduct().getChplProductNumber()));
            return;
        }

        List<CertificationResultDetailsDTO> certResults = certResultDetailsDao
                .getCertificationResultsForSurveillanceListing(surv);

        for (SurveillanceRequirement req : surv.getRequirements()) {
            checkRequirementExists(surv, req);
            checkRequirementTypeExists(surv, req);
            if (req.getType() != null && !StringUtils.isEmpty(req.getType().getName())) {
                if (req.getType().getName().equalsIgnoreCase(SurveillanceRequirementType.CERTIFIED_CAPABILITY)
                        && surv.getCertifiedProduct() != null && surv.getCertifiedProduct().getId() != null) {
                    checkCriterionRequirementTypeValidity(surv, req, certResults);
                } else if (req.getType().getName().equals(SurveillanceRequirementType.TRANS_DISCLOSURE_REQ)) {
                    checkTransparencyRequirementTypeValidity(surv, req);
                }
            } else {
                surv.getErrorMessages()
                        .add(msgUtil.getMessage("surveillance.requirementMustHaveValue", req.getRequirementName()));
            }

            checkResultExistsIfSurveillanceClosed(surv, req);
            checkResultTypeValidity(surv, req);
        }
    }

    private void checkRequirementExists(Surveillance surv, SurveillanceRequirement req) {
        if (StringUtils.isEmpty(req.getRequirement()) && req.getCriterion() == null) {
            surv.getErrorMessages().add(msgUtil.getMessage("surveillance.requirementIsRequired"));
        }
    }

    private void checkRequirementTypeExists(Surveillance surv, SurveillanceRequirement req) {
        if (req.getType() == null) {
            surv.getErrorMessages()
                    .add(msgUtil.getMessage("surveillance.typeMissingForRequirement", req.getRequirementName()));
        } else if (req.getType().getId() == null || req.getType().getId().longValue() <= 0) {
            SurveillanceRequirementType reqType = survDao
                    .findSurveillanceRequirementType(req.getType().getName());
            if (reqType == null) {
                surv.getErrorMessages().add(msgUtil.getMessage("surveillance.typeNameMissingForRequirement",
                        req.getType().getName(), req.getRequirementName()));
            } else {
                req.setType(reqType);
            }
        } else {
            SurveillanceRequirementType reqType = survDao
                    .findSurveillanceRequirementType(req.getType().getId());
            if (reqType == null) {
                surv.getErrorMessages().add(msgUtil.getMessage("surveillance.typeIdMissingForRequirement",
                        req.getType().getId(), req.getRequirementName()));
            } else {
                req.setType(reqType);
            }
        }
    }

    private void checkCriterionRequirementTypeValidity(Surveillance surv, SurveillanceRequirement req,
            List<CertificationResultDetailsDTO> certResults) {
        if (req.getCriterion() == null) {
            surv.getErrorMessages().add(
                    msgUtil.getMessage("surveillance.requirementInvalidForRequirementType",
                            req.getRequirementName(), req.getType().getName()));
            return;
        }
        req.setRequirement(req.getCriterion().getNumber());

        // see if the requirement type is a criterion that the product has attested to
        if (certResults != null && certResults.size() > 0) {
            Optional<CertificationResultDetailsDTO> attestedCertResult = certResults.stream()
                    .filter(certResult -> isCriteriaAttestedTo(certResult, req.getCriterion()))
                    .findFirst();
            if (!attestedCertResult.isPresent()) {
                surv.getErrorMessages().add(
                        msgUtil.getMessage("surveillance.requirementInvalidForRequirementType",
                                req.getRequirementName(), req.getType().getName()));
            }
        }
    }

    private boolean isCriteriaAttestedTo(CertificationResultDetailsDTO certResult, CertificationCriterion criterion) {
        return certResult.getCriterion() != null
                && certResult.getSuccess() != null
                && certResult.getSuccess().booleanValue()
                && certResult.getCriterion().getId().equals(criterion.getId());
    }

    private void checkTransparencyRequirementTypeValidity(Surveillance surv, SurveillanceRequirement req) {
        // requirement has to be one of 170.523 (k)(1) or (k)(2)
        req.setRequirement(criterionService.coerceToCriterionNumberFormat(req.getRequirement()));
        if (!RequirementTypeEnum.K1.getName().equals(req.getRequirement())
                && !RequirementTypeEnum.K2.getName().equals(req.getRequirement())) {
            surv.getErrorMessages()
                    .add(msgUtil.getMessage("surveillance.requirementInvalidForTransparencyType",
                            req.getRequirementName(), req.getType().getName(),
                            RequirementTypeEnum.K1.getName(), RequirementTypeEnum.K2.getName()));
        }
    }

    private void checkResultExistsIfSurveillanceClosed(Surveillance surv, SurveillanceRequirement req) {
        if (surv.getEndDate() != null) {
            if (req.getResult() == null) {
                surv.getErrorMessages()
                        .add(msgUtil.getMessage("surveillance.resultNotFound", req.getRequirementName()));
            }
        }
    }

    private void checkResultTypeValidity(Surveillance surv, SurveillanceRequirement req) {
        if (req.getResult() != null
                && (req.getResult().getId() == null || req.getResult().getId().longValue() <= 0)) {
            SurveillanceResultType resType = survDao.findSurveillanceResultType(req.getResult().getName());
            if (resType == null) {
                surv.getErrorMessages().add(msgUtil.getMessage("surveillance.resultWithNameNotFound",
                        req.getResult().getName(), req.getRequirementName()));
            } else {
                req.setResult(resType);
            }
        } else if (req.getResult() != null) {
            SurveillanceResultType resType = survDao.findSurveillanceResultType(req.getResult().getId());
            if (resType == null) {
                surv.getErrorMessages().add(msgUtil.getMessage("surveillance.resultWithIdNotFound",
                        req.getResult().getId(), req.getRequirementName()));
            } else {
                req.setResult(resType);
            }
        }
    }
}
