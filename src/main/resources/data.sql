-- Example Achievement Data
INSERT INTO Achievement (Name, IconUrl, Criteria, Description) VALUES
('First Step', '/icons/first-step.png', '{"type": "daysWithoutSmoking", "value": 1}', 'Complete your first day without smoking'),
('Week Warrior', '/icons/week-warrior.png', '{"type": "daysWithoutSmoking", "value": 7}', 'Stay smoke-free for a full week'),
('Month Master', '/icons/month-master.png', '{"type": "daysWithoutSmoking", "value": 30}', 'Achieve one month of being smoke-free'),
('Money Saver', '/icons/money-saver.png', '{"type": "moneySaved", "value": 100000}', 'Save 100,000 VND by not smoking'),
('Big Saver', '/icons/big-saver.png', '{"type": "moneySaved", "value": 500000}', 'Save 500,000 VND by not smoking'),
('Reduction Champion', '/icons/reduction-champion.png', '{"type": "reductionAmount", "value": 10}', 'Reduce smoking by 10 cigarettes per day'),
('Health Hero', '/icons/health-hero.png', '{"type": "daysWithoutSmoking", "value": 90}', 'Stay smoke-free for 3 months'),
('Quit Master', '/icons/quit-master.png', '{"type": "daysWithoutSmoking", "value": 365}', 'Stay smoke-free for a full year');

-- Example PlanType Data
INSERT INTO PlanType (PlanTypeID, PlanName, Duration, DurationType, Description) VALUES
('GRADUAL', 'Gradual Reduction', 30, 'DAY', 'Gradually reduce smoking over 30 days'),
('COLD_TURKEY', 'Cold Turkey', 90, 'DAY', 'Stop smoking immediately for 90 days'),
('MODERATE', 'Moderate Reduction', 60, 'DAY', 'Moderate reduction over 60 days'),
('EXTENDED', 'Extended Plan', 180, 'DAY', 'Extended 6-month quit plan');

-- Example User Data (if needed for testing)
-- Note: You may need to adjust these based on your actual User/Member structure 