import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Filter, Star } from "lucide-react"
import { specialtyOptions } from "@/constants/specialties"

export interface FilterState {
  specialties: string[]
  minRating: number
}

interface FilterSidebarProps {
  filters: FilterState
  onFiltersChange: (filters: FilterState) => void
  isOpen: boolean
  onToggle: () => void
}

export function FilterSidebar({ filters, onFiltersChange, isOpen, onToggle }: FilterSidebarProps) {
  const handleSpecialtyChange = (specialty: string, checked: boolean) => {
    console.log(`🔧 Filter specialty change: ${specialty} -> ${checked}`)
    const newSpecialties = checked
      ? [...filters.specialties, specialty] // Them specialty nếu được chọn
      : filters.specialties.filter((s) => s !== specialty); // Loại bỏ specialty nếu không được chọn

    console.log('Previous specialties:', filters.specialties)
    console.log('New specialties:', newSpecialties)

    onFiltersChange({ ...filters, specialties: newSpecialties }); // Cập nhật state với specialties mới
  }

  // Function to clear all filters
  const clearFilters = () => {
    onFiltersChange({
      specialties: [],
      minRating: 0,
    });
  };

  return (
    <>
      {/* Mobile Filter Toggle */}
      <div className="lg:hidden mb-4">
        <Button
          variant="outline"
          onClick={onToggle}
          className="w-full border-emerald-200 text-emerald-700 hover:bg-emerald-50 dark:bg-slate-800 dark:border-slate-700 dark:text-slate-300 dark:hover:bg-slate-700"
        >
          <Filter className="w-4 h-4 mr-2" />
          Bộ lọc
        </Button>
      </div>

      {/* Filter Sidebar */}
      <div
        className={`
        ${isOpen ? "block" : "hidden"} lg:block
        bg-white dark:bg-slate-800 rounded-xl border border-emerald-100 dark:border-slate-700 p-6 space-y-6 shadow-sm mb-6
      `}
      >
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold text-slate-900 dark:text-slate-100">Bộ lọc</h3>
          <Button
            variant="ghost"
            size="sm"
            onClick={clearFilters}
            className="text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50 dark:text-emerald-400 dark:hover:bg-slate-700"
          >
            Xóa tất cả
          </Button>
        </div>

        {/* Specialties */}
        <div className="space-y-3">
          <Label className="text-sm font-medium text-slate-700 dark:text-slate-300">Chuyên môn</Label>
          <div className="space-y-2 max-h-48 overflow-y-auto pr-2">
            {specialtyOptions.map((specialty) => (
              <div key={specialty.key} className="flex items-start space-x-2">
                <input
                  type="checkbox"
                  id={specialty.key}
                  checked={filters.specialties.includes(specialty.key)}
                  onChange={(e) => handleSpecialtyChange(specialty.key, e.target.checked)}
                  className="mt-1 h-4 w-4 text-emerald-600 bg-slate-100 border-slate-300 rounded focus:ring-emerald-500 dark:bg-slate-700 dark:border-slate-600 dark:focus:ring-emerald-600 dark:ring-offset-slate-800"
                />
                <Label htmlFor={specialty.key} className="text-sm text-slate-600 dark:text-slate-400 cursor-pointer leading-5">
                  {specialty.label}
                </Label>
              </div>
            ))}
          </div>
        </div>

        {/* Rating */}
        <div className="space-y-3">
          <Label className="text-sm font-medium text-slate-700 dark:text-slate-300">Đánh giá tối thiểu</Label>
          <div className="flex items-center space-x-2">
            <input
              type="range"
              min="0"
              max="5"
              step="0.1"
              value={filters.minRating}
              onChange={(e) => onFiltersChange({ ...filters, minRating: Number.parseFloat(e.target.value) })}
              className="flex-1 h-2 bg-emerald-200 dark:bg-slate-700 rounded-lg appearance-none cursor-pointer dark:focus:outline-none dark:focus:ring-0"
            />
            <div className="flex items-center space-x-1 min-w-[60px]">
              <Star className="w-4 h-4 text-yellow-400 fill-current" />
              <span className="text-sm font-medium dark:text-slate-300">{filters.minRating.toFixed(1)}</span>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}