# API Requirements cho Weekly Schedule Table

## 📋 **API Endpoints cần thiết**

### 1. **GET /api/timeslots** ✅ (Đã có)

Lấy danh sách tất cả time slots có sẵn

```json
{
  "status": 200,
  "message": "Lấy danh sách tất cả time slot thành công",
  "data": [
    {
      "timeSlotId": 1,
      "label": "08:00",
      "startTime": "08:00:00",
      "endTime": "09:00:00",
      "deleted": false
    }
  ]
}
```

### 2. **GET /api/coach/schedule/week** 🔥 (Cần tạo mới)

Lấy lịch tuần của coach với các slot đã đăng ký và appointments

```json
// Query params: ?startDate=2025-06-15 (Chủ nhật đầu tuần)
{
  "status": 200,
  "message": "Lấy lịch tuần thành công",
  "data": {
    "weekStart": "2025-06-15",
    "weekEnd": "2025-06-21",
    "registeredSlots": [
      {
        "date": "2025-06-16",
        "timeSlotId": 1,
        "isAvailable": true,
        "appointment": {
          "appointmentId": 123,
          "clientName": "Nguyễn Văn A",
          "clientId": 456,
          "status": "confirmed",
          "notes": "Tư vấn lần đầu",
          "method": "phone",
          "location": null,
          "createdAt": "2025-06-15T10:00:00Z"
        }
      },
      {
        "date": "2025-06-16",
        "timeSlotId": 2,
        "isAvailable": true,
        "appointment": null
      }
    ]
  }
}
```

### 3. **POST /api/coach/slots/register** 🔥 (Cần tạo mới)

Đăng ký coach cho các time slots

```json
// Request Body:
{
  "slots": [
    {
      "date": "2025-06-16",
      "timeSlotIds": [1, 2, 3, 4]
    },
    {
      "date": "2025-06-17",
      "timeSlotIds": [1, 2, 5, 6]
    }
  ]
}

// Response:
{
  "status": 200,
  "message": "Đăng ký slots thành công",
  "data": {
    "registeredCount": 8,
    "registeredSlots": [...] // Same format as GET week
  }
}
```

### 4. **DELETE /api/coach/slots/unregister** 🔥 (Cần tạo mới)

Hủy đăng ký slot của coach

```json
// Request Body:
{
  "date": "2025-06-16",
  "timeSlotId": 1
}

// Response:
{
  "status": 200,
  "message": "Hủy đăng ký slot thành công"
}
```

### 5. **PUT /api/appointments/{appointmentId}/status** 🔥 (Cần tạo mới)

Cập nhật trạng thái appointment

```json
// Request Body:
{
  "status": "confirmed", // confirmed | cancelled | completed
  "notes": "Cập nhật ghi chú mới"
}

// Response:
{
  "status": 200,
  "message": "Cập nhật trạng thái thành công",
  "data": {
    "appointmentId": 123,
    "status": "confirmed",
    "updatedAt": "2025-06-16T10:30:00Z"
  }
}
```

### 6. **GET /api/appointments/{appointmentId}/details** 🔥 (Cần tạo mới)

Lấy chi tiết appointment

```json
{
  "status": 200,
  "message": "Lấy chi tiết appointment thành công",
  "data": {
    "appointmentId": 123,
    "clientName": "Nguyễn Văn A",
    "clientEmail": "nguyenvana@email.com",
    "clientPhone": "0123456789",
    "date": "2025-06-16",
    "timeSlot": {
      "timeSlotId": 1,
      "label": "08:00",
      "startTime": "08:00:00",
      "endTime": "09:00:00"
    },
    "status": "confirmed",
    "notes": "Tư vấn lần đầu",
    "method": "phone",
    "location": null,
    "createdAt": "2025-06-15T10:00:00Z",
    "updatedAt": "2025-06-16T10:30:00Z"
  }
}
```

### 7. **POST /api/coach/appointments/create** 🔥 (Cần tạo mới)

Tạo appointment cho client từ coach

```json
// Request Body:
{
  "clientName": "Nguyễn Văn B",
  "clientEmail": "nguyenvanb@email.com", // Optional
  "clientPhone": "0987654321", // Optional
  "date": "2025-06-17",
  "timeSlotId": 2,
  "method": "in-person",
  "location": "Phòng tư vấn 101",
  "notes": "Cuộc hẹn khẩn cấp"
}

// Response:
{
  "status": 200,
  "message": "Tạo appointment thành công",
  "data": {
    "appointmentId": 124,
    "status": "scheduled",
    "createdAt": "2025-06-16T11:00:00Z"
  }
}
```

## 🗂️ **Database Schema Suggestions**

### **coach_slots table** (Mới)

```sql
CREATE TABLE coach_slots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    coach_id BIGINT NOT NULL,
    date DATE NOT NULL,
    time_slot_id BIGINT NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (coach_id) REFERENCES users(id),
    FOREIGN KEY (time_slot_id) REFERENCES time_slots(id),
    UNIQUE KEY unique_coach_slot (coach_id, date, time_slot_id)
);
```

### **appointments table** (Cập nhật)

```sql
-- Thêm các columns mới nếu chưa có:
ALTER TABLE appointments ADD COLUMN coach_slot_id BIGINT;
ALTER TABLE appointments ADD COLUMN method ENUM('phone', 'in-person') DEFAULT 'phone';
ALTER TABLE appointments ADD COLUMN location VARCHAR(255);
ALTER TABLE appointments ADD COLUMN status ENUM('scheduled', 'confirmed', 'cancelled', 'completed') DEFAULT 'scheduled';

-- Foreign key
ALTER TABLE appointments ADD FOREIGN KEY (coach_slot_id) REFERENCES coach_slots(id);
```

## 🔐 **Authorization Requirements**

- **Coach Role:** Chỉ có thể truy cập/chỉnh sửa slots và appointments của chính mình
- **Admin Role:** Có thể xem tất cả coaches
- **Member Role:** Chỉ có thể book appointments, không thể access coach APIs

## ⚡ **Performance Considerations**

1. **Index trên coach_slots:** (coach_id, date) để query nhanh theo tuần
2. **Cache:** Cache time_slots vì ít thay đổi
3. **Pagination:** Cho danh sách appointments nếu cần
4. **Real-time:** WebSocket để update appointments real-time (tùy chọn)

## 🧪 **Testing Scenarios**

1. **Coach đăng ký slots cho tuần mới**
2. **Member book appointment vào slot available**
3. **Coach confirm/cancel appointments**
4. **Conflict handling:** 2 members book cùng 1 slot
5. **Coach unregister slot có appointment**
6. **Week navigation và data loading**

---

## 📝 **Next Steps Implementation**

1. **Backend:** Tạo các API endpoints trên
2. **Frontend:** Integrate với WeeklyScheduleTable
3. **Testing:** Test các edge cases
4. **UI/UX:** Polish animations và responsiveness
5. **Real-time:** Add WebSocket nếu cần
