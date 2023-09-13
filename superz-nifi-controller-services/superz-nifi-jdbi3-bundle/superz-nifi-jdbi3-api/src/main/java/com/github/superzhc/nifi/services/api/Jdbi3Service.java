package com.github.superzhc.nifi.services.api;

import org.apache.nifi.controller.ControllerService;
import org.jdbi.v3.core.Jdbi;

public interface Jdbi3Service extends ControllerService {
    Jdbi getJdbi();
}
