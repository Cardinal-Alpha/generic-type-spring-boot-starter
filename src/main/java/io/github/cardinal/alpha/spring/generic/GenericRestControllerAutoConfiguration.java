/*
 * The MIT License
 *
 * Copyright 2022 Ryuuji.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.cardinal.alpha.spring.generic;

import io.github.cardinal.alpha.spring.generic.generator.MultipleBeanSubclassTypeGenerator;
import io.github.cardinal.alpha.spring.generic.generator.action.GenericControllerCandidateFinder;
import io.github.cardinal.alpha.spring.generic.generator.action.GenericControllerDefiner;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author Cardinal Alpha <renaldi96.aldi@gmail.com>
 */
@AutoConfiguration
public class GenericRestControllerAutoConfiguration {
    
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public GenericMvcMapping genericMvcMapping(){
        return new GenericMvcMapping();
    }
    
    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public GenericReactiveMapping genericReactiveMapping(){
        return new GenericReactiveMapping();
    }
    
    @Bean
    public BeanFactoryPostProcessor genericControllerGenerator(){
        return new MultipleBeanSubclassTypeGenerator(new GenericControllerCandidateFinder(),
                                                            new GenericControllerDefiner());
    }
    
}
