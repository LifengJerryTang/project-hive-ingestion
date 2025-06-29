package com.projecthive.summarization.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.projecthive.summarization.bedrock.BedrockModelInvoker;

public class BedrockModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BedrockModelInvoker.class)
                .in(Scopes.SINGLETON);
    }
}
