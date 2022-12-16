package com.bancolombia;

import software.amazon.awscdk.*;
import software.amazon.awscdk.services.apigatewayv2.alpha.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.alpha.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegrationProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.s3.assets.AssetOptions;
import software.constructs.Construct;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static software.amazon.awscdk.BundlingOutput.ARCHIVED;

public class LambdaUploadArchiveBancolombiaStack extends Stack {
    public LambdaUploadArchiveBancolombiaStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public LambdaUploadArchiveBancolombiaStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

//        final Queue queue = Queue.Builder.create(this, "LambdaUploadArchiveBancolombiaQueue")
//                .visibilityTimeout(Duration.seconds(300))
//                .build();
//
//        final Topic topic = Topic.Builder.create(this, "LambdaUploadArchiveBancolombiaTopic")
//            .displayName("My First Topic Yeah")
//            .build();
//
//        topic.addSubscription(new SqsSubscription(queue));
        List<String> lambdaFunctionPackagingInstructions = Arrays.asList(
                "/bin/sh",
                "-c",
                "cd upload-archive " +
                        "&& mvn clean install " +
                        "&& cp /asset-input/upload-archive/target/upload-archive.jar /asset-output/"
        );

        BundlingOptions.Builder builderOptions = BundlingOptions.builder()
                .command(lambdaFunctionPackagingInstructions)
                .image(Runtime.JAVA_11.getBundlingImage())
                .volumes(singletonList(
                        // Mount local .m2 repo to avoid download all the dependencies again inside the container
                        DockerVolume.builder()
                                .hostPath(System.getProperty("user.home") + "/.m2/")
                                .containerPath("/root/.m2/")
                                .build()
                ))
                .user("root")
                .outputType(ARCHIVED);

        Function bmiCalculatorFunction = new Function(this, "upload-archive-bancolombia", FunctionProps.builder()
                .functionName("upload-archive-bancolombia")
                .runtime(Runtime.JAVA_11)
                .code(Code.fromAsset("../", AssetOptions.builder().bundling(
                                builderOptions.command(
                                        lambdaFunctionPackagingInstructions
                                ).build()
                        ).build()
                ))
                .handler("com.bancolombia.App")
                .memorySize(1024)
                .timeout(Duration.seconds(10))
                .logRetention(RetentionDays.ONE_WEEK)
                .build());

        HttpApi httpApi = new HttpApi(this, "HttpApi");

        HttpLambdaIntegration httpLambdaIntegration = new HttpLambdaIntegration(
                "this",
                bmiCalculatorFunction,
                HttpLambdaIntegrationProps.builder()
                        .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
                        .build()
        );

        httpApi.addRoutes(AddRoutesOptions.builder()
                .path("/upload")
                .methods(singletonList(HttpMethod.POST))
                .integration(httpLambdaIntegration)
                .build()
        );

        new CfnOutput(this, "HttApi", CfnOutputProps.builder()
                .description("HTTP API BANCOLOMBIA")
                .value(httpApi.getApiEndpoint())
                .build());

    }
}
