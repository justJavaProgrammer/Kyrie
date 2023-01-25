package com.odeyalo.kyrie.support.bpp.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import java.lang.reflect.Constructor;

/**
 * <p>Support abstract class that used to wrap the bean in wrapper class.</p>
 * <strong>NOTE:</strong> <p>The wrapper does not create a new bean, it simply creates a new object with all required parameters and returns it</p>
 *
 * The implementations can change their order through {@link #getOrder} method, by default all implementations have highest precedence.
 *
 * @see Ordered
 * @see BeanPostProcessor
 */
public abstract class AbstractDecoratorWrapperBeanPostProcessorSupport implements BeanPostProcessor, ApplicationContextAware, Ordered {
    protected ApplicationContext context;
    protected Logger logger = LoggerFactory.getLogger(AbstractDecoratorWrapperBeanPostProcessorSupport.class);

    /**
     * Abstract to make all implementation wrap object
     *
     * @param bean     - current bean
     * @param beanName - bean name
     * @return - wrapped object
     * @throws BeansException - if any exception occurred
     */
    @Override
    public abstract Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

    /**
     * Method to wrap the bean into other class, it automatically resolves all required constructor parameters and create object
     * @param beanToWrap - bean that must be wrapped
     * @param wrapper - wrapper class to create
     * @return - wrapped bean
     */
    protected Object wrap(Object beanToWrap, Class<?> wrapper) {
        // Resolve constructor and all required parameters
        Constructor<?> constructor = BeanUtils.getResolvableConstructor(wrapper);
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        // Contains beans to inject into wrapper
        Object[] args = new Object[parameterTypes.length];
        int index = 0;

        this.logger.info("Starting wrapping bean: {} to: {}", beanToWrap, wrapper.getName());
        // Iterate through all constructor parameters and put it in args array
        for (Class<?> parameterType : parameterTypes) {
            Object bean = parameterType.isAssignableFrom(beanToWrap.getClass()) ? beanToWrap : context.getBean(parameterType);
            this.logger.debug("Set argument parameter: {} with value: {}", parameterType.getName(), bean);
            args[index] = bean;
            index++;
        }

        try {
            Object wrapped = wrapper.getConstructor(parameterTypes).newInstance(args);
            this.logger.info("Successfully wrapped the bean to: {}", wrapped);
            return wrapped;
        } catch (Exception ex) {
            throw new BeanCreationException(String.format("Wrapper initialization has been failed. Cannot wrap bean class %s to: %s", beanToWrap.getClass().getName(), wrapper.getTypeName()), ex);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
