/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.WithItem;

import java.util.Iterator;

import static java.util.stream.Collectors.joining;

public class DeleteDeParser extends AbstractDeParser<Delete> {

    private ExpressionVisitor expressionVisitor = new ExpressionVisitorAdapter();

    public DeleteDeParser() {
        super(new StringBuilder());
    }

    public DeleteDeParser(ExpressionVisitor expressionVisitor, StringBuilder buffer) {
        super(buffer);
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public void deParse(Delete delete) {
        if (delete.getWithItemsList() != null && !delete.getWithItemsList().isEmpty()) {
            buffer.append("WITH ");
            for (Iterator<WithItem> iter = delete.getWithItemsList().iterator(); iter.hasNext(); ) {
                WithItem withItem = iter.next();
                buffer.append(withItem);
                if (iter.hasNext()) {
                    buffer.append(",");
                }
                buffer.append(" ");
            }
        }
        buffer.append("DELETE");
        if (delete.getTables() != null && !delete.getTables().isEmpty()) {
            buffer.append(
                    delete.getTables().stream().map(Table::getFullyQualifiedName).collect(joining(", ", " ", "")));
        }
        if (delete.isHasFrom()) {
            buffer.append(" FROM");
        }
        buffer.append(" ").append(delete.getTable().toString());

        if (delete.getUsingList() != null && !delete.getUsingList().isEmpty()) {
            buffer.append(" USING").append(
                    delete.getUsingList().stream().map(Table::toString).collect(joining(", ", " ", "")));
        }
        if (delete.getJoins() != null) {
            for (Join join : delete.getJoins()) {
                if (join.isSimple()) {
                    buffer.append(", ").append(join);
                } else {
                    buffer.append(" ").append(join);
                }
            }
        }

        if (delete.getWhere() != null) {
            buffer.append(" WHERE ");
            delete.getWhere().accept(expressionVisitor);
        }

        if (delete.getOrderByElements() != null) {
            new OrderByDeParser(expressionVisitor, buffer).deParse(delete.getOrderByElements());
        }
        if (delete.getLimit() != null) {
            new LimitDeparser(buffer).deParse(delete.getLimit());
        }

    }

    public ExpressionVisitor getExpressionVisitor() {
        return expressionVisitor;
    }

    public void setExpressionVisitor(ExpressionVisitor visitor) {
        expressionVisitor = visitor;
    }

    public static void main(String[] args) throws JSQLParserException {
        DeleteDeParser deleteDeParser = new DeleteDeParser();
        deleteDeParser.deParse((Delete) CCJSqlParserUtil.parse("DELETE FROM table1 WHERE name = 'john'"));
        System.out.println(deleteDeParser.getBuffer());
    }
}
