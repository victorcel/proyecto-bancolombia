package com.bancolombia;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App implements RequestHandler<SQSEvent, Void>
{
    static Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for(SQSEvent.SQSMessage msg : event.getRecords()){
            logger.info(msg.getBody());
            logger.info(msg.getAttributes().toString());
        }
        return null;

    }
}
