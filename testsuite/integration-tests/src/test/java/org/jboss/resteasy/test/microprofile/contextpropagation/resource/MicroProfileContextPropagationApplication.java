package org.jboss.resteasy.test.microprofile.contextpropagation.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("app")
public class MicroProfileContextPropagationApplication extends Application {

   @Override
   public Set<Class<?>> getClasses() {
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(MicroProfileContextPropagationResource.class);
      return classes;
  }
}
