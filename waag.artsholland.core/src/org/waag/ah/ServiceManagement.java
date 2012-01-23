package org.waag.ah;

import org.jboss.ejb3.annotation.Management;

@Management
public interface ServiceManagement {
   void create() throws Exception;
//   void start() throws Exception;
//   void stop();
   void destroy();
}
