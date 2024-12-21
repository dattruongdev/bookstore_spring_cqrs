package com.dattruongdev.bookstore_cqrs.cqrs;

import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.BaseHandler;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.Dispatchable;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.DispatchableHandler;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.HandledBy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class DispatchableProcessor implements DispatchableHandler {
    private final ApplicationContext applicationContext;

    @Override
    public <TResult> TResult dispatch(Dispatchable<TResult> dispatchable) {
        HandledBy handledByAnnotation = dispatchable.getClass().getAnnotation(HandledBy.class);
        if (handledByAnnotation == null) {
            throw new RuntimeException("Dispatchable class must be annotated with @HandledBy");
        }

        Class<? extends BaseHandler<?, ?>> handlerClass = handledByAnnotation.handler();
        Map<String, ? extends BaseHandler<?, ?>> handlers =  applicationContext.getBeansOfType(handlerClass);
        if (handlers.size() > 1) {
            throw new RuntimeException("There must be exactly one handler for dispatchable class");
        }
        if (handlers.values().isEmpty()) {
            throw new RuntimeException(String.format("Dispatchable %s has no handler", dispatchable.getClass().getName()));
        }

        BaseHandler<Dispatchable<TResult>, TResult> handler = (BaseHandler<Dispatchable<TResult>, TResult>) handlers.values().iterator().next();

        return handler.handle(dispatchable);
    }
}
