---
title: Using Nashorn for Scripting in Data Processing
author: Michael McAlynn
datestamp: October 11th 2017 - 11:00am
---

## Introduction

In this post I'm going to talk about the Nashorn engine in Java 8 and give some examples of how we utilize it in Data Processing.

## What is Nashorn?

Nashorn is a scripting engine written in Java that implements the ECMAScript 5.1 language specification, which is the same specification JavaScript can be written against. What this means is that the JVM can use this engine to execute a script provided in JavaScript. The JavaScript will be compiled to JVM bytecode for execution, this happens at runtime only. Nashorn is the default (only) scripting engine included in Java 8. Through Nashorn Java code can call into JavaScript and JavaScript can also call into Java code.

## Why use Nashorn?

### Popularity of JavaScript

The popularity of JavaScript as a development language has continued to grow over recent years. Traditionally its use was focused on client-side browser applications but this realm has expanded with the introduction of technology such as NodeJS which allows for implementing the back-end in JavaScript also. The language is seen as beginner friendly, evidenced by Stanford University moving their Introduction to Computer Science course language from Java to JavaScript. With that in mind it becomes beneficial to offer support to execute the language in a Java environment to support the growing numbers of those familiar with the language. 

### Isomorphic Development

The Nashorn engine allows for sharing of JavaScript code between the client and server implementations. A model can be defined once in JavaScript and then used in the client and also in a Java-based server environment. being able to share this common codebase avoids the pitfalls of having to maintain duplicate models in different languages and supports rapid delivery.

### Runtime Adaptive Coding

Support for scripting provides the capability to allow portions of logic for an application to be easily modified after the application has been deployed. Scripts can be passed to the application at runtime and executed alongside the existing logic, for example to perform some user specific post processing on a result.

### Shell Scripting

Nashorn offers support for using the JVM to execute JavaScript on the command line. The usefulness of this is that it enables someone already familiar with JavaScript to be able to write scripts in that language without having to pick up and utilize a second language to service the need for an executable script, avoiding time spent on issues arising from an unfamiliar language.

## Using Nashorn

### Shell Scripting

The application 'jjs' should be used to execute JavaScript from the command line. This is included in the `bin` folder of a JDK installation. To execute simply pass the path to your script file.

```
jjs $scriptFileName
```

Arguments can be passed to the script by using `--` after the script file name and then separating each separate argument with a space.

```
jjs $scriptFileName -- $arg1 $arg2
```

So given a script file 'example.js' which adds all the numbers it receives as arguments and prints them out;

```
var result = 0;

for each(var argument in arguments){
  if(isNaN(argument)===false){
    result += Number(argument);
  }
}

print(result);
```

The command to execute this and output would be;

```
jjs jjs_example.js -- 1 2 3 4 hello
>>> 10
```

The arguments passed are all received as string values so the script is responsible for coercing values to numbers where appropriate. Nashorn provides the method 'print' here and the 'for each' loop construct for convenience.

### Executing from Java

To execute JavaScript from within Java code the Nashorn engine must be retrieved from the ScriptEngineManager, which keeps track of all available scripting engines. The ScriptEngine retrieved represents the environment that scripts are evaluated in and serves as a global space for all evaluated scripts.

#### Evaluate Script

The script passed to the engine can be simple enough to simply need evaluation and nothing else. The below script will simply print out "Hello World" on evaluation.

```
engine.eval(“print(‘Hello World’)”);
```

The engine can be used with external scripts by loading them into the engine environment.

```
engine.eval("load('example.js');")
```

#### Using an Evaluated Script

Once a script has been evaluated in the engine, it's functions and objects can be accessed (if available).

```
function run(arguments){
  var result = 0;

  for each(var argument in arguments){
    if(isNaN(argument)===false){
      result += Number(argument);
    }
  }

  print(result);
}
```

Given the above modified version of example.js where the number addition is now wrapped in a function, the way this script is utilized now changes to evaluating the script so it is loaded into the engine environment and then invoking the `run` function from Java.

```
List<Integer> arguments = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

// Create a ScriptEngineManager object to allow us to retrieve the Nashorn script engine
final ScriptEngineManager engineManager = new ScriptEngineManager();

// Retrieve the Nashorn script engine
final ScriptEngine engine = engineManager.getEngineByName("nashorn");

// Evaluate the script so we can call functions in the script
engine.eval(testJs);

Invocable invoker = (Invocable) engine;
// Call the 'run' function in our script, passing the arguments defined earlier.
invoker.invokeFunction("run", arguments);
```

The sample Java project linked at the end of this post contains exaples of invoking a function, method on a JavaScript object and implementing an interface for use in Java with a JavaScript based object.

### Example of Data Processing Usage

Data Processing makes use of being able to execute provided JavaScript from Java in a number of ways.

#### Configuration Files

Originally configuration files for use with the Workers used by Data Processing were JSON files. Some enhancements were added over time such as allowing variable substitution for string values but certain desirable features were still lacking such as conditional selection of values to use in configuration, "if this environment value is present use it otherwise default to this" and even the ability to add comments to offer more detail in the file.

By using a configuration reader implementation built around Nashorn the configuration files for workers can now be supplied as JavaScript files. A simple method `getenv` was added to the engine environment the configuration file is evaluated in as a shorthand for Java System.getenv. The end result is something quite close to the original JSON configuration but with much more flexibility available.

##### Original JSON based config
```
{
    "prefetchBuffer": 1,
    "inputQueue": "langdetect-in",
    "retryLimit": 10,
    "rejectedQueue": "langdetect-rejectedqueue"
}

```

##### JavaScript based config

```
({
    prefetchBuffer: getenv("CAF_RABBITMQ_PREFETCH_BUFFER") || 1,
    inputQueue: getenv("CAF_WORKER_INPUT_QUEUE")
            || (getenv("CAF_WORKER_BASE_QUEUE_NAME") || getenv("CAF_WORKER_NAME") || "worker") + "-in",    
    retryLimit: getenv("CAF_WORKER_RETRY_LIMIT") || 10,
    rejectedQueue: "langdetect-rejectedqueue",
});
```

The JavaScript allows for an additional level of configuration that was not previously possible in the JSON version without continuing to add further custom logic. Using Nashorn we benefit from an already defined condition syntax that looks close enough to the original JSON to maintain clarity.

#### Runtime Configurable Condition Evaluation

In Data Processing the Markup Worker was to be used with different configurations depending on the metadata of the document. Each configuration needed to be a separate action entry in the workflow, with four entries in total;
* A configuration to generate fields for emails
* A configuration to generate hashes for emails
* A configuration to generate hashes for attachments
* A configuration to generate hashes for anything else

To streamline this into a single action in the workflow we wanted to move this condition evaluation and configuration passing to the markup worker itself. Rather than rewrite the worker input task we instead wrapped the core markup logic into a library and used a generic Document Worker that received a document. This then passes that document into a JavaScript function `processDocument` which calls the markup code with the required configuration depending on the conditions the document matches. This avoids making multiple trips between markup and the workflow worker, removing the overhead of transmitting the additional messages and converting results each time.

The JavaScript used can also be overridden in the container so if the conditions require modifying or if configuration eneds updating a new script file simply needs passed into the container and will be used during processing of the document.

### Summary

For Data Processing Nashorn has allowed us to offer additional functionality, flexibility and simplify our existing services. A sample project demonstrating invoking Nashorn is available in an archive [here](../../../../assets/files/nashorn-brown-bag-project.zip) and some useful learning resources for Nashorn are linked below.

### Links
[http://winterbe.com/posts/2014/04/05/java8-nashorn-tutorial](http://winterbe.com/posts/2014/04/05/java8-nashorn-tutorial/ )
- A tutorial for getting started.
 
[https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/toc.html ](https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/toc.html)
- Oracle Docs

#### Books
* Java in a Nutshell
* Scripting in Java
