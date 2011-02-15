/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.impl.enricher.resource;

import java.net.URL;

import junit.framework.Assert;

import org.jboss.arquillian.api.ArquillianResource;
import org.jboss.arquillian.impl.AbstractManagerTestBase;
import org.jboss.arquillian.impl.core.ManagerBuilder;
import org.jboss.arquillian.impl.enricher.resource.ArquillianResourceTestEnricher;
import org.jboss.arquillian.spi.TestEnricher;
import org.jboss.arquillian.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.spi.client.protocol.metadata.Servlet;
import org.jboss.arquillian.spi.core.Injector;
import org.jboss.arquillian.spi.core.Instance;
import org.jboss.arquillian.spi.core.annotation.ApplicationScoped;
import org.jboss.arquillian.spi.core.annotation.Inject;
import org.junit.Test;


/**
 * ArquillianTestEnricherTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ArquillianTestEnricherContextTestCase extends AbstractManagerTestBase
{
   @Inject
   private Instance<Injector> injector;
   
   @Override
   protected void addExtensions(ManagerBuilder builder)
   {
   }

   @Test
   public void shouldBeAbleToInjectBaseContext() throws Exception
   {
      bind(ApplicationScoped.class, ProtocolMetaData.class, new ProtocolMetaData().addContext(new HTTPContext("TEST", 8080)));
      TestEnricher enricher = new ArquillianResourceTestEnricher();
      injector.get().inject(enricher);

      URLBaseContextClass test = new URLBaseContextClass();
      enricher.enrich(test);
      
      Assert.assertEquals("http://TEST:8080", test.url.toExternalForm());
   }
   
   @Test
   public void shouldBeAbleToInjectServlet() throws Exception
   {
      bind(ApplicationScoped.class, ProtocolMetaData.class, 
            new ProtocolMetaData().addContext(new HTTPContext("TEST", 8080)
            .add(new Servlet(URLServletContextClass.class.getSimpleName(), "/test"))));
      
      TestEnricher enricher = new ArquillianResourceTestEnricher();
      injector.get().inject(enricher);

      URLServletContextClass test = new URLServletContextClass();
      enricher.enrich(test);
      
      Assert.assertEquals("http://TEST:8080/test", test.url.toExternalForm());
   }
   
   public class URLBaseContextClass 
   {
      @ArquillianResource
      public URL url;
   }

   public class URLServletContextClass 
   {
      @ArquillianResource(URLServletContextClass.class)
      public URL url;
   }
}
