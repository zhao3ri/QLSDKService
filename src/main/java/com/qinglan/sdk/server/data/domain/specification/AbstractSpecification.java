package com.qinglan.sdk.server.data.domain.specification;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractSpecification<T> implements Specification<T> {
    public AbstractSpecification() {
    }

    public abstract boolean isSatisfiedBy(T var1);

    public Specification<T> and(Specification<T> specification) {
        return new AndSpecification(this, specification);
    }

    public Specification<T> or(Specification<T> specification) {
        return new OrSpecification(this, specification);
    }

    public Specification<T> not() {
        return new NotSpecification(this);
    }

    public Specification<T> conjunction(Specification... others) {
        List<Specification> list = Arrays.asList(others);
        list.add(this);
        return new ConjunctionSpecification(list);
    }
}
