## Auth Service

### POST /auth/login

Аутентификация пользователя и получение JWT токена.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "qwerty123"
}
````

**Response:**

```json
{
  "accessToken": "jwt-token-value",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**Error Response:**

```json
{
  "timestamp": "2026-03-26T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/auth/login"
}
```

---

## User Profile Service

### GET /users/me

Получение данных текущего пользователя.

**Headers:**

```
Authorization: Bearer <JWT>
```

**Response:**

```json
{
  "id": 1,
  "firstName": "Name",
  "lastName": "Lastname",
  "email": "user@example.com",
  "phone": "+79991234567"
}
```

---

### PUT /users/me

Обновление профиля пользователя.

**Headers:**

```
Authorization: Bearer <JWT>
```

**Request:**

```json
{
  "firstName": "Ira",
  "lastName": "Savelova",
  "phone": "+79991234567"
}
```

**Response:**

```json
{
  "id": 1,
  "firstName": "Ira",
  "lastName": "Savelova",
  "email": "user@example.com",
  "phone": "+79991234567"
}
```

---

## Rates Service

### POST /rates/calculate

Расчёт тарифов доставки.

**Request:**

```json
{
  "fromCity": "Moscow",
  "toCity": "Saint Petersburg",
  "weightKg": 2.5,
  "lengthCm": 30,
  "widthCm": 20,
  "heightCm": 15,
  "deliverySpeed": "STANDARD"
}
```

**Response:**

```json
[
  {
    "carrier": "FastShip",
    "price": 1290.0,
    "estimatedDays": 2,
    "deliveryMethod": "COURIER"
  },
  {
    "carrier": "EcoDelivery",
    "price": 890.0,
    "estimatedDays": 4,
    "deliveryMethod": "PICKUP_POINT"
  }
]
```

---

### POST /rates/calculate?sortBy=price

Расчёт тарифов с сортировкой.

**Query Parameters:**

* `sortBy`: `price` | `time`

**Request:**

```json
{
  "fromCity": "Moscow",
  "toCity": "Saint Petersburg",
  "weightKg": 2.5,
  "lengthCm": 30,
  "widthCm": 20,
  "heightCm": 15,
  "deliveryType": "STANDARD"
}
```

**Response:** аналогичен `/rates/calculate`

---

## API Gateway

Frontend взаимодействует с backend через API Gateway.

Базовый URL:

```text
http://localhost:8080/api
```

Примеры маршрутов:

* `POST /api/auth/login`
* `GET /api/users/me`
* `PUT /api/users/me`
* `POST /api/rates/calculate`

---

## Примечания

* Все защищённые endpoint требуют JWT токен.
* Для неавторизованных пользователей используется `guestId`.

````

---
