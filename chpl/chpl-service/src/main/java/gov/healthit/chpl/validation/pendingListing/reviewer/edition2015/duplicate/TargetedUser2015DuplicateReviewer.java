package gov.healthit.chpl.validation.pendingListing.reviewer.edition2015.duplicate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.dto.listing.pending.PendingCertifiedProductDTO;
import gov.healthit.chpl.dto.listing.pending.PendingCertifiedProductTargetedUserDTO;
import gov.healthit.chpl.util.ErrorMessageUtil;
import gov.healthit.chpl.validation.pendingListing.reviewer.duplicate.DuplicateReviewResult;

@Component("targetedUser2015DuplicateReviewer")
public class TargetedUser2015DuplicateReviewer {
    private ErrorMessageUtil errorMessageUtil;

    @Autowired
    public TargetedUser2015DuplicateReviewer(final ErrorMessageUtil errorMessageUtil) {
        this.errorMessageUtil = errorMessageUtil;
    }

    public void review(final PendingCertifiedProductDTO listing) {

        DuplicateReviewResult<PendingCertifiedProductTargetedUserDTO> targetedUserDuplicateResults =
                new DuplicateReviewResult<PendingCertifiedProductTargetedUserDTO>(getPredicate());

        if (listing.getTargetedUsers() != null) {
            for (PendingCertifiedProductTargetedUserDTO dto : listing.getTargetedUsers()) {
                targetedUserDuplicateResults.addObject(dto);
            }
        }
        if (targetedUserDuplicateResults.duplicatesExist()) {
            listing.getWarningMessages().addAll(
                    getWarnings(targetedUserDuplicateResults.getDuplicateList()));
            listing.setTargetedUsers(targetedUserDuplicateResults.getUniqueList());
        }
    }

    private List<String> getWarnings(final List<PendingCertifiedProductTargetedUserDTO> duplicates) {
        List<String> warnings = new ArrayList<String>();
        for (PendingCertifiedProductTargetedUserDTO duplicate : duplicates) {
            String warning = errorMessageUtil.getMessage("listing.duplicateTargetedUser.2015", duplicate.getName());
            warnings.add(warning);
        }
        return warnings;
    }

    private BiPredicate<PendingCertifiedProductTargetedUserDTO, PendingCertifiedProductTargetedUserDTO> getPredicate() {
        return new BiPredicate<PendingCertifiedProductTargetedUserDTO, PendingCertifiedProductTargetedUserDTO>() {
            @Override
            public boolean test(final PendingCertifiedProductTargetedUserDTO dto1,
                    final PendingCertifiedProductTargetedUserDTO dto2) {
                return ObjectUtils.allNotNull(dto1.getName(), dto2.getName())
                        && dto1.getName().equals(dto2.getName());
            }
        };
    }
}
