package com.github.superzhc.nifi.processors.db.jdbi3;

import com.github.superzhc.nifi.services.api.Jdbi3Service;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.jdbi.v3.core.Jdbi;

import java.sql.DatabaseMetaData;

public class Jdbi3Metadata extends Jdbi3BaseProcessor{
    @Override
    public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        Jdbi3Service jdbi3Service = context.getProperty(JDBI_SERVICE).asControllerService(Jdbi3Service.class);
        Jdbi jdbi = jdbi3Service.getJdbi();

        jdbi.withHandle(handle -> {

            handle.queryMetadata(DatabaseMetaData::getCatalogs).mapTo(String.class).list();

            return null;
        });
    }
}
