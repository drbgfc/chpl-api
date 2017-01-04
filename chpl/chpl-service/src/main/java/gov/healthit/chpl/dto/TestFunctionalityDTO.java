package gov.healthit.chpl.dto;

import java.io.Serializable;

import gov.healthit.chpl.entity.TestFunctionalityEntity;

public class TestFunctionalityDTO implements Serializable {
	private static final long serialVersionUID = -4607291382443032361L;
	private Long id;
	private String name;
	private String number;
	
	public TestFunctionalityDTO(){}
	
	public TestFunctionalityDTO(TestFunctionalityEntity entity){		
		this.id = entity.getId();
		this.name = entity.getName();
		this.number = entity.getNumber();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
