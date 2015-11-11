package ro.fortech.edu.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * The persistent class for the EVALUATION_RESULT_RULE_DETAIL database table.
 * 
 */
@Entity
@Table(name="EVALUATION_RESULT_RULE_DETAIL")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="EvaluationResultRuleDetail.findAll", query="SELECT e FROM EvaluationResultRuleDetail e")
public class EvaluationResultRuleDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_EVALUATION_RESULT_RULE_DETAIL", unique=true, nullable=false)
	private long idEvaluationResultRuleDetail;

	@Lob
	private String message;

	@Column(name="RULE_STATUS", length=10)
	private String ruleStatus;

	//bi-directional many-to-one association to EvaluationResult
	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="ID_EVALUATION_RESULT_IND")
	//@XmlInverseReference(mappedBy="evaluationResultRuleDetails") 	
	@XmlTransient
	private EvaluationResult evaluationResult;
	
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
		   this.evaluationResult = (EvaluationResult) parent;
		   //this.evaluationRule = (EvaluationRule) parent;
		}
	/*
	//bi-directional many-to-one association to EvaluationRule
	@ManyToOne
	@JoinColumn(name="ID_EVALUATION_RULE_IND")
	//@XmlInverseReference(mappedBy="evaluationResultRuleDetails")
	@XmlTransient
	private EvaluationRule evaluationRule;
	*/
	
	public EvaluationResultRuleDetail() {
	}

	public long getIdEvaluationResultRuleDetail() {
		return this.idEvaluationResultRuleDetail;
	}

	public void setIdEvaluationResultRuleDetail(long idEvaluationResultRuleDetail) {
		this.idEvaluationResultRuleDetail = idEvaluationResultRuleDetail;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRuleStatus() {
		return this.ruleStatus;
	}

	public void setRuleStatus(String ruleStatus) {
		this.ruleStatus = ruleStatus;
	}

	public EvaluationResult getEvaluationResult() {
		return this.evaluationResult;
	}

	public void setEvaluationResult(EvaluationResult evaluationResult) {
		this.evaluationResult = evaluationResult;
	}
	/*
	public EvaluationRule getEvaluationRule() {
		return this.evaluationRule;
	}

	public void setEvaluationRule(EvaluationRule evaluationRule) {
		this.evaluationRule = evaluationRule;
	}
	*/
	

}