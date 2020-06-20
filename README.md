# MSSC Beer Order Service

```
mvn clean package docker:build docker:push -Ddocker.username=<user> -Ddocker.password=<password>
```

```
docker run -it --rm -p 8161:8161 -p 61616:61616 vromero/activemq-artemis
```

```
docker run -d -p 9411:9411 openzipkin/zipkin
```