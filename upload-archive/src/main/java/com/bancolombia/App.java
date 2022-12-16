package com.bancolombia;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    static Logger logger = LoggerFactory.getLogger(App.class);

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(responseHeaders);

        try {
            logger.info(input.getBody());
            //Set up contentType String
            String contentType = "";

            byte[] bI = Base64.decodeBase64(input.getBody().getBytes());
            //Get the content-type header and extract the boundary
            Map<String, String> hps = input.getHeaders();
            if (hps != null) {
                contentType = hps.get("content-type");
            }
            String[] boundaryArray = contentType.split("=");
            //Transform the boundary to a byte array
            byte[] boundary = boundaryArray[1].getBytes();
            //Log the extraction for verification purposes
            logger.info(new String(bI, "UTF-8") + "\n");

            return response
                    .withStatusCode(200)
                    .withBody(new String(bI, "UTF-8") + "\n");

        } catch (Exception exception) {
            return response
                    .withStatusCode(500)
                    .withBody("{ \"message\": \"Error al momento de cargar el archivo .:verificar:.\" }");
        }
    }

}