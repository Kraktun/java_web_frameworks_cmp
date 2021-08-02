# README

## Index

* [Intro](#intro)
* [Content](#content)
* [Discussion](discussion.md)
* [API](api.md)
* [Tests](testing.md)
* [Results](results/results.md)

## Intro

The objective of this project is to assess the learning curve to build a small REST API with three of the most used frameworks for Java, to try some of their unique features, to check their compatibility with [GraalVM](https://www.graalvm.org/) native imager and to test their performance both as JVM-based and native executables.

Be aware that this is not meant to be a complete analysis of the frameworks, nor it exploits all the capabilities of them, this is the result of a few weeks/months of development starting from scratch, with no prior knowledge of IoC or AOP programming.  

## Content

This repo contains the necessary code and scripts to reproduce the results provided in the [results file](results/results.md).  

The code includes a simple REST API written in Java with the following three frameworks: [Spring](https://spring.io/), [Micronaut](https://micronaut.io/), [Quarkus](https://quarkus.io/).  
Note that the functionality may vary slightly among the frameworks and it's not a 1:1 map among them.  

This repository includes also a small library written in Kotlin that handles computations related to [RSA](https://en.wikipedia.org/wiki/RSA_(cryptosystem)) keys. This is used as a way to spend a specific amount of time of computing power. I don't use the default library available in Java because it imposes a minimum length for the keys, while for this type of test we must be able to choose an arbitrary length, and in particular one that is small enough so that keys can be cracked in a feasible amount of time.  
Note that RSA keys are not meant to encrypt texts, but rather to encrypt other keys (e.g. [AES](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)) and as such there is a limit to the length of the text a RSA key can encrypt, which is proportional to the length of the key itself.  
