# DeliverX Frontend

Next.js frontend for DeliverX.

## Stack

- Next.js 16
- React 19
- TypeScript
- Tailwind CSS
- Radix UI primitives
- Lucide icons

## Run Locally

```bash
npm install
npm run dev
```

Open:

```text
http://localhost:3000
```

## Scripts

| Command | Description |
| --- | --- |
| `npm run dev` | Start the development server. |
| `npm run build` | Build the production bundle. |
| `npm run start` | Start the production server after build. |
| `npm run lint` | Run ESLint. |

## Backend

The frontend is expected to work with the backend API Gateway:

```text
http://localhost:8080
```

Start backend services from the repository root:

```bash
docker compose up -d
```

API contracts are documented in:

```text
../docs/api-contracts.md
```
