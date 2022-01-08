```
docker-compose -f ./axon-server/docker-compose.yml up --force-recreate
```

```
jenv shell 11
```

```
./gradlew :order-service:bootRun
./gradlew :payment-service:bootRun
./gradlew :shipment-service:bootRun
```

* http://localhost:8024
* http://localhost:8024/actuator/health
* http://localhost:8080/swagger-ui/