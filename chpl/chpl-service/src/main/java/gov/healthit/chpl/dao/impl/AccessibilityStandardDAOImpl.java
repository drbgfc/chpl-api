package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Repository;

import gov.healthit.chpl.dao.AccessibilityStandardDAO;
import gov.healthit.chpl.dto.AccessibilityStandardDTO;
import gov.healthit.chpl.entity.AccessibilityStandardEntity;
import gov.healthit.chpl.exception.EntityCreationException;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.util.AuthUtil;

@Repository("accessibilityStandardDAO")
public class AccessibilityStandardDAOImpl extends BaseDAOImpl implements AccessibilityStandardDAO {
    private static final Logger LOGGER = LogManager.getLogger(AccessibilityStandardDAOImpl.class);
    @Autowired
    MessageSource messageSource;

    @Override
    public AccessibilityStandardDTO create(AccessibilityStandardDTO dto) throws EntityCreationException {

        AccessibilityStandardEntity entity = null;
        if (dto.getId() != null) {
            entity = this.getEntityById(dto.getId());
        }

        if (entity != null) {
            throw new EntityCreationException("An entity with this ID already exists.");
        } else {
            try {
                entity = new AccessibilityStandardEntity();
                entity.setCreationDate(new Date());
                entity.setDeleted(false);
                entity.setLastModifiedDate(new Date());
                entity.setLastModifiedUser(AuthUtil.getAuditId());
                entity.setName(dto.getName());
                create(entity);
            } catch (Exception ex) {
                String msg = String.format(
                        messageSource.getMessage(new DefaultMessageSourceResolvable("listing.badAccessibilityStandard"),
                                LocaleContextHolder.getLocale()),
                        dto.getName());
                LOGGER.error(msg, ex);
                throw new EntityCreationException(msg);
            }
            return new AccessibilityStandardDTO(entity);
        }
    }

    @Override
    public AccessibilityStandardDTO update(AccessibilityStandardDTO dto) throws EntityRetrievalException {
        AccessibilityStandardEntity entity = this.getEntityById(dto.getId());

        if (entity == null) {
            throw new EntityRetrievalException("Entity with id " + dto.getId() + " does not exist");
        }

        entity.setName(dto.getName());

        update(entity);
        return new AccessibilityStandardDTO(entity);
    }

    @Override
    public void delete(Long id) throws EntityRetrievalException {

        AccessibilityStandardEntity toDelete = getEntityById(id);

        if (toDelete != null) {
            toDelete.setDeleted(true);
            toDelete.setLastModifiedDate(new Date());
            toDelete.setLastModifiedUser(AuthUtil.getAuditId());
            update(toDelete);
        }
    }

    @Override
    public AccessibilityStandardDTO getById(Long id) {

        AccessibilityStandardDTO dto = null;
        AccessibilityStandardEntity entity = getEntityById(id);

        if (entity != null) {
            dto = new AccessibilityStandardDTO(entity);
        }
        return dto;
    }

    @Override
    public AccessibilityStandardDTO getByName(String name) {

        AccessibilityStandardDTO dto = null;
        List<AccessibilityStandardEntity> entities = getEntitiesByName(name);

        if (entities != null && entities.size() > 0) {
            dto = new AccessibilityStandardDTO(entities.get(0));
        }
        return dto;
    }

    @Override
    public List<AccessibilityStandardDTO> findAll() {

        List<AccessibilityStandardEntity> entities = getAllEntities();
        List<AccessibilityStandardDTO> dtos = new ArrayList<AccessibilityStandardDTO>();

        for (AccessibilityStandardEntity entity : entities) {
            AccessibilityStandardDTO dto = new AccessibilityStandardDTO(entity);
            dtos.add(dto);
        }
        return dtos;

    }

    @Override
    public AccessibilityStandardDTO findOrCreate(Long id, String name) throws EntityCreationException {
        AccessibilityStandardDTO result = null;
        if (id != null) {
            result = getById(id);
        } else if (!StringUtils.isEmpty(name)) {
            result = getByName(name);
        }

        if (result == null) {
            AccessibilityStandardDTO toCreate = new AccessibilityStandardDTO();
            toCreate.setName(name.trim());
            result = create(toCreate);
        }
        return result;
    }

    private void create(AccessibilityStandardEntity entity) {

        entityManager.persist(entity);
        entityManager.flush();

    }

    private void update(AccessibilityStandardEntity entity) {

        entityManager.merge(entity);
        entityManager.flush();
    }

    private List<AccessibilityStandardEntity> getAllEntities() {
        return entityManager.createQuery("from AccessibilityStandardEntity where (NOT deleted = true) ",
                AccessibilityStandardEntity.class).getResultList();
    }

    private AccessibilityStandardEntity getEntityById(Long id) {

        AccessibilityStandardEntity entity = null;

        Query query = entityManager.createQuery(
                "from AccessibilityStandardEntity where (NOT deleted = true) AND (id = :entityid) ",
                AccessibilityStandardEntity.class);
        query.setParameter("entityid", id);
        List<AccessibilityStandardEntity> result = query.getResultList();

        if (result.size() > 0) {
            entity = result.get(0);
        }

        return entity;
    }

    private List<AccessibilityStandardEntity> getEntitiesByName(String name) {

        Query query = entityManager.createQuery(
                "from AccessibilityStandardEntity where (NOT deleted = true) AND (name = :name) ",
                AccessibilityStandardEntity.class);
        query.setParameter("name", name);
        List<AccessibilityStandardEntity> result = query.getResultList();

        return result;
    }

}
