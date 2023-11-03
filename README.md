# netroles

[![DOI](https://zenodo.org/badge/671527401.svg)](https://zenodo.org/badge/latestdoi/671527401) ![Build CI status](https://github.com/muellerj2/netroles/actions/workflows/gradle_build.yml/badge.svg?branch=main&event=push) ![Coverage](https://github.com/muellerj2/netroles/blob/profile-badges/jacoco.svg?raw=true)

The netroles library offers tools for analyzing network data in terms of role equivalences and structures.

This library implements many common notions of role equivalences, such as structural, weak or regular equivalence in their standard formulation.  
While implementations in other software packages tend to be restricted to these standard equivalence concepts on unweighted networks, netroles has been deliberately designed to expand the scope of applicability for role equivalences. It facilitates the expression of alternative and more nuanced definitions of role, such as notions that build on other mechanisms or those that are intended to accomodate and combine various actor and tie attributes as appropriate for the performed analysis.  
In this way, netroles generalizes prior tools and expands the scope of applicability of role equivalence concepts.

Netroles is a library implemented in and provided for Java, requiring Java 8 or newer. 

## Installation

Netroles is available in Maven Central. You can add it by using your preferred dependency management tool. When using Maven, for example, you can add netroles as a dependency in your POM file (replacing "x.y" by the appropriate version tag):

```xml
<dependency>
    <groupId>io.github.muellerj2</groupId>
    <artifactId>netroles-api</artifactId>
    <version>x.y</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>io.github.muellerj2</groupId>
    <artifactId>netroles-engine</artifactId>
    <version>x.y</version>
    <scope>runtime</scope>
</dependency>
```


Netroles includes its own implementation of network structures. Optionally, you can add the following artifacts to read networks into netroles' default network representation or write the network representation to files:

```xml
<dependency>
    <groupId>io.github.muellerj2</groupId>
    <artifactId>netroles-io-api</artifactId>
    <version>x.y</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>io.github.muellerj2</groupId>
    <artifactId>netroles-io-engine</artifactId>
    <version>x.y</version>
    <scope>runtime</scope>
</dependency>
```

In Gradle, you can add these dependencies as follows:
```gradle
dependencies {
  // required
  implementation "io.github.muellerj2:netroles-api:x.y"
  runtimeOnly "io.github.muellerj2:netroles-engine:x.y"
  
  // optional IO support
  implementation "io.github.muellerj2:netroles-io-api:x.y"
  runtimeOnly "io.github.muellerj2:netroles-io-engine:x.y"
}
```

Alternatively, you can add the following artifacts (available on the Releases page) to your Java project manually:
* netroles-api-\*.\*.jar
* netroles-engine-\*.\*.jar (only needed at runtime)
* netroles-networks-api-\*.\*.jar
* netroles-networks-engine-\*.\*.jar (only needed at runtime)
* netroles-io-api-\*.\*.jar (optional IO support)
* netroles-io-engine-\*.\*.jar (optional IO support, only needed at runtime)

In addition, netroles uses [SLF4J](https://www.slf4j.org/) for logging purposes, so you have to add SLF4J and an SLF4J binding to some logging library to your build path. If you also add the optional IO support to your project, you should further add the following external libraries:
* [Univocity parsers](https://github.com/uniVocity/univocity-parsers)
* [JSON-Java](https://github.com/stleary/JSON-java)
* [FasterXML Jackson Databind](https://github.com/FasterXML/jackson-databind)


> &#9432; ***Note***
> 
> You can make use of another library's network data structure than the default one bundled with netroles if you prefer to do so. To achieve this, you have to write an adapter implementing the NetworkView interface in package ch.ethz.sn.visone3.roles.position. You can then use this adapter to specify role concepts on these networks.


## Tutorial

The [tutorial](./docs/USAGE.md) presents common usage examples step by step.

## Scientific background and design

The [design document](./docs/DESIGN.md) explains:

* the theoretical background of this library in the area of Social Network Analysis, including the relevant ideas from the role analysis literature.
* the library's design principles.


## API Documentation

For each release, corresponding Javadoc documentation is made available on Maven Central and can be accessed on the external website [javadoc.io](https://javadoc.io/doc/io.github.muellerj2).

You can follow the links below to view the Javadoc of each API module on javadoc.io:
* [netroles-lang-api](https://javadoc.io/doc/io.github.muellerj2/netroles-lang-api/)
* [netroles-networks-api](https://javadoc.io/doc/io.github.muellerj2/netroles-networks-api/)
* [netroles-api](https://javadoc.io/doc/io.github.muellerj2/netroles-api/)
* [netroles-io-api](https://javadoc.io/doc/io.github.muellerj2/netroles-io-api/)

Besides the API modules, netroles also includes engine modules that implement the library's API. Users of netroles usually do not need to interact with these modules directly. But if you wish to access some feature of an engine module directly, you can browse the Javadoc for these modules as well:
* [netroles-lang-engine](https://javadoc.io/doc/io.github.muellerj2/netroles-lang-engine/)
* [netroles-networks-engine](https://javadoc.io/doc/io.github.muellerj2/netroles-networks-engine/)
* [netroles-engine](https://javadoc.io/doc/io.github.muellerj2/netroles-engine/)
* [netroles-io-engine](https://javadoc.io/doc/io.github.muellerj2/netroles-io-engine/)

## Working on the code

If you would like to work directly on the library's source code, fork this repository and download the library's source. Finally, you can import it into your favorite IDE for Java programming. The project uses Gradle as its build tool, so you might have to install a Gradle plugin to import the project easily into your IDE.


## Contributing

### Support, bug reports and enhancement requests

If you have questions how to use netroles, find a bug or would like to suggest an idea or enhancement, [please file an issue on the project's Github page](https://github.com/muellerj2/netroles/issues).

### Contributing code

If you would like to contribute code to the project, [please open a pull request](https://github.com/muellerj2/netroles/pulls). Your code will be considered for inclusion if you are agree to make it available under the project's license.