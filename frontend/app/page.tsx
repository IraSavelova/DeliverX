"use client";

import { useState } from "react";
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

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

function getGuestId(): string {
  const storageKey = "guestId";

  if (typeof window === "undefined") {
    return "";
  }

  const existingGuestId = localStorage.getItem(storageKey);
  if (existingGuestId) {
    return existingGuestId;
  }

  const newGuestId = crypto.randomUUID();
  localStorage.setItem(storageKey, newGuestId);

  return newGuestId;
}

function markBestOptions(options: DeliveryOption[]): DeliveryOption[] {
  if (options.length === 0) {
    return [];
  }

  const cheapestOption = options.reduce((prev, curr) =>
    prev.price < curr.price ? prev : curr
  );

  const fastestOption = options.reduce((prev, curr) =>
    prev.estimatedDays < curr.estimatedDays ? prev : curr
  );

  const optimalOption = options.reduce((prev, curr) => {
    const prevScore = prev.price / 1000 + prev.estimatedDays * 100;
    const currScore = curr.price / 1000 + curr.estimatedDays * 100;
    return prevScore < currScore ? prev : curr;
  });

  return options.map((option, index) => ({
    ...option,
    id: `${option.carrier}-${index}`,
    isCheapest: option.carrier === cheapestOption.carrier && option.price === cheapestOption.price,
    isFastest:
      option.carrier === fastestOption.carrier &&
      option.estimatedDays === fastestOption.estimatedDays,
    isOptimal:
      option.carrier === optimalOption.carrier &&
      option.price === optimalOption.price &&
      option.estimatedDays === optimalOption.estimatedDays,
  }));
}

export default function RatesPage() {
  const [deliveryOptions, setDeliveryOptions] = useState<DeliveryOption[]>([]);
  const [showResults, setShowResults] = useState(false);
  const [activeFilter, setActiveFilter] = useState<ActiveFilter>("all");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleCalculate = async (formData: DeliveryFormData) => {
    try {
      setLoading(true);
      setErrorMessage("");

      const guestId = getGuestId();

      const requestBody = {
        fromCity: formData.fromCity,
        toCity: formData.toCity,
        weightKg: Number(formData.weight),
        lengthCm: Number(formData.length),
        widthCm: Number(formData.width),
        heightCm: Number(formData.height),
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

      if (!response.ok) {
        throw new Error("Не удалось получить тарифы");
      }

      const rates: RateResponse[] = await response.json();

      const normalizedOptions: DeliveryOption[] = rates.map((rate, index) => ({
        id: `${rate.carrier}-${index}`,
        carrier: rate.carrier,
        price: rate.price,
        estimatedDays: rate.estimatedDays,
        deliveryMethod: rate.deliveryMethod,
      }));

      const updatedOptions = markBestOptions(normalizedOptions);

      setDeliveryOptions(updatedOptions);
      setShowResults(true);
    } catch (error) {
      console.error(error);
      setErrorMessage("Не удалось загрузить тарифы");
      setShowResults(false);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectOption = (option: DeliveryOption) => {
    alert(
      `Вы выбрали: ${option.carrier}\nСтоимость: ${option.price} ₽\nСрок: ${option.estimatedDays} дн.`
    );
  };

  const getFilteredOptions = () => {
    switch (activeFilter) {
      case "cheapest":
        return deliveryOptions.filter((option) => option.isCheapest);
      case "fastest":
        return deliveryOptions.filter((option) => option.isFastest);
      case "optimal":
        return deliveryOptions.filter((option) => option.isOptimal);
      default:
        return deliveryOptions;
    }
  };

  const filteredOptions = getFilteredOptions();

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 via-white to-purple-50">
      <AuthSidebar />

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
                Сравните тарифы разных служб доставки и выберите оптимальный вариант
                за несколько секунд
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
                src="https://images.unsplash.com/photo-1770927423939-bae721171237?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxkZWxpdmVyeSUyMHNjb290ZXIlMjBjb3VyaWVyJTIwdXJiYW58ZW58MXx8fHwxNzcxOTQ4NjcyfDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral"
                alt="Логистика и доставка"
                className="h-full w-full object-cover"
              />
            </div>
          </div>
        </div>

        <DeliveryForm onCalculate={handleCalculate} loading={loading} />

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
              <p className="text-gray-600">
                Выберите наиболее подходящий вариант или отфильтруйте результаты
              </p>
            </div>

            <FilterTabs
              onFilterChange={setActiveFilter}
              activeFilter={activeFilter}
            />

            {filteredOptions.length > 0 ? (
              <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
                {filteredOptions.map((option) => (
                  <DeliveryCard
                    key={option.id}
                    option={option}
                    onSelect={handleSelectOption}
                  />
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
            <p className="text-lg text-gray-500">
              Заполните форму выше, чтобы рассчитать стоимость доставки
            </p>
          </div>
        )}
      </main>

      <footer className="mt-16 border-t border-gray-800 bg-gray-900">
        <div className="mx-auto max-w-7xl px-4 py-8 text-center text-sm text-gray-300 sm:px-6 lg:px-8">
          <p>
            © 2026 Deliver<span className="text-orange-500">X</span>. Агрегатор
            логистических тарифов
          </p>
          <p className="mt-2">
            Сравнивайте цены и выбирайте лучший вариант доставки
          </p>
        </div>
      </footer>
    </div>
  );
}