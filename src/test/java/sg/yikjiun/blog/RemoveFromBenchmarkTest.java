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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Lee Yik Jiun
 */
public class RemoveFromBenchmarkTest {
    RemoveFromBenchmark test = new RemoveFromBenchmark();

    // match char is 'e' which is hard coded in RemoveFromBenchmark class.
    String empty = "";
    String noMatch = "abcdfgh";
    String allMatch = "eeeeeeee";
    String oneCharMatch = "e";
    String oneCharNoMatch = "a";
    String matchThenNoMatch = "e" + noMatch;
    String noMatchThenMatch = "a" + allMatch;

    @Test
    public void testEugeneRemoveFrom() throws Exception {
        assertEquals(empty, test.eugeneRemoveFrom(empty));
        assertEquals(noMatch, test.eugeneRemoveFrom(noMatch));
        assertEquals(empty, test.eugeneRemoveFrom(allMatch));
        assertEquals(empty, test.eugeneRemoveFrom(oneCharMatch));
        assertEquals(oneCharNoMatch, test.eugeneRemoveFrom(oneCharNoMatch));
        assertEquals(matchThenNoMatch.substring(1), test.eugeneRemoveFrom(matchThenNoMatch));
        assertEquals(noMatchThenMatch.substring(0, 1), test.eugeneRemoveFrom(noMatchThenMatch));
    }

    @Test
    public void testGoogleRemoveFrom() throws Exception {
        assertEquals(empty, test.googleRemoveFrom(empty));
        assertEquals(noMatch, test.googleRemoveFrom(noMatch));
        assertEquals(empty, test.googleRemoveFrom(allMatch));
        assertEquals(empty, test.googleRemoveFrom(oneCharMatch));
        assertEquals(oneCharNoMatch, test.googleRemoveFrom(oneCharNoMatch));
        assertEquals(matchThenNoMatch.substring(1), test.googleRemoveFrom(matchThenNoMatch));
        assertEquals(noMatchThenMatch.substring(0, 1), test.googleRemoveFrom(noMatchThenMatch));
    }

    @Test
    public void testYikjiunRemoveFrom() throws Exception {
        assertEquals(empty, test.yikjiunRemoveFrom(empty));
        assertEquals(noMatch, test.yikjiunRemoveFrom(noMatch));
        assertEquals(empty, test.yikjiunRemoveFrom(allMatch));
        assertEquals(empty, test.yikjiunRemoveFrom(oneCharMatch));
        assertEquals(oneCharNoMatch, test.yikjiunRemoveFrom(oneCharNoMatch));
        assertEquals(matchThenNoMatch.substring(1), test.yikjiunRemoveFrom(matchThenNoMatch));
        assertEquals(noMatchThenMatch.substring(0, 1), test.yikjiunRemoveFrom(noMatchThenMatch));
    }
}