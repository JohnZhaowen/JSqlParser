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
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.IfElseStatement;
import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import net.sf.jsqlparser.statement.upsert.Upsert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class StatementDeParserTest {
    @Mock
    private ExpressionDeParser expressionDeParser;

    @Mock
    private SelectDeParser selectDeParser;

    private StatementDeParser statementDeParser;

    @Before
    public void setUp() {
        statementDeParser = new StatementDeParser(expressionDeParser, selectDeParser, new StringBuilder());
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeparsersWhenDeParsingDelete() {
        Delete delete = new Delete();
        Table table = new Table();
        Expression where = mock(Expression.class);
        List<OrderByElement> orderByElements = new ArrayList<OrderByElement>();
        OrderByElement orderByElement1 = new OrderByElement();
        OrderByElement orderByElement2 = new OrderByElement();
        Expression orderByElement1Expression = mock(Expression.class);
        Expression orderByElement2Expression = mock(Expression.class);

        delete.setTable(table);
        delete.setWhere(where);
        delete.setOrderByElements(orderByElements);
        orderByElements.add(orderByElement1);
        orderByElements.add(orderByElement2);
        orderByElement1.setExpression(orderByElement1Expression);
        orderByElement2.setExpression(orderByElement2Expression);

        statementDeParser.visit(delete);

        then(where).should().accept(expressionDeParser);
        then(orderByElement1Expression).should().accept(expressionDeParser);
        then(orderByElement2Expression).should().accept(expressionDeParser);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeparsersWhenDeParsingInsert() throws JSQLParserException {
        Insert insert = new Insert();
        Table table = new Table();
        List<Column> duplicateUpdateColumns = new ArrayList<Column>();
        List<Expression> duplicateUpdateExpressionList = new ArrayList<Expression>();
        Column duplicateUpdateColumn1 = new Column();
        Column duplicateUpdateColumn2 = new Column();
        Expression duplicateUpdateExpression1 = mock(Expression.class);
        Expression duplicateUpdateExpression2 = mock(Expression.class);
        Select select = new Select();
        List<WithItem> withItemsList = new ArrayList<WithItem>();
        WithItem withItem1 = spy(new WithItem());
        WithItem withItem2 = spy(new WithItem());
        SubSelect withItem1SubSelect = mock(SubSelect.class);
        SubSelect withItem2SubSelect = mock(SubSelect.class);
        SelectBody selectBody = mock(SelectBody.class);

        insert.setSelect(select);
        insert.setTable(table);
        insert.setUseDuplicate(true);
        insert.setDuplicateUpdateColumns(duplicateUpdateColumns);
        insert.setDuplicateUpdateExpressionList(duplicateUpdateExpressionList);
        duplicateUpdateColumns.add(duplicateUpdateColumn1);
        duplicateUpdateColumns.add(duplicateUpdateColumn2);
        duplicateUpdateExpressionList.add(duplicateUpdateExpression1);
        duplicateUpdateExpressionList.add(duplicateUpdateExpression2);
        insert.setDuplicateUpdateExpressionList(duplicateUpdateExpressionList);
        select.setWithItemsList(withItemsList);
        select.setSelectBody(selectBody);
        withItemsList.add(withItem1);
        withItemsList.add(withItem2);
        withItem1.setSubSelect(withItem1SubSelect);
        withItem2.setSubSelect(withItem2SubSelect);

        statementDeParser.visit(insert);
        
        then(withItem1).should().accept(selectDeParser);
        then(withItem2).should().accept(selectDeParser);
        then(selectBody).should().accept(selectDeParser);
        then(duplicateUpdateExpression1).should().accept(expressionDeParser);
        then(duplicateUpdateExpression1).should().accept(expressionDeParser);
    }
    
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeParsersWhenDeParsingReplaceWithoutItemsList() {
        Replace replace = new Replace();
        Table table = new Table();
        List<Column> columns = new ArrayList<Column>();
        List<Expression> expressions = new ArrayList<Expression>();
        Column column1 = new Column();
        Column column2 = new Column();
        Expression expression1 = mock(Expression.class);
        Expression expression2 = mock(Expression.class);

        replace.setTable(table);
        replace.setColumns(columns);
        replace.setExpressions(expressions);
        columns.add(column1);
        columns.add(column2);
        expressions.add(expression1);
        expressions.add(expression2);

        statementDeParser.visit(replace);

        then(expression1).should().accept(expressionDeParser);
        then(expression2).should().accept(expressionDeParser);
    }

//    @Test
//    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
//    public void shouldUseProvidedDeParsersWhenDeParsingReplaceWithItemsList() {
//        Replace replace = new Replace();
//        Table table = new Table();
//        ItemsList itemsList = mock(ItemsList.class);
//
//        replace.setTable(table);
//        replace.setItemsList(itemsList);
//
//        statementDeParser.visit(replace);
//
//        then(itemsList).should().accept(argThat(is(replaceDeParserWithDeParsers(equalTo(expressionDeParser), equalTo(selectDeParser)))));
//    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeParsersWhenDeParsingSelect() {
        Select select = new Select();
        WithItem withItem1 = spy(new WithItem());
        WithItem withItem2 = spy(new WithItem());
        SelectBody selectBody = mock(SelectBody.class);
        List<WithItem> withItemsList = new ArrayList<WithItem>();

        select.setWithItemsList(withItemsList);
        select.setSelectBody(selectBody);
        withItemsList.add(withItem1);
        withItemsList.add(withItem2);

        statementDeParser.visit(select);

        then(withItem1).should().accept(selectDeParser);
        then(withItem2).should().accept(selectDeParser);
        then(selectBody).should().accept(selectDeParser);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeParsersWhenDeParsingUpdateNotUsingSelect() {
        Update update = new Update();
        Expression where = mock(Expression.class);
        List<OrderByElement> orderByElements = new ArrayList<OrderByElement>();
        Column column1 = new Column();
        Column column2 = new Column();
        Expression expression1 = mock(Expression.class);
        Expression expression2 = mock(Expression.class);
        OrderByElement orderByElement1 = new OrderByElement();
        OrderByElement orderByElement2 = new OrderByElement();
        Expression orderByElement1Expression = mock(Expression.class);
        Expression orderByElement2Expression = mock(Expression.class);

        update.setWhere(where);
        update.setOrderByElements(orderByElements);

        update.addUpdateSet(column1, expression1);
        update.addUpdateSet(column2, expression2);

        orderByElements.add(orderByElement1);
        orderByElements.add(orderByElement2);
        orderByElement1.setExpression(orderByElement1Expression);
        orderByElement2.setExpression(orderByElement2Expression);

        statementDeParser.visit(update);

        then(expressionDeParser).should().visit(column1);
        then(expressionDeParser).should().visit(column2);
        then(expression1).should().accept(expressionDeParser);
        then(expression2).should().accept(expressionDeParser);
        then(where).should().accept(expressionDeParser);
        then(orderByElement1Expression).should().accept(expressionDeParser);
        then(orderByElement2Expression).should().accept(expressionDeParser);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeParsersWhenDeParsingUpdateUsingSelect() {
        Update update = new Update();
        List<Column> columns = new ArrayList<Column>();
        Expression where = mock(Expression.class);
        List<OrderByElement> orderByElements = new ArrayList<OrderByElement>();
        Column column1 = new Column();
        Column column2 = new Column();
        SelectBody selectBody = mock(SelectBody.class);
        OrderByElement orderByElement1 = new OrderByElement();
        OrderByElement orderByElement2 = new OrderByElement();
        Expression orderByElement1Expression = mock(Expression.class);
        Expression orderByElement2Expression = mock(Expression.class);

        update.setWhere(where);
        update.setOrderByElements(orderByElements);

        SubSelect subSelect = new SubSelect().withSelectBody(selectBody);
        ExpressionList expressionList = new ExpressionList().addExpressions(subSelect);

        UpdateSet updateSet=new UpdateSet();
        updateSet.add(column1);
        updateSet.add(column2);
        updateSet.add(expressionList);

        update.addUpdateSet(updateSet);

        orderByElements.add(orderByElement1);
        orderByElements.add(orderByElement2);
        orderByElement1.setExpression(orderByElement1Expression);
        orderByElement2.setExpression(orderByElement2Expression);

        statementDeParser.visit(update);

        then(expressionDeParser).should().visit(column1);
        then(expressionDeParser).should().visit(column2);
        then(expressionDeParser).should().visit(subSelect);
        then(where).should().accept(expressionDeParser);
        then(orderByElement1Expression).should().accept(expressionDeParser);
        then(orderByElement2Expression).should().accept(expressionDeParser);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeParserWhenDeParsingExecute() {
        Execute execute = new Execute();
        ExpressionList exprList = new ExpressionList();
        List<Expression> expressions = new ArrayList<Expression>();
        Expression expression1 = mock(Expression.class);
        Expression expression2 = mock(Expression.class);

        execute.setExprList(exprList);
        exprList.setExpressions(expressions);
        expressions.add(expression1);
        expressions.add(expression2);

        statementDeParser.visit(execute);

        then(expression1).should().accept(expressionDeParser);
        then(expression2).should().accept(expressionDeParser);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeParserWhenDeParsingSetStatement() {
        String name = "name";
        Expression expression = mock(Expression.class);
        ArrayList<Expression> expressions = new ArrayList<>();
        expressions.add(expression);

        SetStatement setStatement = new SetStatement(name, expressions);

        statementDeParser.visit(setStatement);

        then(expression).should().accept(expressionDeParser);
    }

//    private Matcher<ReplaceDeParser> replaceDeParserWithDeParsers(final Matcher<ExpressionDeParser> expressionDeParserMatcher, final Matcher<SelectDeParser> selectDeParserMatcher) {
//        Description description = new StringDescription();
//        description.appendText("replace de-parser with expression de-parser ");
//        expressionDeParserMatcher.describeTo(description);
//        description.appendText(" and select de-parser ");
//        selectDeParserMatcher.describeTo(description);
//        return new CustomTypeSafeMatcher<ReplaceDeParser>(description.toString()) {
//            @Override
//            public boolean matchesSafely(ReplaceDeParser item) {
//                return expressionDeParserMatcher.matches(item.getExpressionVisitor()) && selectDeParserMatcher.matches(item.getSelectVisitor());
//            }
//        };
//    }
    
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeparsersWhenDeParsingUpsertWithExpressionList() throws JSQLParserException {
        Upsert upsert = new Upsert();
        Table table = new Table();
        List<Column> duplicateUpdateColumns = new ArrayList<Column>();
        List<Expression> duplicateUpdateExpressionList = new ArrayList<Expression>();
        Column duplicateUpdateColumn1 = new Column();
        Column duplicateUpdateColumn2 = new Column();
        Expression duplicateUpdateExpression1 = mock(Expression.class);
        Expression duplicateUpdateExpression2 = mock(Expression.class);
        Select select = new Select();
        List<WithItem> withItemsList = new ArrayList<WithItem>();
        WithItem withItem1 = spy(new WithItem());
        WithItem withItem2 = spy(new WithItem());
        SubSelect withItem1SubSelect = mock(SubSelect.class);
        SubSelect withItem2SubSelect = mock(SubSelect.class);
        SelectBody selectBody = mock(SelectBody.class);

        upsert.setSelect(select);
        upsert.setTable(table);
        upsert.setUseDuplicate(true);
        upsert.setDuplicateUpdateColumns(duplicateUpdateColumns);
        upsert.setDuplicateUpdateExpressionList(duplicateUpdateExpressionList);
        duplicateUpdateColumns.add(duplicateUpdateColumn1);
        duplicateUpdateColumns.add(duplicateUpdateColumn2);
        duplicateUpdateExpressionList.add(duplicateUpdateExpression1);
        duplicateUpdateExpressionList.add(duplicateUpdateExpression2);
        upsert.setDuplicateUpdateExpressionList(duplicateUpdateExpressionList);
        select.setWithItemsList(withItemsList);
        select.setSelectBody(selectBody);
        withItemsList.add(withItem1);
        withItemsList.add(withItem2);
        withItem1.setSubSelect(withItem1SubSelect);
        withItem2.setSubSelect(withItem2SubSelect);

        statementDeParser.visit(upsert);

        then(withItem1).should().accept(selectDeParser);
        then(withItem2).should().accept(selectDeParser);
        then(selectBody).should().accept(selectDeParser);
        then(duplicateUpdateExpression1).should().accept(expressionDeParser);
        then(duplicateUpdateExpression1).should().accept(expressionDeParser);
    }
    
    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void shouldUseProvidedDeparsersWhenDeParsingIfThenStatement() throws JSQLParserException {
        String sqlStr = "IF OBJECT_ID('tOrigin', 'U') IS NOT NULL DROP TABLE tOrigin1";
        IfElseStatement ifElseStatement  = (IfElseStatement) CCJSqlParserUtil.parse(sqlStr);
        statementDeParser.deParse(ifElseStatement);
    }
    
}
