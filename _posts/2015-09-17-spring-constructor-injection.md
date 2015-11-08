---
title: "Spring: Constructor injection"
tags: Java, Spring
---
Recently, I needed to pass some values to the constructor at runtime using Spring. However, it didn't work no matter how many tutorials and StackOverflow I read. I just couldn't find anything on "spring passing values to constructor at runtime not working".

Consider the following constructor injection using Spring:
{% highlight java %}
public class Foo {
    private String bar;

    public Foo(String bar) {
        this.bar = bar;
    }

    public String getBar() {
        return bar;
    }
}
{% endhighlight %}
foo.xml
{% highlight xml %}
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">

    <bean id="foo" class="sg.yikjiun.spring.Foo">
        <constructor-arg type="java.lang.String" value="bar"/>
    </bean>
</beans>
{% endhighlight %}
To get the Foo object from Spring:
{% highlight java %}
public class App {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:foo.xml");
        Foo foo = (Foo) context.getBean("foo");
        System.out.println(foo.getBar());
    }
}
{% endhighlight %}
The printout will be "bar" as expected from foo.xml.

If we want to override the bar value (to foo) during runtime, we need to use the following method instead.
{% highlight java %}
Foo foo = (Foo) context.getBean("foo", "foo");
{% endhighlight %}
The following method allows us to override the constructor's parameters with runtime values.
{% highlight java %}
Object getBean(String name, Object... args) throws BeansException;
{% endhighlight %}
If you tried running the above code, you'll find that the value of bar is still "bar". It was not overridden by "foo".

Probing into the JavaDocs,

> @param args arguments to use when creating a bean instance using explicit arguments (only applied when creating a new instance as opposed to retrieving an existing one)

The args are only applied when a new bean instance is created. By default, the scope of a bean is singleton, that means that no bean is created when getBean is called.

The solution is to change the scope of the bean to prototype, which forces Spring to create a new bean instance every time.
{% highlight xml %}
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd">

    <bean id="foo" class="sg.yikjiun.spring.Foo" scope="prototype">
        <constructor-arg type="java.lang.String" value="bar"/>
    </bean>
</beans>
{% endhighlight %}
Running the code with the updated xml now successfully prints "foo".

I hope this helps if you ran into the same problem as me.

If you would like to use annotation based Spring instead.
{% highlight java %}
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Foo {
    private String bar;

    public Foo(String bar) {
        this.bar = bar;
    }

    public String getBar() {
        return bar;
    }
}
{% endhighlight %}
foo.xml
{% highlight xml %}
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context-2.5.xsd">

    <context:component-scan base-package="sg.yikjiun.spring" />
</beans>
{% endhighlight %}

Note that without the prototype scope, the above code will crash as Spring tries to create the Foo object with a default constructor that doesn't exist.
