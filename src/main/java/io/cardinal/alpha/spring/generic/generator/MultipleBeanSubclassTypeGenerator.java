/*
 * The MIT License
 *
 * Copyright (c) 2022 Cardinal Alpha
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
package io.cardinal.alpha.spring.generic.generator;

import io.cardinal.alpha.spring.generic.exception.RuntimeTypeGeneratorException;
import io.cardinal.alpha.spring.generic.generator.action.type.MultipleBeanSubtypeDefiner;
import io.cardinal.alpha.spring.generic.generator.action.type.TargetBeansClassExtractor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 *
 * @author Cardinal Alpha <renaldi96.aldi@gmail.com>
 */
public class MultipleBeanSubclassTypeGenerator extends TypeGeneratorBase implements BeanFactoryPostProcessor{
    
    private TargetBeansClassExtractor beanClsExtractor;
    private MultipleBeanSubtypeDefiner subBeanDefiner;

    public MultipleBeanSubclassTypeGenerator(TargetBeansClassExtractor beanClsExtractor, MultipleBeanSubtypeDefiner subBeanDefiner) {
        assert beanClsExtractor != null;
        assert subBeanDefiner != null;
        this.beanClsExtractor = beanClsExtractor;
        this.subBeanDefiner = subBeanDefiner;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory clbf) throws BeansException {
        final DefaultListableBeanFactory bf = (DefaultListableBeanFactory) clbf;
        final ByteBuddy bb = new ByteBuddy();
        beanClsExtractor.extractBeanCls(bf)
                .forEach(beanCls->{
                    try{
                        subBeanDefiner.defineSubtypes(beanCls)
                                .forEach(definedType->{
                                    Class<?> generatedCls = definedType
                                                                .make()
                                                                .load(this.getClass().getClassLoader(),
                                                                                ClassLoadingStrategy.Default.INJECTION)
                                                                .getLoaded();
                                    bf.registerBeanDefinition(defaultBeanName(generatedCls),
                                                                getBeanDefinition(generatedCls));
                                    generatedType.add(generatedCls);
                                });
                    }catch(Throwable ex){
                        throw new RuntimeTypeGeneratorException(String.format("Something wrong when generate subtype of %s", beanCls.getName()),
                                                                ex);
                    }
                });
    }
    
}
