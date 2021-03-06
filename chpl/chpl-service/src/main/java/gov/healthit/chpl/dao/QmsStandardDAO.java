package gov.healthit.chpl.dao;

import java.util.List;

import gov.healthit.chpl.dto.QmsStandardDTO;
import gov.healthit.chpl.exception.EntityCreationException;
import gov.healthit.chpl.exception.EntityRetrievalException;

public interface QmsStandardDAO {

    QmsStandardDTO update(QmsStandardDTO dto) throws EntityRetrievalException;

    void delete(Long id) throws EntityRetrievalException;

    List<QmsStandardDTO> findAll();

    QmsStandardDTO getById(Long id);

    QmsStandardDTO getByName(String name);

    QmsStandardDTO findOrCreate(Long id, String name) throws EntityCreationException;
}
