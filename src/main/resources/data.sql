-- BUILDINGS --

INSERT INTO buildings (name, location, radius_meters) VALUES

(
    'ML',
    ST_SetSRID(ST_MakePoint(-74.065750, 4.601790), 4326)::geography,
    60
),

(
    'SD',
    ST_SetSRID(ST_MakePoint(-74.065200, 4.602550), 4326)::geography,
    60
),

(
    'RGD',
    ST_SetSRID(ST_MakePoint(-74.064600, 4.602100), 4326)::geography,
    50
),

(
    'AULAS',
    ST_SetSRID(ST_MakePoint(-74.066400, 4.601300), 4326)::geography,
    70
),

(
    'B',
    ST_SetSRID(ST_MakePoint(-74.066900, 4.600900), 4326)::geography,
    50
),

(
    'W',
    ST_SetSRID(ST_MakePoint(-74.064100, 4.601050), 4326)::geography,
    55
),

(
    'LLERAS',
    ST_SetSRID(ST_MakePoint(-74.065900, 4.602800), 4326)::geography,
    45
),

(
    'TX',
    ST_SetSRID(ST_MakePoint(-74.065300, 4.600500), 4326)::geography,
    50
),

(
    'C',
    ST_SetSRID(ST_MakePoint(-74.067200, 4.601600), 4326)::geography,
    50
),

(
    'Q',
    ST_SetSRID(ST_MakePoint(-74.067000, 4.602300), 4326)::geography,
    55
)

ON CONFLICT (name) DO NOTHING;

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
('Cooking')
ON CONFLICT (name) DO NOTHING;


--ACTIVITIES--
INSERT INTO activities (title, description, duration_minutes, activity_effort_level, interest_id)
SELECT v.* FROM (VALUES
-- Football
('Quick 5-a-side Football Match'::varchar, 'Play a fast-paced 5-a-side football game at the nearest courts'::varchar, 30, 'ACTIVE'::varchar, (SELECT id FROM interests WHERE name = 'Football')),
('Watch Champions League Match', 'Watch a football match at the lounge or campus cafeteria', 30, 'QUIET', (SELECT id FROM interests WHERE name = 'Football')),

-- Basketball
('Free Throws & 21', 'Shoot some quick hoops or play a 3x3 game at the basketball court', 30, 'ACTIVE', (SELECT id FROM interests WHERE name = 'Basketball')),
('Watch NBA Highlights', 'Share game highlights or catch an NBA game together', 30, 'QUIET', (SELECT id FROM interests WHERE name = 'Basketball')),

-- Music
('Listen to a New Album', 'Share headphones and listen to a recent music release', 30, 'QUIET', (SELECT id FROM interests WHERE name = 'Music')),
('Jam Session / Play Instruments', 'Play guitar or sing together in a quiet spot on campus', 30, 'NORMAL', (SELECT id FROM interests WHERE name = 'Music')),

-- Reading
('Express Book Club', 'Read a chapter or discuss a book both of you are currently reading', 30, 'QUIET', (SELECT id FROM interests WHERE name = 'Reading')),
('Book Swap', 'Bring a favorite book to recommend or lend to each other', 30, 'NORMAL', (SELECT id FROM interests WHERE name = 'Reading')),

-- Gaming
('Handheld Gaming Session', 'Play a casual match on a Switch, Steam Deck, or mobile device', 30, 'NORMAL', (SELECT id FROM interests WHERE name = 'Gaming')),
('Esports & Gaming Chat', 'Discuss recent game releases, consoles, or competitive tournaments', 30, 'QUIET', (SELECT id FROM interests WHERE name = 'Gaming')),

-- Photography
('Campus Photo Walk', 'Take photos at the best architectural and scenic spots on campus', 30, 'ACTIVE', (SELECT id FROM interests WHERE name = 'Photography')),
('Portfolio Review & Editing', 'Show recent photos and exchange editing tips in Lightroom/Photoshop', 30, 'QUIET', (SELECT id FROM interests WHERE name = 'Photography')),

-- Coffee
('Grab a Coffee', 'Go to a coffee shop and have a relaxed chat', 30, 'NORMAL', (SELECT id FROM interests WHERE name = 'Coffee')),
('Express Coffee Tasting', 'Try a specialty pour-over or brew method at the campus cafe', 30, 'NORMAL', (SELECT id FROM interests WHERE name = 'Coffee')),

-- Chess
('Blitz Chess Game', 'Play one or two fast-paced blitz chess matches', 30, 'NORMAL', (SELECT id FROM interests WHERE name = 'Chess')),
('Solve Tactical Puzzles', 'Solve chess puzzles and analyze tactical positions together', 30, 'QUIET', (SELECT id FROM interests WHERE name = 'Chess')),

-- Hiking
('Campus Nature Walk', 'Take a refreshing outdoor walk through green areas on campus', 30, 'ACTIVE', (SELECT id FROM interests WHERE name = 'Hiking')),
('Plan Weekend Hike', 'Check out trail maps and plan a hiking trip for the weekend', 90, 'QUIET', (SELECT id FROM interests WHERE name = 'Hiking')),

-- Movies
('Watch Short Films / Trailers', 'Watch a short movie or catch up on the latest film trailers', 30, 'QUIET', (SELECT id FROM interests WHERE name = 'Movies')),
('Film & Series Chat', 'Discuss favorite movie directors, classics, or trending TV shows', 30, 'NORMAL', (SELECT id FROM interests WHERE name = 'Movies')),

-- Volunteering
('Plan Social Initiative', 'Brainstorm or organize a quick community service project', 40, 'NORMAL', (SELECT id FROM interests WHERE name = 'Volunteering')),
('Discuss Social Impact', 'Share ideas on social projects, sustainability, and outreach', 60, 'QUIET', (SELECT id FROM interests WHERE name = 'Volunteering')),

-- Cooking
('Recipe Exchange', 'Share cooking tips, favorite recipes, or meal prep ideas', 90, 'NORMAL', (SELECT id FROM interests WHERE name = 'Cooking')),
('Eat Lunch Together', 'Share a snack or lunch together at the campus dining area', 30, 'NORMAL', (SELECT id FROM interests WHERE name = 'Cooking'))
) AS v (title, description, duration_minutes, activity_effort_level, interest_id)
WHERE NOT EXISTS (
  SELECT 1 FROM activities a WHERE a.title = v.title
);

