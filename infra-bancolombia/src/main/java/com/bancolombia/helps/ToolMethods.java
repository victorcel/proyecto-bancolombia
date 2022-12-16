package com.bancolombia.helps;

import com.bancolombia.LambdaUploadArchiveBancolombiaStack;
import software.amazon.awscdk.BundlingOptions;
import software.amazon.awscdk.DockerVolume;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.destinations.SqsDestination;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.amazon.awscdk.services.sqs.Queue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;

public class ToolMethods {

    public List<String> listPackagingInstructions(String dataPath) {
        return Arrays.asList(
                "/bin/sh",
                "-c",
                String.format("cd %s ", dataPath) +
                        "&& mvn clean install " +
                        String.format("&& cp /asset-input/%s/target/%s.jar /asset-output/", dataPath, dataPath)
        );
    }

    public BundlingOptions.Builder bundlingOption(String dataPath) {
        return BundlingOptions.builder()
                .command(listPackagingInstructions(dataPath))
                .image(Runtime.JAVA_11.getBundlingImage())
                .volumes(singletonList(

                        DockerVolume.builder()
                                .hostPath(System.getProperty("user.home") + "/.m2/")
                                .containerPath("/root/.m2/")
                                .build()
                ))
                .user("root")
                .outputType(ARCHIVED);
    }

    public Function uploadArchiveFunction(
            LambdaUploadArchiveBancolombiaStack lambdaUploadArchiveBancolombiaStack,
            String dataPath,
            Queue queue) {

        Map<String, String> environment = new HashMap<>();
        environment.put("QUEUE_URL", queue.getQueueUrl());

        Function function = new Function(lambdaUploadArchiveBancolombiaStack, dataPath, FunctionProps.builder()
                .functionName(dataPath)
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../", AssetOptions.builder().bundling(
                                bundlingOption(dataPath).command(
                                        listPackagingInstructions(dataPath)
                                ).build()
                        ).build()
                ))
                .onSuccess(new SqsDestination(queue))
                .handler("com.bancolombia.App")
                .memorySize(1024)
                .timeout(Duration.seconds(10))
                .logRetention(RetentionDays.ONE_WEEK)
                .environment(environment)
                .build());

        queue.grantSendMessages(function);

        return function;
    }

    public Function downloadArchiveFunction(
            LambdaUploadArchiveBancolombiaStack
                    lambdaUploadArchiveBancolombiaStack,
            String dataPath
    ) {
        return new Function(lambdaUploadArchiveBancolombiaStack, dataPath, FunctionProps.builder()
                .functionName(dataPath)
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../", AssetOptions.builder().bundling(
                                bundlingOption(dataPath).command(
                                        listPackagingInstructions(dataPath)
                                ).build()
                        ).build()
                ))
                .handler("com.bancolombia.App")
                .memorySize(1024)
                .timeout(Duration.seconds(10))
                .logRetention(RetentionDays.ONE_WEEK)
                .build());
    }

}
