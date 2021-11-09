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

import net.sf.jsqlparser.JSQLParserException;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TimestampValueTest {

    @Test
    public void testTimestampValue_issue525() throws JSQLParserException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        TimestampValue tv = new TimestampValue(currentDate);
        System.out.println(tv.toString());
        assertEquals(currentDate, tv.getRawValue());
    }

    @Test
    public void testTimestampValueWithQuotation_issue525() throws JSQLParserException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        TimestampValue tv = new TimestampValue("'" + currentDate + "'");
        System.out.println(tv.toString());
        assertEquals("'" + currentDate + "'", tv.getRawValue());
    }

    @Test
    public void testTimestampValueToString() {
        String dateStr = "2021-11-06 13:23:37.334";
        TimestampValue tv = new TimestampValue(dateStr);
        assertEquals("{ts '2021-11-06 13:23:37.334'}", tv.toString());
    }
}
