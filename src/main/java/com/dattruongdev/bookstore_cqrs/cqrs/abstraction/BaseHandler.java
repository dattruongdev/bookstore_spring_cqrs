package com.dattruongdev.bookstore_cqrs.cqrs.abstraction;

public interface BaseHandler<TDispatchable extends Dispatchable<TResult>, TResult> {
    TResult handle(final TDispatchable dispatchable);
}
