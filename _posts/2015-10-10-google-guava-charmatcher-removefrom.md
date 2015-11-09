---
title: "Google Guava CharMatcher.removeFrom"
tags: Java, Guava
---
While reading Google's [Guava](https://github.com/google/guava) source code, I notice that there's a very unusual code snippet in the removeFrom method in the CharMatcher class.

{% highlight java %}
public String removeFrom(CharSequence sequence) {
    String string = sequence.toString();
    int pos = indexIn(string);
    if (pos == -1) {
      return string;
    }

    char[] chars = string.toCharArray();
    int spread = 1;

    // This unusual loop comes from extensive benchmarking
    OUT:
    while (true) {
      pos++;
      while (true) {
        if (pos == chars.length) {
          break OUT;
        }
        if (matches(chars[pos])) {
          break;
        }
        chars[pos - spread] = chars[pos];
        pos++;
      }
      spread++;
    }
    return new String(chars, 0, pos - spread);
}
{% endhighlight %}
And I'm not the only person that has interest in this piece of code here. It seems like we can do better with a more straightforward version than this unusual loop.

I'm going to benchmark this code together with my version and Eugene's [version](http://stackoverflow.com/questions/22014605/guavas-charmatcher-removefrom). Adapting from his [post](http://mail.openjdk.java.net/pipermail/jmh-dev/2014-August/001218.html) at jmh mailing list, I made the following benchmark.

I varied two parameters, the length of the string and the percentage of matches. So, a 0.2 percent match means that 20% of the characters are matches and 80% of the characters will remain after calling removeFrom.

In order to keep the comparison fair, I copied the relevant codes over from guava and only modified the loop part. Also, I modified Eugene's code to make use of the pos value.

Eugene's version
{% highlight java %}
public String eugeneRemoveFrom(CharSequence sequence) {
    String string = sequence.toString();
    int pos = indexIn(string);
    if (pos == -1) {
        return string;
    }

    char[] chars = string.toCharArray();
    int totalCount = 1;

    boolean reachedTheEnd = false;
    for (int i = pos + 1; i < chars.length; ++i) {
        int howManyInIteration = 0;
        while (matches(chars[i])) {
            ++i;
            ++howManyInIteration;
            if (i == chars.length) {
                reachedTheEnd = true;
                break;
            }
        }
        totalCount += howManyInIteration;
        if (!reachedTheEnd) chars[i - totalCount] = chars[i];
    }
    return new String(chars, 0, (chars.length - totalCount));
}
{% endhighlight %}

Yik Jiun's version
{% highlight java %}
public String yikjiunRemoveFrom(CharSequence sequence) {
    String string = sequence.toString();
    int pos = indexIn(string);
    if (pos == -1) {
        return string;
    }

    char[] chars = string.toCharArray();
    int spread = 1;

    ++pos;
    while (pos < chars.length) {
        if (!matches(chars[pos])) {
            chars[pos - spread] = chars[pos];
        } else {
            spread++;
        }
        pos++;
    }
    return new String(chars, 0, pos - spread);
}
{% endhighlight %}

Since two parameters are being varied, the resulting graph should be in 3D. To keep it simple, I chose to display the following two graphs: varying percent match on a short string (should be the bulk of the use cases) and varying string length of a fixed percent match (not really sure what percent this should be).

<iframe frameborder="0" height="371" scrolling="no" seamless="" src="https://docs.google.com/spreadsheets/d/1KdxsbvurDfnRHEzwXLDgd8nHiWVVYwQKvmV7HHsZSC8/pubchart?oid=507989399&amp;format=interactive" width="600"></iframe>
<iframe frameborder="0" height="371" scrolling="no" seamless="" src="https://docs.google.com/spreadsheets/d/1KdxsbvurDfnRHEzwXLDgd8nHiWVVYwQKvmV7HHsZSC8/pubchart?oid=2141647739&amp;format=interactive" width="600"></iframe>

In the last graph, the length of the string is 10, 100, ... , 1000000 and is presented in a logarithmic scale of log10.

As you can see from the results, the unusual loop doesn't seems to be doing any better than a straightforward loop.

If you're interested in the entire results, you can view it [here](https://raw.githubusercontent.com/leeyikjiun/blog/master/benchmarks/remove_from_benchmark.txt). If you're interested in running the benchmark yourself, the entire code is hosted [here](https://github.com/leeyikjiun/blog) on github. To run the benchmark, simply download the maven project and use the following command:
{% highlight bash %}
mvn clean install
java -jar target/benchmarks.jar RemoveFromBenchmark -wi 3 -i 3 -f 3
{% endhighlight %}
to run the benchmark with 3 warmup iterations, 3 iterations, and 3 forks.

I'll update with Caliper's benchmark soon.
