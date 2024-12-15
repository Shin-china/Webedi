package customer.service.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringUtil.class);

    @Autowired
    private ApplicationContext context;

    /**
     * 获取 Spring Bean
     * @param clazz 类
     * @param <T>   泛型
     * @return 对象
     */
    public <T> T getBean(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        return context.getBean(clazz);
    }

    /**
     * 获取 Spring Bean
     * @param bean 名称
     * @param <T>  泛型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(String bean) {
        if (bean == null) {
            return null;
        }
        return (T) context.getBean(bean);
    }

    /**
     * 获取 Spring Bean
     * @param beanName 名称
     * @param clazz    类
     * @param <T>      泛型
     * @return 对象
     */
    public <T> T getBean(String beanName, Class<T> clazz) {
        if (null == beanName || "".equals(beanName.trim())) {
            return null;
        }
        if (clazz == null) {
            return null;
        }
        return (T) context.getBean(beanName, clazz);
    }

    /**
     * 获取上下文
     * @return 上下文
     */
    public ApplicationContext getContext() {
        if (context == null) {
            throw new RuntimeException("There has no Spring ApplicationContext!");
        }
        return context;
    }

    /**
     * 发布事件
     * @param event 事件
     */
    public void publishEvent(ApplicationEvent event) {
        if (context == null) {
            return;
        }
        try {
            context.publishEvent(event);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }
    }

}
