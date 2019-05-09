package gov.healthit.chpl.permissions.domains.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.FilterDAO;
import gov.healthit.chpl.dto.FilterDTO;
import gov.healthit.chpl.permissions.domains.ActionPermissions;

@Component("filterGetByIdActionPermissions")
public class GetByIdActionPermissions extends ActionPermissions {

    private FilterDAO filterDAO;

    @Autowired
    public GetByIdActionPermissions(final FilterDAO filterDAO) {
        this.filterDAO = filterDAO;
    }

    @Override
    public boolean hasAccess() {
        return false;
    }

    @Override
    public boolean hasAccess(Object obj) {
        if (!(obj instanceof Long)) {
            return false;
        } else {
            try {
                Long filterId = (Long) obj;
                FilterDTO origFilter = filterDAO.getById(filterId);
                if (Util.getCurrentUser() == null) {
                    return false;
                } else {
                    return origFilter.getUser().getId().equals(Util.getCurrentUser().getId());
                }
            } catch (Exception e) {
                return false;
            }
        }
    }

}
