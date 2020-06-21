# MSSC Beer Order Service

[![Build Status](https://travis-ci.com/shreeshasa/mssc-beer-order-service.svg?branch=master)](https://travis-ci.com/shreeshasa/mssc-beer-order-service)

```
mvn clean package docker:build docker:push -Ddocker.username=<user> -Ddocker.password=<password>
```

```
docker run -it --rm -p 8161:8161 -p 61616:61616 vromero/activemq-artemis
```

```
docker run -d -p 9411:9411 openzipkin/zipkin
```

```
keytool -importcert -alias DoMySQLCert -file ca-certificate.crt -keystore <truststore> -storepass <password>
```

```
-Djavax.net.ssl.trustStore=<path_to_truststore> -Djavax.net.ssl.trustStore=<password>
```