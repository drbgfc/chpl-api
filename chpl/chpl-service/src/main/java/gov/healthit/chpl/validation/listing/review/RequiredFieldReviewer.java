package gov.healthit.chpl.validation.listing.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.util.ErrorMessageUtil;

@Component("requiredFieldReviewer")
public class RequiredFieldReviewer implements Reviewer {
    @Autowired private ErrorMessageUtil msgUtil;
    
    @Override
    public void review(CertifiedProductSearchDetails listing) {
        if (listing.getCertificationEdition() == null || listing.getCertificationEdition().get("id") == null) {
            listing.getErrorMessages().add("Certification edition is required but was not found.");
        }
        if (listing.getTestingLabs() == null || listing.getTestingLabs().size() == 0) {
            listing.getErrorMessages().add(msgUtil.getMessage("atl.notFound"));
        }
        if (StringUtils.isEmpty(listing.getAcbCertificationId())) {
            listing.getWarningMessages().add("CHPL certification ID was not found.");
        }
        if (listing.getCertificationDate() == null) {
            listing.getErrorMessages().add("Certification date was not found.");
        }
        if (listing.getDeveloper() == null) {
            listing.getErrorMessages().add("A developer is required.");
        }
        if (listing.getProduct() == null || StringUtils.isEmpty(listing.getProduct().getName())) {
            listing.getErrorMessages().add("A product name is required.");
        }
        if (listing.getVersion() == null || StringUtils.isEmpty(listing.getVersion().getVersion())) {
            listing.getErrorMessages().add("A product version is required.");
        }
        if(listing.getOldestStatus() == null) {
            listing.getErrorMessages().add(msgUtil.getMessage("listing.noStatusProvided"));
        }
    }
}
