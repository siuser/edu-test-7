package ro.fortech.edu.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ro.fortech.edu.model.EvaluationResult;
import ro.fortech.edu.model.EvaluationResultRuleDetail;
import ro.fortech.edu.model.EvaluationRule;
import ro.fortech.edu.model.MarketRule;
import ro.fortech.edu.model.RuleActivity;
import ro.fortech.edu.model.RuleCondition;
import ro.fortech.edu.model.Vehicle;

/**
 * Service class for Vehicle This bean requires transactions as it needs to
 * write to the database Making this an EJB gives us access to declarative
 * transactions (vs manual transaction control)
 * 
 * @author Silviu
 *
 */
@Stateless
public class VehicleService {

	@PersistenceContext
	private EntityManager entityManager;

	@EJB
	private StockCategoryService stockCategoryService;

	@EJB
	private EvaluationRuleService evaluationRuleService;

	@EJB
	private MarketRuleService marketRuleService;

	@EJB
	private EvaluationResultService evaluationResultService;

	@EJB
	private EvaluationResultRuleDetailService evaluationResultRuleDetailService;

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Service method to persist a new vehicle into database
	 * 
	 * @param vehicle
	 * @throws Exception
	 */
	public void register(Vehicle vehicle) throws Exception {
		// System.out.println("Registering Vehicle id= " +
		// vehicle.getIdVehicle());
		entityManager.persist(vehicle);
	}

	/**
	 * Service method to update an existent vehicle
	 * 
	 * @param vehicle
	 * @throws Exception
	 */
	public void update(Vehicle vehicle) throws Exception {
		// System.out.println("Updating Vehicle id= " + vehicle.getIdVehicle());
		entityManager.merge(vehicle);
	}

	/**
	 * 
	 * @return a list of all vehicles from database
	 */
	public List<Vehicle> findAllVehicles() {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Vehicle> criteria = criteriaBuilder.createQuery(Vehicle.class);
		Root<Vehicle> vehicle = criteria.from(Vehicle.class);
		criteria.select(vehicle);
		return entityManager.createQuery(criteria).getResultList();
	}

	/**
	 * 
	 * @param id
	 * @return a vehicle found by unique id
	 */
	public Vehicle findById(long id) {
		return entityManager.find(Vehicle.class, id);
	}

	public List<String> getAllStockCategoryAsStringList() {

		return stockCategoryService.findAllStockCategoryAsString();

	}

	/**
	 * 
	 * @param vehicle
	 * @param ruleIds
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */

	public EvaluationResult evaluate(Vehicle vehicle, List<String> ruleIds) {

		// A new Evaluation Result item every time an evaluate method call
		// (every evaluation)
		EvaluationResult evaluationResult = new EvaluationResult();
		evaluationResult.setVehicle(vehicle);

		// Append all rules in allRuleIds
		StringBuilder allRuleIds = new StringBuilder();
		// Append rules not found in database in ruleIdsNotInDb
		StringBuilder ruleIdsNotInDb = new StringBuilder();
		for (String ruleIdString : ruleIds) {
			allRuleIds.append(ruleIdString + ",");
			Long ruleIdLong = new Long(ruleIdString);
			if (evaluationRuleService.findEvaluationRuleById(ruleIdLong) == null) {
				// No EvaluationRule in database correspond to this ruleIdLong
				System.out.println("No such Rule in db for id=" + ruleIdLong);
				ruleIdsNotInDb.append(ruleIdString + ",");
			} else {
				// There is an EvaluationRule in db
				System.out.println("There is Rule in db for id=" + ruleIdLong);
				EvaluationRule evaluationRule = evaluationRuleService.findEvaluationRuleById(ruleIdLong);

				// A new EvaluationResultRuleDetail for every rule applied
				EvaluationResultRuleDetail evaluationResultRuleDetail = new EvaluationResultRuleDetail();

				evaluationResultRuleDetail.setEvaluationResult(evaluationResult);
				//evaluationResultRuleDetail.setEvaluationRule(evaluationRule);
				

				// The message for EvaluationResultRuleDetail
				StringBuilder evaluationMessage = new StringBuilder();
				evaluationMessage.append("EvaluationRule rule id= " + evaluationRule.getIdEvaluationRule());
				evaluationMessage.append(LINE_SEPARATOR);

				// The map of vehicle fields (field name as map key,field value
				// as map value)
				Map<String, String> vehicleFieldsMap = null;
				try {
					vehicleFieldsMap = this.getVehicleFiels(vehicle);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Get the marketRule corresponding to this rule
				long marketRuleId = evaluationRule.getMarketRuleId();
				MarketRule marketRule = marketRuleService.findMarketRuleById(marketRuleId);
				if (marketRule != null) {
					// Database contains a MarketRule for this EvaluationRule
					// Let's check if marketRule matches with vehicle
					// Assume that vehicle has indeed fields: countryNumber,
					// branch and stockCategory
					if (vehicle.getCountryNumber().equals(marketRule.getCountryNumber())
							&& (vehicle.getBranch() == marketRule.getBranch())
							&& vehicle.getStockCategory().toLowerCase().equals(vehicle.getStockCategory().toLowerCase())
							&& marketRule.getIsActive()) {

						// This marketRule matches vehicle
						// So we can apply evaluationRule
						evaluationMessage.append("*****Market rule id= " + marketRule.getIdMarketRule()
								+ " matches vehicle and is active");
						evaluationMessage.append(LINE_SEPARATOR);
						// Find conditions list
						List<RuleCondition> conditionsList = evaluationRule.getRuleConditions();
						// Need to check if all conditions are met
						boolean isConditionListApplicable = true;
						for (RuleCondition ruleCondition : conditionsList) {
							Long conditionId = ruleCondition.getIdRuleCondition();
							String ruleConditionKey = ruleCondition.getVehicleAttributeName();
							// System.out.println("ruleConditionKey= " +
							// ruleConditionKey);
							if (vehicleFieldsMap.containsKey(ruleConditionKey)) {
								// Condition vehicle attribute has a
								// correspondent in vehicle fields
								evaluationMessage
										.append("*****Condition id=" + conditionId + " condition's vehicle attribute "
												+ "(" + ruleConditionKey + ")" + " is in vehicle fields");
								evaluationMessage.append(LINE_SEPARATOR);

								// System.out.println("ruleCondition.getVehicleAttributeValue().trimm().length="+ruleCondition.getVehicleAttributeValue().trim().length());

								if ((ruleCondition.getVehicleAttributeValue() == null)
										|| (ruleCondition.getVehicleAttributeValue().trim().length() == 0)) {
									//
									isConditionListApplicable = false;
									evaluationMessage.append(
											"*****Condition id=" + conditionId + " condition's vehicle value for " + "("
													+ ruleConditionKey + ")" + " is NOT set or null");
									evaluationMessage.append(LINE_SEPARATOR);
									evaluationMessage.append("*****Condition id=" + ruleCondition.getIdRuleCondition()
											+ "verified result = NOK");
									evaluationMessage.append(LINE_SEPARATOR);
									// Let's comment below break to see all
									// conditions in action
									// break;
								} else {
									// Vehicle attribute value not null
									// We can check if vehicle attribute value
									// equals
									// condition attribute value

									if (vehicleFieldsMap.get(ruleConditionKey)
											.equals(ruleCondition.getVehicleAttributeValue())) {
										// Rule condition attribute value equals
										// vehicle attribute value
										// So condition is met
										evaluationMessage.append("*****Condition id="
												+ ruleCondition.getIdRuleCondition() + " verified result = OK");
										evaluationMessage.append(LINE_SEPARATOR);
										// this.setVehicleFieldValue(vehicle,
										// evaluationRule.getVehicleAttribute(),
										// evaluationRule.getVehicleAttributeTarget());
										System.out.println("evaluationMessage= " + evaluationMessage);

									} else {
										// Rule condition attribute value not
										// equal vehicle attribute value
										// Condition not met
										// System.out.println("Rule condition
										// attribute value not equal vehicle
										// attribute value");
										isConditionListApplicable = false;
										evaluationMessage
												.append("*****Condition id=" + ruleCondition.getIdRuleCondition()
														+ " rule condition attribute value ("
														+ ruleCondition.getVehicleAttributeValue()
														+ ") not equal vehicle attribute value ("
														+ vehicleFieldsMap.get(ruleConditionKey) + ")");
										evaluationMessage.append(LINE_SEPARATOR);
										evaluationMessage.append("*****Condition id="
												+ ruleCondition.getIdRuleCondition() + " verified result = NOK");
										evaluationMessage.append(LINE_SEPARATOR);
										// Let's comment below break to see all
										// conditions in action
										// break;
									}

								} // end else attribute value not null

							} else {
								// Condition vehicle attribute has NO
								// correspondent in
								// vehicle fields								
								evaluationMessage
										.append("*****Condition id=" + conditionId + " condition's vehicle attribute "
												+ "(" + ruleConditionKey + ")" + " NOT in vehicle fields");
								evaluationMessage.append(LINE_SEPARATOR);
								evaluationMessage.append("*****Condition id="
										+ ruleCondition.getIdRuleCondition() + " verified result = NOK");
								evaluationMessage.append(LINE_SEPARATOR);
								isConditionListApplicable = false;
								// Let's comment below break to see all
								// conditions in action
								// break;
							}
						} // end for conditionsList

						if (isConditionListApplicable && (!conditionsList.isEmpty())) {
							// All conditions passed and conditionsList not
							// empty
							evaluationMessage.append("*****All conditions applied result = OK");
							evaluationMessage.append(LINE_SEPARATOR);
							System.out.println("evaluationMessage= " + evaluationMessage);

							// So we can apply activities
							List<RuleActivity> activitiesList = evaluationRule.getRuleActivities();
							String ruleActivityKey = null;
							for (RuleActivity ruleActivity : activitiesList) {
								ruleActivityKey = ruleActivity.getVehicleAttributeName();
								if (vehicleFieldsMap.containsKey(ruleActivityKey)) {
									// Activity attribute has a correspondent in
									// vehicle fields
									try {
										this.setVehicleFieldValue(vehicle, ruleActivityKey,
												ruleActivity.getVehicleAttributeValue());
										evaluationMessage.append("*****Activity id= " + ruleActivity.getIdRuleActivity()
										+ " applied result = OK");
								evaluationMessage.append(LINE_SEPARATOR);
								evaluationResultRuleDetail.setRuleStatus("OK");
								System.out.println("evaluationMessage= " + evaluationMessage);

									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NoSuchFieldException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								} else {
									// Activity attribute has NO correspondent
									// in vehicle  fields
									evaluationMessage.append(
											"*****Activity id=" + ruleActivity.getIdRuleActivity() + " activity vehicle attribute ("+ruleActivityKey+") does not have a correspondent in vehicle fields");
									evaluationMessage.append(LINE_SEPARATOR);
									evaluationMessage.append(
											"*****Activity id=" + ruleActivity.getIdRuleActivity() + " NOT applied");
									evaluationMessage.append(LINE_SEPARATOR);
									System.out.println("evaluationMessage= " + evaluationMessage);

								}
							} // end for ruleActivities
						} else {
							// Not all conditions passed or conditionsList empty
							if (conditionsList.isEmpty()) {
								// conditionsList empty
								evaluationMessage.append("*****NO conditions, this  evaluation rule will NOT be applied");
								evaluationMessage.append(LINE_SEPARATOR);
								evaluationResultRuleDetail.setRuleStatus("NOK");
							} else {
								// conditionsList not empty, not all conditions
								// passed
								evaluationMessage
										.append("*****NOT ALL conditions passed, this evaluation rule will NOT be applied");
								evaluationMessage.append(LINE_SEPARATOR);
								evaluationResultRuleDetail.setRuleStatus("NOK");
								System.out.println("evaluationMessage= " + evaluationMessage);

							}

						} // end else NOT all conditions passed

					} else {
						// Market rule does not matches vehicle OR market Rule
						// inactive
						evaluationMessage.append("*****Market rule id= " + marketRule.getIdMarketRule()
								+ " does NOT matches vehicle or is NOT active");
						evaluationMessage.append(LINE_SEPARATOR);
						evaluationMessage.append("*****Evaluation rule id= " + evaluationRule.getIdEvaluationRule()
								+ " will NOT be applied");
						evaluationMessage.append(LINE_SEPARATOR);
						evaluationResultRuleDetail.setRuleStatus("NOK");
						System.out.println("evaluationMessage= " + evaluationMessage);

					} // end else Market rule does not matches vehicle OR market
						// Rule

				} else {
					// Database does NOT contain a MarketRule for this
					// EvaluationRule
					evaluationMessage.append("***** NO market rule  into the database for this Evaluation rule");
					evaluationMessage.append(LINE_SEPARATOR);
					evaluationMessage.append("*****Evaluation rule id= " + evaluationRule.getIdEvaluationRule()
							+ " will NOT be applied");
					evaluationMessage.append(LINE_SEPARATOR);
					evaluationResultRuleDetail.setRuleStatus("NOK");
				}

				evaluationResultRuleDetail.setMessage(evaluationMessage.toString());
				System.out.println("evaluationResultRuleDetail = " + evaluationResultRuleDetail);
				// Below null
				System.out.println("evaluationResult.getEvaluationResultRuleDetails() = "
						+ evaluationResult.getEvaluationResultRuleDetails());

				evaluationResult.getEvaluationResultRuleDetails().add(evaluationResultRuleDetail);
				System.out.println("evaluationMessage= " + evaluationMessage);

			} // end else There is an EvaluationRule in db
		} // end for on rules

		if (allRuleIds.length() > 0) {
			allRuleIds.setLength(allRuleIds.length() - 1);
		}
		if (ruleIdsNotInDb.length() > 0) {
			ruleIdsNotInDb.setLength(ruleIdsNotInDb.length() - 1);
		}
		evaluationResult.setEvaluationRulesApplied(allRuleIds.toString());
		evaluationResult.setEvaluationRuleIdsNotInDatabase(ruleIdsNotInDb.toString());
		System.out.println("allRuleIds = " + allRuleIds + " length = " + allRuleIds.length());
		System.out.println("ruleIdsNotInDb = " + ruleIdsNotInDb + " length = " + ruleIdsNotInDb.length());

		try {
			this.update(vehicle);
			evaluationResultService.register(evaluationResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return evaluationResult;
	}

	/*
	 * public EvaluationResult evaluate(Vehicle vehicle, List<String> ruleIds)
	 * throws IllegalArgumentException, IllegalAccessException,
	 * NoSuchFieldException, SecurityException { // A new Evaluation Result item
	 * every time an evaluate method call // (every evaluation) EvaluationResult
	 * evaluationResult = new EvaluationResult();
	 * evaluationResult.setVehicle(vehicle);
	 * 
	 * // Apply every rule to vehicle for (String ruleId : ruleIds) { // Find
	 * EvaluationRule Long ruleIdAsLong = new Long(ruleId); EvaluationRule
	 * evaluationRule = evaluationRuleRepository.findById(ruleIdAsLong);
	 * 
	 * if (evaluationRule.getIsMappingRule()) { // Apply a mapping rule
	 * applyMappingRule(vehicle, evaluationRule, evaluationResult); } else { //
	 * Apply an interpretation rule applyInterpretationRule(vehicle,
	 * evaluationRule, evaluationResult); }
	 * 
	 * }
	 * 
	 * try { this.evaluationResult = evaluationResult;
	 * this.register(evaluationResult); this.update(vehicle); //
	 * this.register(evaluationResultRuleDetail); } catch (Exception e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } return null; }
	 * 
	 */

	/**
	 * 
	 * @param vehicle
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public Map<String, String> getVehicleFiels(Vehicle vehicle)
			throws IllegalArgumentException, IllegalAccessException {
		System.out.println("Enter method = getVehicleFiels(Vehicle vehicle)");
		// Use java.reflect API to find vehicle's fields
		Class<?> vehicleClass = vehicle.getClass();
		Field[] vehicleFields = vehicleClass.getDeclaredFields();

		// Put vehicle field in a map
		// fieldName, which is unique, will be the key
		// fieldStringValue will be the value
		String fieldName = null;
		String fieldStringValue = null;
		Map<String, String> vehicleFieldsMap = new HashMap<>();
		for (Field field : vehicleFields) {
			// There are some system fields (like _persistence_primaryKey)
			// which start with underscore; no interested
			if (!field.getName().startsWith("_")) {
				field.setAccessible(true);
				Object objValue = field.get(vehicle);

				// fieldType = field.getType().toString();
				fieldName = field.getName();
				if (!(objValue == null)) {
					// objValue not null, so can apply toString
					fieldStringValue = objValue.toString();
				} else {
					// objValue is null, set fieldValue to null
					fieldStringValue = null;
				}
				// System.out.println("Field name= " + fieldName + " string
				// value= " + fieldStringValue);
				vehicleFieldsMap.put(fieldName, fieldStringValue);
			}

		}
		System.out.println("Field map=" + vehicleFieldsMap);
		return vehicleFieldsMap;

	}

	/**
	 * 
	 * @param vehicle
	 * @param fieldName
	 * @param fieldValue
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public void setVehicleFieldValue(Vehicle vehicle, String fieldName, String fieldValue)
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		System.out.println("Enter method = setVehicleFieldValue(Vehicle vehicle)");
		// Use java.reflect API to set vehicle's fields
		Class<?> vehicleClass = vehicle.getClass();
		Field field = vehicleClass.getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(vehicle, fieldValue);
	}

}
