INSERT INTO question_groups (creatorId, name)
VALUES  (2, 'HTML'),
        (2, 'Ogólne'),
        (2, 'E12');

INSERT INTO questions(groupId, value)
VALUES  (1, 'Co to div?'),
        (1, 'Rozwin skrot HTML.'),
        (1, 'Czym jest CSS?'),
        (1, 'Po co nam bootstrap?'),
        (2, 'Co to zmienna?'),
        (2, 'Prędkości dysków SSD:'),
        (2, 'Interfejsy kart graficznych:'),
        (2, 'Co to Thunderbolt:'),
        (3, 'Symulowanie stanów logicznych obwodów cyfrowych umożliwia'),
        (3, 'Standardem komunikacji pomiędzy skanerem a programem graficznym jest'),
        (3, 'Aby możliwe było wykorzystanie macierzy RAID 1, potrzeba minimum'),
        (3, 'Który z interfejsów jest interfejsem równoległym?');

INSERT INTO answers(questionId, value, isCorrect)
VALUES  (1,'Blok mieszkalny.',false),
        (1,'Element programowania obiektowego.',false),
        (1,'Pewnego rodzaju uniwersalny kontener na inne elementy.',true),
        (1,'Typ zmiennej.',false),

        (2,'Hiper Tramwaj Miejsko Ludowy',false),
        (2,'Holenderski Turysta Mieszkalno Lądowy.',false),
        (2,'Hyper Text Markup Language.',true),
        (2,'Hiper Turbo Mega Lolek.',false),

        (3,'Cascading Style Sheets.',true),
        (3,'Cors Super Stronk',false),
        (3,'Jezykiem programowania.',false),
        (3,'Referencją do wskaźników.',false),

        (4,'By zwiększyć czas ładowania strony.',false),
        (4,'By programować.',false),
        (4,'By zapobiec Dead Lock',false),
        (4,'By robić briefa.', true),

        (5,'Nie mam pojęcia',false),
        (5,'Ooo! To pewnie jerzy.',false ),
        (5,'Pojemnik na dane.',true),
        (5,'Rodzaj monitorów.', false),

        (6,'- 7200 rpm/s',false),
        (6,'- 10000 kVh/s',false),
        (6,'- 5400 Km/s',false),
        (6,'- ??? XD', true),

        (7,'USB',false),
        (7,'SAS',false),
        (7,'PS2',false),
        (7,'PCI-E', true),

        (8,'Kanał burzowy',false),
        (8,'Jakiś kabel.',false),
        (8,'Nie',false),
        (8,'Interfejs wymyślony przez firme Apple.', true),

        (9,'Sonometr',false),
        (9,'Kalibrator',false),
        (9,'Impulsator',false),
        (9,'Sonda logiczna', true),

        (10,'USB',false),
        (10,'OPC',false),
        (10,'SCAN',false),
        (10,'TWAIN', true),

        (11,'2 dysków',true),
        (11,'3 dysków',false),
        (11,'4 dysków',false),
        (11,'5 dysków', false ),


        (12,'LPT',true),
        (12,'PS/2',false),
        (12,'RS232',false),
        (12,'USB', false );
