package ro.fortech.edu.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ro.fortech.edu.model.EvaluationRule;

@Stateless
public class EvaluationRuleRestClient {

	public void invokeGetEvaluationRuleById(long evaluationRuleId) { 
		System.out.println("****Enter invokeGetEvaluationRuleById****");	
		
		//Obtain the instance of Client which will be entry point for invoking REST Services.
		//It instantiates a preinitialized Client that you can use right away
		Client client = ClientBuilder.newClient();
		//Target the RESTful Webservice we want to 
	    //invoke by capturing it in WebTarget instance.  
		WebTarget webTarget = client.target("http://localhost:9080/edu-test-7/rest/evaluationRules/" + evaluationRuleId);
		
		//Build the request i.e a GET request to the RESTful Webservice defined		
		//by the URI in the WebTarget instance.	
		Invocation invocation = webTarget.request().buildGet();
		
		 //Invok the request to the RESTful API and capturing the Response.		
		Response response = invocation.invoke();
		
		//Return the XML data which can be unmarshalled		
		//into the instance of Books by using JAXB.
		EvaluationRule evaluationRule = response.readEntity(EvaluationRule.class);
		System.out.println("evaluationRule ID= "+evaluationRule.getIdEvaluationRule());	

	}
	
	public void invokeGetAllEvaluationRule() { 
		System.out.println("****Enter invokeGetAllEvaluationRule****");	
		
		//Obtain the instance of Client which will be entry point for invoking REST Services.
		Client client = ClientBuilder.newClient();
		//List<EvaluationRule> evaluationRuleList = client.target("http://localhost:9080/edu-test-7/rest/evaluationRules")				
				
		//		.get(new GenericType<List<EvaluationRule>>(){});
		//Target the RESTful Webservice we want to 
	    //invoke by capturing it in WebTarget instance.  
		WebTarget webTarget = client.target("http://localhost:9080/edu-test-7/rest/evaluationRules");
		
		//Build the request i.e a GET request to the RESTful Webservice defined		
		//by the URI in the WebTarget instance.	
		Invocation invocation = webTarget.request().buildGet();
		
		 //Invok the request to the RESTful API and capturing the Response.		
		Response response = invocation.invoke();
		//System.out.println("response all= "+response);
		//Return the XML data which can be unmarshalled		
		//into the instance of Books by using JAXB.
		List<EvaluationRule> evaluationRuleList = response.readEntity(new GenericType<List<EvaluationRule>>(){});
		//System.out.println("evaluationRule all= "+evaluationRule);	
		//System.out.println("evaluationRule ID= "+evaluationRule.getIdEvaluationRule());	
		
		//List<EvaluationRule> evaluationRuleList = response.readEntity(EvaluationRule.class);
		System.out.println("evaluationRule list size ID= "+evaluationRuleList.size());
		for(EvaluationRule evaluationRule:evaluationRuleList){
			System.out.println("evaluationRule id="+evaluationRule.getIdEvaluationRule());			
		}

	}
	
	public void addEvaluationRuleRest(EvaluationRule evaluationRule) {

		System.out.println("****Invoking the addEvaluationRuleRest ****");

		// Obtaining the instance of Client which will be entry point to
		// invoking REST Services.
		Client client = ClientBuilder.newClient();
		// Targeting the RESTful Webserivce we want to
		// invoke by capturing it in WebTarget instance.
		//Bind the target to the REST service’s URL using the target() method: 
		WebTarget target = client.target("http://localhost:9080/edu-test-7/rest/evaluationRules/testOneRule");
		Response response = target.request().buildPost(Entity.entity(evaluationRule, MediaType.APPLICATION_XML)).invoke();
		//System.out.println("RESPONSE location="+response.getMetadata().get("Location").get(0));
		// Building the request i.e a GET request to the RESTful Webservice
		// defined
		// by the URI in the WebTarget instance.

		// As we know that this RESTful Webserivce returns the XML data which
		// can be unmarshalled

		// into the instance of marketRule by using JAXB.
		//return response.getMetadata().get("Location").get(0).toString();

	}



}
