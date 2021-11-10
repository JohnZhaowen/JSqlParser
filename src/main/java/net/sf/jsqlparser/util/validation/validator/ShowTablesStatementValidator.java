/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2019 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.validation.validator;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.show.ShowTablesStatement;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.ValidationContext;
import net.sf.jsqlparser.util.validation.ValidationException;
import net.sf.jsqlparser.util.validation.feature.DatabaseType;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 校验show tables是否被支持
 *
 * @author gitmotte
 */
public class ShowTablesStatementValidator extends AbstractValidator<ShowTablesStatement> {

    @Override
    public void validate(ShowTablesStatement show) {
        validateFeatureAndName(Feature.showTables, NamedObject.database, show.getDbName());
    }

    public static void main(String[] args) throws JSQLParserException {
//        String sql = "SHOW TABLES like 'cms'";
        String sql =
                "SHOW TABLES" +
                " FROM myDb";

        ShowTablesStatement stmt = (ShowTablesStatement) CCJSqlParserUtil.parse(sql);

        ShowTablesStatementValidator validator = new ShowTablesStatementValidator();
        validator.setContext(new ValidationContext()
                .setCapabilities(Arrays.asList(DatabaseType.SQLSERVER, DatabaseType.POSTGRESQL, DatabaseType.MYSQL)));

        validator.validate(stmt);

        Map<ValidationCapability, Set<ValidationException>> unsupportedErrors = validator
                .getValidationErrors();
        //{SQLSERVER=[ValidationException: showTables not supported.], POSTGRESQL=[ValidationException: showTables not supported.]}
        System.out.println(unsupportedErrors);
    }

}
