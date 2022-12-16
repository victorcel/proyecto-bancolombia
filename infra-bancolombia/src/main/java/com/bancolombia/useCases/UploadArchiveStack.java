package com.bancolombia.useCases;

import com.bancolombia.LambdaUploadArchiveBancolombiaStack;
import com.bancolombia.helps.ToolMethods;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.services.apigatewayv2.alpha.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.alpha.HttpMethod;
import software.amazon.awscdk.services.apigatewayv2.alpha.PayloadFormatVersion;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigatewayv2.integrations.alpha.HttpLambdaIntegrationProps;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.sqs.Queue;

import static java.util.Collections.singletonList;

public class UploadArchiveStack {
    public static void handler(LambdaUploadArchiveBancolombiaStack lambdaUploadArchiveBancolombiaStack, Queue queue) {
        String dataPath = "upload-archive";

        ToolMethods toolMethods = new ToolMethods();

        Function uploadArchiveFunction = toolMethods.uploadArchiveFunction(
                lambdaUploadArchiveBancolombiaStack,
                dataPath,
                queue
        );

        HttpApi httpApi = new HttpApi(lambdaUploadArchiveBancolombiaStack, "HttpApi");

        HttpLambdaIntegration httpLambdaIntegration = new HttpLambdaIntegration(
                "this",
                uploadArchiveFunction,
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

        new CfnOutput(lambdaUploadArchiveBancolombiaStack, "HttApi", CfnOutputProps.builder()
                .description("HTTP API BANCOLOMBIA")
                .value(httpApi.getApiEndpoint())
                .build());
    }
}
