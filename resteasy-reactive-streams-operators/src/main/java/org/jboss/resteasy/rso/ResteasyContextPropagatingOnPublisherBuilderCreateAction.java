package org.jboss.resteasy.rso;

import java.util.Map;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.functions.BiFunction;

@SuppressWarnings("rawtypes")
public class ResteasyContextPropagatingOnPublisherBuilderCreateAction implements BiFunction<Publisher, Subscriber, Subscriber>
{

   @SuppressWarnings("unchecked")
   @Override
   public Subscriber apply(Publisher t1, Subscriber t2) throws Exception
   {
      return new ContextCapturerSubscriber<>(t2);
   }

   static final class ContextCapturerSubscriber<T> implements Subscriber<T>
   {

      final Map<Class<?>, Object> contextDataMap = ResteasyProviderFactory.getContextDataMap();

      final Subscriber<T> actual;

      ContextCapturerSubscriber(final Subscriber<T> actual)
      {
         this.actual = actual;
      }

      @Override
      public void onError(Throwable e)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onError(e);
         ResteasyProviderFactory.removeContextDataLevel();
      }

      @Override
      public void onNext(T t)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onNext(t);
         ResteasyProviderFactory.removeContextDataLevel();
      }

      @Override
      public void onComplete()
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onComplete();
         ResteasyProviderFactory.removeContextDataLevel();
      }

      @Override
      public void onSubscribe(Subscription d)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onSubscribe(d);
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }
}
