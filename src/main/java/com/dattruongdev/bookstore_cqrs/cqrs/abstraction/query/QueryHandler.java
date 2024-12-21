package com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query;

import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.BaseHandler;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.Dispatchable;

public interface QueryHandler<TQuery extends Dispatchable<TResult>,TResult> extends BaseHandler<TQuery, TResult> {
}
