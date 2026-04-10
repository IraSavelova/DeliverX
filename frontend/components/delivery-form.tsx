import { useState } from 'react';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Button } from '../ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { MapPin, Package, Calendar } from 'lucide-react';

interface DeliveryFormProps {
  onCalculate: (data: DeliveryFormData) => void;
  loading: boolean;
}

export interface DeliveryFormData {
  fromCity: string;
  toCity: string;
  weight: string;
  length: string;
  width: string;
  height: string;
  deliverySpeed: string;
}

export function DeliveryForm({ onCalculate }: DeliveryFormProps) {
  const [formData, setFormData] = useState<DeliveryFormData>({
    fromCity: '',
    toCity: '',
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
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        <div className="space-y-2">
          <Label htmlFor="from" className="flex items-center gap-2">
            <MapPin className="w-4 h-4 text-orange-500" />
            Откуда
          </Label>
          <Input
            id="from"
            placeholder="Город отправления"
            value={formData.fromCity}
            onChange={(e) => handleChange('fromCity', e.target.value)}
            required
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="to" className="flex items-center gap-2">
            <MapPin className="w-4 h-4 text-orange-500" />
            Куда
          </Label>
          <Input
            id="to"
            placeholder="Город назначения"
            value={formData.toCity}
            onChange={(e) => handleChange('toCity', e.target.value)}
            required
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
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
        <p className="text-sm text-gray-500 mt-2">
          <span className="font-medium">Стандартная:</span> оптимальный вариант по сроку и цене. 
          <span className="font-medium ml-2">Экспресс:</span> быстрая доставка. 
          <span className="font-medium ml-2">Экономная:</span> самые дешевые варианты.
        </p>
      </div>

      <Button type="submit" className="w-full bg-orange-500 hover:bg-orange-600 !font-bold">
        Рассчитать стоимость
      </Button>
    </form>
  );
}