package ro.fortech.edu.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the EVALUATION_RESULT database table.
 * 
 */
@Entity
@Table(name="EVALUATION_RESULT")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="EvaluationResult.findAll", query="SELECT e FROM EvaluationResult e")
public class EvaluationResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_EVALUATION_RESULT", unique=true, nullable=false)
	private long idEvaluationResult;

	@Column(name="DATE_OF_EVALUATION")
	private Timestamp dateOfEvaluation;

	@Column(name="EVALUATION_RULES_APPLIED", length=255)
	private String evaluationRulesApplied;
	
	@Column(name="EVALUATION_RULE_IDS_NOT_IN_DATABASE", length=255)
	private String evaluationRuleIdsNotInDatabase;

	public String getEvaluationRuleIdsNotInDatabase() {
		return evaluationRuleIdsNotInDatabase;
	}

	public void setEvaluationRuleIdsNotInDatabase(String evaluationRuleIdsNotInDatabase) {
		this.evaluationRuleIdsNotInDatabase = evaluationRuleIdsNotInDatabase;
	}

	//bi-directional many-to-one association to Vehicle
	@ManyToOne
	@JoinColumn(name="ID_VEHICLE_IND")
	//@XmlInverseReference(mappedBy="evaluationResults")
	@XmlTransient
	private Vehicle vehicle;
	
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		   this.vehicle = (Vehicle) parent;
		}


	//bi-directional many-to-one association to EvaluationResultRuleDetail
	@OneToMany(mappedBy="evaluationResult", cascade=CascadeType.ALL)
	private List<EvaluationResultRuleDetail> evaluationResultRuleDetails = new ArrayList<>();

	public EvaluationResult() {
	}

	public long getIdEvaluationResult() {
		return this.idEvaluationResult;
	}

	public void setIdEvaluationResult(long idEvaluationResult) {
		this.idEvaluationResult = idEvaluationResult;
	}

	public Timestamp getDateOfEvaluation() {
		return this.dateOfEvaluation;
	}

	public void setDateOfEvaluation(Timestamp dateOfEvaluation) {
		this.dateOfEvaluation = dateOfEvaluation;
	}

	public String getEvaluationRulesApplied() {
		return this.evaluationRulesApplied;
	}

	public void setEvaluationRulesApplied(String evaluationRulesApplied) {
		this.evaluationRulesApplied = evaluationRulesApplied;
	}

	public Vehicle getVehicle() {
		return this.vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public List<EvaluationResultRuleDetail> getEvaluationResultRuleDetails() {
		return this.evaluationResultRuleDetails;
	}

	public void setEvaluationResultRuleDetails(List<EvaluationResultRuleDetail> evaluationResultRuleDetails) {
		this.evaluationResultRuleDetails = evaluationResultRuleDetails;
	}

	public EvaluationResultRuleDetail addEvaluationResultRuleDetail(EvaluationResultRuleDetail evaluationResultRuleDetail) {
		getEvaluationResultRuleDetails().add(evaluationResultRuleDetail);
		evaluationResultRuleDetail.setEvaluationResult(this);

		return evaluationResultRuleDetail;
	}

	public EvaluationResultRuleDetail removeEvaluationResultRuleDetail(EvaluationResultRuleDetail evaluationResultRuleDetail) {
		getEvaluationResultRuleDetails().remove(evaluationResultRuleDetail);
		evaluationResultRuleDetail.setEvaluationResult(null);

		return evaluationResultRuleDetail;
	}

}