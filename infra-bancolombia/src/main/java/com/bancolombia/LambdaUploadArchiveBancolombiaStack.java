package com.bancolombia;

import com.bancolombia.useCases.DownloadArchiveStack;
import com.bancolombia.useCases.UploadArchiveStack;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class LambdaUploadArchiveBancolombiaStack extends Stack {
    public LambdaUploadArchiveBancolombiaStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public LambdaUploadArchiveBancolombiaStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        final Queue queue = Queue.Builder.create(this, "LambdaUploadArchiveBancolombiaQueue")
                .queueName("LambdaUploadArchiveBancolombiaQueue")
                .visibilityTimeout(Duration.seconds(300))
                .build();

        UploadArchiveStack.handler(this,queue);
        DownloadArchiveStack.handler(this,queue);


    }
}
