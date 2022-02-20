package com.github.jcommon.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JDK注解验证工具类型
 *
 * @author 史健
 * @Email shijianws@163.com
 * @Date 2019-02-01
 */
public final class ValidateUtil {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    /**
     * 未指定分组检查的默认参数
     */
    private static final Class<?>[] DEFAULT_GROUP_ARRAY = {};

    /**
     * 检查
     */
    public static void validate(Object bean) {
        validate(bean, DEFAULT_GROUP_ARRAY);
    }

    /**
     * 检查指定分组类型
     *
     * @param bean   需要检查的对象
     * @param groups 检查对象的指定分组注解的属性值, 为空(null或length=0)则检查所有注解,
     *               该参数只是一种标识, 一个空接口即可, {@link javax.validation.groups.Default}
     *               {@link com.github.jcommon.validation.groups.Save,com.github.jcommon.validation.groups.Update,com.github.jcommon.validation.groups.Query}
     */
    public static void validate(Object bean, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = VALIDATOR.validate(bean, groups == null ? DEFAULT_GROUP_ARRAY : groups);
        if (Safes.isEmpty(constraintViolations)) {
            return;
        }
        throw new IllegalArgumentException(constraintViolations.stream().map(ConstraintViolation::getMessage).distinct().collect(Collectors.joining(",")));
    }

    private ValidateUtil() throws IllegalAccessException {
        throw new IllegalAccessException("不允许实例化");
    }
}
