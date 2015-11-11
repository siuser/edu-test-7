package ro.fortech.edu.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ro.fortech.edu.model.EvaluationResultRuleDetail;

@Stateless
public class EvaluationResultRuleDetailService {

	@PersistenceContext
	private EntityManager entityManager;

	public void register(EvaluationResultRuleDetail evaluationResultRuleDetail) throws Exception {
		entityManager.persist(evaluationResultRuleDetail);
	}

	public void update(EvaluationResultRuleDetail evaluationResultRuleDetail) throws Exception {
		entityManager.merge(evaluationResultRuleDetail);
	}

	public EvaluationResultRuleDetail findEvaluationResultRuleDetailById(long id) {

		return entityManager.find(EvaluationResultRuleDetail.class, id);
	}

}
