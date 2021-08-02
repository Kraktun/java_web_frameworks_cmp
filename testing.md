# Testing

- [Testing](#testing)
  - [Machine](#machine)
  - [Java](#java)
  - [Tests](#tests)
  - [Instructions](#instructions)
    - [Setup](#setup)
    - [Compile](#compile)
      - [Compile Quarkus](#compile-quarkus)
      - [Compile Quarkus Native](#compile-quarkus-native)
      - [Compile Spring](#compile-spring)
      - [Compile Spring Jetty](#compile-spring-jetty)
      - [Compile Micronaut](#compile-micronaut)
      - [Compile Micronaut Native](#compile-micronaut-native)
    - [Run tests](#run-tests)

## Machine

The machine used for the tests is a Ubuntu Server 20.04 Virtual Machine with 8 vCPU and 12GB or RAM. The virtualization is managed with [Proxmox](https://www.proxmox.com/en/proxmox-ve) version `6.4-11`. The host machine uses a Intel Xeon 2650v2 with 32GB of RAM, while both the machine and the supervisor run on a NVME SSD.  

## Java

The versions of Java used for the tests are:  

- [OpenJDK](https://openjdk.java.net/) 11.0.11+9
- [AdoptOpenJDK](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=hotspot) with Hotspot 11.0.11+9
- [AdoptOpenJDK](https://adoptopenjdk.net/?variant=openjdk11&jvmVariant=openj9) with OpenJ9 11.0.11+9
- [GraalVM](https://www.graalvm.org/) CE 21.1.0 (build 11.0.11+8-jvmci-21.1-b05)

## Tests

The tests are performed with [JMeter](https://jmeter.apache.org/) running on a separate machine, but hardwired to the test machine, this allows to avoid using the resources of the test machine and at the same time keep the physical latency, due to the use of different machines, below `1ms`. They include:

- **Startup time** = time from when the executable of the application is run until the first response is received. To compute this metric a client on the same machine (localhost) continuously performs requests to a specific `/ping` endpoint until it receives a valid response. The server simply returns a predefined string, to avoid any other type of computation.
- **Memory usage** = the memory usage considers the whole [rss memory](https://en.wikipedia.org/wiki/Resident_set_size) as suggested in [https://quarkus.io/guides/performance-measure](https://quarkus.io/guides/performance-measure). The command used is `ps`.
- **CPU usage** = the CPU usage is obtained using `top` in Linux. Note that by default it reports the total usage of all cores, so the number should be divided by the number of cores available. Besides this, consider that `top` can take some time to return a result (the time can be controlled with `-d`), so memory and CPU values can not be mapped `1:1`, but need to be rescaled to match the time frame of the computation (or you can use a single script to compute both values). In this project this is done directly when the graphs are plotted.
- **Response time/latency** = this value is extracted directly from the JMeter results and is the time from when a request is sent to when the corresponding response is received.

## Instructions

To compile the applications and run the tests follow the procedure below.  
First of all some variables are hardcoded in the instructions and scripts, so to change the folders structure or names, you will have to modify also the scripts.  

### Setup

First clone the git repository and `cd` into it. Then:  

```bash
cd scripts
chmod u+x *.sh
cd setup
chmod u+x setup.sh
# read setup.sh before executing it to check if you need to change something
./setup.sh
```

Then reboot. (**MANDATORY**).

The folder structure should now be like this:

```unknown
.
├── code
│   ├── micronautrest
│   │   ├── gradle
│   │   │   └── wrapper
│   │   ├── libs
│   │   └── src
│   │       ├── main
│   │       │   ├── java
│   │       │   │   └── it
│   │       │   │       └── unipd
│   │       │   │           └── stage
│   │       │   │               └── sl
│   │       │   │                   ├── controllers
│   │       │   │                   ├── errors
│   │       │   │                   ├── objects
│   │       │   │                   └── services
│   │       │   │                       └── repositories
│   │       │   └── resources
│   │       │       ├── db
│   │       │       │   └── migration
│   │       │       └── static
│   │       └── test
│   │           └── java
│   │               └── it
│   │                   └── unipd
│   │                       └── stage
│   │                           └── sl
│   ├── quarkusrest
│   │   ├── gradle
│   │   │   └── wrapper
│   │   ├── libs
│   │   └── src
│   │       ├── main
│   │       │   ├── docker
│   │       │   ├── generated
│   │       │   ├── java
│   │       │   │   └── it
│   │       │   │       └── unipd
│   │       │   │           └── stage
│   │       │   │               └── sl
│   │       │   │                   ├── controllers
│   │       │   │                   ├── exceptions
│   │       │   │                   │   └── wrappers
│   │       │   │                   ├── objects
│   │       │   │                   ├── reactive
│   │       │   │                   ├── services
│   │       │   │                   ├── utils
│   │       │   │                   └── validators
│   │       │   └── resources
│   │       │       └── META-INF
│   │       │           └── resources
│   │       ├── native-test
│   │       │   └── java
│   │       │       └── it
│   │       │           └── unipd
│   │       │               └── stage
│   │       │                   └── sl
│   │       └── test
│   │           └── java
│   │               └── it
│   │                   └── unipd
│   │                       └── stage
│   │                           └── sl
│   ├── quarkusrest_native
│   │   ├── gradle
│   │   │   └── wrapper
│   │   ├── libs
│   │   └── src
│   │       ├── main
│   │       │   ├── docker
│   │       │   ├── java
│   │       │   │   └── it
│   │       │   │       └── unipd
│   │       │   │           └── stage
│   │       │   │               └── sl
│   │       │   │                   ├── controllers
│   │       │   │                   ├── exceptions
│   │       │   │                   │   └── wrappers
│   │       │   │                   ├── objects
│   │       │   │                   ├── reactive
│   │       │   │                   ├── services
│   │       │   │                   ├── utils
│   │       │   │                   └── validators
│   │       │   └── resources
│   │       │       └── META-INF
│   │       │           └── resources
│   │       ├── native-test
│   │       │   └── java
│   │       │       └── it
│   │       │           └── unipd
│   │       │               └── stage
│   │       │                   └── sl
│   │       └── test
│   │           └── java
│   │               └── it
│   │                   └── unipd
│   │                       └── stage
│   │                           └── sl
│   ├── springrest
│   │   ├── gradle
│   │   │   └── wrapper
│   │   ├── libs
│   │   └── src
│   │       ├── main
│   │       │   ├── java
│   │       │   │   └── it
│   │       │   │       └── unipd
│   │       │   │           └── stage
│   │       │   │               └── sl
│   │       │   │                   └── springrest
│   │       │   │                       ├── controllers
│   │       │   │                       ├── errors
│   │       │   │                       ├── objects
│   │       │   │                       └── services
│   │       │   │                           └── jpa
│   │       │   └── resources
│   │       │       ├── static
│   │       │       └── templates
│   │       └── test
│   │           └── java
│   │               └── it
│   │                   └── unipd
│   │                       └── stage
│   │                           └── sl
│   │                               └── springrest
│   └── supportLib
│       ├── gradle
│       │   └── wrapper
│       └── src
│           ├── main
│           │   └── java
│           │       └── it
│           │           └── unipd
│           │               └── stage
│           │                   └── sl
│           │                       └── lib
│           │                           └── rsa
│           │                               └── exceptions
│           └── test
│               └── java
├── jmeter
├── results
│   ├── imgs
│   ├── raw
│   └── startup
│       ├── adopt-hs
│       ├── adopt-j9
│       ├── graal
│       └── open
├── runs
└── scripts
    ├── setup
    └── utils
        └── procname
```

### Compile

First `cd` to the main directory.  
_Run the `gradlew` commands one at a time (i.e. not the whole block) or they don't work._  

#### Compile Quarkus

```bash
cd code/quarkusrest
chmod u+x gradlew
./gradlew clean
./gradlew build
rm -r ../../runs/quarkus-app 2> /dev/null
mv build/quarkus-app ../../runs
# test run with
java -jar ../../runs/quarkus-app/quarkus-run.jar
```

#### Compile Quarkus Native

```bash
. scripts/change_sdk.sh graal
cd code/quarkusrest_native
chmod u+x gradlew
./gradlew clean
./gradlew buildNative -Dquarkus.profile=prod
mv build/quarkusrest_native-1.0.0-SNAPSHOT-runner ../../runs/quarkusrest_native
chmod u+x ../../runs/quarkusrest_native
# test run with
cd ../../runs
./quarkusrest_native
```

#### Compile Spring

```bash
cd code/springrest
chmod u+x gradlew
rm build.gradle
cp build.gradle.tomcat build.gradle
./gradlew clean
./gradlew bootJar
mv build/libs/springrest-0.0.1.jar ../../runs/springrest.jar
# test run with
java -jar ../../runs/springrest.jar
```

#### Compile Spring Jetty

```bash
cd code/springrest
chmod u+x gradlew
rm build.gradle
cp build.gradle.jetty build.gradle
./gradlew clean
./gradlew bootJar
mv build/libs/springrest-0.0.1.jar ../../runs/springrest_jetty.jar
# test run with
java -jar ../../runs/springrest_jetty.jar
```

#### Compile Micronaut

```bash
cd code/micronautrest
chmod u+x gradlew
./gradlew clean
./gradlew shadowJar
mv build/libs/micronautrest-0.1-all.jar ../../runs/micronautrest.jar
# test run with
java -jar ../../runs/micronautrest.jar
```

#### Compile Micronaut Native

```bash
. scripts/change_sdk.sh graal
cd code/micronautrest
chmod u+x gradlew
./gradlew clean
./gradlew nativeImage
mv build/native-image/micronautrest_native ../../runs/micronautrest_native
chmod u+x ../../runs/micronautrest_native
# test run with
cd ../../runs
./micronautrest_native
```

Your `runs` folder should look like this:

```unknown
.
├── micronautrest.jar
├── micronautrest_native
├── quarkus-app
│   ├── app
│   │   └── quarkusrest-1.0.0-SNAPSHOT.jar
│   ├── lib
│   │   ├── boot
│   │   │   └── ...
│   │   └── main
│   │       └── ...
│   ├── quarkus
│   │   └── ...
│   ├── quarkus-app-dependencies.txt
│   └── quarkus-run.jar
├── quarkusrest_native
├── springrest.jar
└── springrest_jetty.jar
```

### Run tests

```bash
cd scripts
```

If you are using a JVM-based application, set the appropriate JVM before any test with

```bash
. change_sdk.sh graal # change 'graal' with whatever you prefer among graal, open, adopt-hs, adopt-j9
```

pay attention to use `.` or `source`, don't use `./` when you change the sdk.

- **Startup time:**  
    For example for Spring  

    ```bash
    ./startup_test.sh spring
    ```

    This will produce the output file `log/spring_startup.txt`.  
    If you want to repeat the test multiple times, for example `10`, run

    ```bash
    ./repeated_startup_tests.sh spring 10
    ```

- **CPU\RAM usage & latency:**  
    On the server run

    ```bash
    ./test_spring.sh
    ```

    When the server is online start your jmeter tests from your local machine or from another machine as follow (`cd` to `jmeter` folder):

    ```bash
    REM_IP= # the IP of the server
    NUM_SECONDS= # number of seconds for the ramp up period
    NUM_THREADS= # number of threads to create
    SERVICE_NAME_CRUD= # output filename
    TARGET_MODE= # either CRUD or rsa_brute

    jmeter -Jjmeter.save.saveservice.output_format=csv -Jjmeter.save.saveservice.assertion_results_failure_message=false -Jjmeter.save.saveservice.assertion_results=none -Jjmeter.save.saveservice.data_type=false -Jjmeter.save.saveservice.label=true -Jjmeter.save.saveservice.response_code=false -Jjmeter.save.saveservice.response_data=false -Jjmeter.save.saveservice.response_data.on_error=false -Jjmeter.save.saveservice.response_message=false -Jjmeter.save.saveservice.successful=true -Jjmeter.save.saveservice.thread_name=true -Jjmeter.save.saveservice.time=true -Jjmeter.save.saveservice.subresults=false -Jjmeter.save.saveservice.assertions=false -Jjmeter.save.saveservice.latency=true -Jjmeter.save.saveservice.connect_time=false -Jjmeter.save.saveservice.samplerData=false -Jjmeter.save.saveservice.responseHeaders=false -Jjmeter.save.saveservice.requestHeaders=false -Jjmeter.save.saveservice.encoding=false -Jjmeter.save.saveservice.bytes=false -Jjmeter.save.saveservice.sent_bytes=false -Jjmeter.save.saveservice.url=false -Jjmeter.save.saveservice.filename=false -Jjmeter.save.saveservice.hostname=false -Jjmeter.save.saveservice.thread_counts=false  -Jjmeter.save.saveservice.sample_count=false -Jjmeter.save.saveservice.idle_time=false -Jjmeter.save.saveservice.default_delimiter=, -Jjmeter.save.saveservice.print_field_names=true -Jjmeter.save.saveservice.timestamp_format=ms -Jrem_ip=$REM_IP -Jnum_threads=$NUM_THREADS -Jramp_sec=$NUM_SECONDS -n -t ${TARGET_MODE}.jmx -l results/${SERVICE_NAME}_${TARGET_MODE}.csv
    ```

    Once jmeter is done stop the server with `q` + `Enter`.

    The output file for latency will be in `results/${SERVICE_NAME}_${TARGET_MODE}.csv` in the client machine, while the results for CPU and RAM will be in the server in `log/*_cpu.txt` and `log/*_rss.txt`.  
    Note that the files are replaced when the application is restarted so make sure to copy them.  
