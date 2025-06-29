package com.projecthive.summarization.dao;


import com.projecthive.summarization.models.Summary;
import lombok.NonNull;

public interface SummaryDao {
    void save(@NonNull Summary summary);
}
