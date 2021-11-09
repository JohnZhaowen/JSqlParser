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

import net.sf.jsqlparser.expression.StringValue;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Tobias Warneke (t.warneke@gmx.net)
 */
public class LikeExpressionTest {

    @Test
    public void testLikeNotIssue660() {
        LikeExpression instance = new LikeExpression();
        assertFalse(instance.isNot());
        assertTrue(instance.withNot(true).isNot());
    }

    @Test
    public void testLike() {
        LikeExpression instance = new LikeExpression();
        instance.withLeftExpression(new StringValue("name"))
                .withCaseInsensitive(true)
                .withEscape("ss")
                .withRightExpression(new StringValue("LiSi"));
        assertEquals("'name' ILIKE 'LiSi' ESCAPE 'ss'", instance.toString());
    }
}
