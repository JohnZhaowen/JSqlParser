/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Expression;

public class OrderByElement {

    /**
     * null的排序方式
     */
    public enum NullOrdering {
        //null值放在前面
        NULLS_FIRST,
        //null值放在后面
        NULLS_LAST
    }

    private Expression expression;
    private boolean asc = true;
    private boolean ascDescPresent = false;
    private NullOrdering nullOrdering;

    public void accept(OrderByVisitor orderByVisitor) {
        orderByVisitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(expression.toString());

        if (!asc) {
            b.append(" DESC");
        } else if (ascDescPresent) {
            b.append(" ASC");
        }

        if (nullOrdering != null) {
            b.append(' ');
            b.append(nullOrdering == NullOrdering.NULLS_FIRST ? "NULLS FIRST" : "NULLS LAST");
        }
        return b.toString();
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public NullOrdering getNullOrdering() {
        return nullOrdering;
    }

    public void setNullOrdering(NullOrdering nullOrdering) {
        this.nullOrdering = nullOrdering;
    }

    public void setAscDescPresent(boolean ascDescPresent) {
        this.ascDescPresent = ascDescPresent;
    }

    public boolean isAscDescPresent() {
        return ascDescPresent;
    }

    public Expression getExpression() {
        return expression;
    }

    public <E extends Expression> E getExpression(Class<E> type) {
        return type.cast(getExpression());
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public OrderByElement withExpression(Expression expression) {
        this.setExpression(expression);
        return this;
    }

    public OrderByElement withAsc(boolean asc) {
        this.setAsc(asc);
        return this;
    }

    public OrderByElement withAscDescPresent(boolean ascDescPresent) {
        this.setAscDescPresent(ascDescPresent);
        return this;
    }

    public OrderByElement withNullOrdering(NullOrdering nullOrdering) {
        this.setNullOrdering(nullOrdering);
        return this;
    }

}
