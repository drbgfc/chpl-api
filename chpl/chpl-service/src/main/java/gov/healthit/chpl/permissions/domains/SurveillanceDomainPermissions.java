package gov.healthit.chpl.permissions.domains;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.permissions.domains.surveillance.AddDocumentActionPermissions;
import gov.healthit.chpl.permissions.domains.surveillance.BasicReportActionPermissions;
import gov.healthit.chpl.permissions.domains.surveillance.CreateActionPermissions;
import gov.healthit.chpl.permissions.domains.surveillance.DeleteActionPermissions;
import gov.healthit.chpl.permissions.domains.surveillance.DeleteDocumentActionPermissions;
import gov.healthit.chpl.permissions.domains.surveillance.UpdateActionPermissions;

@Component
public class SurveillanceDomainPermissions extends DomainPermissions {
    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    public static final String ADD_DOCUMENT = "ADD_DOCUMENT";
    public static final String DELETE_DOCUMENT = "DELETE_DOCUMENT";
    public static final String BASIC_REPORT = "BASIC_REPORT";

    public SurveillanceDomainPermissions(
            @Qualifier("surveillanceCreateActionPermissions") CreateActionPermissions createActionPermissions,
            @Qualifier("surveillanceUpdateActionPermissions") UpdateActionPermissions updateActionPermissions,
            @Qualifier("surveillanceDeleteActionPermissions") DeleteActionPermissions deleteActionPermissions,
            @Qualifier("surveillanceAddDocumentActionPermissions") AddDocumentActionPermissions addDocumentActionPermissions,
            @Qualifier("surveillanceDeleteDocumentActionPermissions") DeleteDocumentActionPermissions deleteDocumentActionPermissions,
            @Qualifier("surveillanceBasicReportActionPermissions") BasicReportActionPermissions basicReportActionPermissions) {

        getActionPermissions().put(CREATE, createActionPermissions);
        getActionPermissions().put(UPDATE, updateActionPermissions);
        getActionPermissions().put(DELETE, deleteActionPermissions);
        getActionPermissions().put(ADD_DOCUMENT, addDocumentActionPermissions);
        getActionPermissions().put(DELETE_DOCUMENT, deleteDocumentActionPermissions);
        getActionPermissions().put(BASIC_REPORT, basicReportActionPermissions);
    }

}
