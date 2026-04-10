import { Tabs, TabsList, TabsTrigger } from '../ui/tabs';
import { DollarSign, Clock, TrendingUp, Grid3x3 } from 'lucide-react';

interface FilterTabsProps {
  onFilterChange: (filter: 'all' | 'cheapest' | 'fastest' | 'optimal') => void;
  activeFilter: string;
}

export function FilterTabs({ onFilterChange, activeFilter }: FilterTabsProps) {
  return (
    <Tabs value={activeFilter} onValueChange={(value) => onFilterChange(value as any)} className="mb-6">
      <TabsList className="grid w-full grid-cols-2 md:grid-cols-4 h-auto">
        <TabsTrigger value="all" className="flex items-center gap-2 py-3">
          <Grid3x3 className="w-4 h-4" />
          Все варианты
        </TabsTrigger>
        <TabsTrigger value="cheapest" className="flex items-center gap-2 py-3">
          <DollarSign className="w-4 h-4" />
          Самый дешевый
        </TabsTrigger>
        <TabsTrigger value="fastest" className="flex items-center gap-2 py-3">
          <Clock className="w-4 h-4" />
          Самый быстрый
        </TabsTrigger>
        <TabsTrigger value="optimal" className="flex items-center gap-2 py-3">
          <TrendingUp className="w-4 h-4" />
          Оптимальный
        </TabsTrigger>
      </TabsList>
    </Tabs>
  );
}
