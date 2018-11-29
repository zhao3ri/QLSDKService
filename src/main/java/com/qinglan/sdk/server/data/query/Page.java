package com.qinglan.sdk.server.data.query;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Page<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    protected int pageNo = 1;
    protected int pageSize = 1;
    protected String orderBy = null;
    protected String order = null;
    protected boolean autoCount = true;
    protected List<T> result = Collections.emptyList();
    protected long totalCount = 0L;

    public Page() {
    }

    public Page(int pageSize) {
        this.pageSize = pageSize;
    }

    public Page(int pageSize, int pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
    }

    public String toString() {
        return "Page [pageNo=" + this.pageNo + ", pageSize=" + this.pageSize + ", orderBy=" + this.orderBy + ", order=" + this.order + ", autoCount=" + this.autoCount + ", result=" + this.result + ", totalCount=" + this.totalCount + "]";
    }

    public int getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
        if (pageNo < 1) {
            this.pageNo = 1;
        }

    }

    public Page<T> pageNo(int thePageNo) {
        this.setPageNo(thePageNo);
        return this;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        if (pageSize < 1) {
            this.pageSize = 1;
        }

    }

    public Page<T> pageSize(int thePageSize) {
        this.setPageSize(thePageSize);
        return this;
    }

    public int getFirstResult() {
        return (this.pageNo - 1) * this.pageSize;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Page<T> orderBy(String theOrderBy) {
        this.setOrderBy(theOrderBy);
        return this;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(String order) {
        String[] orders = order.toLowerCase().split(",");
        String[] var6 = orders;
        int var5 = orders.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            String orderStr = var6[var4];
            if (!"desc".equals(orderStr) || !"asc".equals(orderStr)) {
                throw new IllegalArgumentException("order " + orderStr + " illegal");
            }
        }

        this.order = order.toLowerCase();
    }

    public Page<T> order(String theOrder) {
        this.setOrder(theOrder);
        return this;
    }

    public boolean isOrderBySetted() {
        return this.orderBy != null && !this.orderBy.trim().isEmpty() && this.order != null && !this.order.trim().isEmpty();
    }

    public boolean isAutoCount() {
        return this.autoCount;
    }

    public void setAutoCount(boolean autoCount) {
        this.autoCount = autoCount;
    }

    public Page<T> autoCount(boolean theAutoCount) {
        this.setAutoCount(theAutoCount);
        return this;
    }

    public List<T> getResult() {
        return this.result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getTotalPages() {
        if (this.totalCount <= 0L) {
            return 0L;
        } else {
            long count = this.totalCount / (long) this.pageSize;
            if (this.totalCount % (long) this.pageSize > 0L) {
                ++count;
            }

            return count;
        }
    }

    public boolean isHasNext() {
        return (long) (this.pageNo + 1) <= this.getTotalPages();
    }

    public int getNextPage() {
        return this.isHasNext() ? this.pageNo + 1 : this.pageNo;
    }

    public boolean isHasPre() {
        return this.pageNo - 1 >= 1;
    }

    public int getPrePage() {
        return this.isHasPre() ? this.pageNo - 1 : this.pageNo;
    }
}