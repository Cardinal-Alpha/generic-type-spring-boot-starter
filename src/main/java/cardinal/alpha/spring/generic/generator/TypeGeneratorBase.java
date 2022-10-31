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
package cardinal.alpha.spring.generic.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 *
 * @author <a href="mailto:renaldi96.aldi@gmail.com">Cardinal Alpha</a>
 */
public abstract class TypeGeneratorBase {
    
    protected static List<Class<?>> generatedType = Collections.synchronizedList(new ArrayList<>());
    
    
    protected BeanDefinition getBeanDefinition(Class<?> beanCls){
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClassName(beanCls.getName());
        return bd;
    }
    
    protected String defaultBeanName(Class<?> beanCls){
        String originalName = beanCls.getSimpleName();
        return String.join("", originalName.length() > 0 ? originalName.substring(0, 1).toLowerCase() : "",
                                originalName.length() > 1 ? originalName.substring(1) : "");
    }
}
