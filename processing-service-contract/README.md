# processing-service-contract

This project packages the data-processing-service swagger contract used in the service into a jar file that can be retrieved as an artifact through Maven for use by other projects e.g. service calling library, UI generated from contract.

The generated contract can be added as a dependency as shown below;

```
<dependency>
    <groupId>com.github.cafdataprocessing</groupId>
    <artifactId>processing-service-contract</artifactId>
    <version>${version}</version>
</dependency>
```

  + The '${version}' should be replaced with the appropriate version of the packaged contract to use.