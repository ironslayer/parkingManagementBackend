package io.github.ironslayer.spring_boot_starter_template.common.mediator;

public interface RequestHandler<T extends Request<R>, R> {
    R handle(T request);

    Class<T> getRequestType();
}

