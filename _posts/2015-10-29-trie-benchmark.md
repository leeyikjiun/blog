---
title: "Trie benchmark"
tags: Java, Trie
---
Tries are a great data structure for strings. They can act as a dictionary, checking if a word exists. Also, it can check if a word is a prefix of a word in the dictionary. A trie is supposedly more space-efficient as well since it shares nodes. However, when implemented poorly, it can use much much more memory than a simple hashtable.

I benchmarked the following Trie implementations that I found randomly online, Apache collection's Trie, and my own naive implementation.

- <https://github.com/mlarocca/Algorithms>
- <https://github.com/npgall/concurrent-trees>
- <http://algs4.cs.princeton.edu/52trie/TrieST.java.html>

{% highlight java %}
public class Trie {
    private Node root = new Node();

    public void add(String s) {
        char[] chars = s.toCharArray();
        Node node = root;
        for (char c : chars) {
            Node nextNode = node.map.get(c);
            if (nextNode == null) {
                nextNode = new Node();
                node.map.put(c, nextNode);
            }
            node = nextNode;
        }
    }

    static class Node {
        Map<Character, Node> map = new HashMap<Character, Node>(2, 1f);
    }
}
{% endhighlight %}

Although this naive Trie implementation is technically not working, it suffices for this benchmark. Along with the above Trie implementations, I included ArrayList and HashSet as well just for comparing.

The dictionary used in this benchmark will be the standard Unix dictionary and can be found in /usr/share/dict/. This file contains 235 886 words and is 2.37MB large.

<iframe frameborder="0" height="371" scrolling="no" seamless="" src="https://docs.google.com/spreadsheets/d/1KdxsbvurDfnRHEzwXLDgd8nHiWVVYwQKvmV7HHsZSC8/pubchart?oid=5327982&amp;format=interactive" width="600"></iframe>
<iframe frameborder="0" height="371" scrolling="no" seamless="" src="https://docs.google.com/spreadsheets/d/1KdxsbvurDfnRHEzwXLDgd8nHiWVVYwQKvmV7HHsZSC8/pubchart?oid=682071500&amp;format=interactive" width="600"></iframe>

Admittedly, this benchmark isn't very fair as some of them were made for concurrency or educational purposes. We see that a naive trie implementation uses 10x more memory than a HashSet and we're better off with a HashSet if we need only a dictionary. i.e., the contains method. It is also twice as fast to load the dictionary.Apache's PatriciaTrie actually uses less memory than a HashSet, but is only slightly slower by 7ms. I'm impressed!

The code for this benchmark can be found on my [github](https://github.com/leeyikjiun/blog), and the entire results can be found [here](https://raw.githubusercontent.com/leeyikjiun/blog/master/trie-benchmark/results.txt). I'm unable to run this from the command line, had some weird FileSystemNotFoundException. Ping me if you know what's the problem. To run this benchmark, do a mvn clean install, and then run it from the IDE.
