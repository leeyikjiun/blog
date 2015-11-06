/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Lee Yik Jiun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sg.yikjiun.blog;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.SmartArrayBasedNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.voidvalue.VoidValue;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import edu.princeton.cs.algs4.TrieST;

/**
 * @author Lee Yik Jiun
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class TrieBenchmark {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(TrieBenchmark.class.getSimpleName())
            .addProfiler(GCProfiler.class)
            .warmupIterations(3)
            .measurementIterations(3)
            .forks(1)
            .build();
        new Runner(opt).run();
    }

    private List<String> words;

    @Setup
    public void setup() throws IOException, URISyntaxException {
        URL url = getClass().getResource("/words.txt");
        words = Files.readAllLines(Paths.get(url.toURI()), StandardCharsets.UTF_8);
    }

    @Benchmark
    public List<String> measureArrayList() {
        List<String> list = new ArrayList<String>();
        for (String word : words) {
            list.add(word);
        }
        return list;
    }

    @Benchmark
    public Set<String> measureSet() {
        Set<String> set = new HashSet<String>();
        for (String word : words) {
            set.add(word);
        }
        return set;
    }

    @Benchmark
    public Trie measureTrie() {
        Trie trie = new Trie();
        for (String word : words) {
            trie.add(word);
        }
        return trie;
    }

    @Benchmark
    public org.apache.commons.collections4.trie.PatriciaTrie<Void> measureApachePatriciaTrie() {
        org.apache.commons.collections4.trie.PatriciaTrie<Void> trie = new org.apache.commons.collections4.trie.PatriciaTrie<Void>();
        for (String word : words) {
            trie.put(word, null);
        }
        return trie;
    }

    @Benchmark
    public PatriciaTrie<Void> measureMlaroccaPatriciaTrie() {
        PatriciaTrie<Void> trie = new PatriciaTrie<Void>();
        for (String word : words) {
            trie.insertString(word, null);
        }
        return trie;
    }

    @Benchmark
    public RadixTree<VoidValue> measureConcurrentRadixTree() {
        RadixTree<VoidValue> trie = new ConcurrentRadixTree<VoidValue>(new SmartArrayBasedNodeFactory());
        for (String word : words) {
            trie.put(word, VoidValue.SINGLETON);
        }
        return trie;
    }

    @Benchmark
    public TrieST<VoidValue> measureTrieST() {
        TrieST<VoidValue> trie = new TrieST<VoidValue>();
        for (String word : words) {
            trie.put(word, VoidValue.SINGLETON);
        }
        return trie;
    }
}
