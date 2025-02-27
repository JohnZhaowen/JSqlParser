/*-
 * #%L
 * JSQLParser library
 * %%
 * Copyright (C) 2004 - 2020 JSQLParser
 * %%
 * Dual licensed under GNU LGPL 2.1 or Apache License 2.0
 * #L%
 */
package net.sf.jsqlparser.util.validation.validator;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.feature.Feature;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.util.validation.ValidationCapability;
import net.sf.jsqlparser.util.validation.ValidationContext;
import net.sf.jsqlparser.util.validation.ValidationException;
import net.sf.jsqlparser.util.validation.Validator;
import net.sf.jsqlparser.util.validation.feature.FeatureContext;
import net.sf.jsqlparser.util.validation.feature.FeatureSetValidation;
import net.sf.jsqlparser.util.validation.metadata.DatabaseMetaDataValidation;
import net.sf.jsqlparser.util.validation.metadata.MetadataContext;
import net.sf.jsqlparser.util.validation.metadata.Named;
import net.sf.jsqlparser.util.validation.metadata.NamedObject;

/**
 * A abstract base for a Validation
 * 所有的校验器都继承了该抽象类
 *
 * 1.最为抽象的校验器方法
 * 2.expression的校验方法
 * 3.各种具体expression的校验方法
 *
 * 4.Feature校验器
 * 5.Name校验器
 * 6.FeatureAndName校验器
 *
 * @param <S> the type of statement this DeParser supports
 * @author gitmotte
 */
public abstract class AbstractValidator<S> implements Validator<S> {

    //====================字段==============================

    private ValidationContext context = new ValidationContext();

    /**
     * 用于保存每种ValidationCapability对应的error集合
     */
    private Map<ValidationCapability, Set<ValidationException>> errors = new HashMap<>();

    /**
     * 根据AbstractValidator的class类型，映射到对应的AbstractValidator上
     */
    private Map<Class<? extends AbstractValidator<?>>, AbstractValidator<?>> validatorForwards = new HashMap<>();

    //====================方法====================================

    @Override
    public final void setContext(ValidationContext context) {
        this.context = context;
    }

    @Override
    public final Map<ValidationCapability, Set<ValidationException>> getValidationErrors() {
        Map<ValidationCapability, Set<ValidationException>> map = new HashMap<>();
        map.putAll(errors);
        for (AbstractValidator<?> v : validatorForwards.values()) {
            for (Entry<ValidationCapability, Set<ValidationException>> e : v.getValidationErrors().entrySet()) {
                Set<ValidationException> set = map.get(e.getKey());
                if (set == null) {
                    map.put(e.getKey(), e.getValue());
                } else {
                    set.addAll(e.getValue());
                }
            }
        }
        return map;
    }

    public <T extends AbstractValidator<?>> T getValidator(Class<T> type) {
        return type.cast(validatorForwards.computeIfAbsent(type, this::newObject));
    }

    private <E extends Validator<?>> E newObject(Class<E> type) {
        try {
            E e = type.cast(type.getConstructor().newInstance());
            e.setContext(context());
            return e;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Type " + type + " cannot be constructed by empty constructor!");
        }
    }

    protected ValidationContext context() {
        return context(true);
    }

    protected ValidationContext context(boolean reInit) {
        return context.reinit(reInit);
    }

    protected Consumer<ValidationException> getMessageConsumer(ValidationCapability c) {
        return s -> putError(c, s);
    }

    /**
     * adds an error for this {@link ValidationCapability}
     */
    protected void putError(ValidationCapability capability, ValidationException error) {
        errors.computeIfAbsent(capability, k -> new HashSet<>()).add(error);
    }

    public Collection<ValidationCapability> getCapabilities() {
        return context().getCapabilities();
    }


    //======================抽象校验器，校验的对象element，校验器consumer========================
    protected <E> void validateOptional(E element, Consumer<E> elementConsumer) {
        if (element != null) {
            elementConsumer.accept(element);
        }
    }

    protected <E, V extends Validator<?>> void validateOptionalList(List<E> elementList, Supplier<V> validatorSupplier, BiConsumer<E, V> elementConsumer) {
        if (isNotEmpty(elementList)) {
            V validator = validatorSupplier.get();
            elementList.forEach(e -> elementConsumer.accept(e, validator));
        }
    }


    //=====================expression校验器===========================
    protected void validateOptionalExpression(Expression expression) {
        validateOptional(expression, e -> e.accept(getValidator(ExpressionValidator.class)));
    }

    protected void validateOptionalExpression(Expression expression, ExpressionValidator v) {
        validateOptional(expression, e -> e.accept(v));
    }

    /**
     * a multi-expression in clause: {@code ((a, b), (c, d))}
     */
    protected void validateOptionalMultiExpressionList(MultiExpressionList multiExprList) {
        if (multiExprList != null) {
            ExpressionValidator v = getValidator(ExpressionValidator.class);
            multiExprList.getExpressionLists().stream().map(ExpressionList::getExpressions).flatMap(List::stream)
                    .forEach(e -> e.accept(v));
        }
    }

    protected void validateOptionalExpressions(List<? extends Expression> expressions) {
        validateOptionalList(expressions, () -> getValidator(ExpressionValidator.class), (o, v) -> o.accept(v));
    }

    //==================各种具体的校验器===================

    /**
     * from
     * @param fromItems
     */
    protected void validateOptionalFromItems(FromItem... fromItems) {
        validateOptionalFromItems(Arrays.asList(fromItems));
    }

    protected void validateOptionalFromItems(List<? extends FromItem> fromItems) {
        validateOptionalList(fromItems, () -> getValidator(SelectValidator.class),
                this::validateOptionalFromItem);
    }

    /**
     * order by
     * @param orderByElements
     */
    protected void validateOptionalOrderByElements(List<OrderByElement> orderByElements) {
        validateOptionalList(orderByElements, () -> getValidator(OrderByValidator.class), (o, v) -> o.accept(v));
    }

    protected void validateOptionalFromItem(FromItem fromItem) {
        validateOptional(fromItem, i -> i.accept(getValidator(SelectValidator.class)));
    }

    protected void validateOptionalFromItem(FromItem fromItem, SelectValidator v) {
        validateOptional(fromItem, i -> i.accept(v));
    }

    protected void validateOptionalItemsList(ItemsList itemsList) {
        validateOptional(itemsList, i -> i.accept(getValidator(ItemsListValidator.class)));
    }


    /**
     * 以下分成3大类，一类是Feature相关的校验，一类是named相关的校验，一类是named+feature
     * 开始用到context和capability
     */

    //==================================Feature相关============================================================

    /**
     * Validates if given {@link ValidationCapability} is a
     * {@link FeatureSetValidation}
     *
     */
    protected void validateFeature(ValidationCapability capability, Feature feature) {
        if (capability instanceof FeatureSetValidation) {
            capability.validate(context().put(FeatureContext.feature, feature),
                    getMessageConsumer(capability));
        }
    }


    /**
     * Iterates through all {@link ValidationCapability} and validates the feature
     * with {@link #validateFeature(ValidationCapability, Feature)}
     *
     */
    protected void validateFeature(Feature feature) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, feature);
        }
    }

    /**
     * Validates the feature if given {@link ValidationCapability} is a
     * {@link FeatureSetValidation} and condition is <code>true</code>
     *
     */
    protected void validateFeature(ValidationCapability capability, boolean condition, Feature feature) {
        if (condition) {
            validateFeature(capability, feature);
        }
    }

    /**
     * validates for the feature if given elements is not empty - see
     * {@link #isNotEmpty(Collection)}
     *
     */
    protected void validateOptionalFeature(ValidationCapability capability, List<?> elements, Feature feature) {
        validateFeature(capability, isNotEmpty(elements), feature);
    }

    /**
     * Validates for the feature if given element is not <code>null</code>
     *
     */
    protected void validateOptionalFeature(ValidationCapability capability, Object element, Feature feature) {
        validateFeature(capability, element != null, feature);
    }

    //========================================Named相关======================================================

    /**
     * @param fqn - fully qualified name of named object
     */
    protected void validateName(ValidationCapability capability, NamedObject namedObject, String fqn) {
        validateNameWithAlias(capability, namedObject, fqn, null, true);
    }

    /**
     * @param fqn - fully qualified name of named object
     */
    protected void validateName(ValidationCapability capability, NamedObject namedObject, String fqn, boolean exists,
                                NamedObject... parents) {
        validateNameWithAlias(capability, namedObject, fqn, null, exists, parents);
    }


    /**
     * Iterates through all {@link ValidationCapability} and validates for the name
     * with {@link #validateName(ValidationCapability, NamedObject, String)}
     *
     * @param fqn - fully qualified name of named object
     */
    protected void validateName(NamedObject namedObject, String fqn) {
        validateNameWithAlias(namedObject, fqn, null);
    }


    protected void validateOptionalName(ValidationCapability capability, NamedObject namedObject, String name,
                                        NamedObject... parents) {
        validateOptionalNameWithAlias(capability, namedObject, name, (String) null, parents);
    }

    protected void validateOptionalName(ValidationCapability capability, NamedObject namedObject, String name,
                                        String alias, boolean exists, NamedObject... parents) {
        if (name != null) {
            validateNameWithAlias(capability, namedObject, name, alias, exists, parents);
        }
    }

    /**
     * Validates if given {@link ValidationCapability} is a
     * {@link DatabaseMetaDataValidation}
     *
     * @param fqn - fully qualified name of named object
     * @param exists      - <code>true</code>, check for existence,
     *                    <code>false</code>, check for non-existence
     */
    protected void validateNameWithAlias(ValidationCapability capability, NamedObject namedObject, String fqn, String alias,
                                         boolean exists, NamedObject... parents) {
        if (capability instanceof DatabaseMetaDataValidation) {
            capability.validate(context()
                            .put(MetadataContext.named,
                                    new Named(namedObject, fqn).setAlias(alias).setParents(Arrays.asList(parents))) //
                            .put(MetadataContext.exists, exists),
                    getMessageConsumer(capability));
        }
    }

    /**
     * Iterates through all {@link ValidationCapability} and validates for the name
     * with {@link #validateName(ValidationCapability, NamedObject, String)}
     *
     * @param fqn - fully qualified name of named object
     */
    protected void validateNameWithAlias(NamedObject namedObject, String fqn, String alias) {
        for (ValidationCapability c : getCapabilities()) {
            validateNameWithAlias(c, namedObject, fqn, alias, true);
        }
    }

    /**
     * Validates if given {@link ValidationCapability} is a
     * {@link DatabaseMetaDataValidation}
     *
     * @param fqn - fully qualified name of named object
     */
    protected void validateNameWithAlias(ValidationCapability capability, NamedObject namedObject, String fqn, String alias) {
        validateNameWithAlias(capability, namedObject, fqn, alias, true);
    }


    protected void validateOptionalNameWithAlias(ValidationCapability capability, NamedObject namedObject, String name,
                                                 String alias, NamedObject... parents) {
        validateOptionalName(capability, namedObject, name, alias, true, parents);
    }


    protected void validateOptionalColumnName(ValidationCapability capability, String name) {
        validateOptionalName(capability, NamedObject.column, name, null, true);
    }

    protected void validateOptionalColumnNames(ValidationCapability capability, List<String> columnNames,
                                               NamedObject... parents) {
        validateOptionalColumnNames(capability, columnNames, true, parents);
    }

    protected void validateOptionalColumnNames(ValidationCapability capability, List<String> columnNames,
                                               boolean exists, NamedObject... parents) {
        if (columnNames != null) {
            columnNames.forEach(n -> validateOptionalName(capability, NamedObject.column, n, null, exists, parents));
        }
    }

    protected void validateOptionalColumnNameWithAlias(ValidationCapability capability, String name, String alias) {
        validateOptionalName(capability, NamedObject.column, name, alias, true);
    }

    //===============================FeatureAndName=============================================
    /**
     * Iterates through all {@link ValidationCapability} and validates
     * <ul>
     * <li>the name with
     * {@link #validateName(ValidationCapability, NamedObject, String)}</li>
     * <li>the feature with
     * {@link #validateFeature(ValidationCapability, Feature)}</li>
     * </ul>
     *
     * @param fqn - fully qualified name of named object
     */
    protected void validateFeatureAndName(Feature feature, NamedObject namedObject, String fqn) {
        validateFeatureAndNameWithAlias(feature, namedObject, fqn, null);
    }

    /**
     * Iterates through all {@link ValidationCapability} and validates
     * <ul>
     * <li>the name with
     * {@link #validateName(ValidationCapability, NamedObject, String)}</li>
     * <li>the feature with
     * {@link #validateFeature(ValidationCapability, Feature)}</li>
     * </ul>
     *
     * @param fqn - fully qualified name of named object
     *
     * 如果既校验Feature，又校验Name，那么就依次调用校验Feature和Name的方法
     */
    protected void validateFeatureAndNameWithAlias(Feature feature, NamedObject namedObject, String fqn, String alias) {
        for (ValidationCapability c : getCapabilities()) {
            validateFeature(c, feature);
            validateNameWithAlias(c, namedObject, fqn, alias, true);
        }
    }


    //================================工具类===================================
    protected boolean isNotEmpty(Collection<?> c) {
        return c != null && !c.isEmpty();
    }

    protected boolean isNotEmpty(String c) {
        return c != null && !c.isEmpty();
    }

}
