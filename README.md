:construction: This prototype is in the process of being refined and may exhibit bugs :construction:

# mason-ssa: An SSA extension for MASON

This extension to the [MASON framework](https://cs.gmu.edu/~eclab/projects/mason/) provides facilities to implement agent-based models (ABM) and simulating them via stochastic simulation algorithms (SSAs).
Its key feature is the support of automatic dependency-tracking using an aspect-oriented approach. This way it combines the benefits from both worlds:
**model definition in a general purpose language** (Java) and **increased performance** when inter-agent dependencies can be exploited during a simulation step.
Supported SSAs:

<img align="right" height="150" src="resources/demo-preview.png" alt="A SIRS Model Display with grid-like contact structure">

- First Reaction Method
- Direct Method
- Next Reaction Method (with dependency tracking)
- Direct Method (with dependency tracking)

## Getting Started

### :white_check_mark: Prerequisites

- Maven (tested with 3.8.2)
- Java 8 (or compatible)
- MASON
  - the version found on the Maven Central Repository is out of date (14.0)
  - Version 20 can be installed from its [GitHub repository](https://github.com/eclab/mason)

### :cd: Installation

- manual
  - clone this repository: `git clone https://github.com/justinnk/mason-ssa.git`
  - install the extension in your local repository: `mvn clean install`
- from mvn central
  - include the extension as a dependency via maven using the atrifactId `io.github.justinnk.masonssa.<module>`
  - have a look at the [demo models](https://github.com/justinnk/mason-ssa-demo.git) for an example pom

### :rocket: Run Demo Model

- clone the demo repository: `git clone https://github.com/justinnk/mason-ssa-demo.git`
- navigate into the created folder: `cd mason-ssa-demp`
- run `sh build.sh && sh run_showcase.sh`
- The script currently only works / is only tested on Linux
- with some adaptions, it should also run on windows
- todo: add batch script

## :arrow_forward: Usage

For an example project, have a look at the [demo repository](https://github.com/justinnk/mason-ssa-demo.git).

1. In your maven `pox.xml` add the following dependencies
  ```xml
  <dependecies>
    <dependency>
      <groupId>io.github.justinnk.masonssa</groupId>
      <artifactId>extension</artifactId>
      <version>0.0.1</version>
    </dependency>
    <dependency>
      <groupId>io.github.justinnk.masonssa</groupId>
      <artifactId>aspects-base</artifactId>
      <version>0.0.1</version>
    </dependency>
    <dependency>
      <groupId>io.github.justinnk.masonssa</groupId>
      <artifactId>aspects-nrm</artifactId>
      <version>0.0.1</version>
    </dependency>
    <dependency>
      <groupId>io.github.justinnk.masonssa</groupId>
      <artifactId>aspects-odm</artifactId>
      <version>0.0.1</version>
    </dependency>
  </dependencies>
  ```

2. In order to weave the aspects, the following plugin has to be added in the `build>plugins` section of your `pom.xml`
  ```xml
   <!-- we use a plugin to invoke the aspectj compiler (ajc) in the build process, after the classes have been
              compiled using javac. We define weave dependencies to weave aspects into existing jar files of the
               extension and MASON. We also define the aspect libraries to use, which are provided by the extension. -->
  <plugin>
    <groupId>com.nickwongdev</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.12.6</version>
    <configuration>
      <!-- disable annotation processors, since the extension uses .aj files -->
      <proc>none</proc>
      <complianceLevel>1.8</complianceLevel>
      <source>1.8</source>
      <target>1.8</target>
      <!-- be verbose for educational and debugging purposes -->
      <showWeaveInfo>true</showWeaveInfo>
      <verbose>true</verbose>
      <!-- throw warning instead of error if some aspect has not been applied -->
      <Xlint>warning</Xlint>
      <encoding>UTF-8</encoding>
      <forceAjcCompile>false</forceAjcCompile>
      <!-- enable weaving for mason-internal classes and extension classes -->
      <weaveDependencies>
        <weaveDependency>
          <groupId>cs.gmu.edu.eclab</groupId>
          <artifactId>mason</artifactId>
        </weaveDependency>
        <weaveDependency>
          <groupId>io.github.justinnk.masonssa</groupId>
          <artifactId>extension</artifactId>
        </weaveDependency>
      </weaveDependencies>
      <!-- include aspects from the ssa extension -->
      <aspectLibraries>
        <aspectLibrary>
          <groupId>io.github.justinnk.masonssa</groupId>
          <artifactId>aspects-base</artifactId>
        </aspectLibrary>
        <!-- if you want to use the ODM instead, include aspects-odm here -->
        <aspectLibrary>
          <groupId>io.github.justinnk.masonssa</groupId>
          <artifactId>aspects-nrm</artifactId>
        </aspectLibrary>
      </aspectLibraries>
    </configuration>
    <executions>
      <execution>
        <!-- only weave after classes are compiled by javac -->
        <goals>
          <goal>compile</goal>
        </goals>
      </execution>
    </executions>
    <dependencies>
      <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjtools</artifactId>
        <version>${aspectj.version}</version>
      </dependency>
    </dependencies>
  </plugin> 
  ```

3. Now you can use the extension like described in the documentation section

## :book: Documentation

TODO

## Roadmap

- Add the plugin to maven central
  - set up GitHub CI/CD
- (Re-)implement automatic performance checking for easy testing of improvements
- Improve dependency graph efficiency
- implement capabilities left as future work

## :woman_technologist: :man_technologist: Contributing

Feel free to open issues.
The project is still in the process of being prepared for contributions (i.e. documentation, source-code comments, etc. have to be refined/added).
Once this is done, see [Contributing](CONTRIBUTING.md) for more information.

## :balance_scale: License

This project is distributed under the terms of the Apache-2.0 license, see [LICENSE](LICENSE).
This project makes use of external libraries. Their copyright information can be found in [NOTICES](NOTICES).

## Literature

- Tutorial: [preliminary version](literature/tutorial.pdf)
- Thesis: [abstract](literature/thesis_abstract.pdf)
- Paper: to be published

## Cite

```
TODO
```
