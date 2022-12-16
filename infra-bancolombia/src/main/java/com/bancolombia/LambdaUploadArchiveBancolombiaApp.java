package com.bancolombia;

import software.amazon.awscdk.App;

public final class LambdaUploadArchiveBancolombiaApp {
    public static void main(final String[] args) {
        App app = new App();

        new LambdaUploadArchiveBancolombiaStack(app, "LambdaUploadArchiveBancolombiaStack");

        app.synth();
    }
}
