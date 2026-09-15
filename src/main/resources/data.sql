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

-- ============================================================
-- SEED GAPFINDER - estado basico
-- ============================================================
-- Se anexa DESPUES del data.sql actual (que ya crea buildings,
-- los 10 usuarios iniciales, class_blocks, interests y activities).
--
-- Pobla: users (+20), user_interest, friendships, groups,
--        group_member, open_tables, open_table_participants.
--
-- NO pobla: matches, messages, notifications, ratings, gaps.
-- Esos nacen de la app.
--
-- Todo el archivo es re-ejecutable: cada bloque valida con
-- NOT EXISTS / ON CONFLICT, asi que correrlo dos veces no duplica.
--
-- REQUISITO en application.properties para que Spring lo ejecute:
--   spring.sql.init.mode=always
--   spring.jpa.defer-datasource-initialization=true
-- ============================================================


-- ============================================================
-- 1. USERS (20 nuevos, total 30)
-- ============================================================
-- current_building_id queda NULL: lo actualiza la app con los
-- UserLocationLog cuando el usuario entra a un edificio.

INSERT INTO users (name, email, password_hash, program, semester, mobility_preference, avatar_url, verified, created_at, current_building_id)
VALUES
('Sofia Arias',        'sofia.arias@uniandes.edu.co',        'hash_sofia_101',    'Systems Engineering',       '7',  'NEAR', 'https://i.pravatar.cc/150?img=11', true,  now(), NULL),
('Daniela Gutierrez',  'daniela.gutierrez@uniandes.edu.co',  'hash_daniela_102',  'Design',                    '6',  'MID',  'https://i.pravatar.cc/150?img=12', true,  now(), NULL),
('Nicolas Pardo',      'nicolas.pardo@uniandes.edu.co',      'hash_nicolas_103',  'Systems Engineering',       '8',  'FAR',  'https://i.pravatar.cc/150?img=13', true,  now(), NULL),
('Miguel Cabrera',     'miguel.cabrera@uniandes.edu.co',     'hash_miguel_104',   'Systems Engineering',       '7',  'NEAR', 'https://i.pravatar.cc/150?img=14', true,  now(), NULL),
('Mariana Lopez',      'mariana.lopez@uniandes.edu.co',      'hash_mariana_105',  'Psychology',                '4',  'MID',  'https://i.pravatar.cc/150?img=15', true,  now(), NULL),
('Tomas Restrepo',     'tomas.restrepo@uniandes.edu.co',     'hash_tomas_106',    'Mechanical Engineering',    '5',  'FAR',  'https://i.pravatar.cc/150?img=16', false, now(), NULL),
('Paula Jimenez',      'paula.jimenez@uniandes.edu.co',      'hash_paula_107',    'Medicine',                  '6',  'NEAR', 'https://i.pravatar.cc/150?img=17', true,  now(), NULL),
('Felipe Moreno',      'felipe.moreno@uniandes.edu.co',      'hash_felipe_108',   'Economics',                 '3',  'MID',  'https://i.pravatar.cc/150?img=18', true,  now(), NULL),
('Daniel Vargas',      'daniel.vargas@uniandes.edu.co',      'hash_daniel_109',   'Systems Engineering',       '9',  'NEAR', 'https://i.pravatar.cc/150?img=19', true,  now(), NULL),
('Antonia Silva',      'antonia.silva@uniandes.edu.co',      'hash_antonia_110',  'Literature',                '2',  'FAR',  'https://i.pravatar.cc/150?img=20', false, now(), NULL),
('Sebastian Rincon',   'sebastian.rincon@uniandes.edu.co',   'hash_sebas_111',    'Industrial Engineering',    '7',  'MID',  'https://i.pravatar.cc/150?img=21', true,  now(), NULL),
('Juliana Pena',       'juliana.pena@uniandes.edu.co',       'hash_juliana_112',  'Architecture',              '5',  'NEAR', 'https://i.pravatar.cc/150?img=22', true,  now(), NULL),
('Carlos Mendoza',     'carlos.mendoza@uniandes.edu.co',     'hash_carlos_113',   'Physics',                   '8',  'FAR',  'https://i.pravatar.cc/150?img=23', true,  now(), NULL),
('Natalia Rueda',      'natalia.rueda@uniandes.edu.co',      'hash_natalia_114',  'Biology',                   '4',  'MID',  'https://i.pravatar.cc/150?img=24', false, now(), NULL),
('Esteban Ortiz',      'esteban.ortiz@uniandes.edu.co',      'hash_esteban_115',  'Civil Engineering',         '6',  'NEAR', 'https://i.pravatar.cc/150?img=25', true,  now(), NULL),
('Gabriela Nieto',     'gabriela.nieto@uniandes.edu.co',     'hash_gabriela_116', 'Political Science',         '3',  'MID',  'https://i.pravatar.cc/150?img=26', true,  now(), NULL),
('Ricardo Salazar',    'ricardo.salazar@uniandes.edu.co',    'hash_ricardo_117',  'Chemical Engineering',      '9',  'FAR',  'https://i.pravatar.cc/150?img=27', true,  now(), NULL),
('Valeria Cardenas',   'valeria.cardenas@uniandes.edu.co',   'hash_valeria_118',  'Music',                     '5',  'NEAR', 'https://i.pravatar.cc/150?img=28', true,  now(), NULL),
('Simon Aguilar',      'simon.aguilar@uniandes.edu.co',      'hash_simon_119',    'Anthropology',              '2',  'MID',  'https://i.pravatar.cc/150?img=29', false, now(), NULL),
('Lucia Beltran',      'lucia.beltran@uniandes.edu.co',      'hash_lucia_120',    'Environmental Engineering', '7',  'NEAR', 'https://i.pravatar.cc/150?img=30', true,  now(), NULL)
ON CONFLICT (email) DO NOTHING;


-- ============================================================
-- 2. USER_INTEREST (intereses de los 20 nuevos)
-- ============================================================
-- Los intereses se repiten entre usuarios a proposito: de ahi
-- sale la compatibilidad que calcula MatchService.

INSERT INTO user_interest (user_id, interest_id)
SELECT u.id, i.id
FROM (VALUES
  ('sofia.arias@uniandes.edu.co',        'Coffee'),
  ('sofia.arias@uniandes.edu.co',        'Reading'),
  ('sofia.arias@uniandes.edu.co',        'Music'),
  ('sofia.arias@uniandes.edu.co',        'Photography'),
  ('daniela.gutierrez@uniandes.edu.co',  'Photography'),
  ('daniela.gutierrez@uniandes.edu.co',  'Movies'),
  ('daniela.gutierrez@uniandes.edu.co',  'Coffee'),
  ('nicolas.pardo@uniandes.edu.co',      'Gaming'),
  ('nicolas.pardo@uniandes.edu.co',      'Chess'),
  ('nicolas.pardo@uniandes.edu.co',      'Basketball'),
  ('miguel.cabrera@uniandes.edu.co',     'Gaming'),
  ('miguel.cabrera@uniandes.edu.co',     'Football'),
  ('miguel.cabrera@uniandes.edu.co',     'Music'),
  ('miguel.cabrera@uniandes.edu.co',     'Coffee'),
  ('mariana.lopez@uniandes.edu.co',      'Reading'),
  ('mariana.lopez@uniandes.edu.co',      'Volunteering'),
  ('mariana.lopez@uniandes.edu.co',      'Coffee'),
  ('tomas.restrepo@uniandes.edu.co',     'Football'),
  ('tomas.restrepo@uniandes.edu.co',     'Hiking'),
  ('tomas.restrepo@uniandes.edu.co',     'Gaming'),
  ('paula.jimenez@uniandes.edu.co',      'Cooking'),
  ('paula.jimenez@uniandes.edu.co',      'Volunteering'),
  ('paula.jimenez@uniandes.edu.co',      'Movies'),
  ('paula.jimenez@uniandes.edu.co',      'Coffee'),
  ('felipe.moreno@uniandes.edu.co',      'Chess'),
  ('felipe.moreno@uniandes.edu.co',      'Basketball'),
  ('felipe.moreno@uniandes.edu.co',      'Movies'),
  ('daniel.vargas@uniandes.edu.co',      'Gaming'),
  ('daniel.vargas@uniandes.edu.co',      'Music'),
  ('daniel.vargas@uniandes.edu.co',      'Chess'),
  ('daniel.vargas@uniandes.edu.co',      'Coffee'),
  ('antonia.silva@uniandes.edu.co',      'Reading'),
  ('antonia.silva@uniandes.edu.co',      'Movies'),
  ('antonia.silva@uniandes.edu.co',      'Music'),
  ('sebastian.rincon@uniandes.edu.co',   'Football'),
  ('sebastian.rincon@uniandes.edu.co',   'Basketball'),
  ('sebastian.rincon@uniandes.edu.co',   'Coffee'),
  ('juliana.pena@uniandes.edu.co',       'Photography'),
  ('juliana.pena@uniandes.edu.co',       'Hiking'),
  ('juliana.pena@uniandes.edu.co',       'Cooking'),
  ('carlos.mendoza@uniandes.edu.co',     'Chess'),
  ('carlos.mendoza@uniandes.edu.co',     'Reading'),
  ('carlos.mendoza@uniandes.edu.co',     'Gaming'),
  ('natalia.rueda@uniandes.edu.co',      'Hiking'),
  ('natalia.rueda@uniandes.edu.co',      'Volunteering'),
  ('natalia.rueda@uniandes.edu.co',      'Cooking'),
  ('esteban.ortiz@uniandes.edu.co',      'Football'),
  ('esteban.ortiz@uniandes.edu.co',      'Movies'),
  ('esteban.ortiz@uniandes.edu.co',      'Coffee'),
  ('gabriela.nieto@uniandes.edu.co',     'Volunteering'),
  ('gabriela.nieto@uniandes.edu.co',     'Reading'),
  ('gabriela.nieto@uniandes.edu.co',     'Coffee'),
  ('ricardo.salazar@uniandes.edu.co',    'Basketball'),
  ('ricardo.salazar@uniandes.edu.co',    'Gaming'),
  ('ricardo.salazar@uniandes.edu.co',    'Hiking'),
  ('valeria.cardenas@uniandes.edu.co',   'Music'),
  ('valeria.cardenas@uniandes.edu.co',   'Photography'),
  ('valeria.cardenas@uniandes.edu.co',   'Movies'),
  ('valeria.cardenas@uniandes.edu.co',   'Coffee'),
  ('simon.aguilar@uniandes.edu.co',      'Reading'),
  ('simon.aguilar@uniandes.edu.co',      'Hiking'),
  ('simon.aguilar@uniandes.edu.co',      'Volunteering'),
  ('lucia.beltran@uniandes.edu.co',      'Hiking'),
  ('lucia.beltran@uniandes.edu.co',      'Cooking'),
  ('lucia.beltran@uniandes.edu.co',      'Volunteering'),
  ('lucia.beltran@uniandes.edu.co',      'Coffee')
) AS v(email, interest_name)
JOIN users u     ON u.email = v.email
JOIN interests i ON i.name  = v.interest_name
WHERE NOT EXISTS (
  SELECT 1 FROM user_interest ui
  WHERE ui.user_id = u.id AND ui.interest_id = i.id
);


-- ============================================================
-- 3. FRIENDSHIPS
-- ============================================================
-- Grafo conectado: cada usuario queda con 3 a 7 amigos.
-- La mayoria ACCEPTED (son los que se ven en "amigos libres ahora").
-- Al final hay un bloque PENDING para demostrar solicitudes.

-- 3.1 Amistades aceptadas
INSERT INTO friendships (requester_id, addressee_id, status, created_at)
SELECT r.id, a.id, 'ACCEPTED', now() - (v.dias || ' days')::interval
FROM (VALUES
  ('joel@uniandes.edu.co',              'sofia.arias@uniandes.edu.co',        120),
  ('joel@uniandes.edu.co',              'nicolas.pardo@uniandes.edu.co',      110),
  ('joel@uniandes.edu.co',              'miguel.cabrera@uniandes.edu.co',     105),
  ('joel@uniandes.edu.co',              'daniela.gutierrez@uniandes.edu.co',   95),
  ('joel@uniandes.edu.co',              'juanpablo.diaz@uniandes.edu.co',      90),
  ('sofia.arias@uniandes.edu.co',       'daniela.gutierrez@uniandes.edu.co',  115),
  ('sofia.arias@uniandes.edu.co',       'nicolas.pardo@uniandes.edu.co',      100),
  ('sofia.arias@uniandes.edu.co',       'maria.rojas@uniandes.edu.co',         80),
  ('sofia.arias@uniandes.edu.co',       'valeria.cardenas@uniandes.edu.co',    60),
  ('miguel.cabrera@uniandes.edu.co',    'nicolas.pardo@uniandes.edu.co',       98),
  ('miguel.cabrera@uniandes.edu.co',    'daniel.vargas@uniandes.edu.co',       75),
  ('miguel.cabrera@uniandes.edu.co',    'tomas.restrepo@uniandes.edu.co',      70),
  ('daniela.gutierrez@uniandes.edu.co', 'juliana.pena@uniandes.edu.co',        85),
  ('daniela.gutierrez@uniandes.edu.co', 'valentina.torres@uniandes.edu.co',    82),
  ('nicolas.pardo@uniandes.edu.co',     'carlos.mendoza@uniandes.edu.co',      68),
  ('nicolas.pardo@uniandes.edu.co',     'andres.castro@uniandes.edu.co',       64),
  ('maria.rojas@uniandes.edu.co',       'camila.herrera@uniandes.edu.co',      88),
  ('maria.rojas@uniandes.edu.co',       'mariana.lopez@uniandes.edu.co',       72),
  ('maria.rojas@uniandes.edu.co',       'gabriela.nieto@uniandes.edu.co',      55),
  ('santiago.gomez@uniandes.edu.co',    'diego.ramirez@uniandes.edu.co',       92),
  ('santiago.gomez@uniandes.edu.co',    'sebastian.rincon@uniandes.edu.co',    66),
  ('santiago.gomez@uniandes.edu.co',    'esteban.ortiz@uniandes.edu.co',       58),
  ('valentina.torres@uniandes.edu.co',  'juliana.pena@uniandes.edu.co',        78),
  ('valentina.torres@uniandes.edu.co',  'antonia.silva@uniandes.edu.co',       50),
  ('juanpablo.diaz@uniandes.edu.co',    'daniel.vargas@uniandes.edu.co',       86),
  ('juanpablo.diaz@uniandes.edu.co',    'ricardo.salazar@uniandes.edu.co',     52),
  ('camila.herrera@uniandes.edu.co',    'paula.jimenez@uniandes.edu.co',       74),
  ('camila.herrera@uniandes.edu.co',    'natalia.rueda@uniandes.edu.co',       48),
  ('andres.castro@uniandes.edu.co',     'carlos.mendoza@uniandes.edu.co',      62),
  ('andres.castro@uniandes.edu.co',     'felipe.moreno@uniandes.edu.co',       44),
  ('laura.martinez@uniandes.edu.co',    'natalia.rueda@uniandes.edu.co',       56),
  ('laura.martinez@uniandes.edu.co',    'lucia.beltran@uniandes.edu.co',       42),
  ('diego.ramirez@uniandes.edu.co',     'sebastian.rincon@uniandes.edu.co',    60),
  ('diego.ramirez@uniandes.edu.co',     'esteban.ortiz@uniandes.edu.co',       40),
  ('isabella.ortiz@uniandes.edu.co',    'antonia.silva@uniandes.edu.co',       54),
  ('isabella.ortiz@uniandes.edu.co',    'simon.aguilar@uniandes.edu.co',       38),
  ('mariana.lopez@uniandes.edu.co',     'paula.jimenez@uniandes.edu.co',       46),
  ('mariana.lopez@uniandes.edu.co',     'gabriela.nieto@uniandes.edu.co',      36),
  ('tomas.restrepo@uniandes.edu.co',    'ricardo.salazar@uniandes.edu.co',     34),
  ('tomas.restrepo@uniandes.edu.co',    'esteban.ortiz@uniandes.edu.co',       32),
  ('paula.jimenez@uniandes.edu.co',     'lucia.beltran@uniandes.edu.co',       30),
  ('felipe.moreno@uniandes.edu.co',     'sebastian.rincon@uniandes.edu.co',    28),
  ('daniel.vargas@uniandes.edu.co',     'carlos.mendoza@uniandes.edu.co',      26),
  ('antonia.silva@uniandes.edu.co',     'valeria.cardenas@uniandes.edu.co',    24),
  ('juliana.pena@uniandes.edu.co',      'natalia.rueda@uniandes.edu.co',       22),
  ('gabriela.nieto@uniandes.edu.co',    'simon.aguilar@uniandes.edu.co',       20),
  ('natalia.rueda@uniandes.edu.co',     'lucia.beltran@uniandes.edu.co',       18),
  ('carlos.mendoza@uniandes.edu.co',    'ricardo.salazar@uniandes.edu.co',     16),
  ('simon.aguilar@uniandes.edu.co',     'lucia.beltran@uniandes.edu.co',       14),
  ('valeria.cardenas@uniandes.edu.co',  'juliana.pena@uniandes.edu.co',        12)
) AS v(req_email, addr_email, dias)
JOIN users r ON r.email = v.req_email
JOIN users a ON a.email = v.addr_email
WHERE NOT EXISTS (
  SELECT 1 FROM friendships f
  WHERE (f.requester_id = r.id AND f.addressee_id = a.id)
     OR (f.requester_id = a.id AND f.addressee_id = r.id)
);

-- 3.2 Solicitudes pendientes
INSERT INTO friendships (requester_id, addressee_id, status, created_at)
SELECT r.id, a.id, 'PENDING', now() - (v.dias || ' days')::interval
FROM (VALUES
  ('felipe.moreno@uniandes.edu.co',     'joel@uniandes.edu.co',              3),
  ('simon.aguilar@uniandes.edu.co',     'sofia.arias@uniandes.edu.co',       2),
  ('esteban.ortiz@uniandes.edu.co',     'miguel.cabrera@uniandes.edu.co',    2),
  ('natalia.rueda@uniandes.edu.co',     'daniela.gutierrez@uniandes.edu.co', 1),
  ('lucia.beltran@uniandes.edu.co',     'nicolas.pardo@uniandes.edu.co',     1),
  ('ricardo.salazar@uniandes.edu.co',   'santiago.gomez@uniandes.edu.co',    1)
) AS v(req_email, addr_email, dias)
JOIN users r ON r.email = v.req_email
JOIN users a ON a.email = v.addr_email
WHERE NOT EXISTS (
  SELECT 1 FROM friendships f
  WHERE (f.requester_id = r.id AND f.addressee_id = a.id)
     OR (f.requester_id = a.id AND f.addressee_id = r.id)
);


-- ============================================================
-- 4. GROUPS
-- ============================================================
-- Grupos recien creados: nombre, creador y miembros. Sin Open
-- Tables privadas asociadas (esas se crean desde la app).

INSERT INTO groups (name, creator_id, created_at)
SELECT v.nombre, c.id, now() - (v.dias || ' days')::interval
FROM (VALUES
  ('Parche Sistemas 2026-2', 'joel@uniandes.edu.co',              45),
  ('Cafe de las 10',         'sofia.arias@uniandes.edu.co',       38),
  ('Futbol ML',              'santiago.gomez@uniandes.edu.co',    30),
  ('Club de Lectura',        'camila.herrera@uniandes.edu.co',    25),
  ('Gaming Uniandes',        'nicolas.pardo@uniandes.edu.co',     20),
  ('Salidas y Montana',      'natalia.rueda@uniandes.edu.co',     12)
) AS v(nombre, creator_email, dias)
JOIN users c ON c.email = v.creator_email
WHERE NOT EXISTS (SELECT 1 FROM groups g WHERE g.name = v.nombre);

-- 4.1 Miembros de cada grupo (el creador tambien se incluye)
INSERT INTO group_member (group_id, user_id)
SELECT g.id, u.id
FROM (VALUES
  ('Parche Sistemas 2026-2', 'joel@uniandes.edu.co'),
  ('Parche Sistemas 2026-2', 'sofia.arias@uniandes.edu.co'),
  ('Parche Sistemas 2026-2', 'nicolas.pardo@uniandes.edu.co'),
  ('Parche Sistemas 2026-2', 'miguel.cabrera@uniandes.edu.co'),
  ('Parche Sistemas 2026-2', 'daniel.vargas@uniandes.edu.co'),
  ('Parche Sistemas 2026-2', 'juanpablo.diaz@uniandes.edu.co'),
  ('Cafe de las 10',         'sofia.arias@uniandes.edu.co'),
  ('Cafe de las 10',         'daniela.gutierrez@uniandes.edu.co'),
  ('Cafe de las 10',         'maria.rojas@uniandes.edu.co'),
  ('Cafe de las 10',         'valeria.cardenas@uniandes.edu.co'),
  ('Cafe de las 10',         'gabriela.nieto@uniandes.edu.co'),
  ('Futbol ML',              'santiago.gomez@uniandes.edu.co'),
  ('Futbol ML',              'diego.ramirez@uniandes.edu.co'),
  ('Futbol ML',              'sebastian.rincon@uniandes.edu.co'),
  ('Futbol ML',              'esteban.ortiz@uniandes.edu.co'),
  ('Futbol ML',              'tomas.restrepo@uniandes.edu.co'),
  ('Futbol ML',              'miguel.cabrera@uniandes.edu.co'),
  ('Club de Lectura',        'camila.herrera@uniandes.edu.co'),
  ('Club de Lectura',        'isabella.ortiz@uniandes.edu.co'),
  ('Club de Lectura',        'antonia.silva@uniandes.edu.co'),
  ('Club de Lectura',        'mariana.lopez@uniandes.edu.co'),
  ('Club de Lectura',        'simon.aguilar@uniandes.edu.co'),
  ('Gaming Uniandes',        'nicolas.pardo@uniandes.edu.co'),
  ('Gaming Uniandes',        'miguel.cabrera@uniandes.edu.co'),
  ('Gaming Uniandes',        'daniel.vargas@uniandes.edu.co'),
  ('Gaming Uniandes',        'carlos.mendoza@uniandes.edu.co'),
  ('Gaming Uniandes',        'ricardo.salazar@uniandes.edu.co'),
  ('Gaming Uniandes',        'andres.castro@uniandes.edu.co'),
  ('Salidas y Montana',      'natalia.rueda@uniandes.edu.co'),
  ('Salidas y Montana',      'lucia.beltran@uniandes.edu.co'),
  ('Salidas y Montana',      'juliana.pena@uniandes.edu.co'),
  ('Salidas y Montana',      'simon.aguilar@uniandes.edu.co'),
  ('Salidas y Montana',      'tomas.restrepo@uniandes.edu.co')
) AS v(group_name, user_email)
JOIN groups g ON g.name  = v.group_name
JOIN users  u ON u.email = v.user_email
WHERE NOT EXISTS (
  SELECT 1 FROM group_member gm
  WHERE gm.group_id = g.id AND gm.user_id = u.id
);


-- ============================================================
-- 5. OPEN TABLES
-- ============================================================
-- Todas publicas (group_id NULL) y ACTIVE.
-- Los horarios son RELATIVOS a la hora de ejecucion: el offset
-- es en minutos desde ahora. Asi nunca quedan vencidas.
--   offset negativo -> ya arranco, esta en curso
--   offset positivo -> arranca mas tarde hoy

INSERT INTO open_tables (activity_id, description, start_time, end_time, is_private, status, created_at, creator_id, building_id, group_id)
SELECT
  a.id,
  v.descripcion,
  LOCALTIMESTAMP + (v.offset_min || ' minutes')::interval,
  LOCALTIMESTAMP + ((v.offset_min + v.duracion_min) || ' minutes')::interval,
  false,
  'ACTIVE',
  now(),
  c.id,
  b.id,
  NULL
FROM (VALUES
  ('joel@uniandes.edu.co',             'Grab a Coffee',                 'ML',     'Bajo a tomar cafe, quien se pega',                    -20,  60),
  ('sofia.arias@uniandes.edu.co',      'Express Book Club',             'SD',     'Voy a leer un rato, si alguien quiere caer',          -10,  90),
  ('nicolas.pardo@uniandes.edu.co',    'Blitz Chess Game',              'W',      'Partidas rapidas de ajedrez en el hueco',              15,  60),
  ('santiago.gomez@uniandes.edu.co',   'Quick 5-a-side Football Match', 'AULAS',  'Falta gente para completar el equipo',                 30,  60),
  ('daniela.gutierrez@uniandes.edu.co','Campus Photo Walk',             'LLERAS', 'Vuelta por el campus tomando fotos',                   45,  90),
  ('camila.herrera@uniandes.edu.co',   'Campus Nature Walk',            'Q',      'Caminata corta para despejarse',                       60,  45),
  ('miguel.cabrera@uniandes.edu.co',   'Handheld Gaming Session',       'RGD',    'Traje la Switch, hay para dos',                        75,  60),
  ('valeria.cardenas@uniandes.edu.co', 'Listen to a New Album',         'B',      'Escuchando el album nuevo, traigan audifonos',         90,  45)
) AS v(creator_email, activity_title, building_name, descripcion, offset_min, duracion_min)
JOIN users     c ON c.email = v.creator_email
JOIN activities a ON a.title = v.activity_title
JOIN buildings  b ON b.name  = v.building_name
WHERE NOT EXISTS (
  SELECT 1 FROM open_tables ot
  WHERE ot.creator_id = c.id AND ot.activity_id = a.id
);

-- 5.1 El creador queda como participante con RSVP = IN
-- (es lo que hace la app al crear la mesa; si prefieren que
--  las mesas queden con 0 participantes, borren este bloque)
INSERT INTO open_table_participants (open_table_id, user_id, rsvp, responded_at)
SELECT ot.id, ot.creator_id, 'IN', ot.created_at
FROM open_tables ot
WHERE NOT EXISTS (
  SELECT 1 FROM open_table_participants p
  WHERE p.open_table_id = ot.id AND p.user_id = ot.creator_id
);


-- ============================================================
-- 6. VERIFICACION
-- ============================================================
-- Correr aparte para confirmar que quedo todo:
--
-- SELECT 'users' t, count(*) FROM users
-- UNION ALL SELECT 'user_interest', count(*) FROM user_interest
-- UNION ALL SELECT 'friendships ACCEPTED', count(*) FROM friendships WHERE status='ACCEPTED'
-- UNION ALL SELECT 'friendships PENDING',  count(*) FROM friendships WHERE status='PENDING'
-- UNION ALL SELECT 'groups', count(*) FROM groups
-- UNION ALL SELECT 'group_member', count(*) FROM group_member
-- UNION ALL SELECT 'open_tables', count(*) FROM open_tables;
--
-- Esperado: 30 / 100 / 50 / 6 / 6 / 33 / 8
