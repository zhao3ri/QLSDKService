package com.qinglan.sdk.server.data.domain.specification;

public class NotSpecification<T> extends AbstractSpecification<T> {
    private Specification<T> spec;

    public NotSpecification(Specification<T> spec) {
        this.spec = spec;
    }

    public boolean isSatisfiedBy(T t) {
        return !this.spec.isSatisfiedBy(t);
    }
}
