package com.bancolombia.useCases;

import com.bancolombia.LambdaUploadArchiveBancolombiaStack;
import com.bancolombia.helps.ToolMethods;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSource;
import software.amazon.awscdk.services.lambda.eventsources.SqsEventSourceProps;
import software.amazon.awscdk.services.sqs.Queue;

public class DownloadArchiveStack {


    public static void handler(LambdaUploadArchiveBancolombiaStack lambdaUploadArchiveBancolombiaStack, Queue queue) {
        String dataPath = "download-archive";


        ToolMethods toolMethods = new ToolMethods();

        Function downloadArchiveFunction = toolMethods.downloadArchiveFunction(
                lambdaUploadArchiveBancolombiaStack,
                dataPath
        );

        downloadArchiveFunction.addEventSource(new SqsEventSource(queue, SqsEventSourceProps.builder().build()));
    }

}
