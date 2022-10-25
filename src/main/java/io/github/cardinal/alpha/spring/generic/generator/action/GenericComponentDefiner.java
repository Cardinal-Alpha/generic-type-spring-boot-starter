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

import io.github.cardinal.alpha.spring.generic.bind.GenericComponent;
import io.github.cardinal.alpha.spring.generic.bind.multiple.MultipleGenericComponent;
import io.github.cardinal.alpha.spring.generic.generator.action.type.MultipleBeanSubtypeDefiner;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Cardinal Alpha <renaldi96.aldi@gmail.com>
 */
public class GenericComponentDefiner implements MultipleBeanSubtypeDefiner {

    private ByteBuddy bb = new ByteBuddy();

    private Controller getController() {
        return new Controller() {
            @Override
            public String value() {
                return "";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Controller.class;
            }
        };
    }

    @Override
    public List<DynamicType.Builder<?>> defineSubtypes(Class<?> beanCls) {
        GenericComponent[] targets = beanCls.getAnnotationsByType(GenericComponent.class);
        if (targets.length == 0) {
            targets = beanCls.getAnnotation(MultipleGenericComponent.class).value();
        }
        if (targets.length > 0) {
            Logger.getLogger(this.getClass().getName())
                    .log(Level.INFO, String.format("Register generic components for %s", beanCls.getName()));
        }
        return Arrays.asList(targets)
                .stream()
                .map(annot -> {
                    TypeDescription.Generic generatedType = TypeDescription.Generic.Builder
                                                                .parameterizedType(beanCls, annot.typeParameters())
                                                                .build();
                    DynamicType.Builder<?> result = bb.subclass(generatedType)
                                                        .annotateType(
                                                                getController()
                                                        );
                    return result;
                })
                .collect(Collectors.toList());
    }

}
