/*
 * Copyright (C) 2020 Wigo Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wigoai.nlp.highlight;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * 불용어 하이라이트 rest server starter
 *
 * @author macle
 */
@SpringBootApplication(scanBasePackages = {"com.wigoai"})
public class StopwordHighlightStater {


    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        //noinspection Convert2Lambda
        return new BeanFactoryPostProcessor() {

            @Override
            public void postProcessBeanFactory(
                    ConfigurableListableBeanFactory beanFactory) throws BeansException {
                BeanDefinition bean = beanFactory.getBeanDefinition(
                        DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);

                bean.getPropertyValues().add("loadOnStartup", 1);
            }
        };
    }




    public static void main(String[] args) throws IOException {

        String configPath ;
        String logbackPath = null;

        if(args != null && args.length > 0){
            configPath = args[0];
            if(args.length > 1){
                logbackPath = args[1];
            }

        }else{
            configPath = "config/app.yml";
        }


        StopwordHighlight stopwordHighlight = StopwordHighlight.getInstance();
        stopwordHighlight.setConfigPath(configPath);
        stopwordHighlight.readConfigFile();

        Yaml yaml = new Yaml();
        Reader yamlFile = new FileReader(configPath);

        Map<String, Object> yamlMaps = yaml.load(yamlFile);
        //noinspection unchecked
        Map<String, Object> serverMap = (Map<String, Object>)yamlMaps.get("server");

        yamlFile.close();


        int port  = (int) serverMap.get("port");

        HashMap<String, Object> props = new HashMap<>();
        props.put("server.port", port);

        if(logbackPath != null){
            File file = new File(logbackPath);
            if(file.isFile()) {
                props.put("logging.config", logbackPath);
                LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

                try {
                    JoranConfigurator configurator = new JoranConfigurator();
                    configurator.setContext(context);
                    context.reset();
                    configurator.doConfigure(logbackPath);

                } catch (JoranException ignore) {}
            }
        }

        String [] springbootArgs = new String[0];
        new SpringApplicationBuilder()
                .sources(StopwordHighlightStater.class)
                .properties(props)
                .run(springbootArgs);
    }

}
