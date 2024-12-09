Сборка и запуск проекта - ./gradlew build && docker-compose build && docker-compose up

Сборка и запуск проекта (RabbitMQ) - ./gradlew build && docker-compose -f docker-compose-partitions.yml build && docker-compose -f docker-compose-partitions.yml up

Сборка и запуск проекта (Kafka) - ./gradlew build && docker-compose -f docker-compose-kafka.yml build && docker-compose -f docker-compose-kafka.yml up

Swagger - http://localhost:8080/openapi/swagger-ui.html

Actuator - http://localhost:8080/actuator/health

RabbitMQ - http://localhost:15672/#/queues

Запуск конкретного dockerCompose файла - docker-compose -f имя_файла.yml up