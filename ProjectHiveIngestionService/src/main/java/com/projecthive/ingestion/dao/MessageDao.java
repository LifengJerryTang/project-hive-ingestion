package com.projecthive.ingestion.dao;

import com.projecthive.ingestion.models.Message;
import lombok.NonNull;

public interface MessageDao {

    void save(@NonNull final Message message);

}
