package com.bancolombia;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.bancolombia.models.RequestInput;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    static Logger logger = LoggerFactory.getLogger(App.class);
    final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(responseHeaders);

        try {
            logger.info(input.getBody());

            Gson gson = new Gson();
            RequestInput bodyInput = gson.fromJson(input.getBody(), RequestInput.class);

            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl(System.getenv("QUEUE_URL"))
                    .withMessageBody(input.getBody())
                    .withDelaySeconds(5);

            sqs.sendMessage(send_msg_request);

            return response
                    .withStatusCode(200)
                    .withBody(new Gson().toJson(bodyInput));

        } catch (Exception exception) {
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{ \"error\": %s}",exception));
        }
    }

}