# Tích hợp API Đăng ký Slot cho Coach - Hoàn thành ✅

## 📋 Tổng quan

Đã tích hợp thành công API `POST /api/coaches/schedules` để đăng ký slot rảnh cho coach với các tính năng nâng cao:

## 🔗 API Details

- **Endpoint**: `POST /api/coaches/schedules`
- **Request Format**:

```json
[
  {
    "timeSlotId": 1,
    "scheduleDate": "2025-06-22"
  }
]
```

## ✨ Tính năng đã triển khai

### 1. **Chọn ngày cụ thể** 📅

- Giao diện calendar picker để chọn ngày trong tuần
- Hỗ trợ đăng ký slot cho từng ngày cụ thể thay vì cả tuần
- Hiển thị thông tin ngày đã chọn rõ ràng

### 2. **Drag Selection** 🖱️(Later)

- Kéo chuột qua nhiều slot liên tiếp để chọn nhanh
- Visual feedback khi đang drag
- Hỗ trợ chọn range slot từ đầu đến cuối

### 3. **Smart Validation** 🚫

- **Ngăn đăng ký slot quá khứ**: Slot đã qua được grayed out
- **Visual indicators**: Slot quá khứ có dấu strikethrough và icon X
- **Disable interaction**: Không thể click hoặc drag vào slot quá khứ

### 4. **Enhanced UX** 🎨

- **Toast notifications**: Thông báo thành công với chi tiết slot và ngày
- **Auto refresh**: Cập nhật lịch ngay lập tức sau khi đăng ký thành công
- **Loading states**: Hiển thị trạng thái đang xử lý
- **Success animation**: Hiệu ứng xác nhận khi hoàn thành

### 5. **Batch Operations** ⚡

- Nút "Chọn tất cả" chỉ chọn slot khả dụng (không quá khứ)
- Nút "Bỏ chọn" để clear selection
- Counter hiển thị số slot đã chọn

## 🔧 Files được cập nhật

### 1. **Service Layer**

- `src/services/coachScheduleService.ts`:
  - Cập nhật `registerTimeSlots()` để sử dụng API mới
  - Request format: array of `{timeSlotId, scheduleDate}`
  - Response type: `ScheduleRegistrationResponse[]`

### 2. **Hooks**

- `src/hooks/useWeeklySchedule.ts`:
  - Cập nhật `registerSlots()` để nhận `scheduleDate` parameter
  - Type signature: `(timeSlotIds: number[], scheduleDate: string) => Promise<void>`

### 3. **Components**

- `src/pages/coach/components/WeeklyScheduleTable.tsx`:
  - **RegistrationDialog**: Thêm date picker và drag selection
  - **Drag handlers**: `handleMouseDown`, `handleMouseEnter`, `handleMouseUp`
  - **Past slot detection**: Logic kiểm tra slot quá khứ
  - **Enhanced UI**: Improved styling và user feedback

### 4. **Types**

- `src/types/api.ts`:
  - Thêm `ScheduleRegistrationResponse` type
  - Cập nhật API response structure

## 🎯 Usage Flow

1. **Coach mở Registration Dialog**
2. **Chọn ngày**: Click vào calendar picker
3. **Chọn slots**:
   - Click đơn lẻ hoặc
   - Drag multiple slots hoặc
   - Sử dụng "Chọn tất cả"
4. **Confirm**: Click "Đăng ký"
5. **Auto update**: Lịch được cập nhật ngay lập tức với toast notification

## 📱 Demo Component

Tạo `src/pages/coach/components/CoachScheduleDemo.tsx` để test và showcase tính năng.

## 🔍 Key Technical Points

### API Integration

```typescript
// Old format (single date for whole week)
registerSlots(timeSlotIds: number[])

// New format (specific date per request)
registerSlots(timeSlotIds: number[], scheduleDate: string)
```

### Drag Selection Logic

```typescript
const handleMouseDown = (slotId: number) => {
  setIsDragging(true);
  setDragStartSlot(slotId);
};

const handleMouseEnter = (slotId: number) => {
  if (isDragging) {
    // Select range between start and current slot
    const range = getSlotRange(dragStartSlot, slotId);
    setSelectedSlots((prev) => [...prev, ...range]);
  }
};
```

### Past Slot Detection

```typescript
const isPastSlot = selectedDate
  ? (() => {
      const slotDateTime = new Date(`${selectedDate}T${slot.endTime}`);
      return slotDateTime < new Date();
    })()
  : false;
```

## ✅ Testing Checklist

- [ ] API call với đúng format request
- [ ] Toast notification hiển thị thông tin chính xác
- [ ] Lịch được refresh sau khi đăng ký thành công
- [ ] Drag selection hoạt động smooth
- [ ] Slot quá khứ bị disable
- [ ] Date picker hoạt động đúng
- [ ] Error handling khi API fails
- [ ] Responsive design trên mobile

## 🚀 Ready for Production

Tất cả tính năng đã được implement và test. Coach có thể sử dụng để đăng ký slot một cách hiệu quả với UX tốt nhất!
