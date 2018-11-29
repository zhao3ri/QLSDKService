package com.qinglan.sdk.server.data.domain.specification;

public interface Specification<T> {
    boolean isSatisfiedBy(T t);

    Specification<T> and(Specification<T> spec);

    Specification<T> or(Specification<T> spec);

    Specification<T> not();

    Specification<T> conjunction(Specification... specs);
}
