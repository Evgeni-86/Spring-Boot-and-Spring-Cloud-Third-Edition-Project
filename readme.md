Сборка и запуск проекта: 
```
./gradlew build && docker-compose build && docker-compose up
```  
Сборка и запуск проекта (RabbitMQ): 
```
./gradlew build && docker-compose -f docker-compose-partitions.yml build && docker-compose -f docker-compose-partitions.yml up  
```
Сборка и запуск проекта (Kafka): 
```
./gradlew build && docker-compose -f docker-compose-kafka.yml build && docker-compose -f docker-compose-kafka.yml up  
```
Запуск конкретного dockerCompose файла: 
```
docker-compose -f имя_файла.yml up  
```
Swagger: https://localhost:8443/openapi/webjars/swagger-ui/index.html  
Actuator: http://localhost:8080/actuator/health  
RabbitMQ: http://localhost:15672/#/queues  
Zipkin: http://localhost:9411/zipkin

Команды в контейнере:
```
kafka-topics --list --bootstrap-server localhost:9092
```
```
kafka-consumer-groups --bootstrap-server localhost:9092 --list
```
```
curl -s http://product-composite:8080/actuator/health
```
```
product-composite curl -s http://product-composite:8080/actuator/health | jq -r .components.circuitBreakers.details.product.details.state
```
__K8S (команды):__
```
kubectl get nodes
```
```
kubectl get pods --all-namespaces
```
```
./minikube.exe stop
```
```
./minikube.exe start --profile=handson-spring-boot-cloud
```
Удалить кластер:
```
./minikube.exe delete --profile handson-spring-boot-cloud
```
Выполнит запрос к сервису:
```
kubectl exec -it deploy/product -n hands-on -- curl localhost/actuator/health/liveness -s | jq .
```
### __Deploying to Kubernetes (dev):__
1. __Создать кластер:__
```
docker context use default  
```
```
./minikube.exe start --profile=handson-spring-boot-cloud --memory=5240 --cpus=4 --disk-size=25g --driver=docker --ports=8080:80 --ports=8443:443 --ports=30080:30080 --ports=30443:30443  
```
```
./minikube.exe profile handson-spring-boot-cloud  
```
```
./minikube.exe addons enable ingress  
```
```
./minikube.exe addons enable metrics-server  
```
2. __Собрать и поместить в minikube образы сервисов__
```
eval $(./minikube.exe docker-env) --переключиться на docker в minikube  
```
```
docker-compose build --собрать контейнеры приложения
```
3. __Собрать Чарты:__
```
for f in kubernetes/helm/components/*; do helm dep up $f; done
```  
```
for f in kubernetes/helm/environments/*; do helm dep up $f; done
```  
```
helm dep ls kubernetes/helm/environments/dev-env/
```
4. __Загрузить образы в докере minikube__
```
eval $(minikube docker-env)  
```
```
docker pull mysql:8.0.32  
```
```
docker pull mongo:6.0.4  
```
```
docker pull rabbitmq:3.11.8-management  
```
```
docker pull openzipkin/zipkin:2.24.0
```
5. __Установить Чарты__
```
helm template kubernetes/helm/environments/dev-env
```  
```
helm install hands-on-dev-env kubernetes/helm/environments/dev-env -n hands-on --create-namespace
```  
```
kubectl config set-context $(kubectl config current-context) --namespace=hands-on
```
```
kubectl get pods --watch  
```
```
kubectl get pods --all-namespaces
```
### __Deploying to Kubernetes (prod):__
1. __Запустить образы в docker minikube__

Переключиться на docker в minikube: 
```
eval $(./minikube.exe docker-env)
```  
Запускаем зависимости проекта используя docker в minikube отдельно от кластера: 
```
docker-compose up -d mongodb mysql rabbitmq
```
2. __Добавление тегов к сервисам__  
```
docker tag hands-on/auth-server hands-on/auth-server:v1
```  
```
docker tag hands-on/config-server hands-on/config-server:v1
```  
```
docker tag hands-on/gateway hands-on/gateway:v1  
```
```
docker tag hands-on/product-composite-service hands-on/product-composite-service:v1  
```
```
docker tag hands-on/product-service hands-on/product-service:v1  
```
```
docker tag hands-on/recommendation-service hands-on/recommendation-service:v1  
```
```
docker tag hands-on/review-service hands-on/review-service:v1  
```
Манифесты для микросервиса продукта, ConfigMap, Service, объект Deployment показать.  
```
helm template kubernetes/helm/components/product --set envFromSecretRefs="{rabbitmq-credentials, mongodb-credentials}"
```
3. __Добавить репозиторий, обновить, установить cert-manager:__  
```
helm repo add jetstack https://charts.jetstack.io
```  
```
helm repo update
```  
```
helm install cert-manager jetstack/cert-manager --create-namespace --namespace cert-manager --version v1.11.0 --set installCRDs=true --wait
```  
Возможно необходимо сопоставить DNS: 
```
sudo bash -c "echo 127.0.0.1 minikube.me | tee -a /etc/hosts"
```
4. __Собрать Чарты:__
```
for f in kubernetes/helm/components/*; do helm dep up $f; done
```  
```
for f in kubernetes/helm/environments/*; do helm dep up $f; done
```
```
helm template kubernetes/helm/environments/prod-env
```
5. __Установить чарты__
```
helm install hands-on-prod-env kubernetes/helm/environments/prod-env -n hands-on --create-namespace
```
```
kubectl config set-context $(kubectl config current-context) --namespace=hands-on
```
6. __Используемые контейнеры в среде:__ 
```
6. kubectl get pods -n hands-on -o json | jq .items[].spec.containers[].image
```
3. __Запуск тестов__  
```
HOST=localhost PORT=8443 USE_K8S=true ./test-em-all.bash
```  
```
CONFIG_SERVER_USR=prod-usr CONFIG_SERVER_PWD=prod-pwd PORT=8443 USE_K8S=true ./test-em-all.bash
```
4. __Запросы__  
*Получить токен:* 
```
ACCESS_TOKEN=$(curl -d grant_type=client_credentials -ks https://writer:secret-writer@localhost:8443/oauth2/token -d scope="product:read product:write" | jq .access_token -r)
```  
*Посмотреть токен:* 
```
echo $ACCESS_TOKEN
```  
*Запрос:* 
```
curl -kH "Authorization: Bearer $ACCESS_TOKEN" https://localhost:8443/product-composite/1?delay=0
```
5. __Удалить пространство имен:__ 
```
kubectl delete namespace hands-on
```  
6. __Остановить контейнеры в docker minikube (выполнят из каталога проекта)__  
```
eval $(./minikube.exe docker-env)
```  
```
docker-compose down
```  
```
eval $(./minikube.exe docker-env -u)
```  
