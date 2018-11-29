package com.qinglan.sdk.server.data.domain.specification;

import java.util.Iterator;
import java.util.List;

public class ConjunctionSpecification<T> extends AbstractSpecification<T> {
    private List<Specification<T>> list;

    public ConjunctionSpecification(List<Specification<T>> list) {
        this.list = list;
    }

    public boolean isSatisfiedBy(T candidate) {
        Iterator specificationIterator = this.list.iterator();

        while (specificationIterator.hasNext()) {
            Specification<T> spec = (Specification) specificationIterator.next();
            if (!spec.isSatisfiedBy(candidate)) {
                return false;
            }
        }

        return true;
    }
}
