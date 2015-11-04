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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lee Yik Jiun
 */
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