Сборка и запуск проекта: ./gradlew build && docker-compose build && docker-compose up  
Сборка и запуск проекта (RabbitMQ): ./gradlew build && docker-compose -f docker-compose-partitions.yml build && docker-compose -f docker-compose-partitions.yml up  
Сборка и запуск проекта (Kafka): ./gradlew build && docker-compose -f docker-compose-kafka.yml build && docker-compose -f docker-compose-kafka.yml up  
Запуск конкретного dockerCompose файла: docker-compose -f имя_файла.yml up  
Посмотреть контейнеры: docker-compose ps gateway eureka product-composite product recommendation review

Swagger: https://localhost:8443/openapi/webjars/swagger-ui/index.html  
Actuator: http://localhost:8080/actuator/health  
RabbitMQ: http://localhost:15672/#/queues  
Eureka: http://localhost:8080/eureka/web  
Zipkin: http://localhost:9411/zipkin

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
3. curl -s http://product-composite:8080/actuator/health
4. product-composite curl -s http://product-composite:8080/actuator/health | jq -r .components.circuitBreakers.details.product.details.state

K8S
1. docker context use default
2. ./minikube.exe start --profile=handson-spring-boot-cloud --memory=5240 --cpus=4 --disk-size=25g --driver=docker --ports=8080:80 --ports=8443:443 --ports=30080:30080 --ports=30443:30443
3. ./minikube.exe profile handson-spring-boot-cloud
4. ./minikube.exe addons enable ingress
5. ./minikube.exe addons enable metrics-server
6. kubectl get nodes
7. kubectl get pods --all-namespaces
8. ./minikube.exe stop
9. ./minikube.exe start --profile=handson-spring-boot-cloud
10. Создать пространство имен: kubectl create namespace first-attempts
11. Обновление контекста для использования пространства имен: kubectl config set-context $(kubectl config current-context) --namespace=first-attempts
12. Создание деплоймента(развертывание): kubectl apply -f .\nginx-deployment.yaml
13. Посмотреть сервисы: kubectl get services. Запрос к сервису кластера: http://localhost:30080
14. Запуск пода для выполнения curl запроса: kubectl run -i --rm --restart=Never curl-client --image=curlimages/curl --command -- curl -s 'http://nginx-service:80'
15. Удалить пространство имен: kubectl delete namespace first-attempts
16. Удалить кластер: ./minikube.exe delete --profile handson-spring-boot-cloud
17. Выполнит запрос к сервису: kubectl exec -it deploy/product -n hands-on -- curl localhost/actuator/health/liveness -s | jq .

Deploying to Kubernetes (dev):
1. Создать кластер

docker context use default  
./minikube.exe start --profile=handson-spring-boot-cloud --memory=5240 --cpus=4 --disk-size=25g --driver=docker --ports=8080:80 --ports=8443:443 --ports=30080:30080 --ports=30443:30443  
./minikube.exe profile handson-spring-boot-cloud  
./minikube.exe addons enable ingress  
./minikube.exe addons enable metrics-server  

2. Собрать и поместить в minikube образы сервисов 

eval $(./minikube.exe docker-env) --переключиться на docker в minikube  
docker-compose build --собрать контейнеры приложения

for f in kubernetes/helm/components/*; do helm dep up $f; done  
for f in kubernetes/helm/environments/*; do helm dep up $f; done  
helm dep ls kubernetes/helm/environments/dev-env/

eval $(minikube docker-env)  
docker pull mysql:8.0.32  
docker pull mongo:6.0.4  
docker pull rabbitmq:3.11.8-management  
docker pull openzipkin/zipkin:2.24.0

helm template kubernetes/helm/environments/dev-env  
helm install hands-on-dev-env kubernetes/helm/environments/dev-env -n hands-on --create-namespace  
kubectl config set-context $(kubectl config current-context) --namespace=hands-on

kubectl get pods --watch  
kubectl get pods --all-namespaces

Deploying to Kubernetes (prod):
1. Запустить образы в docker minikube  
Переключиться на docker в minikube: ```eval $(./minikube.exe docker-env)```  
Запускаем зависимости отдельно от кластера: ```docker-compose up -d mongodb mysql rabbitmq```

2. Добавление тегов к сервисам  
docker tag hands-on/auth-server hands-on/auth-server:v1  
docker tag hands-on/config-server hands-on/config-server:v1  
docker tag hands-on/gateway hands-on/gateway:v1  
docker tag hands-on/product-composite-service hands-on/product-composite-service:v1  
docker tag hands-on/product-service hands-on/product-service:v1  
docker tag hands-on/recommendation-service hands-on/recommendation-service:v1  
docker tag hands-on/review-service hands-on/review-service:v1  

```helm install hands-on-prod-env kubernetes/helm/environments/prod-env -n hands-on --create-namespace```  
Используемые контейнеры в среде: ```kubectl get pods -n hands-on -o json | jq .items[].spec.containers[].image```

3. Запуск тестов  
```CONFIG_SERVER_USR=prod-usr CONFIG_SERVER_PWD=prod-pwd PORT=30443 USE_K8S=true ./test-em-all.bash```

4. Запросы  
Получить токен: ```ACCESS_TOKEN=$(curl -d grant_type=client_credentials -ks https://writer:secret-writer@localhost:30443/oauth2/token -d scope="product:read product:write" | jq .access_token -r)```  
Посмотреть токен: ```echo $ACCESS_TOKEN```  
Запрос: ```curl -kH "Authorization: Bearer $ACCESS_TOKEN" https://localhost:30443/product-composite/1?delay=0```  

5. Удалить пространство имен: ```kubectl delete namespace hands-on```  
6. Остановить контейнеры в docker minikube (выполнят из каталога проекта)  
```eval $(./minikube.exe docker-env)```  
```docker-compose down```  
```eval $(./minikube.exe docker-env -u)```  
