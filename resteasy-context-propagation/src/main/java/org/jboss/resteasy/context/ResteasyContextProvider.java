package org.jboss.resteasy.context;

import java.util.Collections;
import java.util.Map;

import org.eclipse.microprofile.context.spi.ThreadContextProvider;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class ResteasyContextProvider implements ThreadContextProvider
{

   private static final String JAXRS_CONTEXT = "JAX-RS";

   @Override
   public ThreadContextSnapshot currentContext(Map<String, String> props)
   {
      Map<Class<?>, Object> context = ResteasyProviderFactory.getContextDataMap();
      return () -> {
         ResteasyProviderFactory.pushContextDataMap(context);
         return () -> {
            ResteasyProviderFactory.removeContextDataLevel();
         };
      };
   }

   @Override
   public ThreadContextSnapshot clearedContext(Map<String, String> props)
   {
      Map<Class<?>, Object> context = Collections.emptyMap();
      return () -> {
         ResteasyProviderFactory.pushContextDataMap(context);
         return () -> {
            ResteasyProviderFactory.removeContextDataLevel();
         };
      };
   }

   @Override
   public String getThreadContextType()
   {
      return JAXRS_CONTEXT;
   }
}
