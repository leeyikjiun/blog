---
title: Use a Deque, not a Stack
---

If you're looking to use a Stack in Java, use a Deque instead. And here's why.

The first thing that I've noticed when I looked into the source code of Stack is that it extends Vector. Vector is a synchronized class and we've all been told to use the faster ArrayList instead as it is unsynchronized. Even if we need a synchronized version of ArrayList, we use Collections.synchronizedList instead of the old Vector. Since Stack extends Vector, it is going to be slow for single threaded applications.

If you read the [JavaDocs for Stack](http://docs.oracle.com/javase/7/docs/api/java/util/Stack.html), it even recommended us to use Deque.

> A more complete and consistent set of LIFO stack operations is provided by the [`Deque`](http://docs.oracle.com/javase/7/docs/api/java/util/Deque.html) interface and its implementations, which should be used in preference to this class.

If Stack is just an extension of Vector, then why can't we just roll out our own Stack and extend an ArrayList? We can, and is also the reason why we're recommended to use an ArrayDeque. The implementations of ArrayList and ArrayDeque are actually very similar.

As with all performance issues, profile, profile, and profile. Let the numbers do the talking.

The following benchmark tests how fast each stack can perform push operations on Integers. This benchmark was run on Java 1.8.0_60 using [JMH Benchmark](http://openjdk.java.net/projects/code-tools/jmh/) on my desktop with Intel i5-4570 and G.Skill 2x 4GB RAM.

Here, I've created two classes, `AStack` and `AStackInt`. AStack is a generic Stack that simply extends ArrayList, while AStackInt is backed by a primitive integer array.

The other collections libraries used are:

- Trove 3.0.3
- GoldmanSachs Collections 6.2.0
- FastUtil 7.0.7
- HPPC 0.7.1

I've included only collections that contains a Stack, and have excluded Google Guava and Mahout. If there are any more collections that I should benchmark on, please let me know.

Some interesting observation is that for the IntStack interface of FastUtil, it does not contain a clear() method. I have to declare a IntArrayList object instead even though it implements the IntStack interface.

<iframe frameborder="0" height="371" scrolling="no" seamless="" src="https://docs.google.com/spreadsheets/d/1KdxsbvurDfnRHEzwXLDgd8nHiWVVYwQKvmV7HHsZSC8/pubchart?oid=1406743461&amp;format=interactive" width="600"></iframe>

The graph above shows the time taken for each stack to run n push operations as shown in the X-axis.

As you can see, Stack is extremely slow when compared to a Deque. Also, an ArrayList Stack (AStack) is just slightly slower than a Deque. The primitive Stacks are also among the fastest in this benchmark with HPPC performing slightly faster than the rest.

The benchmark classes are as follows:

{% highlight java %}
@State(Scope.Thread)
public abstract class StackBenchmark {
    @Param({"4096", "16384", "65536", "262144", "1048576", "4194304"})
    protected int n;

    protected int[] arr;

    @Setup(Level.Trial)
    public void setupTrial() {
        arr = new int[n];
        Random r = new Random();
        for (int i = 0; i < n; ++i) {
            arr[i] = r.nextInt();
        }
    }
}
{% endhighlight %}
{% highlight java %}
@State(Scope.Thread)
public abstract class PushBenchmark extends StackBenchmark {
    @Setup(Level.Invocation)
    public void setupInvocation() {
        clear();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public void measurePush() {
        for (int i = 0; i < n; ++i) {
            push(arr[i]);
        }
    }

    public abstract void push(int i);
    public abstract void clear();
}
{% endhighlight %}
where the measurePush method will measure the push method of each stack for n times.

If you want to add a new collections class to be benchmarked, then simply extend the PushBenchmark class. For example,

{% highlight java %}
public class StackPushBenchmark extends PushBenchmark {
    private Stack<integer> stack = new Stack<integer>();

    @Override
    public void push(int i) {
        stack.push(i);
    }

    @Override
    public void clear() {
        stack.clear();
    }
}
{% endhighlight %}
Finally just run the following commands to start benchmarking

{% highlight bash %}
mvn clean install
java -jar target/benchmarks.jar PushBenchmark -i 5 -wi 5 -f 5 -tu ms
{% endhighlight %}
where there will be 5 warmup iterations, 5 iterations, 5 forks, and the output in milliseconds.

If you're interested, here's the source code for the two classes that I've created. As of now, they are still untested.

{% highlight java %}
import java.util.ArrayList;

public class AStack<e> extends ArrayList<e> {
    public void push(E e) {
        add(e);
    }

    public void pop() {
        remove(size() - 1);
    }
}
{% endhighlight %}
{% highlight java %}
public class AStackInt {
    private int[] arr;

    private int size;

    public AStackInt() {
        arr = new int[16];
    }

    public void push(int i) {
        arr[size++] = i;
        if (size == arr.length) {
            doubleCapacity();
        }
    }

    public void pop() {
        --size;
    }

    public int size() {
        return size;
    }

    public void clear() {
        size = 0;
    }

    private void doubleCapacity() {
        int newLength = arr.length << 1;
        int[] newArr = new int[newLength];
        System.arraycopy(arr, 0, newArr, 0, size);
        arr = newArr;
    }
}
{% endhighlight %}
I will make the entire code available on my GitHub as soon as I figure out the best way to organise the code.