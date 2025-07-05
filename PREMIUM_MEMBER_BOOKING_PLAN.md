# PREMIUM MEMBER BOOKING SYSTEM - DETAILED PLAN

## 🎯 MỤC TIÊU

Cho phép Premium Members có thể book lịch hẹn với Coach thông qua Weekly Schedule Table.

## 📋 KIẾN TRÚC HỆ THỐNG

### 1. DATA FLOW

```
Coach → Đăng ký Time Slots → Slots trở thành Available
Premium Member → Xem Available Slots → Book Appointment
Coach → Confirm/Reject Appointment → Update Status
Member → Receive Notification → Attend Session
```

### 2. DATABASE SCHEMA EXTENSIONS

#### CoachSchedule Table

```sql
- scheduleId (PK)
- coachId (FK)
- timeSlotId (FK)
- scheduleDate
- isAvailable (boolean)
- maxBookings (default 1)
- currentBookings (default 0)
- created_at
- updated_at
```

#### Appointment Table

```sql
- appointmentId (PK)
- memberId (FK)
- scheduleId (FK)
- status (PENDING, CONFIRMED, CANCELLED, COMPLETED)
- bookingTime
- notes
- method (PHONE, IN_PERSON)
- location
- created_at
- updated_at
```

### 3. API ENDPOINTS

#### Coach APIs

```
GET /api/coach/schedule/weekly?startDate=2025-06-16
POST /api/coach/slots/register
PUT /api/coach/appointments/{id}/status
DELETE /api/coach/slots/{scheduleId}
```

#### Member APIs

```
GET /api/member/coaches/{coachId}/availability?date=2025-06-16
POST /api/member/appointments/book
GET /api/member/appointments/my-bookings
PUT /api/member/appointments/{id}/cancel
```

## 🛠️ IMPLEMENTATION PHASES

### PHASE 1: Member UI Components

- [ ] AvailableSlotViewer.tsx - Xem slots available của coach
- [ ] BookingModal.tsx - Modal book appointment
- [ ] MyBookingsPage.tsx - Quản lý lịch hẹn đã book
- [ ] BookingStatusBadge.tsx - Hiển thị trạng thái booking

### PHASE 2: Booking Flow

- [ ] Member chọn Coach
- [ ] Xem lịch available slots của Coach
- [ ] Book appointment với notes
- [ ] Receive confirmation/rejection
- [ ] Manage booked appointments

### PHASE 3: Notifications & Real-time

- [ ] Email notifications khi có booking mới
- [ ] Real-time updates với WebSocket
- [ ] Push notifications (nếu có mobile app)

### PHASE 4: Advanced Features

- [ ] Recurring appointments
- [ ] Group coaching sessions
- [ ] Rating & feedback system
- [ ] Payment integration (nếu có phí)

## 🎨 UI/UX DESIGN

### Member View

```
Weekly Calendar View:
- Hiển thị tất cả coaches available
- Filter by coach, time, method
- Color coding: Available, Booked, Full
- Quick book button
```

### Booking Modal

```
- Coach info (name, avatar, rating)
- Selected time slot
- Booking method (phone/in-person)
- Personal notes field
- Confirm/Cancel buttons
```

## 🔄 STATUS FLOW

```
Coach Side:    [AVAILABLE] → [BOOKED] → [CONFIRMED] → [COMPLETED]
                                   ↘ [REJECTED]
                                   ↘ [CANCELLED]

Member Side:   [BOOK] → [PENDING] → [CONFIRMED] → [ATTEND]
                               ↘ [REJECTED]
                               ↘ [CANCELLED]
```

## 📱 RESPONSIVE DESIGN

- Mobile-first approach
- Touch-friendly buttons
- Swipe navigation for calendar
- Bottom sheet modals on mobile

## 🔧 TECHNICAL CONSIDERATIONS

### Performance

- Virtual scrolling for large date ranges
- Lazy loading of appointments
- Optimistic updates for better UX

### Security

- Rate limiting for booking requests
- Validation cho booking conflicts
- Authorization checks

### Accessibility

- Keyboard navigation
- Screen reader support
- High contrast mode
- Focus management

## 📊 METRICS TO TRACK

- Booking success rate
- Coach utilization rate
- Member satisfaction scores
- No-show rates
- Popular time slots

---

## 🚀 NEXT STEPS

1. Create Member booking components
2. Setup API integration
3. Add notification system
4. Testing & optimization
