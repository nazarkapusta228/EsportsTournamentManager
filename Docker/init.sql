-- Initialization script for Esports Tournament Manager
-- Creates database schema and initial data

-- Create sequences
CREATE SEQUENCE IF NOT EXISTS teams_seq START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS players_seq START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS tournaments_seq START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS matches_seq START 1 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS groups_seq START 1 INCREMENT 1;

-- Table: players (SINGLE_TABLE inheritance strategy)
CREATE TABLE IF NOT EXISTS players (
    id BIGINT PRIMARY KEY DEFAULT nextval('players_seq'),
    player_type VARCHAR(31) NOT NULL,
    nickname VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(255) NOT NULL,
    salary DECIMAL(10,2),
    sponsor VARCHAR(100),
    rank VARCHAR(50),
    hours_played INTEGER,
    version BIGINT DEFAULT 0
);

-- Table: teams
CREATE TABLE IF NOT EXISTS teams (
    id BIGINT PRIMARY KEY DEFAULT nextval('teams_seq'),
    name VARCHAR(255) UNIQUE NOT NULL,
    region VARCHAR(255) NOT NULL,
    version BIGINT DEFAULT 0
);

-- Table: team_players
CREATE TABLE IF NOT EXISTS team_players (
    team_id BIGINT NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    player_id BIGINT NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    PRIMARY KEY (team_id, player_id)
);

-- Table: tournaments
CREATE TABLE IF NOT EXISTS tournaments (
    id BIGINT PRIMARY KEY DEFAULT nextval('tournaments_seq'),
    tournament_type VARCHAR(31) NOT NULL,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    prize_pool DECIMAL(15,2),
    status VARCHAR(50) NOT NULL DEFAULT 'UPCOMING',
    max_teams INTEGER NOT NULL,
    version BIGINT DEFAULT 0
);

-- Table: online_tournaments
CREATE TABLE IF NOT EXISTS online_tournaments (
    id BIGINT PRIMARY KEY REFERENCES tournaments(id) ON DELETE CASCADE,
    stream_url VARCHAR(255),
    server_location VARCHAR(100)
);

-- Table: lan_tournaments
CREATE TABLE IF NOT EXISTS lan_tournaments (
    id BIGINT PRIMARY KEY REFERENCES tournaments(id) ON DELETE CASCADE,
    venue VARCHAR(255),
    city VARCHAR(100)
);

-- Insert initial Pro Players
INSERT INTO players (id, player_type, nickname, role, salary, sponsor, version) VALUES
(1, 'PRO', 's1mple', 'AWPer', 50000.00, 'HyperX', 0),
(2, 'PRO', 'electroNic', 'Rifler', 45000.00, 'ASUS', 0),
(3, 'PRO', 'b1t', 'Rifler', 40000.00, 'HyperX', 0)
ON CONFLICT (nickname) DO NOTHING;

-- Insert initial Amateur Players
INSERT INTO players (id, player_type, nickname, role, rank, hours_played, version) VALUES
(6, 'AMATEUR', 'ProdigyKid', 'AWPer', 'Global Elite', 5000, 0),
(7, 'AMATEUR', 'RookieStar', 'Rifler', 'Supreme', 3500, 0)
ON CONFLICT (nickname) DO NOTHING;

-- Insert teams
INSERT INTO teams (id, name, region, version) VALUES
(1, 'Natus Vincere', 'EU', 0),
(2, 'FaZe Clan', 'NA', 0)
ON CONFLICT (name) DO NOTHING;

-- Link players to team
INSERT INTO team_players (team_id, player_id) VALUES
(1, 1), (1, 2), (1, 3)
ON CONFLICT DO NOTHING;

-- Insert tournaments
INSERT INTO tournaments (id, tournament_type, name, start_date, end_date, prize_pool, status, max_teams, version) VALUES
(1, 'ONLINE', 'ESL Pro League', '2024-02-01', '2024-03-15', 1000000.00, 'UPCOMING', 32, 0)
ON CONFLICT DO NOTHING;

INSERT INTO online_tournaments (id, stream_url, server_location) VALUES
(1, 'https://twitch.tv/esl_csgo', 'EU-West')
ON CONFLICT DO NOTHING;

-- Sync sequences to avoid conflicts with manually inserted IDs
SELECT setval('teams_seq', (SELECT MAX(id) FROM teams));
SELECT setval('players_seq', (SELECT MAX(id) FROM players));
SELECT setval('tournaments_seq', (SELECT MAX(id) FROM tournaments));
