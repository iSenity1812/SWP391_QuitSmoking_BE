# 🔧 Hướng dẫn tích hợp API cho WeeklyScheduleTable

## 📋 **Những chỗ cần sửa khi tích hợp API Backend**

### **1. Import API Service**

```typescript
// Thêm vào đầu file WeeklyScheduleTable.tsx
import { coachScheduleService } from "@/services/coachScheduleService";
```

### **2. Thay thế Mock Data bằng API Calls**

#### **A. Fetch Weekly Schedule Data:**

```typescript
// Trong WeeklyScheduleTable component
const [weeklySchedule, setWeeklySchedule] = useState<WeeklyScheduleSlot[]>([]);
const [isLoading, setIsLoading] = useState(false);

const fetchWeeklyData = async (weekStart: Date) => {
  setIsLoading(true);
  try {
    const startDate = weekStart.toISOString().split("T")[0];
    const response = await coachScheduleService.getWeeklySchedule(startDate);
    setWeeklySchedule(response.data.registeredSlots);
  } catch (error) {
    console.error("Error fetching weekly schedule:", error);
    // Show error toast
  } finally {
    setIsLoading(false);
  }
};

// Replace trong handleWeekChange:
const handleWeekChange = async (newWeekStart: Date) => {
  setCurrentWeekStart(newWeekStart);
  await fetchWeeklyData(newWeekStart); // ← Replace này
};

// Add useEffect để load initial data:
useEffect(() => {
  fetchWeeklyData(currentWeekStart);
}, []);
```

#### **B. Slot Registration API Call:**

```typescript
// Trong SlotRegistrationDialog component, thay thế:
const handleRegister = async () => {
  if (selectedSlots.length === 0) return;

  setIsLoading(true);
  try {
    // TODO: Replace mock với API thật
    const response = await coachScheduleService.registerSlots({
      slots: [
        {
          date: getCurrentSelectedDate(), // Cần add logic chọn ngày
          timeSlotIds: selectedSlots,
        },
      ],
    });

    setIsLoading(false);
    setShowSuccess(true);

    setTimeout(() => {
      setShowSuccess(false);
      onSuccess();
      onClose();
    }, 1500);
  } catch (error) {
    setIsLoading(false);
    // Show error toast với message từ API
    console.error("Registration failed:", error);
  }
};
```

### **3. API Service File cần tạo**

#### **Tạo file: `src/services/coachScheduleService.ts`**

```typescript
import { axiosInstance } from "@/config/axiosConfig";

export const coachScheduleService = {
  // Get weekly schedule
  getWeeklySchedule: async (startDate: string) => {
    return await axiosInstance.get(
      `/api/coach/schedule/week?startDate=${startDate}`
    );
  },

  // Register slots
  registerSlots: async (data: {
    slots: Array<{ date: string; timeSlotIds: number[] }>;
  }) => {
    return await axiosInstance.post("/api/coach/slots/register", data);
  },

  // Unregister slot
  unregisterSlot: async (data: { date: string; timeSlotId: number }) => {
    return await axiosInstance.delete("/api/coach/slots/unregister", { data });
  },

  // Update appointment status
  updateAppointmentStatus: async (
    appointmentId: number,
    data: { status: string; notes?: string }
  ) => {
    return await axiosInstance.put(
      `/api/appointments/${appointmentId}/status`,
      data
    );
  },

  // Get appointment details
  getAppointmentDetails: async (appointmentId: number) => {
    return await axiosInstance.get(
      `/api/appointments/${appointmentId}/details`
    );
  },

  // Create appointment for client
  createAppointment: async (data: {
    clientName: string;
    clientEmail?: string;
    clientPhone?: string;
    date: string;
    timeSlotId: number;
    method: string;
    location?: string;
    notes?: string;
  }) => {
    return await axiosInstance.post("/api/coach/appointments/create", data);
  },
};
```

### **4. Toast Notifications**

#### **Thêm Toast Library (nếu chưa có):**

```bash
npm install react-hot-toast
# hoặc
npm install sonner
```

#### **Replace alert() bằng toast:**

```typescript
import toast from "react-hot-toast";

// Thay thế tất cả alert() thành:
toast.success("Đăng ký slot thành công! 🎉");
toast.error("Có lỗi xảy ra khi đăng ký slots");
toast.warning("Vui lòng chọn ít nhất một time slot");
```

### **5. Error Handling**

#### **Add proper error handling:**

```typescript
const handleApiError = (error: any) => {
  if (error.response?.data?.message) {
    toast.error(error.response.data.message)
  } else if (error.message) {
    toast.error(error.message)
  } else {
    toast.error("Có lỗi xảy ra, vui lòng thử lại")
  }
}

// Sử dụng trong catch blocks:
} catch (error) {
  handleApiError(error)
  setIsLoading(false)
}
```

### **6. Real-time Updates (Optional)**

#### **WebSocket cho real-time appointment updates:**

```typescript
import { useEffect } from "react";
import { io } from "socket.io-client";

const useRealtimeSchedule = (
  coachId: number,
  onUpdate: (data: any) => void
) => {
  useEffect(() => {
    const socket = io(
      process.env.NEXT_PUBLIC_WEBSOCKET_URL || "ws://localhost:8080"
    );

    socket.emit("join-coach-room", coachId);
    socket.on("appointment-updated", onUpdate);
    socket.on("slot-booked", onUpdate);

    return () => {
      socket.disconnect();
    };
  }, [coachId, onUpdate]);
};

// Sử dụng trong WeeklyScheduleTable:
useRealtimeSchedule(user.id, (data) => {
  // Refresh schedule data
  fetchWeeklyData(currentWeekStart);
  toast.success("Lịch đã được cập nhật!");
});
```

### **7. Loading States**

#### **Add loading indicators:**

```typescript
// Trong WeeklyScheduleTable component
{
  isLoading && (
    <div className="absolute inset-0 bg-white/50 dark:bg-slate-900/50 flex items-center justify-center z-10">
      <Loader2 className="w-8 h-8 animate-spin text-emerald-500" />
    </div>
  );
}
```

### **8. Data Validation**

#### **Add type checking và validation:**

```typescript
import { z } from "zod";

const WeeklyScheduleSchema = z.object({
  weekStart: z.string(),
  weekEnd: z.string(),
  registeredSlots: z.array(
    z.object({
      date: z.string(),
      timeSlotId: z.number(),
      isAvailable: z.boolean(),
      appointment: z
        .object({
          appointmentId: z.number(),
          clientName: z.string(),
          status: z.enum(["confirmed", "scheduled", "cancelled", "completed"]),
        })
        .optional(),
    })
  ),
});

// Validate API response:
const response = await coachScheduleService.getWeeklySchedule(startDate);
const validatedData = WeeklyScheduleSchema.parse(response.data);
```

---

## ⚠️ **Những lưu ý quan trọng:**

1. **Environment Variables:** Đảm bảo có API URL trong `.env`
2. **Authorization:** Thêm JWT token vào headers
3. **Error Boundaries:** Wrap component trong Error Boundary
4. **Cache:** Implement caching cho time slots (ít thay đổi)
5. **Optimistic Updates:** Update UI trước, rollback nếu API fail
6. **Pagination:** Nếu có nhiều appointments, cần pagination
7. **Debouncing:** Cho search/filter features
8. **Mobile Responsive:** Test trên mobile devices

---

## 🧪 **Testing Checklist:**

- [ ] **API Integration:** Tất cả endpoints hoạt động
- [ ] **Error Handling:** Test với API fails
- [ ] **Loading States:** Spinner hiển thị đúng
- [ ] **Toast Notifications:** Messages hiển thị đúng
- [ ] **Real-time Updates:** WebSocket hoạt động (nếu có)
- [ ] **Mobile Responsive:** UI responsive trên mobile
- [ ] **Performance:** Load time < 2s
- [ ] **Edge Cases:** Empty data, network errors, etc.

---

## 📝 **Files cần tạo/sửa:**

1. ✅ `WeeklyScheduleTable.tsx` - Đã hoàn thành
2. 🔄 `src/services/coachScheduleService.ts` - Cần tạo
3. 🔄 `src/types/schedule.ts` - Cần tạo types
4. 🔄 Toast provider setup - Cần config
5. 🔄 Error boundary - Cần tạo nếu chưa có

**Sau khi hoàn thành tích hợp API, component sẽ hoạt động hoàn chỉnh với real data! 🚀**
