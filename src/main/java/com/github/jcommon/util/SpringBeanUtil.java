package com.github.jcommon.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * SpringBean工具类
 *
 * @author shijian
 * @email shijianws@163.com
 * @date 2019-12-29
 */
@Configuration
public class SpringBeanUtil implements ApplicationContextAware {
    /**
     * BeanFactory实例
     */
    private static ApplicationContext APPLICATION_CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 获取对象
     */
    public static <T> T getBean(String beanName) {
        return getBean(beanName, APPLICATION_CONTEXT);
    }

    /**
     * 获取对象
     */
    public static <T> T getBean(Class<T> resultType) {
        return getBean(resultType, APPLICATION_CONTEXT);
    }

    /**
     * 获取一组对象
     */
    public static <T> Map<String, T> getBeans(Class<T> resultType) {
        return getBeans(resultType, APPLICATION_CONTEXT);
    }

    /**
     * 注册一个Bean
     */
    public static <T> void registerBeanIfNotExists(T bean, String beanName) {
        if (APPLICATION_CONTEXT instanceof ConfigurableBeanFactory) {
            registerBeanIfNotExists(bean, beanName, (ConfigurableBeanFactory) APPLICATION_CONTEXT);
        }
    }

    /**
     * 注册一个Bean描述
     */
    public static <T> void registerBeanDefinitionIfNotExists(Class<T> beanType, String beanName) {
        if (APPLICATION_CONTEXT instanceof BeanDefinitionRegistry) {
            registerBeanDefinitionIfNotExists(beanType, beanName, (BeanDefinitionRegistry) APPLICATION_CONTEXT);
        }
    }

    /**
     * 注册一个Bean描述
     */
    public static <T> void registerBeanDefinitionIfNotExists(Class<T> beanType) {
        if (APPLICATION_CONTEXT instanceof BeanDefinitionRegistry) {
            registerBeanDefinitionIfNotExists(beanType, (BeanDefinitionRegistry) APPLICATION_CONTEXT);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getBean(String beanName, BeanFactory factory) {
        Assert.notBlank(beanName, "beanName不能为空");

        if (factory != null) {
            try {
                return (T) factory.getBean(beanName);
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        return null;
    }

    private static <T> T getBean(Class<T> resultType, BeanFactory factory) {
        Assert.notNull(resultType, "resultType不能为空");

        if (factory != null) {
            try {
                return factory.getBean(resultType);
            } catch (NoSuchBeanDefinitionException e) {
            }
        }
        return null;
    }

    private static <T> Map<String, T> getBeans(Class<T> resultType, ListableBeanFactory factory) {
        Assert.notNull(resultType, "resultType不能为空");

        if (factory != null) {
            return BeanFactoryUtils.beansOfTypeIncludingAncestors(factory, resultType);
        }
        return Collections.emptyMap();
    }

    private static <T> void registerBeanIfNotExists(T bean, String beanName, ConfigurableBeanFactory factory) {
        Assert.notNull(bean, "bean不能为空");
        Assert.notBlank(beanName, "beanName不能为空");

        if (factory.containsBean(beanName)) {
            // 已存在
            return;
        }

        factory.registerSingleton(beanName, bean);
    }

    private static <T> void registerBeanDefinitionIfNotExists(Class<T> beanType, String beanName, BeanDefinitionRegistry registry) {
        Assert.notNull(beanType, "beanType不能为空");
        Assert.notNull(registry, "registry不能为空");
        if (StringUtil.isBlank(beanName)) {
            registerBeanDefinitionIfNotExists(beanType, registry);
            return;
        }

        if (registry.containsBeanDefinition(beanName)) {
            // 已存在
            return;
        }

        for (String definitionName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(definitionName);
            if (Objects.equals(beanDefinition.getBeanClassName(), beanType.getName())) {
                // 已存在
                return;
            }
        }

        AbstractBeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(beanType).getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(definition, registry);
    }

    private static <T> void registerBeanDefinitionIfNotExists(Class<T> beanType, BeanDefinitionRegistry registry) {
        Assert.notNull(beanType, "beanType不能为空");
        Assert.notNull(registry, "registry不能为空");

        for (String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (Objects.equals(beanDefinition.getBeanClassName(), beanType.getName())) {
                // 已存在
                return;
            }
        }

        AbstractBeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(beanType).getBeanDefinition();
        BeanDefinitionReaderUtils.registerWithGeneratedName(definition, registry);
    }


}
