package gov.healthit.chpl.manager.rules.complaints;

import gov.healthit.chpl.manager.rules.ValidationRule;

public class AcbComplaintIdValidation extends ValidationRule<ComplaintValidationContext> {

    @Override
    public boolean isValid(ComplaintValidationContext context) {
        // Received Date type is required
        if (context.getComplaintDTO().getAcbComplaintId() == null
                || context.getComplaintDTO().getAcbComplaintId().equals("")) {
            getMessages().add(getErrorMessage("complaints.acbComplaintId.required"));
            return false;
        }

        return true;
    }

}
