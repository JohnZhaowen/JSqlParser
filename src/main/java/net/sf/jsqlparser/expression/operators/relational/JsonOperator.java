/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.StringValue;

public class JsonOperator extends BinaryExpression {

    private String op; //"@>"

    public JsonOperator(String op) {
        this.op = op;
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    @Override
    public String getStringExpression() {
        return op;
    }

    @Override
    public JsonOperator withLeftExpression(Expression arg0) {
        return (JsonOperator) super.withLeftExpression(arg0);
    }

    @Override
    public JsonOperator withRightExpression(Expression arg0) {
        return (JsonOperator) super.withRightExpression(arg0);
    }

    public static void main(String[] args) {
        String string = new JsonOperator("@>").withLeftExpression(new StringValue("A"))
                .withRightExpression(new StringValue("b"))
                .toString();
        System.out.println(string);

    }
}
