--BUILDINGS--
INSERT INTO buildings (name, latitude, longitude, radius_meters) VALUES
('ML', 4.601790, -74.065750, 60),
('SD', 4.602550, -74.065200, 60),
('RGD', 4.602100, -74.064600, 50),
('AULAS', 4.601300, -74.066400, 70),
('B', 4.600900, -74.066900, 50),
('W', 4.601050, -74.064100, 55),
('LLERAS', 4.602800, -74.065900, 45),
('TX', 4.600500, -74.065300, 50),
('C', 4.601600, -74.067200, 50),
('Q', 4.602300, -74.067000, 55);

--USERS--
INSERT INTO users (name, email, password_hash, program, semester, mobility_preference, avatar_url, verified, created_at, current_building_id) VALUES
('Joel', 'joel@uniandes.edu.co', 'hash_joel_123', 'Systems Engineering', '7', 'NEAR', 'https://i.pravatar.cc/150?img=1', true, now(), NULL),
('Maria Rojas', 'maria.rojas@uniandes.edu.co', 'hash_maria_456', 'Industrial Engineering', '5', 'MID', 'https://i.pravatar.cc/150?img=2', true, now(), NULL),
('Santiago Gomez', 'santiago.gomez@uniandes.edu.co', 'hash_santi_789', 'Economics', '9', 'FAR', 'https://i.pravatar.cc/150?img=3', true, now(), NULL),
('Valentina Torres', 'valentina.torres@uniandes.edu.co', 'hash_valen_012', 'Design', '3', 'NEAR', 'https://i.pravatar.cc/150?img=4', false, now(), NULL),
('Juan Pablo Diaz', 'juanpablo.diaz@uniandes.edu.co', 'hash_juanp_345', 'Systems Engineering', '10', 'MID', 'https://i.pravatar.cc/150?img=5', true, now(), NULL),
('Camila Herrera', 'camila.herrera@uniandes.edu.co', 'hash_cami_678', 'Law', '4', 'NEAR', 'https://i.pravatar.cc/150?img=6', true, now(), NULL),
('Andres Castro', 'andres.castro@uniandes.edu.co', 'hash_andres_901', 'Mathematics', '6', 'FAR', 'https://i.pravatar.cc/150?img=7', false, now(), NULL),
('Laura Martinez', 'laura.martinez@uniandes.edu.co', 'hash_laura_234', 'Biomedical Engineering', '2', 'MID', 'https://i.pravatar.cc/150?img=8', true, now(), NULL),
('Diego Ramirez', 'diego.ramirez@uniandes.edu.co', 'hash_diego_567', 'Business Administration', '8', 'NEAR', 'https://i.pravatar.cc/150?img=9', true, now(), NULL),
('Isabella Ortiz', 'isabella.ortiz@uniandes.edu.co', 'hash_isa_890', 'Philosophy', '1', 'FAR', 'https://i.pravatar.cc/150?img=10', false, now(), NULL);


--CLASS_BLOCK--

INSERT INTO class_blocks (user_id, day_of_week, start_time, end_time, subject, location) VALUES
-- Grupo 1: gap común 10:00 - 12:00
((SELECT id FROM users WHERE email = 'joel@uniandes.edu.co'), 'MON', '08:00', '10:00', 'Machine Learning', 'ML-301'),
((SELECT id FROM users WHERE email = 'joel@uniandes.edu.co'), 'MON', '12:00', '14:00', 'Software Engineering', 'SD-405'),

((SELECT id FROM users WHERE email = 'maria.rojas@uniandes.edu.co'), 'MON', '07:00', '10:00', 'Operations Research', 'SD-201'),
((SELECT id FROM users WHERE email = 'maria.rojas@uniandes.edu.co'), 'MON', '12:00', '15:00', 'Statistics', 'SD-302'),

((SELECT id FROM users WHERE email = 'santiago.gomez@uniandes.edu.co'), 'MON', '08:30', '10:00', 'Macroeconomics', 'W-101'),
((SELECT id FROM users WHERE email = 'santiago.gomez@uniandes.edu.co'), 'MON', '12:00', '13:30', 'Econometrics', 'W-204'),

((SELECT id FROM users WHERE email = 'valentina.torres@uniandes.edu.co'), 'MON', '09:00', '10:00', 'Design Studio', 'AULAS-110'),
((SELECT id FROM users WHERE email = 'valentina.torres@uniandes.edu.co'), 'MON', '12:00', '14:00', 'Typography', 'AULAS-115'),

((SELECT id FROM users WHERE email = 'juanpablo.diaz@uniandes.edu.co'), 'MON', '07:30', '10:00', 'Capstone Project', 'B-210'),
((SELECT id FROM users WHERE email = 'juanpablo.diaz@uniandes.edu.co'), 'MON', '12:00', '13:00', 'Databases', 'B-105'),

((SELECT id FROM users WHERE email = 'camila.herrera@uniandes.edu.co'), 'MON', '08:00', '10:00', 'Constitutional Law', 'LLERAS-301'),
((SELECT id FROM users WHERE email = 'camila.herrera@uniandes.edu.co'), 'MON', '12:00', '14:00', 'Civil Law', 'LLERAS-205'),

-- Grupo 2: gap común 14:00 - 16:00
((SELECT id FROM users WHERE email = 'andres.castro@uniandes.edu.co'), 'MON', '08:00', '11:00', 'Linear Algebra', 'TX-401'),
((SELECT id FROM users WHERE email = 'andres.castro@uniandes.edu.co'), 'MON', '16:00', '18:00', 'Real Analysis', 'TX-402'),

((SELECT id FROM users WHERE email = 'laura.martinez@uniandes.edu.co'), 'MON', '09:00', '14:00', 'Anatomy Lab', 'C-101'),
((SELECT id FROM users WHERE email = 'laura.martinez@uniandes.edu.co'), 'MON', '16:00', '17:30', 'Biomechanics', 'C-102'),

((SELECT id FROM users WHERE email = 'diego.ramirez@uniandes.edu.co'), 'MON', '08:00', '14:00', 'Finance', 'Q-301'),
((SELECT id FROM users WHERE email = 'diego.ramirez@uniandes.edu.co'), 'MON', '16:00', '18:00', 'Marketing', 'Q-302'),

((SELECT id FROM users WHERE email = 'isabella.ortiz@uniandes.edu.co'), 'MON', '10:00', '14:00', 'Ethics', 'B-301'),
((SELECT id FROM users WHERE email = 'isabella.ortiz@uniandes.edu.co'), 'MON', '16:00', '17:00', 'Metaphysics', 'B-302');

INSERT INTO class_blocks (user_id, day_of_week, start_time, end_time, subject, location) VALUES
-- Gap común 09:00 - 11:00
((SELECT id FROM users WHERE email = 'joel@uniandes.edu.co'), 'WED', '07:00', '09:00', 'Algorithms', 'ML-201'),
((SELECT id FROM users WHERE email = 'joel@uniandes.edu.co'), 'WED', '11:00', '13:00', 'Distributed Systems', 'ML-305'),

((SELECT id FROM users WHERE email = 'santiago.gomez@uniandes.edu.co'), 'WED', '07:30', '09:00', 'Microeconomics', 'W-102'),
((SELECT id FROM users WHERE email = 'santiago.gomez@uniandes.edu.co'), 'WED', '11:00', '12:30', 'Game Theory', 'W-205'),

((SELECT id FROM users WHERE email = 'juanpablo.diaz@uniandes.edu.co'), 'WED', '08:00', '09:00', 'Cloud Computing', 'B-211'),
((SELECT id FROM users WHERE email = 'juanpablo.diaz@uniandes.edu.co'), 'WED', '11:00', '13:00', 'Capstone Project', 'B-210'),

((SELECT id FROM users WHERE email = 'andres.castro@uniandes.edu.co'), 'WED', '07:00', '09:00', 'Topology', 'TX-403'),
((SELECT id FROM users WHERE email = 'andres.castro@uniandes.edu.co'), 'WED', '11:00', '14:00', 'Linear Algebra', 'TX-401'),

((SELECT id FROM users WHERE email = 'diego.ramirez@uniandes.edu.co'), 'WED', '08:00', '09:00', 'Accounting', 'Q-303'),
((SELECT id FROM users WHERE email = 'diego.ramirez@uniandes.edu.co'), 'WED', '11:00', '14:00', 'Finance', 'Q-301'),

-- Gap común 15:00 - 17:00
((SELECT id FROM users WHERE email = 'maria.rojas@uniandes.edu.co'), 'WED', '09:00', '15:00', 'Operations Research', 'SD-201'),
((SELECT id FROM users WHERE email = 'maria.rojas@uniandes.edu.co'), 'WED', '17:00', '18:30', 'Quality Management', 'SD-303'),

((SELECT id FROM users WHERE email = 'valentina.torres@uniandes.edu.co'), 'WED', '10:00', '15:00', 'Design Studio', 'AULAS-110'),
((SELECT id FROM users WHERE email = 'valentina.torres@uniandes.edu.co'), 'WED', '17:00', '18:00', 'Photography', 'AULAS-120'),

((SELECT id FROM users WHERE email = 'camila.herrera@uniandes.edu.co'), 'WED', '09:00', '15:00', 'Constitutional Law', 'LLERAS-301'),
((SELECT id FROM users WHERE email = 'camila.herrera@uniandes.edu.co'), 'WED', '17:00', '18:30', 'Criminal Law', 'LLERAS-208'),

((SELECT id FROM users WHERE email = 'laura.martinez@uniandes.edu.co'), 'WED', '08:00', '15:00', 'Anatomy Lab', 'C-101'),
((SELECT id FROM users WHERE email = 'laura.martinez@uniandes.edu.co'), 'WED', '17:00', '18:00', 'Physiology', 'C-103'),

((SELECT id FROM users WHERE email = 'isabella.ortiz@uniandes.edu.co'), 'WED', '09:00', '15:00', 'Ethics', 'B-301'),
((SELECT id FROM users WHERE email = 'isabella.ortiz@uniandes.edu.co'), 'WED', '17:00', '18:00', 'Logic', 'B-303');

INSERT INTO class_blocks (user_id, day_of_week, start_time, end_time, subject, location) VALUES
-- Gap común 11:00 - 13:00
((SELECT id FROM users WHERE email = 'joel@uniandes.edu.co'), 'FRI', '08:00', '11:00', 'Machine Learning', 'ML-301'),
((SELECT id FROM users WHERE email = 'joel@uniandes.edu.co'), 'FRI', '13:00', '15:00', 'Software Engineering', 'SD-405'),

((SELECT id FROM users WHERE email = 'maria.rojas@uniandes.edu.co'), 'FRI', '08:00', '11:00', 'Statistics', 'SD-302'),
((SELECT id FROM users WHERE email = 'maria.rojas@uniandes.edu.co'), 'FRI', '13:00', '16:00', 'Operations Research', 'SD-201'),

((SELECT id FROM users WHERE email = 'valentina.torres@uniandes.edu.co'), 'FRI', '09:00', '11:00', 'Typography', 'AULAS-115'),
((SELECT id FROM users WHERE email = 'valentina.torres@uniandes.edu.co'), 'FRI', '13:00', '15:00', 'Design Studio', 'AULAS-110'),

((SELECT id FROM users WHERE email = 'andres.castro@uniandes.edu.co'), 'FRI', '08:00', '11:00', 'Real Analysis', 'TX-402'),
((SELECT id FROM users WHERE email = 'andres.castro@uniandes.edu.co'), 'FRI', '13:00', '15:00', 'Topology', 'TX-403'),

((SELECT id FROM users WHERE email = 'isabella.ortiz@uniandes.edu.co'), 'FRI', '09:00', '11:00', 'Metaphysics', 'B-302'),
((SELECT id FROM users WHERE email = 'isabella.ortiz@uniandes.edu.co'), 'FRI', '13:00', '15:00', 'Ethics', 'B-301'),

-- Gap común 13:00 - 15:00 (solapa parcialmente con el grupo de arriba)
((SELECT id FROM users WHERE email = 'santiago.gomez@uniandes.edu.co'), 'FRI', '08:00', '13:00', 'Econometrics', 'W-204'),
((SELECT id FROM users WHERE email = 'santiago.gomez@uniandes.edu.co'), 'FRI', '15:00', '17:00', 'Macroeconomics', 'W-101'),

((SELECT id FROM users WHERE email = 'juanpablo.diaz@uniandes.edu.co'), 'FRI', '08:00', '13:00', 'Databases', 'B-105'),
((SELECT id FROM users WHERE email = 'juanpablo.diaz@uniandes.edu.co'), 'FRI', '15:00', '17:00', 'Cloud Computing', 'B-211'),

((SELECT id FROM users WHERE email = 'camila.herrera@uniandes.edu.co'), 'FRI', '08:00', '13:00', 'Civil Law', 'LLERAS-205'),
((SELECT id FROM users WHERE email = 'camila.herrera@uniandes.edu.co'), 'FRI', '15:00', '17:00', 'Constitutional Law', 'LLERAS-301'),

((SELECT id FROM users WHERE email = 'diego.ramirez@uniandes.edu.co'), 'FRI', '08:00', '13:00', 'Marketing', 'Q-302'),
((SELECT id FROM users WHERE email = 'diego.ramirez@uniandes.edu.co'), 'FRI', '15:00', '17:00', 'Finance', 'Q-301'),

((SELECT id FROM users WHERE email = 'laura.martinez@uniandes.edu.co'), 'FRI', '08:00', '13:00', 'Biomechanics', 'C-102'),
((SELECT id FROM users WHERE email = 'laura.martinez@uniandes.edu.co'), 'FRI', '15:00', '17:00', 'Anatomy Lab', 'C-101');

--GAPS--
--La app los calcula automáticamente--

--INTERESTS--

INSERT INTO interests (name) VALUES
('Football'),
('Basketball'),
('Music'),
('Reading'),
('Gaming'),
('Photography'),
('Coffee'),
('Chess'),
('Hiking'),
('Movies'),
('Volunteering'),
('Cooking');

--ASOCIAR INTERESES A USUARIOS--
INSERT INTO user_interest (user_id, interest_id)
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'joel@uniandes.edu.co' AND i.name IN ('Gaming', 'Music', 'Coffee', 'Chess')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'maria.rojas@uniandes.edu.co' AND i.name IN ('Coffee', 'Reading', 'Volunteering')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'santiago.gomez@uniandes.edu.co' AND i.name IN ('Football', 'Basketball', 'Coffee', 'Movies', 'Chess')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'valentina.torres@uniandes.edu.co' AND i.name IN ('Photography', 'Music', 'Movies')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'juanpablo.diaz@uniandes.edu.co' AND i.name IN ('Gaming', 'Coffee')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'camila.herrera@uniandes.edu.co' AND i.name IN ('Reading', 'Volunteering', 'Coffee', 'Hiking')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'andres.castro@uniandes.edu.co' AND i.name IN ('Chess', 'Gaming', 'Music', 'Reading', 'Hiking')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'laura.martinez@uniandes.edu.co' AND i.name IN ('Cooking', 'Music', 'Volunteering')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'diego.ramirez@uniandes.edu.co' AND i.name IN ('Football', 'Basketball', 'Coffee')
UNION ALL
SELECT u.id, i.id FROM users u, interests i WHERE u.email = 'isabella.ortiz@uniandes.edu.co' AND i.name IN ('Reading', 'Movies', 'Hiking', 'Coffee', 'Chess');


--ACTIVITIES--
INSERT INTO activities (title, description, duration_minutes, interest_id) VALUES
-- Football
('Quick 5-a-side Football Match', 'Play a fast-paced 5-a-side football game at the nearest courts', 30, (SELECT id FROM interests WHERE name = 'Football')),
('Watch Champions League Match', 'Watch a football match at the lounge or campus cafeteria', 30, (SELECT id FROM interests WHERE name = 'Football')),

-- Basketball
('Free Throws & 21', 'Shoot some quick hoops or play a 3x3 game at the basketball court', 30, (SELECT id FROM interests WHERE name = 'Basketball')),
('Watch NBA Highlights', 'Share game highlights or catch an NBA game together', 30, (SELECT id FROM interests WHERE name = 'Basketball')),

-- Music
('Listen to a New Album', 'Share headphones and listen to a recent music release', 30, (SELECT id FROM interests WHERE name = 'Music')),
('Jam Session / Play Instruments', 'Play guitar or sing together in a quiet spot on campus', 30, (SELECT id FROM interests WHERE name = 'Music')),

-- Reading
('Express Book Club', 'Read a chapter or discuss a book both of you are currently reading', 30, (SELECT id FROM interests WHERE name = 'Reading')),
('Book Swap', 'Bring a favorite book to recommend or lend to each other', 30, (SELECT id FROM interests WHERE name = 'Reading')),

-- Gaming
('Handheld Gaming Session', 'Play a casual match on a Switch, Steam Deck, or mobile device', 30, (SELECT id FROM interests WHERE name = 'Gaming')),
('Esports & Gaming Chat', 'Discuss recent game releases, consoles, or competitive tournaments', 30, (SELECT id FROM interests WHERE name = 'Gaming')),

-- Photography
('Campus Photo Walk', 'Take photos at the best architectural and scenic spots on campus', 30, (SELECT id FROM interests WHERE name = 'Photography')),
('Portfolio Review & Editing', 'Show recent photos and exchange editing tips in Lightroom/Photoshop', 30, (SELECT id FROM interests WHERE name = 'Photography')),

-- Coffee
('Grab a Coffee', 'Go to a coffee shop and have a relaxed chat', 30, (SELECT id FROM interests WHERE name = 'Coffee')),
('Express Coffee Tasting', 'Try a specialty pour-over or brew method at the campus cafe', 30, (SELECT id FROM interests WHERE name = 'Coffee')),

-- Chess
('Blitz Chess Game', 'Play one or two fast-paced blitz chess matches', 30, (SELECT id FROM interests WHERE name = 'Chess')),
('Solve Tactical Puzzles', 'Solve chess puzzles and analyze tactical positions together', 30, (SELECT id FROM interests WHERE name = 'Chess')),

-- Hiking
('Campus Nature Walk', 'Take a refreshing outdoor walk through green areas on campus', 30, (SELECT id FROM interests WHERE name = 'Hiking')),
('Plan Weekend Hike', 'Check out trail maps and plan a hiking trip for the weekend', 30, (SELECT id FROM interests WHERE name = 'Hiking')),

-- Movies
('Watch Short Films / Trailers', 'Watch a short movie or catch up on the latest film trailers', 30, (SELECT id FROM interests WHERE name = 'Movies')),
('Film & Series Chat', 'Discuss favorite movie directors, classics, or trending TV shows', 30, (SELECT id FROM interests WHERE name = 'Movies')),

-- Volunteering
('Plan Social Initiative', 'Brainstorm or organize a quick community service project', 30, (SELECT id FROM interests WHERE name = 'Volunteering')),
('Discuss Social Impact', 'Share ideas on social projects, sustainability, and outreach', 30, (SELECT id FROM interests WHERE name = 'Volunteering')),

-- Cooking
('Recipe Exchange', 'Share cooking tips, favorite recipes, or meal prep ideas', 30, (SELECT id FROM interests WHERE name = 'Cooking')),
('Eat Lunch Together', 'Share a snack or lunch together at the campus dining area', 30, (SELECT id FROM interests WHERE name = 'Cooking'));