/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.expression;

import java.util.Arrays;
import java.util.List;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

/**
 * A string as in 'example_string'
 */
public final class StringValue extends ASTNodeAccessImpl implements Expression {

    private String value = "";
    private String prefix = null;

    public static final List<String> ALLOWED_PREFIXES = Arrays.asList("N", "U", "E", "R", "B", "RB", "_utf8");

    public StringValue() {
        // empty constructor
    }

    public StringValue(String escapedValue) {
        // removing "'" at the start and at the end

        //如果被单引号包裹，则去除前后的单引号直接赋值给value，结束
        if (escapedValue.startsWith("'") && escapedValue.endsWith("'")) {
            value = escapedValue.substring(1, escapedValue.length() - 1);
            return;
        }

        //字符长度大于2
        if (escapedValue.length() > 2) {
            for (String p : ALLOWED_PREFIXES) {
                //字符长度大于前缀长度
                if (escapedValue.length() > p.length()
                        //入参截取当前遍历的前缀长度，且必需是当前遍历的前缀相等
                        && escapedValue.substring(0, p.length()).equalsIgnoreCase(p)
                        //入参的前缀字符后一个字符为单引号
                        && escapedValue.charAt(p.length()) == '\''
                        //&& escapedValue.charAt(escapedValue.length() - 1) == '\''
                ) {
                    this.prefix = p;
                    //去除前缀及单引号
                    value = escapedValue.substring(p.length() + 1, escapedValue.length() - 1);
                    return;
                }
            }
        }

        value = escapedValue;
    }

    public String getValue() {
        return value;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNotExcapedValue() {
        StringBuilder buffer = new StringBuilder(value);
        int index = 0;
        int deletesNum = 0;
        while ((index = value.indexOf("''", index)) != -1) {
            buffer.deleteCharAt(index - deletesNum);
            index += 2;
            deletesNum++;
        }
        return buffer.toString();
    }

    public void setValue(String string) {
        value = string;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    @Override
    public String toString() {
        return (prefix != null ? prefix : "") + "'" + value + "'";
    }

    public StringValue withPrefix(String prefix) {
        this.setPrefix(prefix);
        return this;
    }

    public StringValue withValue(String value) {
        this.setValue(value);
        return this;
    }
}
