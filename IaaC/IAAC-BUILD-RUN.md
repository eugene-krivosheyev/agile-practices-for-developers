# Infrastructure-as-a-Сode
Хосты и программные активы конвейера CI, реализованные на Ansible и Docker Compose.

# Как запустить и остановить докеризованные сервисы CI локально
```bash
cd IaaC
docker-compose --file src/docker/docker-compose.yml up --detach
docker-compose --file src/docker/docker-compose.yml down
```

# Smoke test for Ansible remote host provisioning 
```bash
cd IaaC
ansible -i src/ansible/hosts.yml -m shell -a 'uname -a' all
ansible -i src/ansible/hosts.yml -m setup all
```

# Как запустить процесс раскатки окружения удаленно
```bash
cd IaaC
ansible-galaxy install -r src/ansible/requirements.yml
ansible-playbook -i src/ansible/hosts.yml src/ansible/inventory.yml [-vvv]
```
