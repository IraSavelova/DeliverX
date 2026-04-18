import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate, Counter } from 'k6/metrics';


const pekDuration    = new Trend('pek_duration',    true);
const dellinDuration = new Trend('dellin_duration', true);
const errorRate      = new Rate('error_rate');
const emptyResults   = new Counter('empty_results');

export const options = {
  stages: [
    { duration: '1s', target: 10  },
    { duration: '1m',  target: 10  },
    { duration: '30s', target: 20 },
    { duration: '1m',  target: 20 },
    { duration: '30s', target: 50 }, 
    { duration: '1m',  target: 50 },
    { duration: '30s', target: 0  }, 
  ],

  thresholds: {

    http_req_duration: ['p(95)<10000'],

    error_rate: ['rate<0.05'],
  },
};

const ROUTES = [
  { from: 'Москва',        to: 'Новосибирск'    },
  { from: 'Санкт-Петербург', to: 'Екатеринбург' },
  { from: 'Казань',        to: 'Краснодар'      },
  { from: 'Новосибирск',   to: 'Москва'         },
  { from: 'Екатеринбург',  to: 'Санкт-Петербург'},
  { from: 'Краснодар',  to: 'Санкт-Петербург'},
  { from: 'Екатеринбург',  to: 'Новосибирск'},
];

const CARGOS = [
  { weightKg: 1.0,  lengthCm: 20, widthCm: 15, heightCm: 10 },
  { weightKg: 5.0,  lengthCm: 30, widthCm: 20, heightCm: 15 },
  { weightKg: 15.0, lengthCm: 50, widthCm: 40, heightCm: 30 },
  { weightKg: 3,  lengthCm: 5, widthCm: 25, heightCm: 2 },
   { weightKg: 5,  lengthCm: 25, widthCm: 2, heightCm: 25 },
    { weightKg: 1,  lengthCm: 25, widthCm: 5, heightCm: 25 },
     { weightKg: 2.5,  lengthCm: 25, widthCm: 25, heightCm: 35 },

];

export default function () {
  const route = ROUTES[Math.floor(Math.random() * ROUTES.length)];
  const cargo = CARGOS[Math.floor(Math.random() * CARGOS.length)];

  const payload = JSON.stringify({
    fromCity:      route.from,
    toCity:        route.to,
    weightKg:      cargo.weightKg,
    lengthCm:      cargo.lengthCm,
    widthCm:       cargo.widthCm,
    heightCm:      cargo.heightCm,
    deliverySpeed: 'STANDARD',
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
    timeout: '15s',
    tags: { route: `${route.from}-${route.to}` },
  };

  const start = Date.now();
  const res = http.post('http://localhost:8080/rates/calculate', payload, params);
  const duration = Date.now() - start;

  const ok = check(res, {
    'статус 200':            (r) => r.status === 200,
    'ответ не пустой':       (r) => r.body && r.body.length > 2,
    'быстрее 10 секунд':     (r) => r.timings.duration < 10000,
  });

  errorRate.add(!ok);

  if (res.status === 200) {
    try {
      const body = JSON.parse(res.body);
      if (Array.isArray(body) && body.length === 0) {
        emptyResults.add(1);
        console.warn(`Пустой результат: ${route.from} → ${route.to}`);
      }

      if (duration > 7000) {
        console.warn(`Медленный запрос (${duration}ms): ${route.from} → ${route.to}`);
      }
    } catch (e) {
      console.error(`Не удалось распарсить ответ: ${res.body}`);
    }
  } else {
    console.error(`Ошибка ${res.status}: ${route.from} → ${route.to} — ${res.body}`);
  }


  sleep(Math.random() * 2 + 1);
}

export function handleSummary(data) {
  const duration = data.metrics.http_req_duration;
  const reqs     = data.metrics.http_reqs;
  const errors   = data.metrics.error_rate;

  const fmt = (v) => (v != null ? v.toFixed(0) : 'н/д');

  console.log('\n========== ИТОГИ НАГРУЗОЧНОГО ТЕСТА ==========');
  console.log(`Всего запросов:     ${reqs.values.count}`);
  console.log(`Запросов в секунду: ${reqs.values.rate.toFixed(2)}`);
  console.log(`Среднее время:      ${fmt(duration.values.avg)} мс`);
  console.log(`Медиана (p50):      ${fmt(duration.values.med)} мс`);
  console.log(`p90:                ${fmt(duration.values['p(90)'])} мс`);
  console.log(`p95:                ${fmt(duration.values['p(95)'])} мс`);
  console.log(`Максимум:           ${fmt(duration.values.max)} мс`);
  console.log(`Процент ошибок:     ${(errors.values.rate * 100).toFixed(1)}%`);
  console.log('===============================================\n');

  return {
    'load-test-summary.json': JSON.stringify(data, null, 2),
  };
}