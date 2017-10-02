package gov.healthit.chpl.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;


/** 
 * Object mapping for hibernate-handled table: certification_criterion.
 * 
 *
 * @author autogenerated / cwatson
 */

@Entity
@Table(name = "certification_criterion")
public class CertificationCriterionEntity implements Serializable {

	/** Serial Version UID. */
	private static final long serialVersionUID = 5366674516357955978L;
	
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic( optional = false )
    @Column (name = "certification_criterion_id", nullable = false)
    @JoinColumn (name = "certification_criterion_id", nullable = false)
	private Long id;

	@Basic( optional = true )
	@Column( name = "automated_measure_capable"  )
	private Boolean automatedMeasureCapable;
	
	@Basic( optional = true )
	@Column( name = "automated_numerator_capable"  )
	private Boolean automatedNumeratorCapable;
	
	@Basic( optional = false )
	@Column( name = "certification_edition_id", nullable = false  )
	private Long certificationEditionId;
	
	@Basic( optional = true )
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "certification_edition_id", unique=true, nullable = true, insertable=false, updatable= false)
	private CertificationEditionEntity certificationEdition;

	@Basic( optional = false )
	@Column( name = "creation_date", nullable = false  )
	private Date creationDate;
	
	@Basic( optional = false )
	@Column( name = "deleted", nullable = false  )
	private Boolean deleted;
	
	
	@Basic( optional = true )
	@Column( name = "description", length = 1000 )
	private String description;

	
	@Basic( optional = false )
	@Column( name = "last_modified_date", nullable = false  )
	private Date lastModifiedDate;
	
	@Basic( optional = false )
	@Column( name = "last_modified_user", nullable = false  )
	private Long lastModifiedUser;
	
	@Basic( optional = true )
	@Column( length = 15  )
	private String number;
	
	@Basic( optional = true )
	@Column( name = "requires_sed"  )
	private Boolean requiresSed;
	
	@Basic( optional = true )
	@Column( length = 250  )
	private String title;
	/**
	 * Default constructor, mainly for hibernate use.
	 */
	public CertificationCriterionEntity() {
		// Default constructor
	}

	/** Constructor taking a given ID.
	 * @param id to set
	 */
	public CertificationCriterionEntity(Long id) {
		this.id = id;
	}
	
 
	/** Return the type of this class. Useful for when dealing with proxies.
	* @return Defining class.
	*/
	@Transient
	public Class<?> getClassType() {
		return CertificationCriterionEntity.class;
	}

	 /**
	 * Return the value associated with the column: automatedMeasureCapable.
	 * @return A Boolean object (this.automatedMeasureCapable)
	 */
	public Boolean isAutomatedMeasureCapable() {
		return this.automatedMeasureCapable;
		
	}
	

  
	 /**  
	 * Set the value related to the column: automatedMeasureCapable.
	 * @param automatedMeasureCapable the automatedMeasureCapable value you wish to set
	 */
	public void setAutomatedMeasureCapable(final Boolean automatedMeasureCapable) {
		this.automatedMeasureCapable = automatedMeasureCapable;
	}

	 /**
	 * Return the value associated with the column: automatedNumeratorCapable.
	 * @return A Boolean object (this.automatedNumeratorCapable)
	 */
	public Boolean isAutomatedNumeratorCapable() {
		return this.automatedNumeratorCapable;
		
	}
	
	 /**  
	 * Set the value related to the column: automatedNumeratorCapable.
	 * @param automatedNumeratorCapable the automatedNumeratorCapable value you wish to set
	 */
	public void setAutomatedNumeratorCapable(final Boolean automatedNumeratorCapable) {
		this.automatedNumeratorCapable = automatedNumeratorCapable;
	}
	
	 /**
	 * Return the value associated with the column: certificationEdition.
	 * @return A CertificationEdition object (this.certificationEdition)
	 */
	public Long getCertificationEditionId() {
		return this.certificationEditionId;
		
	}
	

  
	 /**  
	 * Set the value related to the column: certificationEdition.
	 * @param certificationEdition the certificationEdition value you wish to set
	 */
	public void setCertificationEdition(final Long certificationEditionId) {
		this.certificationEditionId = certificationEditionId;
	}

	 /**
	 * Return the value associated with the column: creationDate.
	 * @return A Date object (this.creationDate)
	 */
	public Date getCreationDate() {
		return this.creationDate;
		
	}
  
	 /**  
	 * Set the value related to the column: creationDate.
	 * @param creationDate the creationDate value you wish to set
	 */
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}
	
	 /**
	 * Return the value associated with the column: deleted.
	 * @return A Boolean object (this.deleted)
	 */

	public Boolean isDeleted() {
		return this.deleted;
		
	}
  
	 /**  
	 * Set the value related to the column: deleted.
	 * @param deleted the deleted value you wish to set
	 */
	public void setDeleted(final Boolean deleted) {
		this.deleted = deleted;
	}

	 /**
	 * Return the value associated with the column: description.
	 * @return A String object (this.description)
	 */
	public String getDescription() {
		return this.description;
		
	}
	

  
	 /**  
	 * Set the value related to the column: description.
	 * @param description the description value you wish to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	 /**
	 * Return the value associated with the column: id.
	 * @return A Long object (this.id)
	 */
	public Long getId() {
		return this.id;
		
	}
	

  
	 /**  
	 * Set the value related to the column: id.
	 * @param id the id value you wish to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	 /**
	 * Return the value associated with the column: lastModifiedDate.
	 * @return A Date object (this.lastModifiedDate)
	 */

	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
		
	}
	

  
	 /**  
	 * Set the value related to the column: lastModifiedDate.
	 * @param lastModifiedDate the lastModifiedDate value you wish to set
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	 /**
	 * Return the value associated with the column: lastModifiedUser.
	 * @return A Long object (this.lastModifiedUser)
	 */
	public Long getLastModifiedUser() {
		return this.lastModifiedUser;
		
	}
	

  
	 /**  
	 * Set the value related to the column: lastModifiedUser.
	 * @param lastModifiedUser the lastModifiedUser value you wish to set
	 */
	public void setLastModifiedUser(final Long lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}

	 /**
	 * Return the value associated with the column: number.
	 * @return A String object (this.number)
	 */
	public String getNumber() {
		return this.number;
		
	}
  
	 /**  
	 * Set the value related to the column: number.
	 * @param number the number value you wish to set
	 */
	public void setNumber(final String number) {
		this.number = number;
	}

	 /**
	 * Return the value associated with the column: requiresSed.
	 * @return A Boolean object (this.requiresSed)
	 */
	public Boolean isRequiresSed() {
		return this.requiresSed;
		
	}
	

  
	 /**  
	 * Set the value related to the column: requiresSed.
	 * @param requiresSed the requiresSed value you wish to set
	 */
	public void setRequiresSed(final Boolean requiresSed) {
		this.requiresSed = requiresSed;
	}
	

	 /**
	 * Return the value associated with the column: title.
	 * @return A String object (this.title)
	 */
	public String getTitle() {
		return this.title;
	}
	

  
	 /**  
	 * Set the value related to the column: title.
	 * @param title the title value you wish to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
	

	/** Provides toString implementation.
	 * @see java.lang.Object#toString()
	 * @return String representation of this class.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("automatedMeasureCapable: " + this.isAutomatedMeasureCapable() + ", ");
		sb.append("automatedNumeratorCapable: " + this.isAutomatedNumeratorCapable() + ", ");
		sb.append("creationDate: " + this.getCreationDate() + ", ");
		sb.append("deleted: " + this.isDeleted() + ", ");
		sb.append("description: " + this.getDescription() + ", ");
		sb.append("id: " + this.getId() + ", ");
		sb.append("lastModifiedDate: " + this.getLastModifiedDate() + ", ");
		sb.append("lastModifiedUser: " + this.getLastModifiedUser() + ", ");
		sb.append("number: " + this.getNumber() + ", ");
		sb.append("requiresSed: " + this.isRequiresSed() + ", ");
		sb.append("title: " + this.getTitle());
		return sb.toString();		
	}

	public CertificationEditionEntity getCertificationEdition() {
		return certificationEdition;
	}

	public void setCertificationEdition(CertificationEditionEntity certificationEdition) {
		this.certificationEdition = certificationEdition;
	}
}
