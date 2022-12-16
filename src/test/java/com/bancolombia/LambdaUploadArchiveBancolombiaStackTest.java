package com.bancolombia;

import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;

import java.io.IOException;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

public class LambdaUploadArchiveBancolombiaStackTest {

    @Test
    public void testStack() throws IOException {
        App app = new App();
        LambdaUploadArchiveBancolombiaStack stack = new LambdaUploadArchiveBancolombiaStack(app, "test");

        Template template = Template.fromStack(stack);

        template.hasResourceProperties("AWS::SQS::Queue", new HashMap<String, Number>() {{
          put("VisibilityTimeout", 300);
        }});

        template.resourceCountIs("AWS::SNS::Topic", 1);
    }
}
