/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.statement.delete;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;

import static java.util.stream.Collectors.joining;

public class Delete implements Statement {

    private List<WithItem> withItemsList;
    private Table table;
    private OracleHint oracleHint = null;
    private List<Table> tables;
    private List<Table> usingList;
    private List<Join> joins;
    private Expression where;
    private Limit limit;
    private List<OrderByElement> orderByElements;
    private boolean hasFrom = true;

    @Override
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (withItemsList != null && !withItemsList.isEmpty()) {
            b.append("WITH ");
            for (Iterator<WithItem> iter = withItemsList.iterator(); iter.hasNext(); ) {
                WithItem withItem = iter.next();
                b.append(withItem);
                if (iter.hasNext()) {
                    b.append(",");
                }
                b.append(" ");
            }
        }

        b.append("DELETE");

        if (tables != null && tables.size() > 0) {
            b.append(" ");
            b.append(tables.stream()
                    .map(t -> t.toString())
                    .collect(joining(", ")));
        }

        if (hasFrom) {
            b.append(" FROM");
        }
        b.append(" ").append(table);

        if (usingList != null && usingList.size() > 0) {
            b.append(" USING ");
            b.append(usingList.stream()
                    .map(Table::toString)
                    .collect(joining(", ")));
        }

        if (joins != null) {
            for (Join join : joins) {
                if (join.isSimple()) {
                    b.append(", ").append(join);
                } else {
                    b.append(" ").append(join);
                }
            }
        }

        if (where != null) {
            b.append(" WHERE ").append(where);
        }

        if (orderByElements != null) {
            b.append(PlainSelect.orderByToString(orderByElements));
        }

        if (limit != null) {
            b.append(limit);
        }
        return b.toString();
    }

    @Override
    public void accept(StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }

    public List<WithItem> getWithItemsList() {
        return withItemsList;
    }

    public void setWithItemsList(List<WithItem> withItemsList) {
        this.withItemsList = withItemsList;
    }

    public Delete withWithItemsList(List<WithItem> withItemsList) {
        this.setWithItemsList(withItemsList);
        return this;
    }

    public Delete addWithItemsList(WithItem... withItemsList) {
        List<WithItem> collection = Optional.ofNullable(getWithItemsList()).orElseGet(ArrayList::new);
        Collections.addAll(collection, withItemsList);
        return this.withWithItemsList(collection);
    }

    public Delete addWithItemsList(Collection<? extends WithItem> withItemsList) {
        List<WithItem> collection = Optional.ofNullable(getWithItemsList()).orElseGet(ArrayList::new);
        collection.addAll(withItemsList);
        return this.withWithItemsList(collection);
    }

    public List<OrderByElement> getOrderByElements() {
        return orderByElements;
    }

    public void setOrderByElements(List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table name) {
        table = name;
    }

    public Expression getWhere() {
        return where;
    }

    public void setWhere(Expression expression) {
        where = expression;
    }

    public OracleHint getOracleHint() {
        return oracleHint;
    }

    public void setOracleHint(OracleHint oracleHint) {
        this.oracleHint = oracleHint;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public List<Table> getUsingList() {
        return usingList;
    }

    public void setUsingList(List<Table> usingList) {
        this.usingList = usingList;
    }

    public List<Join> getJoins() {
        return joins;
    }

    public void setJoins(List<Join> joins) {
        this.joins = joins;
    }

    public boolean isHasFrom() {
        return this.hasFrom;
    }

    public void setHasFrom(boolean hasFrom) {
        this.hasFrom = hasFrom;
    }

    public Delete withTables(List<Table> tables) {
        this.setTables(tables);
        return this;
    }

    public Delete withUsingList(List<Table> usingList) {
        this.setUsingList(usingList);
        return this;
    }

    public Delete withJoins(List<Join> joins) {
        this.setJoins(joins);
        return this;
    }

    public Delete withLimit(Limit limit) {
        this.setLimit(limit);
        return this;
    }

    public Delete withOrderByElements(List<OrderByElement> orderByElements) {
        this.setOrderByElements(orderByElements);
        return this;
    }

    public Delete withTable(Table table) {
        this.setTable(table);
        return this;
    }

    public Delete withWhere(Expression where) {
        this.setWhere(where);
        return this;
    }

    public Delete withHasFrom(boolean hasFrom) {
        this.setHasFrom(hasFrom);
        return this;
    }

    public Delete addTables(Table... tables) {
        List<Table> collection = Optional.ofNullable(getTables()).orElseGet(ArrayList::new);
        Collections.addAll(collection, tables);
        return this.withTables(collection);
    }

    public Delete addTables(Collection<? extends Table> tables) {
        List<Table> collection = Optional.ofNullable(getTables()).orElseGet(ArrayList::new);
        collection.addAll(tables);
        return this.withTables(collection);
    }

    public Delete addUsingList(Table... usingList) {
        List<Table> collection = Optional.ofNullable(getUsingList()).orElseGet(ArrayList::new);
        Collections.addAll(collection, usingList);
        return this.withUsingList(collection);
    }

    public Delete addUsingList(Collection<? extends Table> usingList) {
        List<Table> collection = Optional.ofNullable(getUsingList()).orElseGet(ArrayList::new);
        collection.addAll(usingList);
        return this.withUsingList(collection);
    }

    public Delete addJoins(Join... joins) {
        List<Join> collection = Optional.ofNullable(getJoins()).orElseGet(ArrayList::new);
        Collections.addAll(collection, joins);
        return this.withJoins(collection);
    }

    public Delete addJoins(Collection<? extends Join> joins) {
        List<Join> collection = Optional.ofNullable(getJoins()).orElseGet(ArrayList::new);
        collection.addAll(joins);
        return this.withJoins(collection);
    }

    public Delete addOrderByElements(OrderByElement... orderByElements) {
        List<OrderByElement> collection = Optional.ofNullable(getOrderByElements()).orElseGet(ArrayList::new);
        Collections.addAll(collection, orderByElements);
        return this.withOrderByElements(collection);
    }

    public Delete addOrderByElements(Collection<? extends OrderByElement> orderByElements) {
        List<OrderByElement> collection = Optional.ofNullable(getOrderByElements()).orElseGet(ArrayList::new);
        collection.addAll(orderByElements);
        return this.withOrderByElements(collection);
    }

    public <E extends Expression> E getWhere(Class<E> type) {
        return type.cast(getWhere());
    }
}
