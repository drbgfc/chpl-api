package gov.healthit.chpl.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import gov.healthit.chpl.auth.permission.GrantedPermission;
import gov.healthit.chpl.auth.user.JWTAuthenticatedUser;
import gov.healthit.chpl.dao.AddressDAO;
import gov.healthit.chpl.dao.DeveloperDAO;
import gov.healthit.chpl.dao.EntityRetrievalException;
import gov.healthit.chpl.dto.AddressDTO;
import gov.healthit.chpl.dto.DecertifiedDeveloperDTO;
import gov.healthit.chpl.dto.DeveloperACBMapDTO;
import gov.healthit.chpl.dto.DeveloperDTO;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { gov.healthit.chpl.CHPLTestConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
@DatabaseSetup("classpath:data/testData.xml")
public class DeveloperDaoTest extends TestCase {

	@Autowired 
	private DeveloperDAO developerDao;
	
	@Autowired 
	private AddressDAO addressDao;
	
	private static JWTAuthenticatedUser authUser;

	@BeforeClass
	public static void setUpClass() throws Exception {
		authUser = new JWTAuthenticatedUser();
		authUser.setFirstName("Admin");
		authUser.setId(-2L);
		authUser.getPermissions().add(new GrantedPermission("ROLE_ADMIN"));
	}

	@Test
	@Transactional
	public void getAllDevelopers() {
		List<DeveloperDTO> results = developerDao.findAll();
		assertNotNull(results);
		assertEquals(9, results.size());
		DeveloperDTO first = results.get(0);
		assertNotNull(first.getStatus());
		assertNotNull(first.getStatus().getId());
		assertNotNull(first.getStatus().getStatusName());
	}

	@Test
	@Transactional
	public void getDeveloperWithAddress() {
		Long developerId = -1L;
		DeveloperDTO developer = null;
		try {
			developer = developerDao.getById(developerId);
		} catch(EntityRetrievalException ex) {
			fail("Could not find developer with id " + developerId);
		}
		assertNotNull(developer);
		assertEquals(-1, developer.getId().longValue());
		assertNotNull(developer.getAddress());
		assertEquals(-1, developer.getAddress().getId().longValue());
		assertNotNull(developer.getStatus());
	}
	
	@Test
	@Transactional
	public void getDeveloperWithoutAddress() {
		Long developerId = -3L;
		DeveloperDTO developer = null;
		try {
			developer = developerDao.getById(developerId);
		} catch(EntityRetrievalException ex) {
			fail("Could not find developer with id " + developerId);
		}
		assertNotNull(developer);
		assertEquals(-3, developer.getId().longValue());
		assertNull(developer.getAddress());
		assertNotNull(developer.getStatus());
	}
	
	@Test
	@Transactional
	@Rollback
	public void createDeveloperWithoutAddress() throws EntityRetrievalException {
		DeveloperDTO developer = new DeveloperDTO();
		developer.setCreationDate(new Date());
		developer.setDeleted(false);
		developer.setLastModifiedDate(new Date());
		developer.setLastModifiedUser(-2L);
		developer.setName("Unit Test Developer!");
		developer.setWebsite("http://www.google.com");
		
		DeveloperDTO result = null;
		try {
			result = developerDao.create(developer);
		} catch(Exception ex) {
			fail("could not create developer!");
			System.err.println(ex.getStackTrace());
		}
		
		assertNotNull(result);
		assertNotNull(result.getId());
		assertTrue(result.getId() > 0L);
		assertNull(result.getAddress());
		assertNotNull(developerDao.getById(result.getId()));
		assertNotNull(result.getStatus());
		assertEquals(1, result.getStatus().getId().longValue());
	}
	
	@Test
	@Transactional
	@Rollback
	public void createDeveloperWithNewAddress() {
		DeveloperDTO developer = new DeveloperDTO();
		developer.setCreationDate(new Date());
		developer.setDeleted(false);
		developer.setLastModifiedDate(new Date());
		developer.setLastModifiedUser(-2L);
		developer.setName("Unit Test Developer!");
		developer.setWebsite("http://www.google.com");
		
		AddressDTO newAddress = new AddressDTO();
		newAddress.setStreetLineOne("11 Holmehurst Ave");
		newAddress.setCity("Catonsville");
		newAddress.setState("MD");
		newAddress.setZipcode("21228");
		newAddress.setCountry("USA");
		newAddress.setLastModifiedUser(-2L);
		newAddress.setCreationDate(new Date());
		newAddress.setLastModifiedDate(new Date());
		newAddress.setDeleted(false);
		developer.setAddress(newAddress);
		
		DeveloperDTO result = null;
		try {
			result = developerDao.create(developer);
		} catch(Exception ex) {
			fail("could not create developer!");
			System.err.println(ex.getStackTrace());
		}
		
		assertNotNull(result);
		assertNotNull(result.getId());
		assertTrue(result.getId() > 0L);
		assertNotNull(result.getAddress());
		assertNotNull(result.getAddress().getId());
		assertTrue(result.getAddress().getId() > 0L);
	}
	
	@Test
	@Transactional
	@Rollback
	public void createDeveloperWithExistingAddress() {
		DeveloperDTO developer = new DeveloperDTO();
		developer.setCreationDate(new Date());
		developer.setDeleted(false);
		developer.setLastModifiedDate(new Date());
		developer.setLastModifiedUser(-2L);
		developer.setName("Unit Test Developer!");
		developer.setWebsite("http://www.google.com");
		
		try 
		{
			AddressDTO existingAddress = addressDao.getById(-1L);
			existingAddress.setCountry("Russia");
			developer.setAddress(existingAddress);
		} catch(EntityRetrievalException ex) {
			fail("could not find existing address to set on developer");
		}
		
		DeveloperDTO result = null;
		try {
			result = developerDao.create(developer);
		} catch(Exception ex) {
			fail("could not create developer!");
			System.out.println(ex.getStackTrace());
		}
		
		assertNotNull(result);
		assertNotNull(result.getId());
		assertTrue(result.getId() > 0L);
		assertNotNull(result.getAddress());
		assertNotNull(result.getAddress().getId());
		assertTrue(result.getAddress().getId() == -1L);
		assertEquals("Russia", result.getAddress().getCountry());
	}
	
	@Test
	@Transactional
	@Rollback
	public void updateDeveloper() {
		DeveloperDTO developer = developerDao.findAll().get(0);
		developer.setName("UPDATED NAME");
		
		DeveloperDTO result = null;
		try {
			result = developerDao.update(developer);
		} catch(Exception ex) {
			fail("could not update developer!");
			System.out.println(ex.getStackTrace());
		}
		assertNotNull(result);

		try {
			DeveloperDTO updatedDeveloper = developerDao.getById(developer.getId());
			assertEquals("UPDATED NAME", updatedDeveloper.getName());
		} catch(Exception ex) {
			fail("could not find developer!");
			System.out.println(ex.getStackTrace());
		}
	}
	
	@Test
	@Transactional
	@Rollback
	public void createDeveloperAcbMap() {
		SecurityContextHolder.getContext().setAuthentication(authUser);
		DeveloperDTO developer = developerDao.findAll().get(0);
		
		DeveloperACBMapDTO dto = new DeveloperACBMapDTO();
		dto.setAcbId(-8L);
		dto.setDeveloperId(developer.getId());
		dto.setTransparencyAttestation("N/A");
		DeveloperACBMapDTO createdMapping = developerDao.createTransparencyMapping(dto);
		
		assertNotNull(createdMapping);
		
		dto = developerDao.getTransparencyMapping(developer.getId(), -8L);
		assertNotNull(dto);
		assertEquals("N/A", dto.getTransparencyAttestation());
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	/**
	 * Given the CHPL is accepting search requests
	 * When I call the REST API's /decertified/developers
	 * Then DeveloperDAOImpl.getCertifiedDevelopers() returns List<DeveloperDecertifiedDTO>
	 * Then the returned list contains no duplicates (unique developerName for each row)
	 * Then the returned list contains the proper array of ONC_ACBs
	 * Then the returned list contains the sum of all of the developer's CP.numMeaningfulUse
	 */
	@Test
	@Transactional
	public void getDecertifiedDevelopers() {
		List<DecertifiedDeveloperDTO> dtoList = new ArrayList<DecertifiedDeveloperDTO>();
		dtoList = developerDao.getDecertifiedDevelopers();
		assertTrue(dtoList.size() == 2);
		assertTrue(dtoList.get(0).getDeveloperName().equals("VendorBanned") || dtoList.get(0).getDeveloperName().equals("VendorSuspended"));
		assertTrue(dtoList.get(0).getNumMeaningfulUse() == 66 || dtoList.get(0).getNumMeaningfulUse() == 73);
		assertTrue(dtoList.get(0).getDeveloperStatus().equals("Under certification ban by ONC") || dtoList.get(0).getDeveloperStatus().equals("Suspended by ONC"));
		if(dtoList.get(0).getDeveloperName().equals("VendorBanned")){
			assertTrue(dtoList.get(0).getOncacb().contains("ICSA Labs"));
			assertTrue(dtoList.get(0).getOncacb().contains("Surescripts LLC"));
			assertTrue(dtoList.get(0).getOncacb().contains("SLI Global"));
		}
		else{
			assertTrue(dtoList.get(0).getOncacb().contains("Drummond Group Inc."));
			assertTrue(dtoList.get(0).getOncacb().contains("CCHIT"));
		}
		
		assertTrue(dtoList.get(1).getNumMeaningfulUse() == 66 || dtoList.get(1).getNumMeaningfulUse() == 73);
		assertTrue(dtoList.get(1).getDeveloperStatus().equals("Under certification ban by ONC") || dtoList.get(1).getDeveloperStatus().equals("Suspended by ONC"));
		if(dtoList.get(1).getDeveloperName().equals("VendorBanned")){
			assertTrue(dtoList.get(1).getOncacb().contains("ICSA Labs"));
			assertTrue(dtoList.get(1).getOncacb().contains("Surescripts LLC"));
			assertTrue(dtoList.get(1).getOncacb().contains("SLI Global"));
		}
		else{
			assertTrue(dtoList.get(1).getOncacb().contains("Drummond Group Inc."));
			assertTrue(dtoList.get(1).getOncacb().contains("CCHIT"));
		}
	}
	
}
