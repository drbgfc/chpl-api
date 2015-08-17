package gov.healthit.chpl.manager;

import java.util.List;

import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dto.VendorDTO;

public interface VendorManager {
	public List<VendorDTO> getAll();
	public VendorDTO getById(Long id) throws EntityRetrievalException;
	public void update(VendorDTO vendor) throws EntityRetrievalException;
	public void create(VendorDTO dto) throws EntityRetrievalException, EntityCreationException;
	public void delete(VendorDTO dto);
	public void delete(Long vendorId);
}
