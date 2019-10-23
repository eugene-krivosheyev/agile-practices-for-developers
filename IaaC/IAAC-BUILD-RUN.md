# Infrastructure-as-a-Сode
Хосты и программные активы конвейера CI, реализованные на Ansible и Docker Compose.

# Запуск и остановка докеризованных сервисов CI и ELK локально
```bash
cd IaaC

docker-compose --file ansible/files/ci-docker-compose.yml up --detach
docker-compose --file ansible/files/elk-docker-compose.yml up --detach

docker-compose --file ansible/files/elk-docker-compose.yml down
docker-compose --file ansible/files/ci-docker-compose.yml down
```

# Развертывание инфраструктуры удаленно
## Smoke test перед удаленной раскаткой 
```bash
cd IaaC
ansible -i ansible/hosts.yml -m shell -a 'uname -a' all
ansible -i ansible/hosts.yml -m setup all
```
## Установка завсисмостей Ansible
```bash
ansible-galaxy install -r ansible/requirements.yml
```

## Раскатка сервисов CI/CD
```bash
cd IaaC
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml --limit ci_hosting --tags "ci" [--skip-tags "maven"] [--start-at-task='Shut down CI docker containers'] [--step] [-vvv] 
```

## Ручная настройка сервисов CI/CD
### [Bitbucket](http://84.201.134.115:7990)
- [ ] Лицензия
- [ ] Учетка
- [ ] Репошечка: git remote от репо текущего проекта
### [Bamboo](http://84.201.134.115:8085)
- [ ] Лицензия
- [ ] Учетка
- [ ] Disable Default Agent
- [ ] Remove unused capabilities
- [ ] Agents -> Disable remote agent authentication
- [ ] Maven capability for remote agents: Executable -> Maven 3.x -> Maven 3.6 -> /opt/maven/apache-maven-3.6.2
- [ ] General Configuration -> Broker configuration:
```
Broker URL: tcp://0.0.0.0:54663?wireFormat.maxInactivityDuration=300000
Broker client URL: failover:(tcp://84.201.134.115:54663?wireFormat.maxInactivityDuration=300000)?initialReconnectDelay=15000&maxReconnectAttempts=10
```
- [ ] [Sonar for Bamboo](http://84.201.134.115:8085/plugins/servlet/upm/marketplace/featured?source=side_nav_find_new_addons) Plugin
- [ ] Restart Bamboo CI
- [ ] [Artifactory Plugin](https://marketplace.atlassian.com/apps/27818/artifactory-for-bamboo?tab=installation)
### [Artifactory](http://84.201.134.115:8081)
- [ ] Учетка
- [ ] Репошечка: dbo, Allow Content Browsing
- [ ] Обновить данные в IaaC/ansible/files/maven-settings.xml
### [SonarQube](http://84.201.134.115:9000)
- [ ] Учетка
- [ ] Плагины покрытия
- [ ] Настройки плагинов покрытия

## Раскатка сервисов ELK
```bash
cd IaaC
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml --limit ci_hosting --tags "elk"
```
- [ ] [Kibana](http://84.201.134.115:5601/app/kibana#/management/elasticsearch/index_management/indices?_g=())

## Раскатка сервисов на сборочный агент ci_agent
- [ ] Ensure [Bamboo](http://84.201.134.115:8085) is running
```bash
cd IaaC
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml --limit ci_agent
```

## Раскатка сервисов на pre_prod
- [ ] Ensure [Bamboo](http://84.201.134.115:8085) is running
```bash
cd IaaC
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml --limit pre_prod
```

## Ручное копирование приложения на пре-прод и запуск
```bash
cd ..
./mvnw -T 1C package -DskipTests -DdependencyLocationsEnabled=false -Dlogback.configurationFile=logback.xml -Djava.awt.headless=true
[ssh-keygen -R 84.201.157.139]
ssh -i ~/Dropbox/Eugene/Backups/agile-practices-dev.pem admin@84.201.157.139
scp -i ~/Dropbox/Eugene/Backups/agile-practices-dev.pem target/dbo-1.0-SNAPSHOT.jar admin@84.201.157.139:/dbo/
[admin@pre-prod:~$ mvn -s /home/bambooagent/.m2/settings.xml org.apache.maven.plugins:maven-dependency-plugin:2.4:get -Dtransitive=false -Dartifact=com.acme.banking:dbo:1.0-SNAPSHOT -Ddest=/dbo/dbo-1.0-SNAPSHOT.jar -DremoteRepositories=dbo-artifacts-server::::http://84.201.134.115:8081/artifactory/dbo] 
admin@pre-prod:~$ cd /dbo
admin@pre-prod:~$ nohup java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=84.201.157.139 -jar dbo-1.0-SNAPSHOT.jar &
```
- [ ] Test Restful WebService: 
```bash
curl --request GET --header "X-API-VERSION:1" --url http://84.201.157.139:8080/dbo/api/client
curl --request GET --header "X-API-VERSION:1" --url http://84.201.157.139:8080/dbo/api/client/11
```
- [ ] [Kibana](http://84.201.134.115:5601/app/kibana#/management/elasticsearch/index_management/indices?_g=())
- [ ] [Create Index Pattern wizard](http://84.201.134.115:5601/app/kibana#/management/kibana/index_pattern?_g=())
- [ ] [dbo index](http://84.201.134.115:5601/app/kibana#/discover?_g=())
- [ ] [dbo stream](http://84.201.134.115:5601/app/infra#/logs/settings?_g=()), Log indices -> logstash*
- [ ] Stream live
```bash
admin@pre-prod: ps -af
jvisualvm -> Add remote JMX connection 84.201.157.139:9999
curl --request POST http://84.201.157.139:8080/dbo/actuator/shutdown
[admin@pre-prod: pkill -9 -f "dbo-1.0-SNAPSHOT.jar"]
```

## Запуск приложения как сервиса
```bash
[admin@pre-prod:/$ [/bin/sh -c '] sudo update-rc.d dbo-app defaults]
admin@pre-prod:/$ [/bin/sh -c '] sudo service dbo-app restart
admin@pre-prod:/$ sudo service dbo-app status
admin@pre-prod:/$ systemctl status dbo-app.service
```
