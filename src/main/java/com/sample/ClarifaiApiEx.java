package com.sample;

import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.api.request.model.Action;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.model.output_info.ConceptOutputInfo;
import clarifai2.dto.prediction.Concept;

public class ClarifaiApiEx {
	
	/* Your API Key */
	private final static String API_KEY = "*****";


	/* Clarifai Client */
	public static ClarifaiClient getClient() {
		return new ClarifaiBuilder(API_KEY).buildSync();
	}

	
	/* General Model */
	public static String getGeneralPredictionResult(ClarifaiClient client,String image) {
		return client.getDefaultModels().generalModel() 
		.predict().withInputs(ClarifaiInput.forImage(ClarifaiImage.of(image))).executeSync().rawBody();
	}

	
	/* Food Model */
	public static List<ClarifaiOutput<Concept>> getFoodPredictionResult(ClarifaiClient client,String image) {
		return client.getDefaultModels().foodModel() 
		.predict().withInputs(ClarifaiInput.forImage(ClarifaiImage.of(image))).executeSync().get();
	}

	
	/* Creating a Custom Model */
	public static void getCustomPredictionResult(ClarifaiClient client,String image) {
		
		// add new concept
		boolean isAdded = client.addConcepts().plus(Concept.forID("cuTestconcept")).executeSync().isSuccessful();
		
		
		// add images into concept
		String input = client.addInputs().plus(ClarifaiInput.forImage(ClarifaiImage.of("https://images-na.ssl-images-amazon.com/images/M/MV5BMjAxNzUwNjExOV5BMl5BanBnXkFtZTcwNDUyMTUxNw@@._V1_SY1000_CR0,0,1339,1000_AL_.jpg"))
				.withConcepts(Concept.forID("cuTestconcept")), 
				ClarifaiInput.forImage(ClarifaiImage.of("http://pinthisstar.com/images/priyanka-chopra-hair-20.jpg"))
                .withConcepts(
                        Concept.forID("cuTestconcept").withValue(true)
                    )).allowDuplicateURLs(true).executeSync().rawBody();
		
		System.out.println("Adding images into concept "+input);
		
	   
	   // create a model
	   ClarifaiResponse<ConceptModel> modelResponse = client.createModel("testModel")
			        .withOutputInfo(ConceptOutputInfo.forConcepts(	        
			            Concept.forID("cuTestconcept")
			        ))
			        .executeSync();
	   
	   
	   String modelRes = modelResponse.rawBody();
	   System.out.println("Creating model "+modelRes);
	   
	  
	   // add a concept into model
	   int modelConceptRes = client.modifyModel("testModel")
			   		.withConcepts(Action.MERGE, Concept.forID("cuTestconcept"))
			   		.executeSync().responseCode();
	   
	   System.out.println("Adding concept into model "+modelConceptRes);
	   
	     
	   // train a model 
	   int trainResponse = client.trainModel("testModel").executeSync().responseCode();
	   System.out.println("Training model "+trainResponse);
		
	   
	   // predict with custom added model 
	   System.out.println("Prediction "+client.predict("testModel")
        .withInputs(
            ClarifaiInput.forImage(ClarifaiImage.of(image))
        )
        .executeSync().get());
	   
	   
	}
		
	public static void main(String[] args) {
		
		// create a api client
		ClarifaiClient client = ClarifaiApiEx.getClient();
		
		// predict with custom model
		ClarifaiApiEx.getCustomPredictionResult(client, "http://starsunfolded.1ygkv60km.netdna-cdn.com/wp-content/uploads/2014/06/Priyanka-Chopra-5.jpg");
		
		
		String imageUrl = "http://www.planwallpaper.com/static/images/6983664-cute-nature.jpg";

		
		//predict with general model
		String generalPrediction = ClarifaiApiEx.getGeneralPredictionResult(client, imageUrl);
		System.out.println(generalPrediction);
	}

}
