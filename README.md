```
docker-compose up --force-recreate
```

```
jenv shell 11
```

```
./gradlew :order-rest:bootRun
./gradlew :product-rest:bootRun
./gradlew :product-command-handler:bootRun
./gradlew :product-query-handler:bootRun
./gradlew :payment-command-handler:bootRun
```

* http://localhost:8024
* http://localhost:8080/swagger-ui/
* http://localhost:8081/swagger-ui/
