package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.healthit.chpl.auth.Util;
import gov.healthit.chpl.dao.CertificationEventDAO;
import gov.healthit.chpl.dao.CorrectiveActionPlanCertificationResultDAO;
import gov.healthit.chpl.dao.CorrectiveActionPlanDAO;
import gov.healthit.chpl.dao.EntityCreationException;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dto.CertificationEventDTO;
import gov.healthit.chpl.dto.CorrectiveActionPlanCertificationResultDTO;
import gov.healthit.chpl.dto.CorrectiveActionPlanDTO;
import gov.healthit.chpl.entity.CertificationCriterionEntity;
import gov.healthit.chpl.entity.CertificationEventEntity;
import gov.healthit.chpl.entity.CorrectiveActionPlanCertificationEntity;
import gov.healthit.chpl.entity.CorrectiveActionPlanEntity;

@Repository("correctiveActionPlanCertificationResultDAO")
public class CorrectiveActionPlanCertificationResultDAOImpl extends BaseDAOImpl implements CorrectiveActionPlanCertificationResultDAO {
	
	@Autowired CorrectiveActionPlanDAO capDao;
	
	@Override
	public CorrectiveActionPlanCertificationResultDTO create(CorrectiveActionPlanCertificationResultDTO toCreate) throws EntityCreationException,
		EntityRetrievalException {
		CorrectiveActionPlanCertificationEntity entity = null;
		try {
			if(toCreate.getId() != null) {
				entity = getEntityById(toCreate.getId());
			}
		} catch(EntityRetrievalException e) {
			throw new EntityCreationException(e);
		}
		
		if(entity != null) {
			throw new EntityCreationException("An entity with this id already exists.");
		} else {
			CorrectiveActionPlanEntity plan = new CorrectiveActionPlanEntity();
			plan.setId(toCreate.getCorrectiveActionPlanId());
			
			CertificationCriterionEntity cert = new CertificationCriterionEntity();
			cert.setId(toCreate.getCertCriterion().getId());
			
			entity = new CorrectiveActionPlanCertificationEntity();
			entity.setCorrectiveActionPlan(plan);
			entity.setCertificationCriterion(cert);
			entity.setAcbSummary(toCreate.getAcbSummary());
			entity.setDeveloperSummaryDescription(toCreate.getDeveloperSummary());
			entity.setResolution(toCreate.getResolution());
			entity.setCreationDate(new Date());
			entity.setDeleted(false);
			entity.setLastModifiedDate(new Date());
			entity.setLastModifiedUser(Util.getCurrentUser().getId());
			create(entity);
			return new CorrectiveActionPlanCertificationResultDTO(entity);
		}
	}

	@Override
	public CorrectiveActionPlanCertificationResultDTO update(CorrectiveActionPlanCertificationResultDTO toUpdate) throws EntityRetrievalException {
		CorrectiveActionPlanCertificationEntity entity = getEntityById(toUpdate.getId());
		if(entity == null) {
			throw new EntityRetrievalException("Entity with id " + toUpdate.getId() + " does not exist.");
		}
		
		if(toUpdate.getCorrectiveActionPlanId() != null) {
			CorrectiveActionPlanEntity plan = new CorrectiveActionPlanEntity();
			plan.setId(toUpdate.getCorrectiveActionPlanId());
			
			entity.setCorrectiveActionPlan(plan);
		}
		if(toUpdate.getCertCriterion() != null) {
			CertificationCriterionEntity cert = new CertificationCriterionEntity();
			cert.setId(toUpdate.getCertCriterion().getId());
			
			entity.setCertificationCriterion(cert);
		}
		
		entity.setAcbSummary(toUpdate.getAcbSummary());
		entity.setDeveloperSummaryDescription(toUpdate.getDeveloperSummary());
		entity.setResolution(toUpdate.getResolution());
		entity.setLastModifiedDate(new Date());
		entity.setLastModifiedUser(Util.getCurrentUser().getId());
		update(entity);
		return new CorrectiveActionPlanCertificationResultDTO(entity);
	}
	
	@Override
	public CorrectiveActionPlanCertificationResultDTO getById(Long id) throws EntityRetrievalException {
		CorrectiveActionPlanCertificationResultDTO dto = null;
		CorrectiveActionPlanCertificationEntity entity = getEntityById(id);
		if(entity != null) {
			dto = new CorrectiveActionPlanCertificationResultDTO(entity);
		}
		return dto;
	}


	@Override
	public List<CorrectiveActionPlanCertificationResultDTO> getAllForCorrectiveActionPlan(Long correctiveActionPlanId) {
		List<CorrectiveActionPlanCertificationEntity> entities = getEntitiesByCorrectiveActionPlan(correctiveActionPlanId);
		List<CorrectiveActionPlanCertificationResultDTO> dtos = new ArrayList<CorrectiveActionPlanCertificationResultDTO>();
		
		for(CorrectiveActionPlanCertificationEntity entity : entities) {
			CorrectiveActionPlanCertificationResultDTO dto = new CorrectiveActionPlanCertificationResultDTO(entity);
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public void delete(Long id) throws EntityRetrievalException {
		CorrectiveActionPlanCertificationEntity entity = getEntityById(id);
		entity.setDeleted(true);
		entity.setLastModifiedDate(new Date());
		entity.setLastModifiedUser(Util.getCurrentUser().getId());
		update(entity);
	}
	
	private void create(CorrectiveActionPlanCertificationEntity entity) {
		
		entityManager.persist(entity);
		entityManager.flush();
		
	}
	
	private void update(CorrectiveActionPlanCertificationEntity entity) {
		
		entityManager.merge(entity);	
		entityManager.flush();
	}
	
	private CorrectiveActionPlanCertificationEntity getEntityById(Long id) throws EntityRetrievalException {
		
		CorrectiveActionPlanCertificationEntity entity = null;
			
		Query query = entityManager.createQuery( "from CorrectiveActionPlanCertificationEntity capCert "
				+ "JOIN FETCH capCert.correctiveActionPlan cap " 
				+ "JOIN FETCH capCert.certificationCriterion cert " 
				+ "where (NOT capCert.deleted = true) AND (capCert.id = :entityid) ", CorrectiveActionPlanCertificationEntity.class );
		query.setParameter("entityid", id);
		List<CorrectiveActionPlanCertificationEntity> result = query.getResultList();
		
		if (result.size() > 1){
			throw new EntityRetrievalException("Data error. Duplicate corrective action plan certification id in database.");
		}
		
		if (result.size() > 0){
			entity = result.get(0);
		}
		
		return entity;
	}
	
	
	
	private List<CorrectiveActionPlanCertificationEntity> getEntitiesByCorrectiveActionPlan(Long id) {
		
		Query query = entityManager.createQuery( "from CorrectiveActionPlanCertificationEntity capCert "
				+ "JOIN FETCH capCert.correctiveActionPlan cap "
				+ "JOIN FETCH capCert.certificationCriterion cert "
				+ "where (NOT capCert.deleted = true) AND (cap.id = :corrective_action_plan_id) ", CorrectiveActionPlanCertificationEntity.class );
		query.setParameter("corrective_action_plan_id", id);
		List<CorrectiveActionPlanCertificationEntity> result = query.getResultList();
		
		return result;
	}	
}