Сборка и запуск проекта: ./gradlew build && docker-compose build && docker-compose up

Сборка и запуск проекта (RabbitMQ): ./gradlew build && docker-compose -f docker-compose-partitions.yml build && docker-compose -f docker-compose-partitions.yml up

Сборка и запуск проекта (Kafka): ./gradlew build && docker-compose -f docker-compose-kafka.yml build && docker-compose -f docker-compose-kafka.yml up

Запуск конкретного dockerCompose файла: docker-compose -f имя_файла.yml up

Посмотреть контейнеры: docker-compose ps gateway eureka product-composite product recommendation review

Swagger: https://localhost:8443/openapi/webjars/swagger-ui/index.html

Actuator: http://localhost:8080/actuator/health

RabbitMQ: http://localhost:15672/#/queues

Eureka: http://localhost:8080/eureka/web

Eureka (зарегистрированные экземпляры): http://localhost:8080/eureka/api/apps

Eureka (зарегистрированные экземпляры): curl -H "accept:application/json" https://u:p@localhost:8443/eureka/api/apps -ks | jq -r .applications.application[].instance[].instanceId

Получение токена (чтение, запись): curl -k https://writer:secret-writer@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read product:write" -s | jq .

Получение токена (чтение): curl -k https://reader:secret-reader@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read" -s | jq .

Получение токена через код авторизации (чтение + refresh token): 
1. https://localhost:8443/oauth2/authorize?response_type=code&client_id=reader&redirect_uri=https://my.redirect.uri&scope=product:read&state=35725
3. curl -k https://reader:secret-reader@localhost:8443/oauth2/token -d grant_type=authorization_code -d client_id=reader -d redirect_uri=https://my.redirect.uri -d code=$CODE(из предыдущего ответа) -s | jq .

Команды в контейнере:
1. kafka-topics --list --bootstrap-server localhost:9092  == топики
2. kafka-consumer-groups --bootstrap-server localhost:9092 --list