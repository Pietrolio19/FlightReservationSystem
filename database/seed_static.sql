-- Airport
INSERT INTO Airport (id, iata, city, country, name) VALUES
(1, 'FCO', 'Roma', 'Italia', 'Leonardo da Vinci'),
(2, 'MXP', 'Milano', 'Italia', 'Malpensa'),
(3, 'CDG', 'Parigi', 'Francia', 'Charles de Gaulle');

-- Airline
INSERT INTO Airline (id, iata, icao, name, country) VALUES
(1, 'AZ', 'ITY', 'ITA Airways', 'Italia'),
(2, 'AF', 'AFR', 'Air France', 'Francia');

-- Aircraft
INSERT INTO Aircraft (id, model, producer, capacity) VALUES
(1, 'A320', 'Airbus', 180),
(2, 'B737', 'Boeing', 189);
