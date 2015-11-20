package gov.healthit.chpl.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.AdditionalSoftwareDAO;
import gov.healthit.chpl.dao.CQMCriterionDAO;
import gov.healthit.chpl.dao.CQMResultDAO;
import gov.healthit.chpl.dao.CertificationBodyDAO;
import gov.healthit.chpl.dao.CertificationCriterionDAO;
import gov.healthit.chpl.dao.CertificationEventDAO;
import gov.healthit.chpl.dao.CertificationResultDAO;
import gov.healthit.chpl.dao.CertificationStatusDAO;
import gov.healthit.chpl.dao.CertifiedProductDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.EventTypeDAO;
import gov.healthit.chpl.domain.ActivityConcept;
import gov.healthit.chpl.domain.AdditionalSoftware;
import gov.healthit.chpl.domain.CQMResultDetails;
import gov.healthit.chpl.domain.CertificationResult;
import gov.healthit.chpl.domain.CertifiedProductSearchDetails;
import gov.healthit.chpl.domain.PendingCertifiedProductDetails;
import gov.healthit.chpl.dto.AdditionalSoftwareDTO;
import gov.healthit.chpl.dto.AddressDTO;
import gov.healthit.chpl.dto.CQMCriterionDTO;
import gov.healthit.chpl.dto.CQMResultDTO;
import gov.healthit.chpl.dto.CertificationBodyDTO;
import gov.healthit.chpl.dto.CertificationCriterionDTO;
import gov.healthit.chpl.dto.CertificationEventDTO;
import gov.healthit.chpl.dto.CertificationResultDTO;
import gov.healthit.chpl.dto.CertificationStatusDTO;
import gov.healthit.chpl.dto.CertifiedProductDTO;
import gov.healthit.chpl.dto.EventTypeDTO;
import gov.healthit.chpl.dto.ProductDTO;
import gov.healthit.chpl.dto.ProductVersionDTO;
import gov.healthit.chpl.dto.VendorDTO;
import gov.healthit.chpl.manager.ActivityManager;
import gov.healthit.chpl.manager.CertificationBodyManager;
import gov.healthit.chpl.manager.CertifiedProductDetailsManager;
import gov.healthit.chpl.manager.CertifiedProductManager;
import gov.healthit.chpl.manager.ProductManager;
import gov.healthit.chpl.manager.ProductVersionManager;
import gov.healthit.chpl.manager.VendorManager;

@Service
public class CertifiedProductManagerImpl implements CertifiedProductManager {

	@Autowired CertifiedProductDAO dao;
	@Autowired CertificationResultDAO certDao;
	@Autowired CertificationCriterionDAO certCriterionDao;
	@Autowired CQMResultDAO cqmResultDAO;
	@Autowired CQMCriterionDAO cqmCriterionDao;
	@Autowired AdditionalSoftwareDAO softwareDao;
	@Autowired CertificationBodyDAO acbDao;
	@Autowired VendorManager vendorManager;
	@Autowired ProductManager productManager;
	@Autowired ProductVersionManager versionManager;
	@Autowired CertificationEventDAO eventDao;
	@Autowired EventTypeDAO eventTypeDao;
	
	@Autowired
	public ActivityManager activityManager;
	
	@Autowired
	public CertifiedProductDetailsManager detailsManager;
		
	@Autowired 
	public CertificationBodyManager acbManager;
	
	public CertifiedProductManagerImpl() {
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CertifiedProductDTO> getAll() {
		return dao.findAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CertifiedProductDTO> getAllWithEditPermission() {
		List<CertificationBodyDTO> userAcbs = acbManager.getAllForUser();
		if(userAcbs == null || userAcbs.size() == 0) {
			return new ArrayList<CertifiedProductDTO>();
		}
		List<Long> acbIdList = new ArrayList<Long>(userAcbs.size());
		for(CertificationBodyDTO dto : userAcbs) {
			acbIdList.add(dto.getId());
		}
		return dao.getByAcbIds(acbIdList);
	}
	
	@Autowired CertificationStatusDAO statusDao;
	
	@Override
	@Transactional(readOnly = true)
	public CertifiedProductDTO getById(Long id) throws EntityRetrievalException {
		return dao.getById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CertifiedProductDTO> getByVersion(Long versionId) {
		return dao.getByVersionId(versionId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CertifiedProductDTO> getByVersionWithEditPermission(Long versionId) {
		List<CertificationBodyDTO> userAcbs = acbManager.getAllForUser();
		if(userAcbs == null || userAcbs.size() == 0) {
			return new ArrayList<CertifiedProductDTO>();
		}
		List<Long> acbIdList = new ArrayList<Long>(userAcbs.size());
		for(CertificationBodyDTO dto : userAcbs) {
			acbIdList.add(dto.getId());
		}
		return dao.getByVersionAndAcbIds(versionId, acbIdList);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CertifiedProductDTO> getByVersions(List<Long> versionIds) {
		return dao.getByVersionIds(versionIds);
	}
	
	@Override
	@PreAuthorize("(hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN')) "
			+ "and hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin)")
	@Transactional(readOnly = false)
	public CertifiedProductDTO createFromPending(Long acbId, PendingCertifiedProductDetails pendingCp) 
			throws EntityRetrievalException, EntityCreationException, JsonProcessingException {
		
		CertifiedProductDTO toCreate = new CertifiedProductDTO();
		toCreate.setAcbCertificationId(pendingCp.getAcbCertificationId());
		
		String certifyingBodyId = null;
		if(pendingCp.getCertifyingBody().get("id") == null) {
			CertificationBodyDTO acbDto = new CertificationBodyDTO();
			acbDto.setName(pendingCp.getCertifyingBody().get("name").toString());
			if(StringUtils.isEmpty(acbDto.getName())) {
				throw new EntityCreationException("Cannot create a certifying body without a name.");
			}
			acbDto = acbDao.create(acbDto);
			certifyingBodyId = acbDto.getId().toString();
		} else {
			certifyingBodyId = pendingCp.getCertifyingBody().get("id").toString();
		}
		toCreate.setCertificationBodyId(new Long(certifyingBodyId));
		
		if(pendingCp.getCertificationEdition().get("id") == null) {
			throw new EntityCreationException("The ID of an existing certification edition (year) must be provided. A new certification edition cannot be created via this process.");
		}
		String certificationEditionId = pendingCp.getCertificationEdition().get("id").toString();
		toCreate.setCertificationEditionId(new Long(certificationEditionId));
		
		String status = pendingCp.getRecordStatus();
		if(StringUtils.isEmpty(status)) {
			throw new EntityCreationException("Cannot determine certification status. Is this a new record? An update? A removal?");
		}
		if(status.trim().equalsIgnoreCase("new")) {
			CertificationStatusDTO statusDto = statusDao.getByStatusName("Active");
			toCreate.setCertificationStatusId(statusDto.getId());
		} //TODO: handle the other cases?
		
		toCreate.setCreationDate(new Date());
		toCreate.setDeleted(false);
		toCreate.setLastModifiedDate(new Date());
		toCreate.setLastModifiedUser(Util.getCurrentUser().getId());
		
		//can be null
		if(pendingCp.getPracticeType().get("id") != null) {
			String practiceTypeId = pendingCp.getPracticeType().get("id").toString();
			toCreate.setPracticeTypeId(new Long(practiceTypeId));
		}
		
		//can be null
		if(pendingCp.getClassificationType().get("id") != null) {
			String productClassificationTypeId = pendingCp.getClassificationType().get("id").toString();
			toCreate.setProductClassificationTypeId(new Long(productClassificationTypeId));
		}
		
		String vendorId = null; 
		if(pendingCp.getVendor().get("id") == null) {
			VendorDTO newVendor = new VendorDTO();
			if(pendingCp.getVendor().get("name") == null) {
				throw new EntityCreationException("You must provide a vendor name to create a new vendor.");
			}
			newVendor.setName(pendingCp.getVendor().get("name").toString());
			Map<String, Object> vendorAddress = pendingCp.getVendorAddress();
			if(vendorAddress != null) {
				AddressDTO address = new AddressDTO();
				if(vendorAddress.get("line1") != null) {
					address.setStreetLineOne(vendorAddress.get("line1").toString());
				}
				if(vendorAddress.get("city") != null) {
					address.setCity(vendorAddress.get("city").toString());
				}
				if(vendorAddress.get("state") != null) {
					address.setState(vendorAddress.get("state").toString());
				}
				if(vendorAddress.get("zipcode") != null) {
					address.setZipcode(vendorAddress.get("zipcode").toString());
				}
				address.setCountry("USA");
				newVendor.setAddress(address);
			}
			
			newVendor = vendorManager.create(newVendor);
			vendorId = newVendor.getId().toString();
		} else {
			vendorId = pendingCp.getVendor().get("id").toString();
		}
		
		String productId = null;
		if(pendingCp.getProduct().get("id") == null) {
			ProductDTO newProduct = new ProductDTO();
			if(pendingCp.getProduct().get("name") == null) {
				throw new EntityCreationException("Either product name or ID must be provided.");
			}
			newProduct.setName(pendingCp.getProduct().get("name").toString());
			newProduct.setVendorId(new Long(vendorId));
			newProduct.setReportFileLocation(pendingCp.getReportFileLocation());
			newProduct = productManager.create(newProduct);
			productId = newProduct.getId().toString();
		} else {
			productId = pendingCp.getProduct().get("id").toString();
		}
		
		String productVersionId = null;
		if(pendingCp.getProduct().get("versionId") == null) {
			ProductVersionDTO newVersion = new ProductVersionDTO();
			if(pendingCp.getProduct().get("version") == null) {
				throw new EntityCreationException("Either version id or version must be provided.");
			}
			newVersion.setVersion(pendingCp.getProduct().get("version").toString());
			newVersion.setProductId(new Long(productId));
			newVersion = versionManager.create(newVersion);
			productVersionId = newVersion.getId().toString();
		} else {
			productVersionId = pendingCp.getProduct().get("versionId").toString();
		}
		
		toCreate.setProductVersionId(new Long(productVersionId));
		toCreate.setReportFileLocation(pendingCp.getReportFileLocation());
		toCreate.setVisibleOnChpl(true);
		//TODO: this may have to be added to pending certified products if it's in the spreadsheet?
		toCreate.setPrivacyAttestation(false);
		
		CertifiedProductDTO newCertifiedProduct = dao.create(toCreate);
		
		//additional software
		if(pendingCp.getAdditionalSoftware() != null && pendingCp.getAdditionalSoftware().size() > 0) {
			for(AdditionalSoftware software : pendingCp.getAdditionalSoftware()) {
				AdditionalSoftwareDTO newSoftware = new AdditionalSoftwareDTO();
				newSoftware.setCertifiedProductId(newCertifiedProduct.getId());
				newSoftware.setName(software.getName());
				if(!StringUtils.isEmpty(software.getVersion())) {
					newSoftware.setVersion(software.getVersion());
				} else {
					newSoftware.setVersion("-1");
				}
				softwareDao.create(newSoftware);
			}
		}
		
		//certs
		if(pendingCp.getCertificationResults() != null && pendingCp.getCertificationResults().size() > 0) {
			for(CertificationResult certResult : pendingCp.getCertificationResults()) {
				CertificationCriterionDTO criterion = certCriterionDao.getByName(certResult.getNumber());
				if(criterion == null) {
					throw new EntityCreationException("Could not find certification criterion with number " + certResult.getNumber());
				}
				CertificationResultDTO certResultToCreate = new CertificationResultDTO();
				certResultToCreate.setAutomatedMeasureCapable(criterion.getAutomatedMeasureCapable());
				certResultToCreate.setAutomatedNumerator(criterion.getAutomatedNumeratorCapable());
				certResultToCreate.setCertificationCriterionId(criterion.getId());
				certResultToCreate.setCertifiedProduct(newCertifiedProduct.getId());
				certResultToCreate.setCreationDate(new Date());
				certResultToCreate.setDeleted(false);
				certResultToCreate.setLastModifiedDate(new Date());
				certResultToCreate.setLastModifiedUser(Util.getCurrentUser().getId());
				certResultToCreate.setSuccessful(certResult.isSuccess());
				certResultToCreate.setInherited(criterion.getParentCriterionId() != null ? true : false);
				certDao.create(certResultToCreate);
			}
		}
		
		//cqms
		if(pendingCp.getCqmResults() != null && pendingCp.getCqmResults().size() > 0) {
			for(CQMResultDetails cqmResult : pendingCp.getCqmResults()) {
				CQMCriterionDTO criterion = null;
				if(StringUtils.isEmpty(cqmResult.getCmsId())) {
					criterion = cqmCriterionDao.getNQFByNumber(cqmResult.getNumber());
				} else if(cqmResult.getCmsId().startsWith("CMS")) {
					criterion = cqmCriterionDao.getCMSByNumberAndVersion(cqmResult.getCmsId(), cqmResult.getVersion());
				}
				
				if(criterion == null) {
					throw new EntityCreationException("Could not find a CQM with number " + cqmResult.getCmsId() + 
							"and/or version " + cqmResult.getVersion() + ".");
				}
				
				CQMResultDTO cqmResultToCreate = new CQMResultDTO();
				cqmResultToCreate.setCqmCriterionId(criterion.getId());
				cqmResultToCreate.setCertifiedProductId(newCertifiedProduct.getId());
				cqmResultToCreate.setCreationDate(new Date());
				cqmResultToCreate.setDeleted(false);
				cqmResultToCreate.setLastModifiedDate(new Date());
				cqmResultToCreate.setLastModifiedUser(Util.getCurrentUser().getId());
				cqmResultToCreate.setSuccess(cqmResult.isSuccess());
				cqmResultDAO.create(cqmResultToCreate);
			}
		}
		
		
		//if all this was successful, insert a certification_event for the certification date, and the date it went active in CHPL (right now)
		EventTypeDTO certificationEventType = eventTypeDao.getByName("Certification");
		CertificationEventDTO certEvent = new CertificationEventDTO();
		certEvent.setCreationDate(new Date());
		certEvent.setDeleted(false);
		Date certificationDate = new Date(new Long(pendingCp.getCertificationDate()));
		certEvent.setEventDate(certificationDate);
		certEvent.setEventTypeId(certificationEventType.getId());
		certEvent.setLastModifiedDate(new Date());
		certEvent.setLastModifiedUser(Util.getCurrentUser().getId());
		certEvent.setCertifiedProductId(newCertifiedProduct.getId());
		eventDao.create(certEvent);

		//active event
		EventTypeDTO activeEventType = eventTypeDao.getByName("Active");
		CertificationEventDTO activeEvent = new CertificationEventDTO();
		activeEvent.setCreationDate(new Date());
		activeEvent.setDeleted(false);
		activeEvent.setEventDate(new Date());
		activeEvent.setEventTypeId(activeEventType.getId());
		activeEvent.setLastModifiedDate(new Date());
		activeEvent.setLastModifiedUser(Util.getCurrentUser().getId());
		activeEvent.setCertifiedProductId(newCertifiedProduct.getId());
		eventDao.create(activeEvent);
		
		CertifiedProductSearchDetails details = detailsManager.getCertifiedProductDetails(newCertifiedProduct.getId());
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, details.getId(), "Created "+newCertifiedProduct.getChplProductNumberForActivity(), null, details);
		
		return newCertifiedProduct;
	}
	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional(readOnly = false) 
	public CertifiedProductDTO changeOwnership(Long certifiedProductId, Long acbId) throws EntityRetrievalException, JsonProcessingException, EntityCreationException {
		
		CertifiedProductDTO toUpdate = dao.getById(certifiedProductId);
		toUpdate.setCertificationBodyId(acbId);
		return update(acbId, toUpdate);
	}
	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ACB_ADMIN') or hasRole('ROLE_ACB_STAFF')")
	@Transactional(readOnly = false)
	public CertifiedProductDTO updateCertifiedProductVersion(Long certifiedProductId, Long newVersionId) 
		throws EntityRetrievalException {
		CertifiedProductDTO toUpdate = dao.getById(certifiedProductId);
		toUpdate.setProductVersionId(newVersionId);
		return dao.update(toUpdate);
	}
	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or "
			+ "( (hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN'))"
			+ "  and hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin)"
			+ ")")
	@Transactional(readOnly = false)
	public CertifiedProductDTO update(Long acbId, CertifiedProductDTO dto) throws EntityRetrievalException, JsonProcessingException, EntityCreationException {
		
		CertifiedProductDTO before = dao.getById(dto.getId());
		CertifiedProductDTO result = dao.update(dto);
		
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, result.getId(), "Updated " + result.getChplProductNumberForActivity() , before , result);
		return result;
	}
	
	
	/**
	 * both successes and failures are passed in
	 * @throws JsonProcessingException 
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or "
			+ "( (hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN'))"
			+ "  and hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin)"
			+ ")")
	@Transactional(readOnly = false)
	public void updateCertifications(Long acbId, CertifiedProductDTO productDto, Map<CertificationCriterionDTO, Boolean> certResults)
		throws EntityCreationException, EntityRetrievalException, JsonProcessingException {
		
		CertifiedProductSearchDetails before = detailsManager.getCertifiedProductDetails(productDto.getId());
		
		List<CertificationResultDTO> oldCertificationResults = certDao.findByCertifiedProductId(productDto.getId());
		
		for (CertificationResultDTO oldResult : oldCertificationResults){
			
			Long certificationCriterionId = oldResult.getCertificationCriterionId();
			CertificationCriterionDTO criterionDTO = certCriterionDao.getById(certificationCriterionId);
			
			for (Map.Entry<CertificationCriterionDTO, Boolean> certResult : certResults.entrySet()){
				if (certResult.getKey().getNumber().equals(criterionDTO.getNumber())){	
					// replace the value of the result
					oldResult.setSuccessful(certResult.getValue());
					certDao.update(oldResult);
					break;
				}
			}
		}
		
		CertifiedProductSearchDetails after = detailsManager.getCertifiedProductDetails(productDto.getId());
		activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, productDto.getId(), "Certifications for "+productDto.getChplProductNumberForActivity() + " were updated." , before , after);
	}
	
	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or "
			+ "( (hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN'))"
			+ "  and hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin)"
			+ ")")
	@Transactional(readOnly = false)
	public void updateCqms(Long acbId, CertifiedProductDTO productDto, Map<CQMCriterionDTO, Boolean> cqmResults)
		throws EntityCreationException, EntityRetrievalException, JsonProcessingException {
		
		CertifiedProductSearchDetails before = detailsManager.getCertifiedProductDetails(productDto.getId());
		
		Boolean dataHasChanged = false;
		
		List<CQMResultDTO> beforeCQMs = cqmResultDAO.findByCertifiedProductId(productDto.getId());
		
		// Handle NQFs and Additions:
		for (Map.Entry<CQMCriterionDTO, Boolean> cqm : cqmResults.entrySet()){
			
			Boolean isNQF = (cqm.getKey().getCmsId() == null);
			if (isNQF){
				for (CQMResultDTO beforeCQM : beforeCQMs){
					
					Long beforeCQMCriterionID = beforeCQM.getCqmCriterionId();
					CQMCriterionDTO beforeCriterionDTO = cqmCriterionDao.getById(beforeCQMCriterionID);
					
					if ((beforeCriterionDTO.getCmsId() == null) && (beforeCriterionDTO.getNqfNumber().equals(cqm.getKey().getNqfNumber()) ) ){
						beforeCQM.setSuccess(cqm.getValue());
						cqmResultDAO.update(beforeCQM);
						dataHasChanged = true;
						break;
					}
				}
			} else {
				
				Boolean found = false;
				
				for (CQMResultDTO beforeCQM : beforeCQMs){
					
					Long beforeCQMCriterionID = beforeCQM.getCqmCriterionId();
					CQMCriterionDTO beforeCriterionDTO = cqmCriterionDao.getById(beforeCQMCriterionID);
					
					if (beforeCriterionDTO.getCmsId().equals(cqm.getKey().getCmsId())){
						found = true;
						break;
					}
				}
				if (!found){
					
					CQMCriterionDTO criterion = null;
					if(StringUtils.isEmpty(cqm.getKey().getCmsId())) {
						criterion = cqmCriterionDao.getNQFByNumber(cqm.getKey().getNumber());
					} else if(cqm.getKey().getCmsId().startsWith("CMS")) {
						criterion = cqmCriterionDao.getCMSByNumberAndVersion(cqm.getKey().getCmsId(), cqm.getKey().getCqmVersion());
					}
					if(criterion == null) {
						throw new EntityRetrievalException("Could not find CQM with number " + cqm.getKey().getCmsId() + " and version " + cqm.getKey().getCqmVersion());
					}
					
					CQMResultDTO newCQMResult = new CQMResultDTO();
					
					newCQMResult.setCertifiedProductId(productDto.getId());
					newCQMResult.setCqmCriterionId(criterion.getId());
					newCQMResult.setCreationDate(new Date());
					newCQMResult.setDeleted(false);
					newCQMResult.setSuccess(true);
					cqmResultDAO.create(newCQMResult);
					dataHasChanged = true;
				}
			}
		}
		
		// Handle CQM deletions:
		for (CQMCriterionDTO criterion : cqmCriterionDao.findAll()){
			
			Boolean isDeletion = true;
			Boolean isNQF = (criterion == null);
			
			if (isNQF){
				isDeletion = false;
			} else {
							
				for (Map.Entry<CQMCriterionDTO, Boolean> cqm : cqmResults.entrySet()){
					
					Boolean cqmIsNQF = (cqm.getKey().getCmsId() == null);
					if (!cqmIsNQF){
						if (cqm.getKey().getCmsId().equals(criterion.getCmsId())){
							isDeletion = false;
							break;
						}
					}
				}
			}
			if (isDeletion){
				deleteCqmResult(productDto.getId(), criterion.getId());
				dataHasChanged = true;
			}
			
		}
		
		CertifiedProductSearchDetails after = detailsManager.getCertifiedProductDetails(productDto.getId());
		
		if (dataHasChanged){
			activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, productDto.getId(), "Certifications for "+productDto.getChplProductNumberForActivity()+" were updated." , before , after);
		}
		
	}
	
	
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or "
			+ "( (hasRole('ROLE_ACB_STAFF') or hasRole('ROLE_ACB_ADMIN'))"
			+ "  and hasPermission(#acbId, 'gov.healthit.chpl.dto.CertificationBodyDTO', admin)"
			+ ")")
	@Transactional(readOnly = false)
	public void updateAdditionalSoftware(Long acbId, CertifiedProductDTO productDto, List<AdditionalSoftwareDTO> newSoftware) 
		throws EntityCreationException, EntityRetrievalException, JsonProcessingException {
		
		CertifiedProductSearchDetails before = detailsManager.getCertifiedProductDetails(productDto.getId());
		
		List<AdditionalSoftwareDTO> beforeSoftware = softwareDao.findByCertifiedProductId(productDto.getId());
		
		Boolean dataHasChanged = false;
		
		
		for (AdditionalSoftwareDTO beforeSw : beforeSoftware){
			Boolean existingMatchesNew = false;
			for (AdditionalSoftwareDTO newSw : newSoftware){
				
				
				Boolean nameMatches = false;
				if ((beforeSw.getName() != null) && (newSw.getName() != null)) {
					nameMatches = beforeSw.getName().equals(newSw.getName());
				} else if ((beforeSw.getName() == null) && (newSw.getName() == null)){
					nameMatches = true;
				}
				
				Boolean versionMatches = false;
				if ((beforeSw.getVersion() != null) && (newSw.getVersion() != null)) {
					versionMatches = beforeSw.getVersion().equals(newSw.getVersion());
				} else if ((beforeSw.getVersion() == null) && (newSw.getVersion() == null)){
					versionMatches = true;
				}
				
				Boolean justificationMatches = false;
				if ((beforeSw.getJustification() != null) && (newSw.getJustification() != null)) {
					justificationMatches = beforeSw.getJustification().equals(newSw.getJustification());
				} else if ((beforeSw.getJustification() == null) && (newSw.getJustification() == null)){
					justificationMatches = true;
				}
				
				
				if ((nameMatches) && (versionMatches) && (justificationMatches)){
					existingMatchesNew = true;
					break;
				}
			}
			if (!existingMatchesNew){
				softwareDao.delete(beforeSw.getId());
				dataHasChanged = true;
			}
		}
		
		// Handle additions
		beforeSoftware = softwareDao.findByCertifiedProductId(productDto.getId());
		
		for (AdditionalSoftwareDTO newSw : newSoftware){
			
			Boolean newMatchesExisting = false;
			for (AdditionalSoftwareDTO beforeSw : beforeSoftware){
				
				Boolean nameMatches = false;
				if ((beforeSw.getName() != null) && (newSw.getName() != null)) {
					nameMatches = beforeSw.getName().equals(newSw.getName());
				} else if ((beforeSw.getName() == null) && (newSw.getName() == null)){
					nameMatches = true;
				}
				
				Boolean versionMatches = false;
				if ((beforeSw.getVersion() != null) && (newSw.getVersion() != null)) {
					versionMatches = beforeSw.getVersion().equals(newSw.getVersion());
				} else if ((beforeSw.getVersion() == null) && (newSw.getVersion() == null)){
					versionMatches = true;
				}
				
				Boolean justificationMatches = false;
				if ((beforeSw.getJustification() != null) && (newSw.getJustification() != null)) {
					justificationMatches = beforeSw.getJustification().equals(newSw.getJustification());
				} else if ((beforeSw.getJustification() == null) && (newSw.getJustification() == null)){
					justificationMatches = true;
				}
				
				if ((nameMatches) && (versionMatches) && (justificationMatches)){
					newMatchesExisting = true;
					break;
				}
			}
			if (!newMatchesExisting){
				softwareDao.create(newSw);
				dataHasChanged = true;
			}	
		}
		
		if (dataHasChanged){
			CertifiedProductSearchDetails after = detailsManager.getCertifiedProductDetails(productDto.getId());
			activityManager.addActivity(ActivityConcept.ACTIVITY_CONCEPT_CERTIFIED_PRODUCT, productDto.getId(), "Additional Software for "+productDto.getChplProductNumberForActivity()+" was updated." , before , after);
		}
	}
	
	private void deleteCqmResult(Long certifiedProductId, Long cqmId){
		
		List<CQMResultDTO> cqmResults = cqmResultDAO.findByCertifiedProductId(certifiedProductId);
		
		for (CQMResultDTO cqmResult : cqmResults){
			if (cqmResult.getCqmCriterionId().equals(cqmId)){
				cqmResultDAO.delete(cqmResult.getId());
			}
		}
	}
	
}

//	
//	@Override
//	@Transactional(readOnly = true)
//	public void delete(CertifiedProductDTO dto) throws EntityRetrievalException {
//		
//	}
//	
//	@Override
//	@Transactional(readOnly = true)
//	public void delete(Long certifiedProductId) throws EntityRetrievalException {
//		
//	}