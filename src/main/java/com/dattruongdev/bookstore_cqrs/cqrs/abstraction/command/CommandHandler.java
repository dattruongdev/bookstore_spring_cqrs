package com.dattruongdev.bookstore_cqrs.cqrs.abstraction.command;

import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.BaseHandler;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.Dispatchable;
import com.dattruongdev.bookstore_cqrs.cqrs.abstraction.DispatchableHandler;

public interface CommandHandler<TCommand extends Dispatchable<TResult>, TResult> extends BaseHandler<TCommand, TResult> {
}
