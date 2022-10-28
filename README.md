# Generic Type Spring Boot Starter
This starter is designed to be used with generic type design pattern, which could help when you have many similar service flow but different data type to handle. Primary example for this case could be seen in CRUD flow which basically have same flow overall with only data type difference.

## Instalation
Installation can be easily done by adding dependency tag on your Maven project **pom.xml** (replace or set version accordingly)
```
<dependency>
    <groupId>io.cardinal-alpha</groupId>
    <artifactId>generic-type-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

## Build From Source
If on way or another you decide to build from source, here is how to do it.

Requirements :
- JDK 8 or higher
- Apache Maven

Step :
1. Clone or download this repository
2. Open inside your downloaded source root folder any terminal/command prompt
3. Just simply type `mvn clean install` and wait for build to finish

## How to Use
Complete example of this starter implementation on MVC and Reactive backend could be found [here](https://github.com/Cardinal-Alpha/generic-type-spring-example "Generic Implementation Example")

For any generic component you want to register, just apply `@GenericComponent` annotation bind on your class definition and set parameter type class you want to implement in order.
```java
@GenericComponent(typeParameters = {Book.class, Integer.class})
@GenericComponent(typeParameters = {Profile.class, Integer.class})
public class BasicEntityCrudService<T, ID>{
    // Content omitted
}
```

Next for any generic REST controller you want to implement just apply `@GenericRestController` then set your parameter type like in `@GenericComponent` but this time you also add endpoint path prefix to use.
```java
@GenericRestController(typeParameters = {Book.class, Integer.class}, path = "/api/book")
@GenericRestController(typeParameters = {Profile.class, Integer.class}, path = "/api/profile")
public class CrudController<T, ID>{
    // Handler function and content omitted
}
```