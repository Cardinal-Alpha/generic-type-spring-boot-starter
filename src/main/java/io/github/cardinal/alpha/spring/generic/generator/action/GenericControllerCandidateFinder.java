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
package io.github.cardinal.alpha.spring.generic.generator.action;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import io.github.cardinal.alpha.spring.generic.bind.GenericRestController;
import io.github.cardinal.alpha.spring.generic.bind.multiple.MultipleGenericRestController;
import io.github.cardinal.alpha.spring.generic.generator.action.type.TargetBeansClassExtractor;

/**
 *
 * @author Cardinal Alpha <renaldi96.aldi@gmail.com>
 */
public class GenericControllerCandidateFinder implements TargetBeansClassExtractor{
    
    private DefaultListableBeanFactory beanRegistry;
    
    private void preRegisterCleanup(Class<?> componentClass){
        for(String beanName : beanRegistry.getBeanNamesForType(componentClass))
            beanRegistry.removeBeanDefinition(beanName);
    }
    
    private List<Class<?>> getUniqueBeansClassWithAnnotation(Class<? extends Annotation> targetAnnotation){
        return beanRegistry.getBeansWithAnnotation(targetAnnotation)
                        .entrySet().stream()
                        .map(e -> e.getValue().getClass())
                        .collect(Collectors.toList());
    }

    @Override
    public List<Class<?>> extractBeanCls(DefaultListableBeanFactory bf) {
        beanRegistry = bf;
        List<Class<?>> processTarget = getUniqueBeansClassWithAnnotation(GenericRestController.class);
        if(processTarget.size() == 0)
            processTarget = getUniqueBeansClassWithAnnotation(MultipleGenericRestController.class);
        processTarget.forEach(cls->{
            preRegisterCleanup(cls);
        });
        return processTarget;
    }
    
}
