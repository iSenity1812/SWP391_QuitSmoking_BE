describe('FCM Token Flow', () => {
  it('should get FCM token and send to backend', () => {
    cy.visit('http://localhost:5173');
    // Giả lập login, set userId vào localStorage
    cy.window().then(win => win.localStorage.setItem('userId', 'testuser'));
    // Reload để trigger lấy token và gửi về BE
    cy.reload();
    // Kiểm tra log hoặc gọi API BE để xác nhận token đã lưu
    cy.request('GET', 'http://localhost:8080/api/fcm-token/user/testuser')
      .its('status').should('eq', 200);
  });
}); 