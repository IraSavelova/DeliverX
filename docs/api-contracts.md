# API Contracts

Backend requests should normally go through the API Gateway:

```text
http://localhost:8080
```

Some services are also exposed directly by Docker Compose for local debugging.

## Authentication

### POST `/auth/login`

Authenticates a user and returns a JWT access token.

Gateway-compatible path:

```text
POST /api/auth/login
```

Request:

```json
{
  "email": "user@example.com",
  "password": "qwerty123"
}
```

Response:

```json
{
  "accessToken": "jwt-token-value",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

Error response example:

```json
{
  "timestamp": "2026-03-26T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/auth/login"
}
```

## User Profile

Protected user profile endpoints require:

```text
Authorization: Bearer <JWT>
```

### GET `/users/me`

Returns the profile for the authenticated user.

Gateway-compatible path:

```text
GET /api/users/me
```

Response:

```json
{
  "email": "user@example.com",
  "firstName": "Ira",
  "lastName": "Savelova",
  "phone": "+79991234567",
  "address": "Novosibirsk"
}
```

### PUT `/users/me`

Updates the profile for the authenticated user.

Gateway-compatible path:

```text
PUT /api/users/me
```

Request:

```json
{
  "firstName": "Ira",
  "lastName": "Savelova",
  "phone": "+79991234567",
  "address": "Novosibirsk"
}
```

Response:

```json
{
  "email": "user@example.com",
  "firstName": "Ira",
  "lastName": "Savelova",
  "phone": "+79991234567",
  "address": "Novosibirsk"
}
```

Validation:

| Field | Rule |
| --- | --- |
| `firstName` | Maximum 120 characters. |
| `lastName` | Maximum 120 characters. |
| `phone` | Maximum 32 characters, supports digits, spaces, `+`, `-`, and parentheses. |
| `address` | Maximum 255 characters. |

## Rates

### POST `/rates/calculate`

Calculates delivery rates.

Request:

```json
{
  "fromCity": "Moscow",
  "fromAddress": "Tverskaya 1",
  "toCity": "Saint Petersburg",
  "toAddress": "Nevsky Prospect 10",
  "weightKg": 2.5,
  "lengthCm": 30,
  "widthCm": 20,
  "heightCm": 15,
  "deliverySpeed": "STANDARD"
}
```

Response:

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

### POST `/rates/calculate?sortBy=price`

Calculates rates and sorts them by price.

Supported query parameters:

| Parameter | Values | Description |
| --- | --- | --- |
| `sortBy` | `price`, `time` | Optional sorting mode. |

Request body is the same as `/rates/calculate`.

## Gateway Routes

The gateway forwards requests using these route predicates:

| Gateway path | Target service |
| --- | --- |
| `/auth/**` | Auth Service |
| `/api/auth/**` | Auth Service |
| `/rates/**` | Rates Service |
| `/users/**` | User Service |
| `/api/users/**` | User Service |
| `/profile/**` | User Service |

## Postman

A Postman collection is available at:

```text
postman/Delivery_api.postman_collection.json
```
