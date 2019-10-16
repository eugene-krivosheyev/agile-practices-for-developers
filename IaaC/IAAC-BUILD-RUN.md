# Infrastructure-as-a-Сode
Хосты и программные активы, реализованные на Ansible и Docker Compose.


# Smoke test for Ansible configuration
```bash
ansible -i src/ansible/hosts.yml -m shell -a 'uname -a' all
ansible -i src/ansible/hosts.yml -m setup all
```

# Как запустить процесс раскатки окружений
```bash
ansible-galaxy install -r src/ansible/requirements.yml
ansible-playbook -i src/ansible/hosts.yml src/ansible/inventory.yml [-vvv]
```

# Как запустить докеризованные сервисы CI
```
docker-compose --file src/docker/docker-compose.yml up --build --no-recreate --detach
docker-compose --file src/docker/docker-compose.yml down
```
