package com.sgi.service;

import com.sgi.model.AuditLog;
import java.util.List;

public interface AuditService {
    List<AuditLog> obtenerLogs();
    long contarInfo();
    long contarWarn();
    long contarError();
}
