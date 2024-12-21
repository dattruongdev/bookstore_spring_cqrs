package com.dattruongdev.bookstore_cqrs.cqrs.abstraction;

public interface DispatchableHandler {
    <TResult> TResult dispatch(Dispatchable<TResult> dispatchable);
}
