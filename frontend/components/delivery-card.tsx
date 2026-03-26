import { Clock, DollarSign, TrendingUp, Package } from "lucide-react";

export interface DeliveryOption {
  id: string;
  carrier: string;
  price: number;
  estimatedDays: number;
  deliveryMethod: "COURIER" | "PICKUP_POINT";
  isCheapest?: boolean;
  isFastest?: boolean;
  isOptimal?: boolean;
}

interface DeliveryCardProps {
  option: DeliveryOption;
  onSelect: (option: DeliveryOption) => void;
}

function getBorderClass(option: DeliveryOption): string {
  if (option.isOptimal) {
    return "border-blue-600";
  }

  if (option.isCheapest && !option.isOptimal) {
    return "border-orange-500";
  }

  if (option.isFastest && !option.isOptimal) {
    return "border-purple-600";
  }

  return "border-gray-200";
}

function getDeliveryMethodLabel(deliveryMethod: DeliveryOption["deliveryMethod"]): string {
  if (deliveryMethod === "COURIER") {
    return "Курьер";
  }

  if (deliveryMethod === "PICKUP_POINT") {
    return "Пункт выдачи";
  }

  return deliveryMethod;
}

export function DeliveryCard({ option, onSelect }: DeliveryCardProps) {
  const cardClassName = `bg-white rounded-xl p-6 shadow-md border-2 transition-all hover:shadow-xl ${getBorderClass(option)}`;

  return (
    <div className={cardClassName}>
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 bg-gray-100 rounded-lg flex items-center justify-center">
            <Package className="w-6 h-6 text-gray-600" />
          </div>

          <div>
            <h3 className="font-semibold text-lg text-gray-800">{option.carrier}</h3>
            <p className="text-sm text-gray-500 mt-1">
              {getDeliveryMethodLabel(option.deliveryMethod)}
            </p>
          </div>
        </div>

        <div className="flex flex-col items-end gap-2">
          {option.isOptimal && (
            <span className="inline-flex items-center rounded-md bg-blue-600 px-2 py-1 text-xs font-medium text-white">
              <TrendingUp className="w-3 h-3 mr-1" />
              Оптимальный
            </span>
          )}

          {option.isCheapest && !option.isOptimal && (
            <span className="inline-flex items-center rounded-md bg-orange-500 px-2 py-1 text-xs font-medium text-white">
              <DollarSign className="w-3 h-3 mr-1" />
              Дешевле всех
            </span>
          )}

          {option.isFastest && !option.isOptimal && (
            <span className="inline-flex items-center rounded-md bg-purple-600 px-2 py-1 text-xs font-medium text-white">
              <Clock className="w-3 h-3 mr-1" />
              Быстрее всех
            </span>
          )}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4 mb-4 pb-4 border-b border-gray-200">
        <div>
          <p className="text-sm text-gray-500 mb-1">Стоимость</p>
          <p className="text-2xl font-bold text-gray-800">
            {option.price.toLocaleString("ru-RU")} ₽
          </p>
        </div>

        <div>
          <p className="text-sm text-gray-500 mb-1">Срок доставки</p>
          <p className="text-lg font-semibold text-gray-800">
            {option.estimatedDays} дн.
          </p>
        </div>
      </div>

      <div className="mb-4">
        <p className="text-sm font-medium text-gray-700 mb-2">Способ доставки:</p>
        <p className="text-sm text-gray-600">
          {getDeliveryMethodLabel(option.deliveryMethod)}
        </p>
      </div>

      <button
        type="button"
        onClick={() => onSelect(option)}
        className={
          option.isOptimal
            ? "w-full rounded-md bg-black px-4 py-2 text-white hover:opacity-90 transition"
            : "w-full rounded-md border border-gray-300 px-4 py-2 text-gray-800 hover:bg-gray-50 transition"
        }
      >
        Выбрать
      </button>
    </div>
  );
}