package org.jboss.resteasy.test.microprofile.contextpropagation.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.resteasy.spi.HttpRequest;

@Path("test")
public class MicroProfileContextPropagationResource {

   @GET
   @Path("threadcontext")
   @Produces("text/plain")
   public CompletionStage<String> text(@Context HttpRequest request) {
      CompletableFuture<String> cs = new CompletableFuture<>();
      ThreadContext threadContext = ThreadContext.builder()
                                                      .propagated(ThreadContext.ALL_REMAINING)
                                                      .unchanged()
                                                      .cleared()
                                                      .build();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(
            threadContext.contextualRunnable(new Runnable() {
               public void run() {
                  try {
                     cs.complete("hello");
                  } catch (Exception e) {
                     throw new RuntimeException(e);
                  }
               }
            }));
      return cs;
   }
}