import { useState } from 'react';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Button } from '../ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { MapPin, Package, Calendar, Home } from 'lucide-react';

interface DeliveryFormProps {
  onCalculate: (data: DeliveryFormData) => void;
  loading: boolean;
}

export interface DeliveryFormData {
  fromCity: string;
  fromAddress: string;
  toCity: string;
  toAddress: string;
  weight: string;
  length: string;
  width: string;
  height: string;
  deliverySpeed: string;
}

export function DeliveryForm({ onCalculate, loading }: DeliveryFormProps) {
  const [formData, setFormData] = useState<DeliveryFormData>({
    fromCity: '',
    fromAddress: '',
    toCity: '',
    toAddress: '',
    weight: '',
    length: '',
    width: '',
    height: '',
    deliverySpeed: 'standard',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onCalculate(formData);
  };

  const handleChange = (field: keyof DeliveryFormData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-lg p-6 mb-8">
      <h2 className="text-2xl font-semibold mb-6 text-gray-800">Рассчитать стоимость доставки</h2>

      {/* Откуда */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-4">
        <div className="space-y-2">
          <Label htmlFor="from" className="flex items-center gap-2">
            <MapPin className="w-4 h-4 text-orange-500" />
            Город отправления
          </Label>
          <Input
            id="from"
            placeholder="Например: Москва"
            value={formData.fromCity}
            onChange={(e) => handleChange('fromCity', e.target.value)}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="fromAddress" className="flex items-center gap-2">
            <Home className="w-4 h-4 text-orange-500" />
            Адрес отправки <span className="text-gray-400 font-normal">(необязательно)</span>
          </Label>
          <Input
            id="fromAddress"
            placeholder="Улица, дом"
            value={formData.fromAddress}
            onChange={(e) => handleChange('fromAddress', e.target.value)}
          />
        </div>
      </div>

      {/* Куда */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <div className="space-y-2">
          <Label htmlFor="to" className="flex items-center gap-2">
            <MapPin className="w-4 h-4 text-purple-500" />
            Город назначения
          </Label>
          <Input
            id="to"
            placeholder="Например: Новосибирск"
            value={formData.toCity}
            onChange={(e) => handleChange('toCity', e.target.value)}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="toAddress" className="flex items-center gap-2">
            <Home className="w-4 h-4 text-purple-500" />
            Адрес доставки <span className="text-gray-400 font-normal">(необязательно)</span>
          </Label>
          <Input
            id="toAddress"
            placeholder="Улица, дом"
            value={formData.toAddress}
            onChange={(e) => handleChange('toAddress', e.target.value)}
          />
        </div>
      </div>

      {/* Габариты */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        <div className="space-y-2">
          <Label htmlFor="weight" className="flex items-center gap-2">
            <Package className="w-4 h-4 text-orange-500" />
            Вес (кг)
          </Label>
          <Input
            id="weight"
            type="number"
            placeholder="0.0"
            value={formData.weight}
            onChange={(e) => handleChange('weight', e.target.value)}
            required
            min="0.1"
            step="0.1"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="length">Длина (см)</Label>
          <Input
            id="length"
            type="number"
            placeholder="0"
            value={formData.length}
            onChange={(e) => handleChange('length', e.target.value)}
            required
            min="1"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="width">Ширина (см)</Label>
          <Input
            id="width"
            type="number"
            placeholder="0"
            value={formData.width}
            onChange={(e) => handleChange('width', e.target.value)}
            required
            min="1"
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="height">Высота (см)</Label>
          <Input
            id="height"
            type="number"
            placeholder="0"
            value={formData.height}
            onChange={(e) => handleChange('height', e.target.value)}
            required
            min="1"
          />
        </div>
      </div>

      {/* Тип доставки */}
      <div className="mb-6">
        <Label htmlFor="deliveryType" className="flex items-center gap-2 mb-2">
          <Calendar className="w-4 h-4 text-orange-500" />
          Тип доставки
        </Label>
        <Select value={formData.deliverySpeed} onValueChange={(value) => handleChange('deliverySpeed', value)}>
          <SelectTrigger id="deliveryType">
            <SelectValue placeholder="Выберите тип доставки" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="standard">Стандартная</SelectItem>
            <SelectItem value="express">Экспресс</SelectItem>
            <SelectItem value="economy">Экономная</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <Button
        type="submit"
        disabled={loading}
        className="w-full bg-orange-500 hover:bg-orange-600 !font-bold disabled:opacity-60"
      >
        {loading ? 'Рассчитываем...' : 'Рассчитать стоимость'}
      </Button>
    </form>
  );
}
