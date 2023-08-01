## Чарт feature-flag-agent

Агент получает информацию об изменениях фичефлагов от портала и сохраняет ее в k8s configmap.

### Работа с несколькими namespace

По умолчанию агент настроен для работы только со своим неймспейсом. Если необходимо, чтобы агент мог управлять
конфигмапами в других неймспейсах этого же кластера, необходимо в этих неймспейсах создать ресурсы `role.yaml`
и `rolebinding.yaml`.

#### Пример:

Допустим, есть два неймспейса - `default` и `test`. Нам нужно, чтобы в неймспейсе `default` был развернут
feature-flag-agent, который должен управлять конфигмапами и в `default`, и в `test`. Для этого мы можем развернуть
feature-flag-agent в неймспейсе `default` с помощью чарта:

```shell
helm upgrade feature-flag-agent ./feature-flag-agent-chart -f feature-flag-agent-chart/values.yaml
```

После чего в неймспейсе `test` создать ресурсы `role.yaml` и `rolebinding.yaml`:

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: configmap-update-role
  labels:
    app: true-feature-flag
rules:
  - apiGroups:
      - ""
    resources:
      - configmaps
    verbs:
      - get
      - list
      - watch
      - create
      - update
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: configmap-update-rolebinding
  labels:
    app: true-feature-flag
subjects:
  - kind: ServiceAccount
    name: configmap-updater
    # Название неймспейса, в котором развернут feature-flag-agent
    namespace: default
roleRef:
  kind: Role
  name: configmap-update-role
  apiGroup: rbac.authorization.k8s.io
```

Таким образом feature-flag-agent сможет управлять конфигмапами в обоих неймспейсах.