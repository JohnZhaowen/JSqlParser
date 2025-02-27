/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.expression.operators.arithmetic;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class Addition extends BinaryExpression {

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    @Override
    public String getStringExpression() {
        return "+";
    }

    /**
     * 只是进行了返回值的强转
     * @param expression
     * @return
     */
    @Override
    public Addition withLeftExpression(Expression expression) {
        return (Addition) super.withLeftExpression(expression);
    }

    /**
     * 只是进行了返回值的强转
     * @param expression
     * @return
     */
    @Override
    public Addition withRightExpression(Expression expression) {
        return (Addition) super.withRightExpression(expression);
    }
}
