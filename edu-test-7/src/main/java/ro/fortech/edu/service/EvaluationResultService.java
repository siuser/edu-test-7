package ro.fortech.edu.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ro.fortech.edu.model.EvaluationResult;

@Stateless
public class EvaluationResultService {

	@PersistenceContext
    private EntityManager entityManager;

	public void register(EvaluationResult evaluationResult) throws Exception {		
		entityManager.persist(evaluationResult);		
	}
	
	public void update(EvaluationResult evaluationResult) throws Exception {    	
    	entityManager.merge(evaluationResult);        
    }
	
	public EvaluationResult findEvaluationResultById(long id) {

		return entityManager.find(EvaluationResult.class, id);
	}

}
