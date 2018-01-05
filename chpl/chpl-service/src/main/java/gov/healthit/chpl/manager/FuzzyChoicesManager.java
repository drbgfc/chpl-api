package gov.healthit.chpl.manager;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dao.FuzzyChoicesDAO;
import gov.healthit.chpl.dto.FuzzyChoicesDTO;
import gov.healthit.chpl.entity.FuzzyType;

public interface FuzzyChoicesManager {
	public String getTopFuzzyChoice(String query, FuzzyType type, Long id);
	public List<String> getFuzzyChoicesByType(FuzzyType type) throws JsonParseException, JsonMappingException, EntityRetrievalException, IOException;
	public FuzzyChoicesDTO getByType(FuzzyType type) throws EntityRetrievalException, JsonParseException, JsonMappingException, IOException;
	public void setFuzzyChoicesDAO(final FuzzyChoicesDAO FuzzyChoicesDAO);

}
