package com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command;

import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.Dispatchable;

public interface Command<TResult> extends Dispatchable<TResult> {
}
