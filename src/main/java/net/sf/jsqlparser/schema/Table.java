/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.MySQLIndexHint;
import net.sf.jsqlparser.expression.SQLServerHints;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.IntoTableVisitor;
import net.sf.jsqlparser.statement.select.Pivot;
import net.sf.jsqlparser.statement.select.UnPivot;

/**
 * A table. It can have an alias and the schema name it belongs to.
 */
public class Table extends ASTNodeAccessImpl implements FromItem, MultiPartName {

    //表名所在的下标
    private static final int NAME_IDX = 0;
    //scheme所在的下标
    private static final int SCHEMA_IDX = 1;
    //db所在的下标
    private static final int DATABASE_IDX = 2;
    //server所在的下标
    private static final int SERVER_IDX = 3;

    //这里将上述的0-3统一放在该列表内
    private List<String> partItems = new ArrayList<>();
    //table的别名
    private Alias alias;

    private Pivot pivot;

    private UnPivot unpivot;

    private MySQLIndexHint mysqlHints;

    private SQLServerHints sqlServerHints;

    public Table() {
    }

    public Table(String name) {
        setName(name);
    }

    public Table(String schemaName, String name) {
        setName(name);
        setSchemaName(schemaName);
    }

    public Table(Database database, String schemaName, String name) {
        setName(name);
        setSchemaName(schemaName);
        setDatabase(database);
    }

    public Table(List<String> partItems) {
        this.partItems = new ArrayList<>(partItems);
        //反序，partItems中0-3的次序是反的
        Collections.reverse(this.partItems);
    }

    public Database getDatabase() {
        return new Database(getIndex(DATABASE_IDX));
    }

    public Table withDatabase(Database database) {
        setDatabase(database);
        return this;
    }

    public void setDatabase(Database database) {
        setIndex(DATABASE_IDX, database.getDatabaseName());
        if (database.getServer() != null) {
            setIndex(SERVER_IDX, database.getServer().getFullyQualifiedName());
        }
    }

    public String getSchemaName() {
        return getIndex(SCHEMA_IDX);
    }

    public Table withSchemaName(String schemaName) {
        setSchemaName(schemaName);
        return this;
    }

    public void setSchemaName(String schemaName) {
        setIndex(SCHEMA_IDX, schemaName);
    }

    public String getName() {
        return getIndex(NAME_IDX);
    }

    public Table withName(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        setIndex(NAME_IDX, name);
    }

    @Override
    public Alias getAlias() {
        return alias;
    }

    @Override
    public void setAlias(Alias alias) {
        this.alias = alias;
    }

    private void setIndex(int idx, String value) {
        int size = partItems.size();
        //先用null去补齐要插入的位置处前面空缺的数据
        for (int i = 0; i < idx - size + 1; i++) {
            partItems.add(null);
        }

        if (value == null && idx == partItems.size() - 1) {
            partItems.remove(idx);
        } else {
            partItems.set(idx, value);
        }
    }

    private String getIndex(int idx) {
        if (idx < partItems.size()) {
            return partItems.get(idx);
        } else {
            return null;
        }
    }

    @Override
    public String getFullyQualifiedName() {
        StringBuilder fqn = new StringBuilder();
        //反序遍历，拼接全限定名，使用点号分割
        for (int i = partItems.size() - 1; i >= 0; i--) {
            String part = partItems.get(i);
            if (part == null) {
                part = "";
            }
            fqn.append(part);
            if (i != 0) {
                fqn.append(".");
            }
        }

        return fqn.toString();
    }

    @Override
    public void accept(FromItemVisitor fromItemVisitor) {
        fromItemVisitor.visit(this);
    }

    public void accept(IntoTableVisitor intoTableVisitor) {
        intoTableVisitor.visit(this);
    }

    @Override
    public Pivot getPivot() {
        return pivot;
    }

    @Override
    public void setPivot(Pivot pivot) {
        this.pivot = pivot;
    }

    @Override
    public UnPivot getUnPivot() {
        return this.unpivot;
    }

    @Override
    public void setUnPivot(UnPivot unpivot) {
        this.unpivot = unpivot;
    }

    public MySQLIndexHint getIndexHint() {
        return mysqlHints;
    }

    public Table withHint(MySQLIndexHint hint) {
        setHint(hint);
        return this;
    }

    public void setHint(MySQLIndexHint hint) {
        this.mysqlHints = hint;
    }

    public SQLServerHints getSqlServerHints() {
        return sqlServerHints;
    }

    public void setSqlServerHints(SQLServerHints sqlServerHints) {
        this.sqlServerHints = sqlServerHints;
    }

    @Override
    public String toString() {
        return getFullyQualifiedName() + ((alias != null) ? alias.toString() : "")
                + ((pivot != null) ? " " + pivot : "") + ((unpivot != null) ? " " + unpivot : "")
                + ((mysqlHints != null) ? mysqlHints.toString() : "")
                + ((sqlServerHints != null) ? sqlServerHints.toString() : "");
    }

    @Override
    public Table withUnPivot(UnPivot unpivot) {
        return (Table) FromItem.super.withUnPivot(unpivot);
    }

    @Override
    public Table withAlias(Alias alias) {
        return (Table) FromItem.super.withAlias(alias);
    }

    @Override
    public Table withPivot(Pivot pivot) {
        return (Table) FromItem.super.withPivot(pivot);
    }

    public Table withSqlServerHints(SQLServerHints sqlServerHints) {
        this.setSqlServerHints(sqlServerHints);
        return this;
    }
}
