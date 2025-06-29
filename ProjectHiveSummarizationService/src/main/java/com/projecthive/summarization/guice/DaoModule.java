package com.projecthive.summarization.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.projecthive.summarization.dao.SummaryDao;
import com.projecthive.summarization.dao.SummaryDaoImpl;

public class DaoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SummaryDao.class)
                .to(SummaryDaoImpl.class)
                .in(Scopes.SINGLETON);
    }
}
