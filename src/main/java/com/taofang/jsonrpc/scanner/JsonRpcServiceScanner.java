package com.taofang.jsonrpc.scanner;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationScopeMetadataResolver;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import com.taofang.jsonrpc.JsonRpcExporter;

public class JsonRpcServiceScanner implements BeanFactoryPostProcessor, InitializingBean,
        ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String basePackage;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void afterPropertiesSet() throws Exception {

    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        Scanner scanner = new Scanner((BeanDefinitionRegistry) beanFactory);
        scanner.setResourceLoader(this.applicationContext);

        scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));

    }
    
    private final class JsonRpcBeanNameGenerator extends AnnotationBeanNameGenerator {
        
        protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
            
            AnnotationMetadata amd = annotatedDef.getMetadata();
            Set<String> types = amd.getAnnotationTypes();
            String beanName = null;
            for(String type:types){
                
                if(type.equals("com.taofang.jsonrpc.scanner.JsonRpc")){
                    Map<String, Object> attributes = amd.getAnnotationAttributes(type);
                    String value = (String) attributes.get("value");
                    if (StringUtils.hasLength(value)) {
                        beanName = value;
                    }
                }
            }
            
            return beanName;
        }
        
    }

    private final class Scanner extends ClassPathBeanDefinitionScanner {

        private BeanNameGenerator beanNameGenerator = new JsonRpcBeanNameGenerator();

        private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
        private BeanDefinitionRegistry registry;

        public Scanner(BeanDefinitionRegistry registry) {
            super(registry);
            this.registry = registry;
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<BeanDefinitionHolder>();
            for (String basePackage : basePackages) {
                Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
                for (BeanDefinition candidate : candidates) {
                    ScopeMetadata scopeMetadata = this.scopeMetadataResolver
                            .resolveScopeMetadata(candidate);
                    candidate.setScope(scopeMetadata.getScopeName());
                    String originalBeanName = this.beanNameGenerator.generateBeanName(candidate,
                            this.registry);
                    // if (candidate instanceof AbstractBeanDefinition) {
                    // postProcessBeanDefinition((AbstractBeanDefinition)
                    // candidate, originalBeanName);
                    // }
                    
                    ScannedGenericBeanDefinition bd = (ScannedGenericBeanDefinition) candidate;
                    String beanClassName = bd.getBeanClassName();
                    bd.setBeanClassName(JsonRpcExporter.class.getName());
                    bd.setBeanClass(JsonRpcExporter.class);
                    bd.getPropertyValues().add("service",
                            applicationContext.getBean(originalBeanName));
                    bd.getPropertyValues().add("beanClassName", beanClassName);
                    String[] interfaces = bd.getMetadata().getInterfaceNames();
                    if (interfaces == null || interfaces.length == 0)
                        continue;
                  //  HessianServiceExporter.class.getClassLoader().getResource(name)
                    Class interf = null;
                    try {
                        interf = Class.forName(interfaces[0]);
                    } catch (ClassNotFoundException e) {
                        continue;
                    }
                    bd.getPropertyValues().add("serviceInterface", interf);
                    // if (candidate instanceof AnnotatedBeanDefinition) {
                    // AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition)
                    // candidate);
                    // }
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate,
                          "/" +   originalBeanName );
                    definitionHolder = applyScopedProxyMode(scopeMetadata, definitionHolder,
                            this.registry);
                    beanDefinitions.add(definitionHolder);
                    registerBeanDefinition(definitionHolder, this.registry);
                }
            }
            if (beanDefinitions.isEmpty()) {
                System.out.println("not service be scaned");
            } else {
                for (BeanDefinitionHolder holder : beanDefinitions) {
                    AnnotatedBeanDefinition definition = (AnnotatedBeanDefinition) holder
                            .getBeanDefinition();
                    System.out.println(holder.getBeanName());

                    System.out.println(definition.getMetadata().getAnnotationTypes());
                }
            }

            return beanDefinitions;

        }

        @Override
        protected void registerDefaultFilters() {

            addIncludeFilter(new AnnotationTypeFilter(JsonRpc.class));
        }

        BeanDefinitionHolder applyScopedProxyMode(
                ScopeMetadata metadata, BeanDefinitionHolder definition,
                BeanDefinitionRegistry registry) {

            ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
            if (scopedProxyMode.equals(ScopedProxyMode.NO)) {
                return definition;
            }
            boolean proxyTargetClass = scopedProxyMode.equals(ScopedProxyMode.TARGET_CLASS);
            return ScopedProxyUtils.createScopedProxy(definition, registry, proxyTargetClass);
        }

    }

}
