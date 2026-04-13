"use client";

import { useState, useEffect } from "react";
import { Truck } from "lucide-react";
import { DeliveryForm, type DeliveryFormData } from "../components/delivery-form";
import { DeliveryCard, type DeliveryOption } from "../components/delivery-card";
import { FilterTabs } from "../components/filter-tabs";
import { AuthSidebar } from "../components/auth-sidebar";

type ActiveFilter = "all" | "cheapest" | "fastest" | "optimal";

interface RateResponse {
  carrier: string;
  price: number;
  estimatedDays: number;
  deliveryMethod: "COURIER" | "PICKUP_POINT";
}

// Все перевозчики которых опрашиваем — для прогресс-бара
const CARRIERS = ["ПЭК", "Деловые Линии"];

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

function getGuestId(): string {
  if (typeof window === "undefined") return "";
  const key = "guestId";
  const existing = localStorage.getItem(key);
  if (existing) return existing;
  const id = crypto.randomUUID();
  localStorage.setItem(key, id);
  return id;
}

function markBestOptions(options: DeliveryOption[]): DeliveryOption[] {
  if (options.length === 0) return [];

  const cheapest = options.reduce((a, b) => (a.price < b.price ? a : b));
  const fastest  = options.reduce((a, b) => (a.estimatedDays < b.estimatedDays ? a : b));
  const optimal  = options.reduce((a, b) => {
    const scoreA = a.price / 1000 + a.estimatedDays * 100;
    const scoreB = b.price / 1000 + b.estimatedDays * 100;
    return scoreA < scoreB ? a : b;
  });

  return options.map((o, i) => ({
    ...o,
    id: `${o.carrier}-${i}`,
    isCheapest: o.carrier === cheapest.carrier && o.price === cheapest.price,
    isFastest:  o.carrier === fastest.carrier  && o.estimatedDays === fastest.estimatedDays,
    isOptimal:  o.carrier === optimal.carrier  && o.price === optimal.price && o.estimatedDays === optimal.estimatedDays,
  }));
}

export default function RatesPage() {
  const [deliveryOptions, setDeliveryOptions] = useState<DeliveryOption[]>([]);
  const [showResults, setShowResults]         = useState(false);
  const [activeFilter, setActiveFilter]       = useState<ActiveFilter>("all");
  const [loading, setLoading]                 = useState(false);
  const [errorMessage, setErrorMessage]       = useState("");

  // Прогресс-бар: индекс текущего перевозчика (0..CARRIERS.length)
  const [carrierProgress, setCarrierProgress] = useState(0);
  // Полоска вверху страницы (0..100)
  const [topBarWidth, setTopBarWidth]         = useState(0);

  // Анимация верхней полоски — равномерно растёт пока идёт загрузка
  useEffect(() => {
    if (!loading) {
      setTopBarWidth(loading ? topBarWidth : 0);
      return;
    }
    setTopBarWidth(5);
    const interval = setInterval(() => {
      setTopBarWidth((w) => {
        // замедляемся по мере приближения к 90% — последние % добавим когда придёт ответ
        if (w >= 90) return w;
        return w + (90 - w) * 0.04;
      });
    }, 200);
    return () => clearInterval(interval);
  }, [loading]);

  const handleCalculate = async (formData: DeliveryFormData) => {
    try {
      setLoading(true);
      setErrorMessage("");
      setShowResults(false);
      setCarrierProgress(0);
      setTopBarWidth(5);

      const guestId = getGuestId();

      // Имитируем прогресс по перевозчикам пока идёт запрос
      // (реально всё идёт параллельно на бэке, но визуально показываем очерёдность)
      let ci = 0;
      const carrierTimer = setInterval(() => {
        ci += 1;
        setCarrierProgress(ci);
        if (ci >= CARRIERS.length) clearInterval(carrierTimer);
      }, 1500);

      const requestBody = {
        fromCity:      formData.fromCity,
        toCity:        formData.toCity,
        weightKg:      Number(formData.weight),
        lengthCm:      Number(formData.length),
        widthCm:       Number(formData.width),
        heightCm:      Number(formData.height),
        deliverySpeed: formData.deliverySpeed,
      };

      const response = await fetch(`${API_BASE_URL}/rates/calculate`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-Guest-Id": guestId,
        },
        body: JSON.stringify(requestBody),
      });

      clearInterval(carrierTimer);
      setCarrierProgress(CARRIERS.length);

      if (!response.ok) throw new Error("Не удалось получить тарифы");

      const rates: RateResponse[] = await response.json();

      const normalized: DeliveryOption[] = rates.map((r, i) => ({
        id: `${r.carrier}-${i}`,
        carrier: r.carrier,
        price: r.price,
        estimatedDays: r.estimatedDays,
        deliveryMethod: r.deliveryMethod,
      }));

      setDeliveryOptions(markBestOptions(normalized));
      setTopBarWidth(100);
      setShowResults(true);
    } catch (error) {
      console.error(error);
      setErrorMessage("Не удалось загрузить тарифы. Проверьте backend.");
      setTopBarWidth(0);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectOption = (option: DeliveryOption) => {
    alert(`Вы выбрали: ${option.carrier}\nСтоимость: ${option.price} ₽\nСрок: ${option.estimatedDays} дн.`);
  };

  const filteredOptions = (() => {
    switch (activeFilter) {
      case "cheapest": return deliveryOptions.filter((o) => o.isCheapest);
      case "fastest":  return deliveryOptions.filter((o) => o.isFastest);
      case "optimal":  return deliveryOptions.filter((o) => o.isOptimal);
      default:         return deliveryOptions;
    }
  })();

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 via-white to-purple-50">
      <AuthSidebar />

      {/* Полоска прогресса вверху страницы */}
      {loading && (
        <div className="fixed top-0 left-0 right-0 z-50 h-1 bg-gray-200">
          <div
            className="h-full bg-gradient-to-r from-orange-500 to-purple-600 transition-all duration-300"
            style={{ width: `${topBarWidth}%` }}
          />
        </div>
      )}

      <header className="sticky top-0 z-10 bg-gradient-to-r from-gray-900 via-purple-900 to-gray-900 shadow-lg">
        <div className="mx-auto max-w-7xl px-4 py-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gradient-to-br from-orange-500 to-purple-600">
              <Truck className="h-6 w-6 text-white" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-white">
                Deliver<span className="text-orange-500">X</span>
              </h1>
              <p className="text-sm text-gray-300">Агрегатор логистических тарифов</p>
            </div>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <div className="mb-8 overflow-hidden rounded-xl bg-white shadow-lg">
          <div className="grid grid-cols-1 gap-0 md:grid-cols-2">
            <div className="flex flex-col justify-center p-8 md:p-12">
              <h2 className="mb-4 text-3xl font-bold text-gray-900 md:text-4xl">
                Найдите лучший вариант доставки
              </h2>
              <p className="mb-6 text-lg text-gray-600">
                Сравните тарифы разных служб доставки и выберите оптимальный вариант за несколько секунд
              </p>
              <div className="flex gap-4">
                <div className="text-center">
                  <div className="text-3xl font-bold text-orange-500">6+</div>
                  <div className="text-sm text-gray-600">Служб доставки</div>
                </div>
                <div className="text-center">
                  <div className="text-3xl font-bold text-purple-600">100%</div>
                  <div className="text-sm text-gray-600">Точность расчета</div>
                </div>
              </div>
            </div>
            <div className="h-64 md:h-auto">
              <img
                src="https://images.unsplash.com/photo-1770927423939-bae721171237?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxkZWxpdmVyeSUyMHNjb290ZXIlMjBjb3VyaWVyJTIwdXJiYW58ZW58MXx8fHwxNzcxOTQ4NjcyfDA&ixlib=rb-4.1.0&q=80&w=1080"
                alt="Логистика и доставка"
                className="h-full w-full object-cover"
              />
            </div>
          </div>
        </div>

        <DeliveryForm onCalculate={handleCalculate} loading={loading} />

        {/* Прогресс-бар по перевозчикам */}
        {loading && (
          <div className="mb-8 rounded-xl bg-white shadow-lg p-6">
            <p className="text-sm font-medium text-gray-500 mb-4">Запрашиваем тарифы...</p>
            <div className="space-y-3">
              {CARRIERS.map((carrier, i) => {
                const done    = i < carrierProgress;
                const active  = i === carrierProgress;
                return (
                  <div key={carrier} className="flex items-center gap-3">
                    {/* Иконка статуса */}
                    <div className={`w-5 h-5 rounded-full flex items-center justify-center flex-shrink-0 text-xs font-bold
                      ${done   ? "bg-green-500 text-white" :
                        active ? "bg-orange-500 text-white animate-pulse" :
                                 "bg-gray-200 text-gray-400"}`}>
                      {done ? "✓" : i + 1}
                    </div>

                    <div className="flex-1">
                      <div className="flex justify-between mb-1">
                        <span className={`text-sm font-medium ${done ? "text-green-600" : active ? "text-orange-600" : "text-gray-400"}`}>
                          {carrier}
                        </span>
                        <span className="text-xs text-gray-400">
                          {done ? "Готово" : active ? "Загрузка..." : "Ожидание"}
                        </span>
                      </div>
                      <div className="h-1.5 bg-gray-100 rounded-full overflow-hidden">
                        <div
                          className={`h-full rounded-full transition-all duration-700
                            ${done   ? "bg-green-500 w-full" :
                              active ? "bg-orange-500 w-2/3 animate-pulse" :
                                       "bg-gray-200 w-0"}`}
                        />
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}

        {errorMessage && (
          <div className="mb-6 rounded-xl border border-red-200 bg-red-50 p-4 text-red-700">
            {errorMessage}
          </div>
        )}

        {showResults && (
          <div>
            <div className="mb-6">
              <h2 className="mb-2 text-2xl font-semibold text-gray-800">
                Найдено {deliveryOptions.length} вариантов доставки
              </h2>
              <p className="text-gray-600">Выберите наиболее подходящий вариант или отфильтруйте результаты</p>
            </div>

            <FilterTabs onFilterChange={setActiveFilter} activeFilter={activeFilter} />

            {filteredOptions.length > 0 ? (
              <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
                {filteredOptions.map((option) => (
                  <DeliveryCard key={option.id} option={option} onSelect={handleSelectOption} />
                ))}
              </div>
            ) : (
              <div className="rounded-xl bg-white p-12 text-center">
                <p className="text-gray-500">Нет вариантов с выбранным фильтром</p>
              </div>
            )}
          </div>
        )}

        {!showResults && !loading && !errorMessage && (
          <div className="rounded-xl bg-white p-12 text-center">
            <Truck className="mx-auto mb-4 h-16 w-16 text-gray-300" />
            <p className="text-lg text-gray-500">Заполните форму выше, чтобы рассчитать стоимость доставки</p>
          </div>
        )}
      </main>

      <footer className="mt-16 border-t border-gray-800 bg-gray-900">
        <div className="mx-auto max-w-7xl px-4 py-8 text-center text-sm text-gray-300 sm:px-6 lg:px-8">
          <p>© 2026 Deliver<span className="text-orange-500">X</span>. Агрегатор логистических тарифов</p>
          <p className="mt-2">Сравнивайте цены и выбирайте лучший вариант доставки</p>
        </div>
      </footer>
    </div>
  );
}
