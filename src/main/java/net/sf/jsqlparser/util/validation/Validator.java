/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2020 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.validation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

/**
 * @author gitmotte
 * @param <S>
 *  1. 设置上下文：setContext
 *  2. 校验：validate
 *  3. 是否有效：isValid
 *  4. 如果校验失败，则获取检验的报错信息：getValidationErrors
 *
 */
public interface Validator<S> {

    /**
     * @param ctx
     */
    void setContext(ValidationContext ctx);

    /**
     * validates given statement.
     *
     * @param statement
     * @see #getValidationErrors()
     * @see #getValidationErrors(Collection)
     * @see #getValidationErrors(ValidationCapability...)
     */
    void validate(S statement);

    /**
     * 根据校验结果判断校验对象是否有效，没有入参，则表示只要有报错信息则非有效
     * @return <code>true</code>, all {@link ValidationCapability}'s have no errors
     */
    default boolean isValid() {
        return getValidationErrors().isEmpty();
    }

    /**
     * 根据校验结果判断校验对象是否有效，根据ValidationCapability判断是否有报错信息
     * @param capabilities
     * @return <code>true</code>, if the given {@link ValidationCapability}'s have no errors.
     *         <code>false</code> otherwise.
     */
    default boolean isValid(ValidationCapability... capabilities) {
        return getValidationErrors(capabilities).isEmpty();
    }

    /**
     * 获取所有的校验报错信息
     * @return the {@link ValidationCapability}'s requested mapped to a set of error-messages
     */
    Map<ValidationCapability, Set<ValidationException>> getValidationErrors();

    /**
     * 获取指定capabilities的报错信息
     * @param capabilities
     * @return the filtered view of requested {@link ValidationCapability}'s mapped to a set
     *         of error-messages
     */
    default Map<ValidationCapability, Set<ValidationException>> getValidationErrors(
            ValidationCapability... capabilities) {
        return getValidationErrors(Arrays.asList(capabilities));
    }

    /**
     * 获取指定capabilities的报错信息
     * @param capabilities
     * @return the filtered view of requested {@link ValidationCapability}'s mapped
     *         to a set of error-messages
     */
    default Map<ValidationCapability, Set<ValidationException>> getValidationErrors(
            Collection<ValidationCapability> capabilities) {
        Map<ValidationCapability, Set<ValidationException>> map = new HashMap<>();
        for (Entry<ValidationCapability, Set<ValidationException>> e : getValidationErrors().entrySet()) {
            if (capabilities.contains(e.getKey())) {
                map.put(e.getKey(), e.getValue());
            }
        }
        return map;
    }

}
