Сборка и запуск проекта: ./gradlew build && docker-compose build && docker-compose up

Сборка и запуск проекта (RabbitMQ): ./gradlew build && docker-compose -f docker-compose-partitions.yml build && docker-compose -f docker-compose-partitions.yml up

Сборка и запуск проекта (Kafka): ./gradlew build && docker-compose -f docker-compose-kafka.yml build && docker-compose -f docker-compose-kafka.yml up

Запуск конкретного dockerCompose файла: docker-compose -f имя_файла.yml up

Посмотреть контейнеры: docker-compose ps gateway eureka product-composite product recommendation review

Swagger: http://localhost:8080/openapi/swagger-ui.html

Actuator: http://localhost:8080/actuator/health

RabbitMQ: http://localhost:15672/#/queues

Eureka: http://localhost:8080/eureka/web

Eureka (зарегистрированные экземпляры): http://localhost:8080/eureka/api/apps

test