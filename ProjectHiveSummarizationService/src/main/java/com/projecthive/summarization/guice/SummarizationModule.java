package com.projecthive.summarization.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.projecthive.summarization.controller.SummarizationController;

public class SummarizationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SummarizationController.class)
                .in(Scopes.SINGLETON);
    }
}
