# DeliverX
Агрегатор доставок и логистических тарифов

Сервис по поиску оптимальных условий доставки для физических лиц. Проект включает веб-сайт (frontend) и backend, реализованный в виде микросервисной архитектуры.

---

## Что делает система

DeliverX позволяет пользователю:
- ввести параметры отправления (откуда/куда, вес, габариты);
- получить и сравнить тарифы от нескольких перевозчиков;
  
---

## Архитектура

Frontend обращается к backend через API Gateway.

Компоненты:
- **Frontend** — пользовательский интерфейс
- **API Gateway** — единая точка входа, маршрутизация, проверка JWT.
- **Authentication Service** - аутентификация, выдача JWT, refresh token, guest-access.
- **User Profile Service** — хранение и управление данными пользователя.
- **Rates Service** — агрегация/расчёт тарифов + кэширование популярных направлений.
- **Message Broker** - очередь для асинхронных вызовов API перевозчиков.


---

## Технологии

Backend:
- Java 25+
- Spring Boot, Spring Cloud Gateway
- Spring Security (JWT)
- PostgreSQL
- Maven
- RabbitMQ

Frontend :
- TypeScript
- React
- Next.js

Инфраструктура и качество:
- Docker 
- Unit tests 
- Postman 
- GitHub Flow
- CI/CD (GitHub Actions)
- Sonar (статический анализ качества кода)
---


