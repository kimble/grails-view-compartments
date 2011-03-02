package grails.plugin.viewcompartment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.servlet.ViewResolver;

/**
 * Replaces jspViewResolver (ScaffoldingViewResolver) with our CompartmentAwareViewResovler
 * @author Kim A. Betti
 */
public class ViewResolverPostProcessor implements BeanPostProcessor {

    private final static Logger log = LoggerFactory.getLogger(ViewResolverPostProcessor.class);

    private static final String VIEW_RESOLVER_BEAN_NAME = "jspViewResolver";

    private CompartmentAwareViewResolver compartmentAwareViewResolver;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (VIEW_RESOLVER_BEAN_NAME.equals(beanName)) {
            log.debug("Replacing {} ({}) with our compartment aware implementation", beanName, bean.getClass().getSimpleName());
            ViewResolver viewResolver = (ViewResolver) bean;
            compartmentAwareViewResolver.setOriginalViewResolver(viewResolver);
            return compartmentAwareViewResolver;
        } else {
            return bean;
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void setCompartmentAwareViewResolver(CompartmentAwareViewResolver compartmentAwareViewResolver) {
        this.compartmentAwareViewResolver = compartmentAwareViewResolver;
    }

}
