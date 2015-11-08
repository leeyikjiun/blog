---
title: "Observations on Java Collections Library"
tags: Java, FastUtil, Trove
---
While building [scf4j](https://github.com/leeyikjiun/scf4j), I noticed some interesting facts about the Java Collections Library, FastUtil 7.0.7 and Trove 3.0.3.

FastUtil implements both AVL Tree and Red Black Tree but doesn't have a boolean tree set. Also, it does not have any Maps with boolean as a key.
FastUtil does not implement LinkedList.
FastUtil's Stack interface does not have size and isEmpty.

Trove has no tree sets at all, and does not support anything with boolean as a key or value.
Trove's Stack has no isEmpty.
