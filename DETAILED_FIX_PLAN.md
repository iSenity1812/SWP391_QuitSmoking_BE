# 📋 PLAN CHI TIẾT FIX BUGS VÀ THÊM TÍNH NĂNG - COACH SCHEDULE REGISTRATION

## 🚨 BUGS HIỆN TẠI CẦN FIX

### 1. **Lỗi lệch ngày** (Priority: HIGH)

**Vấn đề**: CN = 23 thay vì 22, T2 = 24 thay vì 23
**Nguyên nhân**: Logic `getWeekStart()` hoặc calendar rendering sai
**Fix**:

- Kiểm tra `DataTransformer.getWeekStart()`
- Đảm bảo tuần VN bắt đầu từ T2 (index 0)
- Fix calendar picker hiển thị đúng ngày

### 2. **Lỗi chọn lẻ** (Priority: HIGH)

**Vấn đề**: Click slot → check xong uncheck luôn
**Nguyên nhân**: Conflict giữa onClick và drag handlers
**Fix**:

- Thêm `isActuallyDragging` state
- Chỉ trigger drag sau khi mouse move > threshold
- Tách riêng click vs drag logic

### 3. **Lỗi không cập nhật UI khi chuyển ngày** (Priority: HIGH)

**Vấn đề**: Chọn T2 slot 1,2 → chuyển T3 vẫn hiện slot 1,2  
**Nguyên nhân**: `selectedSlots` không clear khi `selectedDate` thay đổi
**Fix**:

- useEffect theo dõi selectedDate change
- Clear UI selection khi chuyển ngày

### 4. **Lỗi không persist selection** (Priority: HIGH)

**Vấn đề**: T2 chọn slot 1,2 → T3 chọn slot 3,4 → quay lại T2 mất selection
**Nguyên nhân**: State structure không support multi-date
**Fix**:

- Change `selectedSlots: number[]` → `selectedSlotsByDate: {[date]: number[]}`
- Persist tất cả selections theo ngày

### 5. **Lỗi không hiển thị trạng thái đã đăng ký** (Priority: MEDIUM)

**Vấn đề**: Slot đã đăng ký không được disable/highlight  
**Nguyên nhân**: Không check scheduleData  
**Fix**:

- Check registered slots từ scheduleData
- Disable + visual indicator cho slot đã đăng ký

---

## ✨ TÍNH NĂNG MỚI

### 1. **Multi-date Registration** (Priority: HIGH)

**Mô tả**: Chọn nhiều ngày khác nhau và gửi tất cả cùng lúc
**Implementation**:

- API call: `[{timeSlotId: 1, scheduleDate: "2025-06-22"}, {timeSlotId: 2, scheduleDate: "2025-06-23"}]`
- Summary hiển thị tất cả dates + slots
- Bulk registration với progress indicator

### 2. **Smart Drag trên Weekly Table** (Priority: MEDIUM)

**Mô tả**: Kéo trên bảng tuần, tự động skip slot đã đăng ký
**Implementation**:

- Thêm drag handlers vào TimeSlotCell
- Logic: Check slot availability trước khi add vào selection
- Visual feedback khi drag (highlight path)

### 3. **Enhanced Week Navigation** (Priority: LOW)

**Mô tả**: Chuyển sang các tuần khác dễ dàng
**Implementation**:

- Date picker để jump to specific week
- Keyboard shortcuts (arrow keys)
- Show week number

---

## 🛠 IMPLEMENTATION PLAN

### **PHASE 1: FIX CRITICAL BUGS (2.5 hours)**

#### Step 1.1: Fix Date Logic (30 mins)

```typescript
// File: src/utils/dataTransformers.ts
const getWeekStart = (date: Date): Date => {
  const d = new Date(date);
  const day = d.getDay(); // 0=CN, 1=T2, ..., 6=T7
  const diff = d.getDate() - day + (day === 0 ? -6 : 1); // T2 = start
  return new Date(d.setDate(diff));
};
```

#### Step 1.2: Fix Click vs Drag Logic (45 mins)

```typescript
// File: WeeklyScheduleTable.tsx
const [dragState, setDragState] = useState({
  isDragging: false,
  isActualDrag: false,
  startPos: null,
  startSlot: null,
  dragThreshold: 5, // pixels
});

const handleMouseDown = (e, slotId) => {
  setDragState((prev) => ({
    ...prev,
    startPos: { x: e.clientX, y: e.clientY },
    startSlot: slotId,
  }));
};

const handleMouseMove = (e) => {
  if (dragState.startPos && !dragState.isActualDrag) {
    const distance =
      Math.abs(e.clientX - dragState.startPos.x) +
      Math.abs(e.clientY - dragState.startPos.y);
    if (distance > dragState.dragThreshold) {
      setDragState((prev) => ({
        ...prev,
        isActualDrag: true,
        isDragging: true,
      }));
    }
  }
};

const handleClick = (slotId) => {
  if (!dragState.isActualDrag) {
    // Normal click behavior
    handleSlotToggle(slotId);
  }
};
```

#### Step 1.3: Multi-date State Management (60 mins)

```typescript
// New state structure
const [selectedSlotsByDate, setSelectedSlotsByDate] = useState<{
  [date: string]: number[];
}>({});
const [currentViewSlots, setCurrentViewSlots] = useState<number[]>([]);

useEffect(() => {
  // Update UI when selectedDate changes
  setCurrentViewSlots(selectedSlotsByDate[selectedDate] || []);
}, [selectedDate, selectedSlotsByDate]);

const handleSlotToggle = (slotId: number) => {
  setSelectedSlotsByDate((prev) => {
    const currentSlots = prev[selectedDate] || [];
    const newSlots = currentSlots.includes(slotId)
      ? currentSlots.filter((id) => id !== slotId)
      : [...currentSlots, slotId];

    return {
      ...prev,
      [selectedDate]: newSlots,
    };
  });
};
```

#### Step 1.4: Registration Status Check (45 mins)

```typescript
const getSlotRegistrationStatus = (date: string, slotId: number) => {
  const slot = scheduleData?.registeredSlots.find(
    (s) => s.date === date && s.timeSlotId === slotId
  );
  return {
    isRegistered: !!slot,
    hasAppointment: slot?.appointments.length > 0,
    isAvailable: !slot || slot.appointments.length === 0,
    canRegister: !slot, // Can only register if not already registered
  };
};

// In slot rendering
const status = getSlotRegistrationStatus(selectedDate, slot.timeSlotId);
// Disable interaction if already registered
// Add visual indicators
```

### **PHASE 2: NEW FEATURES (3 hours)**

#### Step 2.1: Multi-date Registration API (30 mins)

```typescript
// File: useWeeklySchedule.ts
const registerSlots = useCallback(
  async (slotRegistrations: { timeSlotId: number; scheduleDate: string }[]) => {
    await coachScheduleService.registerTimeSlots(slotRegistrations);
    await refetch();
  },
  [refetch]
);

// File: WeeklyScheduleTable.tsx
const handleBulkRegister = async () => {
  const allSlots = Object.entries(selectedSlotsByDate).flatMap(
    ([date, slots]) =>
      slots.map((slotId) => ({ timeSlotId: slotId, scheduleDate: date }))
  );

  if (allSlots.length === 0) return;

  try {
    await registerSlots(allSlots);

    toast.success(
      `Đã đăng ký thành công ${allSlots.length} slots cho ${
        Object.keys(selectedSlotsByDate).length
      } ngày!`
    );

    // Clear all selections
    setSelectedSlotsByDate({});
    setCurrentViewSlots([]);
  } catch (error) {
    toast.error("Có lỗi xảy ra khi đăng ký slots");
  }
};
```

#### Step 2.2: Smart Weekly Table Drag (90 mins)

```typescript
// Add to main component
const [weeklyDragState, setWeeklyDragState] = useState({
  isDragging: false,
  startCell: null,
  selections: new Set(),
});

// Add to TimeSlotCell component
const handleCellMouseDown = (e) => {
  const status = getSlotRegistrationStatus(date, timeSlot.timeSlotId);
  if (!status.canRegister) return; // Skip if already registered

  setWeeklyDragState({
    isDragging: true,
    startCell: { date, timeSlotId: timeSlot.timeSlotId },
    selections: new Set([`${date}-${timeSlot.timeSlotId}`]),
  });
};

const handleCellMouseEnter = () => {
  if (weeklyDragState.isDragging) {
    const status = getSlotRegistrationStatus(date, timeSlot.timeSlotId);
    if (status.canRegister) {
      setWeeklyDragState((prev) => ({
        ...prev,
        selections: new Set([
          ...prev.selections,
          `${date}-${timeSlot.timeSlotId}`,
        ]),
      }));
    }
  }
};

const handleCellMouseUp = () => {
  if (weeklyDragState.isDragging) {
    // Apply selections to selectedSlotsByDate
    const newSelections = {};
    weeklyDragState.selections.forEach((cellKey) => {
      const [date, slotId] = cellKey.split("-");
      if (!newSelections[date]) newSelections[date] = [];
      newSelections[date].push(parseInt(slotId));
    });

    setSelectedSlotsByDate((prev) => {
      const updated = { ...prev };
      Object.entries(newSelections).forEach(([date, slots]) => {
        updated[date] = [...(updated[date] || []), ...slots];
      });
      return updated;
    });

    setWeeklyDragState({
      isDragging: false,
      startCell: null,
      selections: new Set(),
    });
  }
};
```

#### Step 2.3: Enhanced Week Navigation (60 mins)

```typescript
// Add date picker for week navigation
const [showWeekPicker, setShowWeekPicker] = useState(false);

const jumpToWeek = (targetDate: Date) => {
  const weekStart = DataTransformer.getWeekStart(targetDate);
  onWeekChange(weekStart);
  setShowWeekPicker(false);
};

// Add keyboard navigation
useEffect(() => {
  const handleKeyPress = (e) => {
    if (e.key === "ArrowLeft") goToPreviousWeek();
    if (e.key === "ArrowRight") goToNextWeek();
  };

  document.addEventListener("keydown", handleKeyPress);
  return () => document.removeEventListener("keydown", handleKeyPress);
}, []);
```

### **PHASE 3: UI/UX POLISH (1 hour)**

#### Step 3.1: Enhanced Visual Feedback (30 mins)

- Progress indicators for bulk registration
- Better loading states
- Confirmation dialogs
- Success animations

#### Step 3.2: Improved Drag Experience (30 mins)

- Smooth animations
- Better cursor states
- Drag preview effects
- Visual path highlighting

---

## 🧪 TESTING STRATEGY

### **Unit Tests:**

- [ ] Date logic: `getWeekStart()` with edge cases
- [ ] State management: `selectedSlotsByDate` updates
- [ ] Registration status checks
- [ ] Click vs drag detection

### **Integration Tests:**

- [ ] Multi-date registration flow
- [ ] Weekly table drag functionality
- [ ] Week navigation
- [ ] API error handling

### **User Acceptance Tests:**

- [ ] Navigate weeks correctly (T2=23, T3=24, etc.)
- [ ] Click slot works (no uncheck bug)
- [ ] Selection persists when switching dates
- [ ] Cannot select already registered slots
- [ ] Bulk registration sends all dates
- [ ] Smart drag skips registered slots

---

## ⚠️ RISKS & MITIGATION

### **HIGH RISK:**

- **Date logic changes** → Could break existing functionality
- **State management refactor** → Could introduce bugs

**Mitigation**:

- Backup current version
- Feature flags for gradual rollout
- Extensive testing on different timezones

### **MEDIUM RISK:**

- **Drag detection sensitivity** → Too sensitive/insensitive
- **Performance** → Large selections could be slow

**Mitigation**:

- Configurable thresholds
- Debouncing for large operations
- Chunked API calls

---

## 📅 DELIVERY TIMELINE

| Phase       | Duration      | Tasks                  |
| ----------- | ------------- | ---------------------- |
| **Phase 1** | 2.5 hours     | Fix all critical bugs  |
| **Phase 2** | 3 hours       | Implement new features |
| **Phase 3** | 1 hour        | Polish & testing       |
| **Total**   | **6.5 hours** | Complete delivery      |

---

## 📁 FILES TO MODIFY

1. **`src/utils/dataTransformers.ts`** - Fix date logic
2. **`src/pages/coach/components/WeeklyScheduleTable.tsx`** - Main component (major changes)
3. **`src/hooks/useWeeklySchedule.ts`** - Multi-date registration hook
4. **`src/services/coachScheduleService.ts`** - API service updates
5. **`src/types/api.ts`** - Type definitions

---

## ✅ DEFINITION OF DONE

- [ ] All 5 bugs fixed and verified
- [ ] Multi-date registration working
- [ ] Smart weekly drag implemented
- [ ] Enhanced week navigation
- [ ] All tests passing
- [ ] User acceptance criteria met
- [ ] Performance acceptable
- [ ] Documentation updated

**Ready for implementation approval! 🚀**
