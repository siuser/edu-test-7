package ro.fortech.edu.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ro.fortech.edu.model.EvaluationRule;
import ro.fortech.edu.service.EvaluationRuleService;

/**
 * 
 * This class produces a RESTful service to read/write evaluationRule 
 */
@Path("/evaluationRules")
@Stateless
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class EvaluationRuleRestService {

	@EJB
	private EvaluationRuleService evaluationRuleService;

	@GET 	
	public Response listAllEvaluationRules() {
		//return evaluationRuleService.findAllEvaluationRules();
		
		GenericEntity<List<EvaluationRule>> evaluationRuleWrapper = new GenericEntity<List<EvaluationRule>>(
				evaluationRuleService.findAllEvaluationRules()) {
		};
		return Response.ok(evaluationRuleWrapper).build();
	}
	
	@GET
    @Path("/{id}")    
    public EvaluationRule lookupEvaluationRuleById(@PathParam("id") long id) {
		EvaluationRule evaluationRule = evaluationRuleService.findEvaluationRuleById(id);
        if (evaluationRule == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return evaluationRule;
    }
	
	@POST
	@Path("/testOneRule")	
	public void testPostOneEvaluationRule(EvaluationRule evaluationRule){
		if ( evaluationRule == null){
			throw new BadRequestException();			
		 	}
		try {
			evaluationRuleService.register(evaluationRule);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@POST
	@Path("/bulk")	
	public void testPostBulk(List<EvaluationRule> evaluationRuleList){
		if ( evaluationRuleList == null){
			throw new BadRequestException();			
		 	}
		try {
			evaluationRuleService.registerList(evaluationRuleList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
