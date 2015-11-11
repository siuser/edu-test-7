package ro.fortech.edu.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import ro.fortech.edu.model.EvaluationRule;
import ro.fortech.edu.rest.EvaluationRuleRestClient;
import ro.fortech.edu.rest.EvaluationRuleRestService;
import ro.fortech.edu.service.EvaluationRuleService;

@RequestScoped
@ManagedBean(name = "evaluationRuleController")
public class EvaluationRuleController {

	@EJB
	private EvaluationRuleService evaluationRuleService;
	
	@EJB
	private EvaluationRuleRestService evaluationRuleRestService;
	
	@EJB
	private EvaluationRuleRestClient evaluationRuleRestClient;

	
	public EvaluationRule getNewEvaluationRule() {
		return newEvaluationRule;
	}

	public void setNewEvaluationRule(EvaluationRule newEvaluationRule) {
		this.newEvaluationRule = newEvaluationRule;
	}

	private EvaluationRule newEvaluationRule;
	
	
	

	@PostConstruct
	public void initNewEvaluationRule() {
		newEvaluationRule = new EvaluationRule();
		getAllEvaluationRuleList();
		
	}

	public List<EvaluationRule> getAllEvaluationRuleList() {
		//System.out.println("Rules nr= "+evaluationRuleService.findAllEvaluationRules().size());
		return evaluationRuleService.findAllEvaluationRules();
	}

	

	private long idMarketRule;

	public long getIdMarketRule() {
		return idMarketRule;
	}

	public void setIdMarketRule(long idMarketRule) {
		this.idMarketRule = idMarketRule;
	}

	public void register() throws Exception {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			evaluationRuleService.register(newEvaluationRule);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful");
			facesContext.addMessage(null, m);
			initNewEvaluationRule();
		} catch (Exception e) {
			String errorMessage = getRootErrorMessage(e);
			FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage, "Registration unsuccessful");
			facesContext.addMessage(null, m);
		}
	}
	
	public void getEvaluationRuleByIdRest(){
		System.out.println("Enter getEvaluationRuleByIdRest");
		long evaluationRuleId = 1;
		evaluationRuleRestClient.invokeGetEvaluationRuleById(evaluationRuleId);
	}
	
	public void getAllEvaluationRuleRest(){
		System.out.println("Enter getAllEvaluationRuleRest");
		evaluationRuleRestClient.invokeGetAllEvaluationRule();
	}
	
	public void addEvaluationRuleRest(){
		System.out.println("Enter addEvaluationRuleRest");
		//Data should come from other sources
		EvaluationRule evaluationRule = new EvaluationRule();	
		evaluationRule.setMarketRuleId(100);
		evaluationRuleRestClient.addEvaluationRuleRest(evaluationRule);;
	}

	

	private String getRootErrorMessage(Exception e) {
		// Default to general error message that registration failed.
		String errorMessage = "Registration failed. See server log for more information";
		if (e == null) {
			// This shouldn't happen, but return the default messages
			return errorMessage;
		}

		// Start with the exception and recurse to find the root cause
		Throwable t = e;
		while (t != null) {
			// Get the message from the Throwable class instance
			errorMessage = t.getLocalizedMessage();
			t = t.getCause();
		}
		// This is the root cause message
		return errorMessage;
	}

}
