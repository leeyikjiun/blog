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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Lee Yik Jiun
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class RemoveFromBenchmark {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(RemoveFromBenchmark.class.getSimpleName())
            .warmupIterations(3)
            .measurementIterations(3)
            .forks(3)
            .build();

        new Runner(opt).run();
    }

    private static final char matchChar = 'e';

    @State(Scope.Benchmark)
    public static class Data {
        @Param({"10", "100", "1000", "10000", "100000", "1000000"})
        int len;

        @Param({"0.0", "0.2", "0.4", "0.6", "0.8", "1.0"})
        double percentMatch;

        CharSequence sequence;

        @Setup
        public void setup() {
            sequence = getCharSequence();
        }

        /**
         * Returns a char sequence of length len with percentMatch percent of matchChar
         */
        private CharSequence getCharSequence() {
            List<Character> chars = new ArrayList<Character>();
            int matchLen = (int) (percentMatch * len);
            for (int i = 0; i < matchLen; ++i) {
                chars.add(matchChar);
            }

            Random random = ThreadLocalRandom.current();
            for (int i = matchLen; i < len; ++i) {
                chars.add((char) random.nextInt(256));
            }

            Collections.shuffle(chars);
            StringBuilder builder = new StringBuilder();
            for (char c : chars) {
                builder.append(c);
            }
            return builder.toString();
        }
    }

    @Benchmark
    public String eugene(Data d) {
        return eugeneRemoveFrom(d.sequence);
    }

    @Benchmark
    public String google(Data d) {
        return googleRemoveFrom(d.sequence);
    }

    @Benchmark
    public String yikjiun(Data d) {
        return yikjiunRemoveFrom(d.sequence);
    }

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

    public String googleRemoveFrom(CharSequence sequence) {
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

    public int indexIn(CharSequence sequence) {
        int length = sequence.length();
        for (int i = 0; i < length; i++) {
            if (matches(sequence.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public boolean matches(char c) {
        return matchChar == c;
    }
}