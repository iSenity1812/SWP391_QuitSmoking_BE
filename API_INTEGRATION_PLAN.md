# 🚀 API INTEGRATION PLAN - WEEKLY SCHEDULE TABLE

## 📋 **OVERVIEW**

Tích hợp 2 APIs vào hệ thống Weekly Schedule Table:

1. **GET /api/coaches/schedules/weekly/{coachId}** - Lấy lịch tuần coach
2. **GET /api/timeslots** - Lấy danh sách time slots

## 🎯 **OBJECTIVES**

- Thay thế mock data bằng real API data
- Hiển thị lịch tuần thực tế từ backend
- Sync data giữa Coach và Member views
- Error handling và loading states

## 📐 **DATA MAPPING ANALYSIS**

### Current Mock Data vs API Response

#### Mock Interface (Current)

```typescript
interface WeeklyScheduleSlot {
  date: string;
  timeSlotId: number;
  isAvailable: boolean;
  appointment?: {
    appointmentId: number;
    clientName: string;
    clientId: number;
    status: "confirmed" | "scheduled" | "cancelled" | "completed";
    notes?: string;
    method: "phone" | "in-person";
    location?: string;
    createdAt: string;
  };
}
```

#### API Response Structure

```typescript
interface ApiWeeklyScheduleResponse {
  status: number;
  message: string;
  data: {
    weekStartDate: string;
    weekEndDate: string;
    registeredSlots: {
      date: string;
      timeSlotId: number;
      label: string;
      startTime: string;
      endTime: string;
      appointmentDetails: {
        appointmentId: number;
        clientName: string;
        clientId: string;
        status: "CONFIRMED" | "PENDING" | "CANCELLED" | "COMPLETED";
        notes: string;
      } | null;
      available: boolean;
    }[];
  };
}
```

## 🔄 **MAPPING STRATEGY**

### 1. Interface Updates Needed

```typescript
// Update existing interfaces to match API
interface TimeSlot {
  timeSlotId: number;
  label: string;
  startTime: string; // API: "08:00" vs Current: "08:00:00"
  endTime: string; // API: "09:00" vs Current: "09:00:00"
  deleted: boolean;
}

interface WeeklyScheduleSlot {
  date: string;
  timeSlotId: number;
  label: string; // NEW from API
  startTime: string; // NEW from API
  endTime: string; // NEW from API
  available: boolean; // API: "available" vs Current: "isAvailable"
  appointmentDetails?: {
    appointmentId: number;
    clientName: string;
    clientId: string; // API: string vs Current: number
    status: "CONFIRMED" | "PENDING" | "CANCELLED" | "COMPLETED";
    notes: string;
  } | null;
}

interface WeeklyScheduleResponse {
  weekStartDate: string;
  weekEndDate: string;
  registeredSlots: WeeklyScheduleSlot[];
}
```

## 🛠️ **IMPLEMENTATION PHASES**

### **PHASE 1: Service Layer Setup**

1. **Create API Service**

   ```typescript
   // src/services/coachScheduleService.ts
   - fetchTimeSlots()
   - fetchWeeklySchedule(coachId, dateInWeek)
   - Error handling with axios interceptors
   - Loading states management
   ```

2. **Create Custom Hooks**
   ```typescript
   // src/hooks/useWeeklySchedule.ts
   - useTimeSlots()
   - useWeeklySchedule(coachId, dateInWeek)
   - Auto-refresh logic
   - Error state management
   ```

### **PHASE 2: Interface Updates**

1. **Update Type Definitions**

   - Modify existing interfaces to match API
   - Add new API response types
   - Update status enums

2. **Data Transformation Layer**
   ```typescript
   // src/utils/dataTransformers.ts
   -transformApiToLocalData() - mapStatusValues() - normalizeTimeFormat();
   ```

### **PHASE 3: Component Integration**

#### WeeklyScheduleTable.tsx

```typescript
// Replace mock data usage
const WeeklyScheduleTable = () => {
  const [currentWeekStart, setCurrentWeekStart] = useState(new Date());
  const { data: timeSlots, loading: timeSlotsLoading } = useTimeSlots();
  const {
    data: weeklySchedule,
    loading: scheduleLoading,
    error: scheduleError,
    refetch,
  } = useWeeklySchedule(coachId, currentWeekStart);

  // Handle loading states
  // Handle error states
  // Transform data for display
};
```

#### AvailableSlotViewer.tsx (Member side)

```typescript
// Update to use real coach data
const AvailableSlotViewer = () => {
  const { data: availableSlots, loading } = useAvailableSlots(selectedWeek);

  // Filter only available slots (available: true)
  // Group by coach
  // Display booking interface
};
```

### **PHASE 4: State Management**

```typescript
// Global state for:
- Current logged-in coach ID
- Selected week/date range
- Real-time updates (future)
- Error boundaries
```

## 📁 **FILES TO CREATE/MODIFY**

### New Files

```
src/services/
├── coachScheduleService.ts    📝 NEW
└── apiClient.ts               📝 NEW

src/hooks/
├── useWeeklySchedule.ts       📝 NEW
├── useTimeSlots.ts           📝 NEW
└── useCoachId.ts             📝 NEW

src/utils/
├── dataTransformers.ts        📝 NEW
└── dateHelpers.ts            📝 NEW

src/types/
└── api.ts                    📝 NEW - API type definitions
```

### Modified Files

```
src/pages/coach/components/
├── WeeklyScheduleTable.tsx    🔄 MODIFY - Replace mock with API
└── AppointmentScheduler.tsx   🔄 MODIFY - Add coach ID context

src/pages/member/components/
├── AvailableSlotViewer.tsx    🔄 MODIFY - Use real coach data
└── MyBookingsPage.tsx         🔄 MODIFY - Connect to API

src/config/
└── axiosConfig.tsx           🔄 MODIFY - Add coach endpoints
```

## 🔧 **TECHNICAL IMPLEMENTATION**

### 1. API Service Layer

```typescript
// src/services/coachScheduleService.ts
import axios from "@/config/axiosConfig";

export const coachScheduleService = {
  async fetchTimeSlots() {
    const response = await axios.get("/api/timeslots");
    return response.data.data.filter((slot) => !slot.deleted);
  },

  async fetchWeeklySchedule(coachId: string, dateInWeek: string) {
    const response = await axios.get(
      `/api/coaches/schedules/weekly/${coachId}?dateInWeek=${dateInWeek}`
    );
    return response.data.data;
  },
};
```

### 2. Custom Hooks

```typescript
// src/hooks/useWeeklySchedule.ts
export const useWeeklySchedule = (coachId: string, weekStart: Date) => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const dateInWeek = formatDateForAPI(weekStart);
      const result = await coachScheduleService.fetchWeeklySchedule(
        coachId,
        dateInWeek
      );
      setData(result);
    } catch (err) {
      setError(err);
      toast.error("Không thể tải lịch trình");
    } finally {
      setLoading(false);
    }
  }, [coachId, weekStart]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, loading, error, refetch: fetchData };
};
```

### 3. Data Transformation

```typescript
// src/utils/dataTransformers.ts
export const transformApiScheduleData = (
  apiData: ApiWeeklyScheduleResponse
) => {
  return {
    weekStartDate: apiData.weekStartDate,
    weekEndDate: apiData.weekEndDate,
    registeredSlots: apiData.registeredSlots.map((slot) => ({
      date: slot.date,
      timeSlotId: slot.timeSlotId,
      label: slot.label,
      startTime: slot.startTime,
      endTime: slot.endTime,
      available: slot.available,
      appointmentDetails: slot.appointmentDetails
        ? {
            appointmentId: slot.appointmentDetails.appointmentId,
            clientName: slot.appointmentDetails.clientName,
            clientId: slot.appointmentDetails.clientId,
            status: slot.appointmentDetails.status.toLowerCase(),
            notes: slot.appointmentDetails.notes,
          }
        : null,
    })),
  };
};
```

## 🎨 **UI/UX CONSIDERATIONS**

### Loading States

```tsx
// Different loading states for different data
{
  timeSlotsLoading && <TimeSlotsSkeleton />;
}
{
  scheduleLoading && <ScheduleSkeleton />;
}
{
  data && <WeeklyScheduleContent data={data} />;
}
```

### Error Handling

```tsx
// Graceful error handling with retry
{
  error && (
    <ErrorBoundary
      error={error}
      onRetry={refetch}
      fallback={<OfflineMode data={cachedData} />}
    />
  );
}
```

### Real-time Updates

```typescript
// Future: WebSocket integration
useEffect(() => {
  const ws = new WebSocket(`/ws/coach/${coachId}/schedule`);
  ws.onmessage = (event) => {
    const update = JSON.parse(event.data);
    updateLocalSchedule(update);
  };
  return () => ws.close();
}, [coachId]);
```

## 📊 **TESTING STRATEGY**

### 1. Unit Tests

- API service functions
- Data transformation utilities
- Custom hooks logic

### 2. Integration Tests

- Component + API integration
- Error handling flows
- Loading state management

### 3. E2E Tests

- Complete user workflows
- Coach schedule management
- Member booking process

## 🚀 **DEPLOYMENT CHECKLIST**

### Pre-deployment

- [ ] All mock data replaced with API calls
- [ ] Error handling implemented
- [ ] Loading states working
- [ ] Data transformation tested
- [ ] Performance optimized

### Post-deployment

- [ ] Monitor API response times
- [ ] Track error rates
- [ ] User feedback collection
- [ ] Performance metrics

## 📈 **PERFORMANCE OPTIMIZATIONS**

### 1. Caching Strategy

```typescript
// Cache time slots (rarely change)
const { data: timeSlots } = useSWR("/api/timeslots", fetcher, {
  revalidateOnFocus: false,
  revalidateOnReconnect: false,
});

// Cache weekly schedule with short TTL
const { data: schedule } = useSWR(
  ["/api/coaches/schedules/weekly", coachId, dateInWeek],
  fetcher,
  { refreshInterval: 30000 } // 30 seconds
);
```

### 2. Optimistic Updates

```typescript
// Update UI immediately, rollback on error
const updateSchedule = async (newData) => {
  setOptimisticData(newData);
  try {
    await api.updateSchedule(newData);
  } catch (error) {
    setOptimisticData(previousData);
    toast.error("Cập nhật thất bại");
  }
};
```

## 🔧 **NEXT STEPS**

1. **Create API service layer** (Priority 1)
2. **Update interfaces** (Priority 1)
3. **Implement custom hooks** (Priority 2)
4. **Update WeeklyScheduleTable component** (Priority 2)
5. **Add error handling** (Priority 3)
6. **Testing & optimization** (Priority 3)

---

**Estimate: 2-3 days for full integration**
**Ready to start implementation!** 🚀
