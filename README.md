# Notes Service

Краткое описание проекта:

Простое backend-приложение для сервиса заметок на Spring Boot

## Реализовано:
- CRUD заметок
- валидация
- единый формат ошибок
- пагинация
- сортировка
- поиск по подстроке в заголовке

## Тестирование:
- Написаны unit-test'ы для основных функций приложения
- Написаны интеграционные тесты для основных HTTP-сценариев

## Как запустить приложение:
Запустить приложение в IDE, используя JDK 21

Запуск на Linux/macOS:
./gradlew bootRun

Запуск тестов на Windows: 
.\gradlew.bat clean test

Запуск тестов на Linux/macOS:
./gradlew clean test

## Коллекция HTTP запросов:
Файл коллекции для импорта в Postman находится в папке по пути:
```text
"/httpQueryCollection/Noteservice.postman_collection.json