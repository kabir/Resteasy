package org.jboss.resteasy.test.microprofile.contextpropagation;

import java.io.File;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.microprofile.contextpropagation.resource.MicroProfileContextPropagationApplication;
import org.jboss.resteasy.test.microprofile.contextpropagation.resource.MicroProfileContextPropagationResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter MicroProfile Context Propagation
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression tests for RESTEASY-2533
 * @tpSince RESTEasy 3.13.0.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
//@Category(MicroProfileDependent.class)
public class MicroProfileContextPropagationTest {

   static ResteasyClient client;

   @Deployment
   public static Archive<?> deploy() {
      JavaArchive[] jars = Maven.resolver().resolve("org.eclipse.microprofile.context-propagation:microprofile-context-propagation-api:1.0.2").withTransitivity().as(JavaArchive.class);
      JavaArchive[] jars2 = Maven.resolver().resolve("io.smallrye:smallrye-context-propagation:1.0.13").withTransitivity().as(JavaArchive.class);
      JavaArchive[] jars3 = Maven.resolver().resolve("org.jboss.resteasy:resteasy-context-propagation:3.13.0-SNAPSHOT").withTransitivity().as(JavaArchive.class);

      WebArchive war = TestUtil.prepareArchive(MicroProfileContextPropagationTest.class.getSimpleName());
      war.addAsLibraries(jars);
      war.addAsLibraries(jars2);
      war.addAsLibraries(jars3);
      war.addClass(MicroProfileContextPropagationApplication.class);
      war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
      war.setWebXML(MicroProfileContextPropagationTest.class.getPackage(), "web.xml");
      Archive<?> ar = TestUtil.finishContainerPrepare(war, null, MicroProfileContextPropagationResource.class);
      ar.as(ZipExporter.class).exportTo(new File("/home/rsigal/" + war.getName()), true);
      return ar;
   }

   @BeforeClass
   public static void before() throws Exception {
      client = (ResteasyClient)ClientBuilder.newClient();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   private String generateURL(String path) {
      return PortProviderUtil.generateURL(path, MicroProfileContextPropagationTest.class.getSimpleName());
   }

   /**
    * @tpTestDetails
    * @tpSince RESTEasy 3.13.0
    */
   @Test
   public void testGet() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/test/threadcontext")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.get();
      Assert.assertEquals("hello", completionStage.toCompletableFuture().get().readEntity(String.class));
   }
}
