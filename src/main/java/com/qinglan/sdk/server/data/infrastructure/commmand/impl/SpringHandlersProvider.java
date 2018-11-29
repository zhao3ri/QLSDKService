package com.qinglan.sdk.server.data.infrastructure.commmand.impl;

import com.qinglan.sdk.server.data.infrastructure.commmand.ICommandHandler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Component
public class SpringHandlersProvider implements CommandContext.HandlersProvider, ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private ConfigurableListableBeanFactory beanFactory;
    private Map<Class<?>, String> handlers = new HashMap();

    public SpringHandlersProvider() {
    }

    public ICommandHandler<Object, Object> getHandler(Object command) {
        String beanName = (String)handlers.get(command.getClass());
        if (beanName == null) {
            throw new RuntimeException("command handler not found. Command class is " + command.getClass());
        } else {
            ICommandHandler<Object, Object> handler = (ICommandHandler)beanFactory.getBean(beanName, ICommandHandler.class);
            return handler;
        }
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        handlers.clear();
        String[] commandHandlersNames = beanFactory.getBeanNamesForType(ICommandHandler.class);

        for(int i = 0; i < commandHandlersNames.length; ++i) {
            String beanName = commandHandlersNames[i];
            BeanDefinition commandHandler = beanFactory.getBeanDefinition(beanName);

            try {
                Class<?> handlerClass = Class.forName(commandHandler.getBeanClassName());
                this.handlers.put(this.getHandledCommandType(handlerClass), beanName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private Class<?> getHandledCommandType(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        ParameterizedType type = this.findByRawType(genericInterfaces, ICommandHandler.class);
        return (Class)type.getActualTypeArguments()[0];
    }

    private ParameterizedType findByRawType(Type[] genericInterfaces, Class<?> expectedRawType) {
        for(int i = 0; i < genericInterfaces.length; ++i) {
            Type type = genericInterfaces[i];
            if (type instanceof ParameterizedType) {
                ParameterizedType parametrized = (ParameterizedType)type;
                if (expectedRawType.equals(parametrized.getRawType())) {
                    return parametrized;
                }
            }
        }

        throw new RuntimeException();
    }
}
