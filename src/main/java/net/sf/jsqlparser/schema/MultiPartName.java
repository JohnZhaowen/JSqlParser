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

/**
 * 所有schema的基础接口
 */
public interface MultiPartName {

    /**
     * 获取全限定名
     * @return
     */
    String getFullyQualifiedName();
}
