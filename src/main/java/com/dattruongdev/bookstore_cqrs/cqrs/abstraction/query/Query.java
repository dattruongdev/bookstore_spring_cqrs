package com.dattruongdev.bookstore_cqrs.cqrs.abstraction.query;

import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.Dispatchable;

public interface Query<TResult> extends Dispatchable<TResult> {
}
