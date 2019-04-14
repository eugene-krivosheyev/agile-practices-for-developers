# Infrastructure-as-a-Сode
Хосты и программные активы, реализованные на Ansible (old-style).

# Как запустить тестовую среду на Docker-образах
```bash
test/build-image
test/start-containers-XXX
...
test/remove-containers-XXX
```

# Как протестировать инструмент Ansible
```bash
ansible -i src/hosts.yml -m shell -a 'uname -a' ci-agents --ask-pass
ansible -i src/hosts.yml -m setup ci-agents --ask-pass
```

# Как запустить процесс раскатки окружений
```bash
ansible-playbook -i src/hosts.yml src/inventory.yml --ask-pass --ask-sudo-pass [-vvv]
```
