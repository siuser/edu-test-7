package ro.fortech.edu.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.List;


/**
 * The persistent class for the VEHICLE database table.
 * 
 */
@Entity
@Table(name="VEHICLE")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQuery(name="Vehicle.findAll", query="SELECT v FROM Vehicle v")
public class Vehicle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID_VEHICLE", unique=true, nullable=false)
	private long idVehicle;

	@Column(nullable=false)
	private int branch;

	@Column(name="CHASSIS_SERIAL", unique=true, nullable=false, length=30)
	private String chassisSerial;

	@Column(length=30)
	private String classe;

	@Column(name="COUNTRY_NUMBER", nullable=false, length=10)
	private String countryNumber;

	@Column(name="ENGINE_CODE", length=30)
	private String engineCode;

	@Column(name="ENGINE_FUEL_TYPE", length=30)
	private String engineFuelType;

	@Column(length=30)
	private String model;

	@Column(name="STOCK_CATEGORY", nullable=false, length=10)
	private String stockCategory;

	//bi-directional many-to-one association to EvaluationResult
	@OneToMany(mappedBy="vehicle", cascade=CascadeType.ALL)
	private List<EvaluationResult> evaluationResults;

	public Vehicle() {
	}

	public long getIdVehicle() {
		return this.idVehicle;
	}

	public void setIdVehicle(long idVehicle) {
		this.idVehicle = idVehicle;
	}

	public int getBranch() {
		return this.branch;
	}

	public void setBranch(int branch) {
		this.branch = branch;
	}

	public String getChassisSerial() {
		return this.chassisSerial;
	}

	public void setChassisSerial(String chassisSerial) {
		this.chassisSerial = chassisSerial;
	}

	public String getClasse() {
		return this.classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getCountryNumber() {
		return this.countryNumber;
	}

	public void setCountryNumber(String countryNumber) {
		this.countryNumber = countryNumber;
	}

	public String getEngineCode() {
		return this.engineCode;
	}

	public void setEngineCode(String engineCode) {
		this.engineCode = engineCode;
	}

	public String getEngineFuelType() {
		return this.engineFuelType;
	}

	public void setEngineFuelType(String engineFuelType) {
		this.engineFuelType = engineFuelType;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getStockCategory() {
		return this.stockCategory;
	}

	public void setStockCategory(String stockCategory) {
		this.stockCategory = stockCategory;
	}

	public List<EvaluationResult> getEvaluationResults() {
		return this.evaluationResults;
	}

	public void setEvaluationResults(List<EvaluationResult> evaluationResults) {
		this.evaluationResults = evaluationResults;
	}

	public EvaluationResult addEvaluationResult(EvaluationResult evaluationResult) {
		getEvaluationResults().add(evaluationResult);
		evaluationResult.setVehicle(this);

		return evaluationResult;
	}

	public EvaluationResult removeEvaluationResult(EvaluationResult evaluationResult) {
		getEvaluationResults().remove(evaluationResult);
		evaluationResult.setVehicle(null);

		return evaluationResult;
	}

}