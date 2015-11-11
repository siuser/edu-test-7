package ro.fortech.edu.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import ro.fortech.edu.model.EvaluationRule;
import ro.fortech.edu.model.MarketRule;

@Stateless
public class EvaluationRuleService {

	@PersistenceContext
    private EntityManager entityManager;
	
	@EJB
	private MarketRuleService marketRuleService;

   

    public void register(EvaluationRule evaluationRule) throws Exception {    	
    	entityManager.persist(evaluationRule);        
    }
    
    public void registerList(List<EvaluationRule> evaluationRuleList) throws Exception {  
    	for(EvaluationRule evaluationRule:evaluationRuleList){
    		entityManager.persist(evaluationRule); 
    	}    	       
    }
    
    public void update(EvaluationRule evaluationRule) throws Exception {    	
    	entityManager.merge(entityManager.merge(evaluationRule));        
    }
    
    public EvaluationRule findEvaluationRuleById(long id){    	
    	return entityManager.find(EvaluationRule.class, id);
    }
    
    public List<EvaluationRule> findAllEvaluationRules() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EvaluationRule> criteriaQuery = criteriaBuilder.createQuery(EvaluationRule.class);
        Root<EvaluationRule> evaluationRule = criteriaQuery.from(EvaluationRule.class);           
        criteriaQuery.select(evaluationRule);        
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
    
    /**
     * 
     * @param evaluationRule
     * @return
     */
    public MarketRule getMarketRule(EvaluationRule evaluationRule){
    	long marketRuleId = evaluationRule.getMarketRuleId();
    	return marketRuleService.findMarketRuleById(marketRuleId);    	 
    }
}