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
ssh-keygen -R 84.201.134.115
ssh-keygen -R 84.201.157.139
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
- [ ] Репошечка: dbo-app
- [ ] git remote для текущего проекта в IDEA
- [ ] git push
```bash
git remote add bitbucket http://84.201.134.115:7990/scm/dbo/dbo-app.git 
git push -u bitbucket 
```
### [Bamboo](http://84.201.134.115:8085)
- [ ] Лицензия
- [ ] Учетка
- [ ] [Связать с Bitbucket черезLinked Repositories](http://84.201.134.115:8085/admin/configureLinkedRepositories!doDefault.action): dbo-app-master
- [ ] [Активировать Specs](http://84.201.134.115:8085/admin/configureLinkedRepositories!doDefault.action): репо -> Scan for Bamboo Specs
- [ ] Disable Default Agent (in-docker)
- [ ] Agents -> Disable remote agent authentication
- [ ] General Configuration -> Broker configuration:
```
Broker URL: tcp://0.0.0.0:54663?wireFormat.maxInactivityDuration=300000
Broker client URL: failover:(tcp://84.201.134.115:54663?wireFormat.maxInactivityDuration=300000)?initialReconnectDelay=15000&maxReconnectAttempts=10
```
- [ ] [Sonar Plugin for Bamboo](http://84.201.134.115:8085/plugins/servlet/upm/marketplace/featured?source=side_nav_find_new_addons) Plugin
- [ ] [Configure](http://84.201.134.115:8085/admin/sonar4bamboo/viewSonarServerConfigs.action) Sonar: sonarqube, http://84.201.134.115:9000
- [ ] Restart Bamboo CI
### [Artifactory](http://84.201.134.115:8081)
- [ ] Учетка
- [ ] Локальная репошечка: dbo-corp, Allow Content Browsing
- [ ] Удаленная репошечка: mvncentral, https://repo1.maven.org/maven2, Allow Content Browsing
- [ ] Виртуальная репошечка dbo: добавить dbo-corp + mavencentral, Artifactory Requests Can Retrieve Remote Artifacts, Default Deployment Repository
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
- [ ] Ensure [Remote Agent](http://84.201.134.115:8085/admin/agent/configureAgents!doDefault.action) online
- [ ] Ensure Capabilities for this Remote Agent:
- JDK 1.8 -> /usr/lib/jvm/java-8-openjdk-amd64
- Maven 3.6 -> /opt/maven/apache-maven-3.6.2

## Раскатка сервисов на pre_prod
- [ ] Ensure [Bamboo](http://84.201.134.115:8085) is running
```bash
cd IaaC
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml --limit pre_prod
```
- [ ] Ensure [Remote Agent](http://84.201.134.115:8085/admin/agent/configureAgents!doDefault.action) online
- [ ] Ensure Capabilities for this Remote Agent:
- JRE 1.8 -> /usr/lib/jvm/java-8-openjdk-amd64/jre
- Maven 3.6 -> /opt/maven/apache-maven-3.6.2
- [ ] [Активировать Specs](http://84.201.134.115:8085/admin/configureLinkedRepositories!doDefault.action): репо -> Scan for Bamboo Specs

## Deploy и проверка работы приложения
- Запуск проекта сборки
- Запуск проекта развертывания
- Test Restful WebService: 
```bash
curl --request GET --header "X-API-VERSION:1" --url http://84.201.157.139:8080/dbo/api/client
curl --request GET --header "X-API-VERSION:1" --url http://84.201.157.139:8080/dbo/api/client/11
```
- [Kibana](http://84.201.134.115:5601/app/kibana#/management/elasticsearch/index_management/indices?_g=())
- [ ] [Create Index Pattern wizard](http://84.201.134.115:5601/app/kibana#/management/kibana/index_pattern?_g=())
- [ ] [dbo index](http://84.201.134.115:5601/app/kibana#/discover?_g=())
- [dbo stream](http://84.201.134.115:5601/app/infra#/logs/settings?_g=()), Log indices -> logstash*
- Stream live

## Ручное копирование приложения на пре-прод и запуск
- Ручное копирование scp
```bash
cd ..
./mvnw package -s Iaac/ansible/files/maven-settings.xml -DskipTests -Djava.awt.headless=true -DdependencyLocationsEnabled=false -Dlogback.configurationFile=logback.xml
scp -i ~/Dropbox/Eugene/Backups/agile-practices-dev.pem target/dbo-1.0-SNAPSHOT.jar admin@84.201.157.139:/home/dboadmin/dbo/
```

- Ручное копирование через Maven Repo
```bash
./mvnw deploy -s Iaac/ansible/files/maven-settings.xml -DskipTests -Djava.awt.headless=true -DdependencyLocationsEnabled=false -Dlogback.configurationFile=logback.xml
ssh -i ~/Dropbox/Eugene/Backups/agile-practices-dev.pem admin@84.201.157.139
admin@pre-prod:/home/dboadmin/dbo$ mvn -s /home/bambooagent/.m2/settings.xml org.apache.maven.plugins:maven-dependency-plugin:2.4:get -Dtransitive=false -Dartifact=com.acme.banking:dbo:1.0-SNAPSHOT -Ddest=/dbo/dbo-1.0-SNAPSHOT.jar -DremoteRepositories=dbo-artifacts-server::::http://84.201.134.115:8081/artifactory/dbo 
```

- Ручной запуск и остановка как приложения
```bash
admin@pre-prod:/home/dboadmin/dbo$ nohup java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=84.201.157.139 -jar /dbo/dbo-1.0-SNAPSHOT.jar &
admin@pre-prod:/home/dboadmin/dbo$ ps -af

admin@pre-prod: pkill -9 -f "dbo-1.0-SNAPSHOT.jar"
```

- Запуск и остановка приложения как сервиса
```bash
[admin@pre-prod:/$ [/bin/sh -c '] sudo systemctl daemon-reload]
[admin@pre-prod:/$ [/bin/sh -c '] sudo systemctl enable dbo-app.service]

admin@pre-prod:/$ sudo systemctl status dbo-app.service

curl --request POST http://84.201.157.139:8080/dbo/actuator/shutdown
```

## Подключение к работающему приложению
```bash
curl --request POST http://84.201.157.139:8080/dbo/actuator/
```
- jvisualvm -> Add remote JMX connection 84.201.157.139:9999
