-- Airport
INSERT INTO Airport (iata, city, country, name) VALUES
('FCO', 'Roma', 'Italia', 'Leonardo da Vinci'),
('MXP', 'Milano', 'Italia', 'Malpensa'),
('CDG', 'Parigi', 'Francia', 'Charles de Gaulle');

-- Airline
INSERT INTO Airline (iata, icao, name, country) VALUES
('AZ', 'ITY', 'ITA Airways', 'Italia'),
('AF', 'AFR', 'Air France', 'Francia');

-- Aircraft
INSERT INTO Aircraft (model, producer, capacity) VALUES
('A320', 'Airbus', 180),
('B737', 'Boeing', 189);
