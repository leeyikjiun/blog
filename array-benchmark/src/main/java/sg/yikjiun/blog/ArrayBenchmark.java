/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class ArrayBenchmark {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ArrayBenchmark.class.getSimpleName())
            .warmupIterations(3)
            .measurementIterations(3)
            .forks(1)
            .build();
        new Runner(opt).run();
    }

    @Param({"1000", "10000", "100000", "1000000", "10000000"})
    int n;

    List<Integer> list;
    int[] array;

    @Setup
    public void setup() {
        list = new ArrayList<Integer>();
        for (int i = 0; i < n; ++i) {
            list.add(i);
        }
        Collections.shuffle(list);

        array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = list.get(i);
        }
    }

    @Benchmark
     public boolean measureArrayListAdd() {
        boolean ret = false;
        List<Integer> lst = new ArrayList<Integer>();
        for (Integer integer : list) {
            ret ^= lst.add(integer);
        }
        return ret;
    }

    @Benchmark
    public boolean measureArrayListAddWithSize() {
        boolean ret = false;
        List<Integer> lst = new ArrayList<Integer>(list.size());
        for (Integer integer : list) {
            ret ^= lst.add(integer);
        }
        return ret;
    }

    @Benchmark
    public int measureArrayFor() {
        int ret = 0;
        for (int i = 0; i < array.length; ++i) {
            ret ^= array[i];
        }
        return ret;
    }

    @Benchmark
    public int measureArrayForWithLen() {
        int ret = 0;
        int len = array.length;
        for (int i = 0; i < len; ++i) {
            ret ^= array[i];
        }
        return ret;
    }

    @Benchmark
    public int measureArrayListFor() {
        int ret = 0;
        for (int i = 0; i < list.size(); ++i) {
            ret ^= list.get(i);
        }
        return ret;
    }

    @Benchmark
    public int measureArrayListForWithLen() {
        int ret = 0;
        int len = list.size();
        for (int i = 0; i < len; ++i) {
            ret ^= list.get(i);
        }
        return ret;
    }

    @Benchmark
    public int measureArrayListForeach() {
        int ret = 0;
        for (Integer integer : list) {
            ret ^= integer;
        }
        return ret;
    }
}
