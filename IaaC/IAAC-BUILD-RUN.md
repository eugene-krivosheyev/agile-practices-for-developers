# Infrastructure-as-a-Сode
Хосты и программные активы конвейера CI, реализованные на Ansible и Docker Compose.

# Как запустить и остановить докеризованные сервисы CI и ELK локально
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
cd IaaC
ansible-galaxy install -r ansible/requirements.yml
```

## Раскатка сервисов CI/CD и ELK
```bash
cd IaaC
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml --limit ci_hosting --tags "maven, ci" [--skip-tags "maven"] [--start-at-task='Shut down CI docker containers'] [--step] [-vvv] 
```

## Ручная настройка сервисов CI/CD
### [Bitbucket](http://84.201.134.115:7990)
[ ] Лицензия
[ ] Учетка
[ ] Репошечка
### [Bamboo](http://84.201.134.115:8085)
[ ] Лицензия
[ ] Учетка
[ ] Maven capability for local agent
[ ] Agents -> Disable remote agent authentication
[ ] General Configuration -> Broker configuration
```
Broker URL: tcp://0.0.0.0:54663?wireFormat.maxInactivityDuration=300000
Broker client URL: failover:(tcp://84.201.134.115:54663?wireFormat.maxInactivityDuration=300000)?initialReconnectDelay=15000&maxReconnectAttempts=10
```
[ ] [Artifactory Plugin](https://marketplace.atlassian.com/apps/27818/artifactory-for-bamboo?tab=installation)
### [Artifactory](http://84.201.134.115:8081)
[ ] Учетка
[ ] Репошечка
### [SonarQube](http://84.201.134.115:9000)
[ ] Учетка
[ ] Плагины покрытия
[ ] Настройки плагинов покрытия


## Раскатка сервисов ELK
```bash
cd IaaC
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml --limit ci_hosting --tags "elk"
```

## Раскатка сервисов на Pre-prod
```bash
cd IaaC
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml --limit pre_prod
```