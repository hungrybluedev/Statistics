# Statistics

![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/hungrybluedev/Statistics?include_prereleases&style=plastic)
![Travis (.org)](https://img.shields.io/travis/hungrybluedev/Statistics)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ce2aaa29ced74f41a1399d4c7e9faeae)](https://app.codacy.com/manual/hungrybluedev/Statistics?utm_source=github.com&utm_medium=referral&utm_content=hungrybluedev/Statistics&utm_campaign=Badge_Grade_Dashboard)

## Introduction

An easy to use Statistics library in Java. It is meant to be as general as possible yet retain the usability and clean implementation.

## Minimal Usage Example

```java
        SampleBuilder builder = new SampleBuilder("Runtimes", "s");

        for (int i = 0; i < ITERATION_LIMIT; i++) {
            long timeStart = System.currentTimeMillis();

            // The code to be timed goes here.

            new BigInteger(1024, 2048, new Random());

            long timeEnd = System.currentTimeMillis();

            builder.addObservation((timeEnd - timeStart) / 1000.0);
        }

        Sample runtimes = builder.buildSample();
        System.out.println(runtimes);
```

Output of the code will be something like:

```none
0.121 s
0.147 s
0.22 s
0.115 s
0.084 s
0.025 s
0.353 s
0.209 s
0.021 s
0.223 s
...
0.015 s
0.521 s
0.03 s
0.017 s
0.036 s

Summary Statistics for Sample: Runtimes

Count   :      50
Sum     : 4.757 s
Mean    : 0.095 s
Variance: 0.011 s
Std Dev : 0.106 s
```

## Documentation

### 0.0.1

Statistics v0.0.1 - [HungryBlueDev.in](https://hungrybluedev.in/docs/Statistics/0.0.1/)

## Status

Currently it is pre-alpha software. There _will_ be breaking changes as we move forward.

## Code Review posts

### 0.0.1

The first esposure to Code Review was in [this post](https://codereview.stackexchange.com/questions/238062/statistics-library-with-sample-samplebuilder-and-tests).
I realised that there are significant architectural changes that have to be made to make the code better.

## To Do

1. Make Sample completely immutable.
2. Replace the idea of state variables with initial parameters during sample build time.
3. Incorporate the use of Persistent Data Structures to improve performance.


