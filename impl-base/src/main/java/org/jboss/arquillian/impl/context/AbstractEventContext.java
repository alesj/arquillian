/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
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
package org.jboss.arquillian.impl.context;

import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.impl.Validate;
import org.jboss.arquillian.impl.event.Event;
import org.jboss.arquillian.impl.event.EventHandler;
import org.jboss.arquillian.impl.event.EventManager;
import org.jboss.arquillian.impl.event.MapEventManager;
import org.jboss.arquillian.spi.ServiceLoader;


/**
 * AbstractEventContext
 *
 * @author <a href="mailto:aknutsen@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public abstract class AbstractEventContext<X extends Context<X, T>, T extends Event> implements Context<X, T>
{
   private EventManager<X, T> eventManager;
   
   // TODO: create ObjectStore
   private Map<Class<?>, Object> objectStore;
   
   public AbstractEventContext()
   {
      this.eventManager = new MapEventManager<X, T>();
      this.objectStore = new HashMap<Class<?>, Object>();
   }

   public <K extends T> void register(Class<? extends K> eventType, EventHandler<X, ? super K> handler)
   {
      eventManager.register(eventType, handler);
   }
   
   protected EventManager<X, T> getEventManager() 
   {
      return eventManager;
   }
   
   public <B> void add(Class<B> type, B instance) 
   {
      Validate.notNull(type, "Type must be specified");
      Validate.notNull(instance, "Instance must be specified");
      
      objectStore.put(type, instance);
   }
   
   @SuppressWarnings("unchecked")
   public <B> B get(Class<B> type)
   {
      Validate.notNull(type, "Type must be specified");
      
      Object instance = objectStore.get(type);
      if(instance == null) 
      {
         Context<?, ?> parentContext = getParentContext();
         if(parentContext != null) 
         {
            instance = parentContext.get(type);
         }
      }
      return (B) instance;
   }
   
   public ServiceLoader getServiceLoader()
   {
      return get(ServiceLoader.class);
   }
}
