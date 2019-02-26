package gov.healthit.chpl.permissions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.permissions.domains.ActivityDomainPermissions;
import gov.healthit.chpl.permissions.domains.CertificationBodyDomainPermissions;
import gov.healthit.chpl.permissions.domains.CertificationResultsDomainPermissions;
import gov.healthit.chpl.permissions.domains.CertifiedProductDomainPermissions;
import gov.healthit.chpl.permissions.domains.CorrectiveActionPlanDomainPermissions;
import gov.healthit.chpl.permissions.domains.DeveloperDomainPermissions;
import gov.healthit.chpl.permissions.domains.DomainPermissions;
import gov.healthit.chpl.permissions.domains.InvitationDomainPermissions;
import gov.healthit.chpl.permissions.domains.JobDomainPermissions;
import gov.healthit.chpl.permissions.domains.PendingCertifiedProductDomainPermissions;
import gov.healthit.chpl.permissions.domains.PendingSurveillanceDomainPermissions;
import gov.healthit.chpl.permissions.domains.ProductDomainPermissions;
import gov.healthit.chpl.permissions.domains.ProductVersionDomainPermissions;
import gov.healthit.chpl.permissions.domains.SecuredUserDomainPermissions;
import gov.healthit.chpl.permissions.domains.SurveillanceDomainPermissions;
import gov.healthit.chpl.permissions.domains.UserPermissionsDomainPermissions;

@Component
public class Permissions {
    public static final String PENDING_SURVEILLANCE = "PENDING_SURVEILLANCE";
    public static final String CERTIFICATION_RESULTS = "CERTIFICATION_RESULTS";
    public static final String CERTIFIED_PRODUCT = "CERTIFIED_PRODUCT";
    public static final String CORRECTIVE_ACTION_PLAN = "CORRECTIVE_ACTION_PLAN";
    public static final String INVITATION = "INVITATION";
    public static final String PENDING_CERTIFIED_PRODUCT = "PENDING_CERTIFIED_PRODUCT";
    public static final String SURVEILLANCE = "SURVEILLANCE";
    public static final String CERTIFICATION_BODY = "CERTIFICATION_BODY";
    public static final String SCHEDULER = "SCHEDULER";
    public static final String USER_PERMISSIONS = "USER_PERMISSIONS";
    public static final String ACTIVITY = "ACTIVITY";
    public static final String JOB = "JOB";
    public static final String PRODUCT = "PRODUCT";
    public static final String DEVELOPER = "DEVELOPER";
    public static final String PRODUCT_VERSION = "PRODUCT_VERSION";
    public static final String SECURED_USER = "SECURED_USER";

    private Map<String, DomainPermissions> domainPermissions = new HashMap<String, DomainPermissions>();

    @Autowired
    public Permissions(final PendingSurveillanceDomainPermissions pendingSurveillanceDomainPermissions,
            final CertificationResultsDomainPermissions certificationResultsDomainPermissions,
            final CertifiedProductDomainPermissions certifiedProductDomainPermissions,
            final CorrectiveActionPlanDomainPermissions correctiveActionPlanDomainPermissions,
            final InvitationDomainPermissions invitationDomainPermissions,
            final PendingCertifiedProductDomainPermissions pendingCertifiedProductDomainPermissions,
            final SurveillanceDomainPermissions surveillanceDomainPermissions,
            final CertificationBodyDomainPermissions certificationBodyDomainPermissions,
            final UserPermissionsDomainPermissions userPermissionsDomainPermissions,
            final ActivityDomainPermissions activityDomainPermissions,
            final JobDomainPermissions jobDomainPermissions,
            final ProductDomainPermissions productDomainPermissions,
            final DeveloperDomainPermissions developerDomainPermissions,
            final ProductVersionDomainPermissions productVersionDomainPermissions,
            final SecuredUserDomainPermissions securedUserDomainPermissions) {

        domainPermissions.put(PENDING_SURVEILLANCE, pendingSurveillanceDomainPermissions);
        domainPermissions.put(CERTIFICATION_RESULTS, certificationResultsDomainPermissions);
        domainPermissions.put(CERTIFIED_PRODUCT, certifiedProductDomainPermissions);
        domainPermissions.put(CORRECTIVE_ACTION_PLAN, correctiveActionPlanDomainPermissions);
        domainPermissions.put(INVITATION, invitationDomainPermissions);
        domainPermissions.put(PENDING_CERTIFIED_PRODUCT, pendingCertifiedProductDomainPermissions);
        domainPermissions.put(SURVEILLANCE, surveillanceDomainPermissions);
        domainPermissions.put(CERTIFICATION_BODY, certificationBodyDomainPermissions);
        domainPermissions.put(USER_PERMISSIONS, userPermissionsDomainPermissions);
        domainPermissions.put(ACTIVITY, activityDomainPermissions);
        domainPermissions.put(JOB, jobDomainPermissions);
        domainPermissions.put(PRODUCT, productDomainPermissions);
        domainPermissions.put(DEVELOPER, developerDomainPermissions);
        domainPermissions.put(PRODUCT_VERSION, productVersionDomainPermissions);
        domainPermissions.put(SECURED_USER, securedUserDomainPermissions);
    }

    public boolean hasAccess(final String domain, final String action) {
        if (domainPermissions.containsKey(domain)) {
            return domainPermissions.get(domain).hasAccess(action);
        } else {
            return false;
        }
    }

    public boolean hasAccess(final String domain, final String action, final Object obj) {
        if (domainPermissions.containsKey(domain)) {
            return domainPermissions.get(domain).hasAccess(action, obj);
        } else {
            return false;
        }
    }

}
