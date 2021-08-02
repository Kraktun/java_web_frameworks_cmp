INSERT INTO chapter (id, number, title) VALUES(1, 'number1', 'title1'),
(2, 'number2', 'title2'),
(3, 'number3', 'title3'),
(4, 'number4', 'title4'),
(5, 'number5', 'title5'),
(6, 'number6', 'title6'),
(7, 'number7', 'title7'),
(8, 'number8', 'title8'),
(9, 'number9', 'title9'),
(10, 'number10', 'title10'),
(11, 'number11', 'title11'),
(12, 'number12', 'title12'),
(13, 'number13', 'title13'),
(14, 'number14', 'title14'),
(15, 'number15', 'title15'),
(16, 'number16', 'title16'),
(17, 'number17', 'title17'),
(18, 'number18', 'title18'),
(19, 'number19', 'title19'),
(20, 'number20', 'title20'),
(21, 'number21', 'title21'),
(22, 'number22', 'title22'),
(23, 'number23', 'title23'),
(24, 'number24', 'title24'),
(25, 'number25', 'title25'),
(26, 'number26', 'title26'),
(27, 'number27', 'title27'),
(28, 'number28', 'title28'),
(29, 'number29', 'title29'),
(30, 'number30', 'title30');

INSERT INTO event (id, text, chapter_id) VALUES(1, 'event1', 1),
(2, 'event2', 1),
(3, 'event3', 2),
(4, 'event4', 3),
(5, 'event5', 3),
(6, 'event6', 4),
(7, 'event7', 5),
(8, 'event8', 6),
(9, 'event9', 8),
(10, 'event10', 16),
(11, 'event11', 16),
(12, 'event12', 16),
(13, 'event13', 16),
(14, 'event14', 7),
(15, 'event15', 27),
(16, 'event16', 5),
(17, 'event17', 25),
(18, 'event18', 22),
(19, 'event19', 16),
(20, 'event20', 15),
(21, 'event21', 14),
(22, 'event22', 11),
(23, 'event23', 30),
(24, 'event24', 8),
(25, 'event25', 9),
(26, 'event26', 19),
(27, 'event27', 11),
(28, 'event28', 21),
(29, 'event29', 10),
(30, 'event30', 9),
(31, 'event31', 3),
(32, 'event32', 1),
(33, 'event33', 5),
(34, 'event34', 25),
(35, 'event35', 20);

UPDATE chapter SET starter_id = 1 WHERE id = 1;
UPDATE chapter SET starter_id = 3 WHERE id = 2;
UPDATE chapter SET starter_id = 5 WHERE id = 3;
UPDATE chapter SET starter_id = 6 WHERE id = 4;
UPDATE chapter SET starter_id = 33 WHERE id = 5;
UPDATE chapter SET starter_id = 8 WHERE id = 6;
UPDATE chapter SET starter_id = 14 WHERE id = 7;
UPDATE chapter SET starter_id = 9 WHERE id = 8;
UPDATE chapter SET starter_id = 25 WHERE id = 9;
UPDATE chapter SET starter_id = 29 WHERE id = 10;
UPDATE chapter SET starter_id = 27 WHERE id = 11;
UPDATE chapter SET starter_id = 28 WHERE id = 21;
UPDATE chapter SET starter_id = 15 WHERE id = 27;
UPDATE chapter SET starter_id = 21 WHERE id = 14;
UPDATE chapter SET starter_id = 20 WHERE id = 15;
UPDATE chapter SET starter_id = 19 WHERE id = 16;
UPDATE chapter SET starter_id = 23 WHERE id = 30;
UPDATE chapter SET starter_id = 34 WHERE id = 25;
UPDATE chapter SET starter_id = 26 WHERE id = 19;
UPDATE chapter SET starter_id = 35 WHERE id = 20;