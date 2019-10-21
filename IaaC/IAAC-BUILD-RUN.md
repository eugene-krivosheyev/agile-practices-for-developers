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

# Smoke test for Ansible remote host provisioning 
```bash
cd IaaC
ansible -i ansible/hosts.yml -m shell -a 'uname -a' all
ansible -i ansible/hosts.yml -m setup all
```

# Как запустить процесс раскатки окружения удаленно
```bash
cd IaaC
ansible-galaxy install -r ansible/requirements.yml
ansible-playbook -i ansible/hosts.yml ansible/inventory.yml [-vvv]
```
