# Агент для работы с feature flags

## Локальный запуск проекта

### Инициализируем переменные окружения
run `init.local.env.sh`

### Поднимаем окружение в docker
 ```shell script
cd config/dev
docker-compose up -d
```


### Собираем проект
```shell script
./gradlew clean build
```

Запускаем!