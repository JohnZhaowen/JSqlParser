/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2020 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.deparser;

/**
 * A base for a Statement DeParser
 *
 * sql逆解析器，就是将对应的Statement反向解析成String类型的sql语句
 *
 *
 * @param <S> the type of statement this DeParser supports
 */
abstract class AbstractDeParser<S> {
    protected StringBuilder buffer;

    protected AbstractDeParser(StringBuilder buffer) {
        this.buffer = buffer;
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }

    /**
     * DeParses the given statement into the buffer
     *
     * @param statement the statement to deparse
     */
    abstract void deParse(S statement);
}
