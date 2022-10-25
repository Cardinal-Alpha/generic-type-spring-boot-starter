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

import io.github.cardinal.alpha.spring.generic.exception.GenericControllerException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.github.cardinal.alpha.spring.generic.bind.GenericRestController;
import io.github.cardinal.alpha.spring.generic.bind.multiple.MultipleGenericRestController;
import io.github.cardinal.alpha.spring.generic.generator.action.type.MultipleBeanSubtypeDefiner;

/**
 *
 * @author Cardinal Alpha <renaldi96.aldi@gmail.com>
 */
public class GenericControllerDefiner implements MultipleBeanSubtypeDefiner {

    private ByteBuddy bb = new ByteBuddy();

    private Pattern pathCamelCasing = Pattern.compile("\\/[^\\/]+");

    private Map<String, Class<?>> registeredPath = new HashMap<>();

    private String camelCasePath(String path) {
        return pathCamelCasing.matcher(path)
                .replaceAll(m -> {
                    String capture = m.group();
                    if (capture.length() > 1) {
                        return String.join("", capture.substring(1, 2).toUpperCase(),
                                capture.length() > 2 ? capture.substring(2) : "");
                    }
                    return capture;
                });
    }

    private RestController getControllerAnnotation() {
        return new RestController() {
            @Override
            public String value() {
                return "";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return RestController.class;
            }
        };
    }

    private RequestMapping getRequestMapping(String path) {
        return new RequestMapping() {
            @Override
            public String name() {
                return "";
            }

            @Override
            public String[] value() {
                String[] value = {path};
                return value;
            }

            @Override
            public String[] path() {
                String[] value = {path};
                return value;
            }

            @Override
            public RequestMethod[] method() {
                return new RequestMethod[0];
            }

            @Override
            public String[] params() {
                return new String[0];
            }

            @Override
            public String[] headers() {
                return new String[0];
            }

            @Override
            public String[] consumes() {
                return new String[0];
            }

            @Override
            public String[] produces() {
                return new String[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return RequestMapping.class;
            }
        };
    }
    

    @Override
    public List<DynamicType.Builder<?>> defineSubtypes(Class<?> beanCls) {
        GenericRestController[] targets = beanCls.getAnnotationsByType(GenericRestController.class);
        if (targets.length == 0) {
            targets = beanCls.getAnnotation(MultipleGenericRestController.class).value();
        }
        if (targets.length > 0) {
            Logger.getLogger(this.getClass().getName())
                    .log(Level.INFO, String.format("Creating generic controller for %s", beanCls.getName()));
        }
        return Arrays.asList(targets)
                .stream()
                .map(target -> {
                    List<String> existingKey = registeredPath.keySet().stream()
                            .filter(k -> k.equalsIgnoreCase(target.path()))
                            .toList();
                    if (!existingKey.isEmpty()) {
                        throw new GenericControllerException(String.format("Duplicate path for %s on %s and %s",
                                target.path(),
                                beanCls.getName(),
                                registeredPath.get(existingKey.get(0)))
                        );
                    }
                    if (target.path() == null) {
                        throw new GenericControllerException(String.format("Generic controller without path detected on %s",
                                beanCls.getName()));
                    }
                    String generatedTypeName = String.join("",
                                                    beanCls.getName(),
                                                    camelCasePath(target.path())
                    );
                    TypeDescription.Generic generatedType = TypeDescription.Generic.Builder
                                                                .parameterizedType(
                                                                        beanCls,
                                                                        target.typeParameters()
                                                                )
                                                                .build();
                    return bb.subclass(generatedType)
                            .name(generatedTypeName)
                            .annotateType(
                                    getControllerAnnotation(),
                                    getRequestMapping(target.path())
                            );
                })
                .collect(Collectors.toList());
    }

}
