INSERT INTO "Carta" ("nome", "cognome", "numero_carta", "scadenza", "cvv") VALUES
('Mario', 'Rossi', '1234567812345678', '12/25', 123),
('Luigi', 'Verdi', '8765432187654321', '11/24', 456),
('Giulia', 'Bianchi', '1122334455667788', '10/23', 789),
('Anna', 'Neri', '9988776655443322', '09/26', 321),
('Paolo', 'Gialli', '8877665544332211', '08/27', 654),
('Chiara', 'Blu', '7766554433221100', '07/28', 987),
('Luca', 'Rossi', '6655443322110099', '06/29', 213),
('Francesca', 'Verdi', '5544332211009988', '05/30', 546),
('Alessandro', 'Bianchi', '4433221100998877', '04/31', 879),
('Elisa', 'Neri', '3322110099887766', '03/32', 132);

INSERT INTO "Utente" ("username", "nome", "cognome", "tipo", "carta") VALUES
('mrossi', 'Mario', 'Rossi', 1, '1234567812345678'),
('lverdi', 'Luigi', 'Verdi', 2, '8765432187654321'),
('gbianchi', 'Giulia', 'Bianchi', 3, '1122334455667788'),
('aneri', 'Anna', 'Neri', 1, '9988776655443322'),
('pgialli', 'Paolo', 'Gialli', 2, '8877665544332211'),
('cblu', 'Chiara', 'Blu', 3, '7766554433221100'),
('lrossi', 'Luca', 'Rossi', 1, '6655443322110099'),
('fverdi', 'Francesca', 'Verdi', 2, '5544332211009988'),
('abianchi', 'Alessandro', 'Bianchi', 3, '4433221100998877'),
('eneri', 'Elisa', 'Neri', 1, '3322110099887766');

INSERT INTO "Credenziali" ("username", "password") VALUES
('mrossi', 'password123'),
('lverdi', 'password456'),
('gbianchi', 'password789'),
('aneri', 'password321'),
('pgialli', 'password654'),
('cblu', 'password987'),
('lrossi', 'password213'),
('fverdi', 'password546'),
('abianchi', 'password879'),
('eneri', 'password132');
