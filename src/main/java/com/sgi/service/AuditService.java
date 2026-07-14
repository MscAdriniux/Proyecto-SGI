/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.sgi.service;

import com.sgi.model.AuditLog;
import java.util.List;

/**
 *
 * @author ad_ri
 */
public interface AuditService {

    List<AuditLog> obtenerLogs();
    
}
