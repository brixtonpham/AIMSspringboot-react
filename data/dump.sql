PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE audit_log (
        audit_log_id integer,
        action_name varchar(255) not null,
        action_type varchar(255) check (
            action_type in (
                'CREATE',
                'UPDATE',
                'DELETE',
                'VIEW',
                'LOGIN',
                'LOGOUT',
                'PAYMENT',
                'ORDER',
                'CANCEL'
            )
        ),
        entity_id bigint,
        entity_type varchar(50),
        note TEXT,
        recorded_at timestamp,
        user_id bigint,
        primary key (audit_log_id)
    );
INSERT INTO audit_log VALUES(1,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751099723247,1);
INSERT INTO audit_log VALUES(2,'Order Created','ORDER',1,'Order','order order with ID: 1',1751099820089,NULL);
INSERT INTO audit_log VALUES(3,'Order Created','ORDER',2,'Order','order order with ID: 2',1751100722462,NULL);
INSERT INTO audit_log VALUES(4,'Order Created','ORDER',3,'Order','order order with ID: 3',1751103252709,NULL);
INSERT INTO audit_log VALUES(5,'Order Created','ORDER',4,'Order','order order with ID: 4',1751103628445,NULL);
INSERT INTO audit_log VALUES(6,'Order Created','ORDER',5,'Order','order order with ID: 5',1751103729001,NULL);
INSERT INTO audit_log VALUES(7,'Order Deleted','ORDER',5,'Order','order order with ID: 5',1751103772335,NULL);
INSERT INTO audit_log VALUES(8,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751103796224,1);
INSERT INTO audit_log VALUES(9,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751104013047,1);
INSERT INTO audit_log VALUES(10,'Order Created','ORDER',6,'Order','order order with ID: 6',1751105467141,NULL);
INSERT INTO audit_log VALUES(11,'Order Created','ORDER',7,'Order','order order with ID: 7',1751105514859,NULL);
INSERT INTO audit_log VALUES(12,'Order Created','ORDER',8,'Order','order order with ID: 8',1751105595389,NULL);
INSERT INTO audit_log VALUES(13,'Order Created','ORDER',9,'Order','order order with ID: 9',1751106140211,NULL);
INSERT INTO audit_log VALUES(14,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751106215326,1);
INSERT INTO audit_log VALUES(15,'Product Updated','UPDATE',1,'Product','update product with ID: 1',1751106240723,NULL);
INSERT INTO audit_log VALUES(16,'User Blocked - Reason: no one uses','UPDATE',2,'User','update user with ID: 2',1751106278161,NULL);
INSERT INTO audit_log VALUES(17,'User Login','LOGIN',NULL,NULL,'User logged in: manager2@itss.com',1751106336369,3);
INSERT INTO audit_log VALUES(18,'Order Confirmed','ORDER',9,'Order','order order with ID: 9',1751106361523,NULL);
INSERT INTO audit_log VALUES(19,'Order Created','ORDER',10,'Order','order order with ID: 10',1751111692156,NULL);
INSERT INTO audit_log VALUES(20,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751115709223,1);
INSERT INTO audit_log VALUES(21,'Order Created','ORDER',11,'Order','order order with ID: 11',1751115929162,NULL);
INSERT INTO audit_log VALUES(22,'Order Created','ORDER',12,'Order','order order with ID: 12',1751115977975,NULL);
INSERT INTO audit_log VALUES(23,'Order Created','ORDER',13,'Order','order order with ID: 13',1751116042886,NULL);
INSERT INTO audit_log VALUES(24,'Order Created','ORDER',14,'Order','order order with ID: 14',1751116246483,NULL);
INSERT INTO audit_log VALUES(25,'Order Created','ORDER',15,'Order','order order with ID: 15',1751116998819,NULL);
INSERT INTO audit_log VALUES(26,'Order Created','ORDER',16,'Order','order order with ID: 16',1751117075941,NULL);
INSERT INTO audit_log VALUES(27,'Order Created','ORDER',17,'Order','order order with ID: 17',1751117231248,NULL);
INSERT INTO audit_log VALUES(28,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751117302993,1);
INSERT INTO audit_log VALUES(29,'Order Created','ORDER',18,'Order','order order with ID: 18',1751117664053,NULL);
INSERT INTO audit_log VALUES(30,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751117711574,1);
INSERT INTO audit_log VALUES(31,'Order Created','ORDER',19,'Order','order order with ID: 19',1751117874704,NULL);
INSERT INTO audit_log VALUES(32,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751117950293,1);
INSERT INTO audit_log VALUES(33,'Product Updated','UPDATE',1,'Product','update product with ID: 1',1751117985195,NULL);
INSERT INTO audit_log VALUES(34,'Order Confirmed','ORDER',19,'Order','order order with ID: 19',1751118000393,NULL);
INSERT INTO audit_log VALUES(35,'User Unblocked','UPDATE',2,'User','update user with ID: 2',1751118023866,NULL);
INSERT INTO audit_log VALUES(36,'User Unblocked','UPDATE',2,'User','update user with ID: 2',1751118027965,NULL);
INSERT INTO audit_log VALUES(37,'Order Created','ORDER',20,'Order','order order with ID: 20',1751123280282,NULL);
INSERT INTO audit_log VALUES(38,'User Login','LOGIN',NULL,NULL,'User logged in: admin@itss.com',1751123499896,1);
INSERT INTO audit_log VALUES(39,'User Login','LOGIN',NULL,NULL,'User logged in: manager1@itss.com',1751123535484,2);
INSERT INTO audit_log VALUES(40,'Order Created','ORDER',21,'Order','order order with ID: 21',1751124774666,NULL);
INSERT INTO audit_log VALUES(41,'Order Created','ORDER',22,'Order','order order with ID: 22',1751124922678,NULL);
INSERT INTO audit_log VALUES(42,'Order Created','ORDER',23,'Order','order order with ID: 23',1751124968994,NULL);
INSERT INTO audit_log VALUES(43,'Order Created','ORDER',24,'Order','order order with ID: 24',1751278226469,NULL);
INSERT INTO audit_log VALUES(44,'Order Created','ORDER',25,'Order','order order with ID: 25',1751280642748,NULL);
CREATE TABLE book (
        authors varchar(500),
        cover_type varchar(50),
        genre varchar(100),
        page_count integer,
        publication_date varchar(255),
        publishers varchar(200),
        product_id bigint not null,
        primary key (product_id)
    );
INSERT INTO book VALUES('F. Scott Fitzgerald','Paperback','Classic Literature',180,'1925-04-10','Charles Scribner''s Sons',1);
INSERT INTO book VALUES('Harper Lee','Paperback','Classic Literature',281,'1960-07-11','J.B. Lippincott & Co.',2);
INSERT INTO book VALUES('George Orwell','Paperback','Science Fiction',328,'1949-06-08','Secker & Warburg',3);
CREATE TABLE cd (
        artists varchar(500),
        genre varchar(100),
        record_label varchar(200),
        release_date varchar(255),
        track_list TEXT,
        product_id bigint not null,
        primary key (product_id)
    );
INSERT INTO cd VALUES('The Beatles','Rock','Apple Records','1969-09-26','Come Together, Something, Maxwell''s Silver Hammer, Oh! Darling, Octopus''s Garden, I Want You (She''s So Heavy), Here Comes the Sun, Because, You Never Give Me Your Money, Sun King, Mean Mr. Mustard, Polythene Pam, She Came in Through the Bathroom Window, Golden Slumbers, Carry That Weight, The End, Her Majesty',4);
INSERT INTO cd VALUES('Pink Floyd','Progressive Rock','Harvest Records','1973-03-01','Speak to Me, Breathe, On the Run, Time, The Great Gig in the Sky, Money, Us and Them, Any Colour You Like, Brain Damage, Eclipse',5);
INSERT INTO cd VALUES('Michael Jackson','Pop','Epic Records','1982-11-30','Wanna Be Startin'' Somethin'', Baby Be Mine, The Girl Is Mine, Thriller, Beat It, Billie Jean, Human Nature, P.Y.T. (Pretty Young Thing), The Lady in My Life',6);
CREATE TABLE delivery_information (
        delivery_id integer,
        address TEXT not null,
        delivery_fee integer,
        delivery_message TEXT,
        district varchar(100) not null,
        email varchar(255) not null,
        name varchar(255) not null,
        phone varchar(20) not null,
        province varchar(100) not null,
        ward varchar(100) not null,
        primary key (delivery_id)
    );
INSERT INTO delivery_information VALUES(1,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Huyện Xín Mần','bxson0301@gmail.com','bui xuan son','0123456789','Hà Giang','Xã Thu Tà');
INSERT INTO delivery_information VALUES(2,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Huyện Đại Từ','bxson0301@gmail.com','bui xuan son','0123456789','Thái Nguyên','Xã Cù Vân');
INSERT INTO delivery_information VALUES(3,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Thành phố Phổ Yên','bxson0301@gmail.com','bui xuan son','0123456789','Thái Nguyên','Phường Nam Tiến');
INSERT INTO delivery_information VALUES(4,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Huyện Sơn Động','bxson0301@gmail.com','bui xuan son','0123456789','Bắc Giang','Xã An Bá');
INSERT INTO delivery_information VALUES(5,'Số 8 - Ngõ 10 - Đường 12',30000,NULL,'Huyện Lạc Sơn','bxson0301@gmail.com','bui xuan son','0123456789','Hoà Bình','Xã Chí Đạo');
INSERT INTO delivery_information VALUES(6,'Số 8 - Ngõ 10 - Đường 12',30000,NULL,'Quận Hoàn Kiếm','bxson0301@gmail.com','bui xuan son','0123456789','Hà Nội','Phường Cửa Nam');
INSERT INTO delivery_information VALUES(7,'Số 8 - Ngõ 10 - Đường 12',30000,NULL,'Huyện Ba Chẽ','bxson0301@gmail.com','bui xuan son','0123456789','Quảng Ninh','Xã Đạp Thanh');
INSERT INTO delivery_information VALUES(8,'Số 8 - Ngõ 10 - Đường 12',50000,NULL,'Huyện Tam Đảo','bxson0301@gmail.com','bui xuan son','0123456789','Vĩnh Phúc','Xã Bồ Lý');
INSERT INTO delivery_information VALUES(9,'Số 8 - Ngõ 10 - Đường 12',50000,NULL,'Thị xã Sơn Tây','bxson0301@gmail.com','bui xuan son','0123456789','Hà Nội','Phường Trung Sơn Trầm');
INSERT INTO delivery_information VALUES(10,'Số 8 - Ngõ 10 - Đường 12',30000,NULL,'Huyện Gia Lâm','admin@itss.com','Admin User','0123456789','Hà Nội','Xã Kiêu Kỵ');
INSERT INTO delivery_information VALUES(11,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Huyện Sóc Sơn','bxson0301@gmail.com','bui xuan son','0123456789','Hà Nội','Xã Tân Dân');
INSERT INTO delivery_information VALUES(12,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Huyện Vĩnh Tường','bxson0301@gmail.com','bui xuan son','0123456789','Vĩnh Phúc','Xã Ngũ Kiên');
INSERT INTO delivery_information VALUES(13,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Thị xã Việt Yên','bxson0301@gmail.com','bui xuan son','0123456789','Bắc Giang','Phường Vân Trung');
INSERT INTO delivery_information VALUES(14,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Quận Ba Đình','bxson0301@gmail.com','bui xuan son','0123456789','Hà Nội','Phường Đội Cấn');
INSERT INTO delivery_information VALUES(15,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Quận Ba Đình','bxson0301@gmail.com','bui xuan son','0123456789','Hà Nội','Phường Ngọc Hà');
INSERT INTO delivery_information VALUES(16,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Huyện Thanh Sơn','bxson0301@gmail.com','bui xuan son','0123456789','Phú Thọ','Xã Yên Lương');
INSERT INTO delivery_information VALUES(17,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Quận Ba Đình','bxson0301@gmail.com','bui xuan son','0123456789','Hà Nội','Phường Ngọc Khánh');
INSERT INTO delivery_information VALUES(18,'Số 8 - Ngõ 10 - Đường 12',30000,NULL,'Huyện Sông Lô','bxson0301@gmail.com','bui xuan son','0123456789','Vĩnh Phúc','Xã Đức Bác');
INSERT INTO delivery_information VALUES(19,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Quận Ba Đình','bxson0301@gmail.com','bui xuan son','0123456789','Hà Nội','Phường Đội Cấn');
INSERT INTO delivery_information VALUES(20,'Số 8 - Ngõ 10 - Đường 12',80000,NULL,'Thành phố Yên Bái','bxson0301@gmail.com','bui xuan son','0123456789','Yên Bái','Phường Yên Thịnh');
INSERT INTO delivery_information VALUES(21,'13',80000,NULL,'Quận Hoàn Kiếm','admin@itss.com','Admin User','0123456789','Hà Nội','Phường Hàng Buồm');
INSERT INTO delivery_information VALUES(22,'ddd',30000,NULL,'Quận Long Biên','customer@itss.com','John Doe','0987653321','Hà Nội','Phường Đức Giang');
INSERT INTO delivery_information VALUES(23,'dep trai',30000,NULL,'Huyện Ba Bể','customer@itss.com','John Doe','0379179107','Bắc Kạn','Xã Phúc Lộc');
INSERT INTO delivery_information VALUES(24,'Số 8 - Ngõ 10 - Đường 12',30000,NULL,'Thị xã Chũ','bxson0301@gmail.com','bui xuan son','0123456789','Bắc Giang','Phường Phượng Sơn');
INSERT INTO delivery_information VALUES(25,'Số 8 - Ngõ 10 - Đường 12',30000,NULL,'Huyện Đại Từ','bxson0301@gmail.com','bui xuan son','0123456789','Thái Nguyên','Xã An Khánh');
CREATE TABLE dvd (
        directors varchar(500),
        duration_minutes integer,
        dvd_type varchar(50),
        genre varchar(100),
        rating varchar(10),
        release_date varchar(255),
        studio varchar(200),
        product_id bigint not null,
        primary key (product_id)
    );
INSERT INTO dvd VALUES('Frank Darabont',142,'DVD','Drama','R','1994-09-23','Columbia Pictures',7);
INSERT INTO dvd VALUES('Francis Ford Coppola',175,'DVD','Crime Drama','R','1972-03-24','Paramount Pictures',8);
INSERT INTO dvd VALUES('Quentin Tarantino',154,'DVD','Crime','R','1994-10-14','Miramax Films',9);
CREATE TABLE invoice (
        invoice_id integer,
        created_at timestamp,
        description TEXT,
        paid_at timestamp,
        payment_method varchar(50),
        payment_status varchar(255) check (
            payment_status in (
                'PENDING',
                'PAID',
                'FAILED',
                'REFUNDED',
                'CANCELLED'
            )
        ),
        order_id bigint not null unique,
        primary key (invoice_id)
    );
INSERT INTO invoice VALUES(1,1751099820086,'Order #1',NULL,NULL,'PENDING',1);
INSERT INTO invoice VALUES(2,1751100722459,'Order #2',NULL,NULL,'PENDING',2);
INSERT INTO invoice VALUES(3,1751103252708,'Order #3',NULL,NULL,'PENDING',3);
INSERT INTO invoice VALUES(4,1751103628441,'Order #4',NULL,NULL,'PENDING',4);
INSERT INTO invoice VALUES(5,1751103729000,'Order #5',NULL,NULL,'PENDING',5);
INSERT INTO invoice VALUES(6,1751105467139,'Order #6',NULL,NULL,'PENDING',6);
INSERT INTO invoice VALUES(7,1751105514857,'Order #7',NULL,NULL,'PENDING',7);
INSERT INTO invoice VALUES(8,1751105595388,'Order #8',NULL,NULL,'PENDING',8);
INSERT INTO invoice VALUES(9,1751106140194,'Order #9',NULL,NULL,'PENDING',9);
INSERT INTO invoice VALUES(10,1751111692153,'Order #10',NULL,NULL,'PENDING',10);
INSERT INTO invoice VALUES(11,1751115929157,'Order #11',NULL,NULL,'PENDING',11);
INSERT INTO invoice VALUES(12,1751115977974,'Order #12',NULL,NULL,'PENDING',12);
INSERT INTO invoice VALUES(13,1751116042885,'Order #13',NULL,NULL,'PENDING',13);
INSERT INTO invoice VALUES(14,1751116246480,'Order #14',NULL,NULL,'PENDING',14);
INSERT INTO invoice VALUES(15,1751116998816,'Order #15',NULL,NULL,'PENDING',15);
INSERT INTO invoice VALUES(16,1751117075940,'Order #16',NULL,NULL,'PENDING',16);
INSERT INTO invoice VALUES(17,1751117231246,'Order #17',NULL,NULL,'PENDING',17);
INSERT INTO invoice VALUES(18,1751117664050,'Order #18',NULL,NULL,'PENDING',18);
INSERT INTO invoice VALUES(19,1751117874701,'Order #19',NULL,NULL,'PENDING',19);
INSERT INTO invoice VALUES(20,1751123280280,'Order #20',NULL,NULL,'PENDING',20);
INSERT INTO invoice VALUES(21,1751124774664,'Order #21',NULL,NULL,'PENDING',21);
INSERT INTO invoice VALUES(22,1751124922677,'Order #22',NULL,NULL,'PENDING',22);
INSERT INTO invoice VALUES(23,1751124968993,'Order #23',NULL,NULL,'PENDING',23);
INSERT INTO invoice VALUES(24,1751278226466,'Order #24',NULL,NULL,'PENDING',24);
INSERT INTO invoice VALUES(25,1751280642746,'Order #25',NULL,NULL,'PENDING',25);
CREATE TABLE lp (
        artist varchar(255),
        music_type varchar(255),
        record_label varchar(255),
        release_date varchar(255),
        rpm integer,
        size_inches float,
        sleeve_condition varchar(255),
        tracklist TEXT,
        vinyl_condition varchar(255),
        product_id bigint not null,
        primary key (product_id)
    );
CREATE TABLE order_item (
        order_item_id integer,
        delivery_time varchar(255),
        instructions TEXT,
        quantity integer not null,
        rush_order_using boolean,
        status varchar(255) check (
            status in (
                'PENDING',
                'CONFIRMED',
                'PROCESSING',
                'SHIPPED',
                'DELIVERED',
                'CANCELLED'
            )
        ),
        total_fee integer not null,
        order_id bigint not null,
        product_id bigint not null,
        primary key (order_item_id)
    );
INSERT INTO order_item VALUES(1,NULL,NULL,1,0,'PENDING',145000,1,3);
INSERT INTO order_item VALUES(2,NULL,NULL,1,0,'PENDING',135000,1,2);
INSERT INTO order_item VALUES(3,NULL,NULL,1,0,'PENDING',145000,2,3);
INSERT INTO order_item VALUES(4,NULL,NULL,1,0,'PENDING',135000,2,2);
INSERT INTO order_item VALUES(5,NULL,NULL,1,0,'PENDING',145000,3,3);
INSERT INTO order_item VALUES(6,NULL,NULL,1,0,'PENDING',135000,3,2);
INSERT INTO order_item VALUES(7,NULL,NULL,1,0,'PENDING',145000,4,3);
INSERT INTO order_item VALUES(8,NULL,NULL,1,0,'PENDING',135000,4,2);
INSERT INTO order_item VALUES(9,NULL,NULL,1,0,'PENDING',120000,5,1);
INSERT INTO order_item VALUES(10,NULL,NULL,1,0,'PENDING',135000,5,2);
INSERT INTO order_item VALUES(11,NULL,NULL,1,0,'PENDING',350000,6,4);
INSERT INTO order_item VALUES(12,NULL,NULL,1,0,'PENDING',350000,7,4);
INSERT INTO order_item VALUES(13,NULL,NULL,1,0,'PENDING',145000,8,3);
INSERT INTO order_item VALUES(14,NULL,NULL,2,0,'PENDING',760000,9,5);
INSERT INTO order_item VALUES(15,NULL,NULL,1,0,'PENDING',120000,10,1);
INSERT INTO order_item VALUES(16,NULL,NULL,1,0,'PENDING',135000,10,2);
INSERT INTO order_item VALUES(17,NULL,NULL,1,0,'PENDING',380000,11,5);
INSERT INTO order_item VALUES(18,NULL,NULL,1,0,'PENDING',350000,11,4);
INSERT INTO order_item VALUES(19,NULL,NULL,1,0,'PENDING',380000,12,5);
INSERT INTO order_item VALUES(20,NULL,NULL,1,0,'PENDING',350000,12,4);
INSERT INTO order_item VALUES(21,NULL,NULL,1,0,'PENDING',380000,13,5);
INSERT INTO order_item VALUES(22,NULL,NULL,1,0,'PENDING',350000,13,4);
INSERT INTO order_item VALUES(23,NULL,NULL,2,0,'PENDING',700000,14,4);
INSERT INTO order_item VALUES(24,NULL,NULL,1,0,'PENDING',380000,14,5);
INSERT INTO order_item VALUES(25,NULL,NULL,2,0,'PENDING',240000,15,1);
INSERT INTO order_item VALUES(26,NULL,NULL,1,0,'PENDING',145000,15,3);
INSERT INTO order_item VALUES(27,NULL,NULL,2,0,'PENDING',240000,16,1);
INSERT INTO order_item VALUES(28,NULL,NULL,1,0,'PENDING',145000,16,3);
INSERT INTO order_item VALUES(29,NULL,NULL,2,0,'PENDING',290000,17,3);
INSERT INTO order_item VALUES(30,NULL,NULL,1,0,'PENDING',135000,17,2);
INSERT INTO order_item VALUES(31,NULL,NULL,1,0,'PENDING',135000,18,2);
INSERT INTO order_item VALUES(32,NULL,NULL,1,0,'PENDING',120000,19,1);
INSERT INTO order_item VALUES(33,NULL,NULL,1,0,'PENDING',145000,19,3);
INSERT INTO order_item VALUES(34,NULL,NULL,2,0,'PENDING',270000,20,2);
INSERT INTO order_item VALUES(35,NULL,NULL,1,0,'PENDING',145000,20,3);
INSERT INTO order_item VALUES(36,NULL,NULL,5,0,'PENDING',675000,21,2);
INSERT INTO order_item VALUES(37,NULL,NULL,6,0,'PENDING',870000,21,3);
INSERT INTO order_item VALUES(38,NULL,NULL,2,0,'PENDING',500000,21,8);
INSERT INTO order_item VALUES(39,NULL,NULL,2,0,'PENDING',420000,22,9);
INSERT INTO order_item VALUES(40,NULL,NULL,2,0,'PENDING',420000,23,9);
INSERT INTO order_item VALUES(41,NULL,NULL,1,0,'PENDING',120000,24,1);
INSERT INTO order_item VALUES(42,NULL,NULL,1,0,'PENDING',135000,24,2);
INSERT INTO order_item VALUES(43,NULL,NULL,1,0,'PENDING',120000,25,1);
INSERT INTO order_item VALUES(44,NULL,NULL,1,0,'PENDING',135000,25,2);
CREATE TABLE IF NOT EXISTS "order_items" (
        order_id integer,
        created_at timestamp,
        status varchar(255) check (
            status in (
                'PENDING',
                'CONFIRMED',
                'PROCESSING',
                'SHIPPED',
                'DELIVERED',
                'CANCELLED'
            )
        ),
        total_after_vat integer not null,
        total_before_vat integer not null,
        updated_at timestamp,
        vat_percentage integer,
        delivery_id bigint unique,
        primary key (order_id)
    );
INSERT INTO order_items VALUES(1,1751099820068,'PENDING',308000,280000,1751099820069,10,1);
INSERT INTO order_items VALUES(2,1751100722446,'PENDING',308000,280000,1751100722446,10,2);
INSERT INTO order_items VALUES(3,1751103252704,'PENDING',308000,280000,1751103252704,10,3);
INSERT INTO order_items VALUES(4,1751103628437,'PENDING',308000,280000,1751103628437,10,4);
INSERT INTO order_items VALUES(5,1751103728995,'PENDING',280500,255000,1751103728995,10,5);
INSERT INTO order_items VALUES(6,1751105467133,'PENDING',385000,350000,1751105467133,10,6);
INSERT INTO order_items VALUES(7,1751105514854,'PENDING',385000,350000,1751105514854,10,7);
INSERT INTO order_items VALUES(8,1751105595383,'PENDING',159500,145000,1751105595383,10,8);
INSERT INTO order_items VALUES(9,1751106140163,'CONFIRMED',836000,760000,1751106361538,10,9);
INSERT INTO order_items VALUES(10,1751111692142,'PENDING',280500,255000,1751111692142,10,10);
INSERT INTO order_items VALUES(11,1751115929139,'PENDING',803000,730000,1751115929139,10,11);
INSERT INTO order_items VALUES(12,1751115977971,'PENDING',803000,730000,1751115977971,10,12);
INSERT INTO order_items VALUES(13,1751116042880,'PENDING',803000,730000,1751116042880,10,13);
INSERT INTO order_items VALUES(14,1751116246474,'PENDING',1188000,1080000,1751116246474,10,14);
INSERT INTO order_items VALUES(15,1751116998810,'PENDING',423500,385000,1751116998810,10,15);
INSERT INTO order_items VALUES(16,1751117075934,'PENDING',423500,385000,1751117075934,10,16);
INSERT INTO order_items VALUES(17,1751117231242,'PENDING',467500,425000,1751117231243,10,17);
INSERT INTO order_items VALUES(18,1751117664044,'PENDING',148500,135000,1751117664044,10,18);
INSERT INTO order_items VALUES(19,1751117874695,'CONFIRMED',291500,265000,1751118000397,10,19);
INSERT INTO order_items VALUES(20,1751123280267,'PENDING',456500,415000,1751123280267,10,20);
INSERT INTO order_items VALUES(21,1751124774658,'PENDING',2249500,2045000,1751124774658,10,21);
INSERT INTO order_items VALUES(22,1751124922676,'PENDING',462000,420000,1751124922676,10,22);
INSERT INTO order_items VALUES(23,1751124968991,'PENDING',462000,420000,1751124968991,10,23);
INSERT INTO order_items VALUES(24,1751278226453,'PENDING',280500,255000,1751278226453,10,24);
INSERT INTO order_items VALUES(25,1751280642736,'PENDING',280500,255000,1751280642736,10,25);
CREATE TABLE product (
        type varchar(31) not null,
        product_id integer,
        barcode varchar(50) unique,
        condition varchar(255),
        created_at timestamp,
        dimensions varchar(255),
        image_url varchar(500),
        import_date varchar(255),
        introduction TEXT,
        price integer not null,
        quantity integer,
        rush_order_supported boolean,
        title varchar(255) not null,
        updated_at timestamp,
        weight float,
        primary key (product_id)
    );
INSERT INTO product VALUES('book',1,'978-0-7432-7356-5','New',1751074513000,'20x13x2 cm',NULL,NULL,'A classic American novel about the Jazz Age and the American Dream.',120000,16,0,'The Great Gatsby\',1751280642753,0.30000001192092895507);
INSERT INTO product VALUES('book',2,'978-0-06-112008-4','New',1751074513000,'21x14x2.5 cm','https://covers.openlibrary.org/b/id/8226816-L.jpg','2024-01-10','A gripping tale of racial injustice and childhood innocence in the American South.',135000,13,0,'To Kill a Mockingbird',1751280642755,0.34999999403953552246);
INSERT INTO product VALUES('book',3,'978-0-452-28423-4','New',1751074513000,'20x13x2 cm','https://covers.openlibrary.org/b/id/8226112-L.jpg','2024-01-12','A dystopian social science fiction novel about totalitarianism.',145000,23,1,'1984',1751124774669,0.2800000011920928955);
INSERT INTO product VALUES('cd',4,'094638241621','New',1751074513000,'14x12x1 cm','https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg','2024-01-20','The Beatles final studio album featuring Come Together.',350000,8,0,'Abbey Road',1751116246485,0.10000000149011611938);
INSERT INTO product VALUES('cd',5,'094637420621','New',1751074513000,'14x12x1 cm','https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png','2024-01-18','Pink Floyd masterpiece exploring conflict and madness.',380000,14,1,'Dark Side of the Moon',1751116246485,0.10000000149011611938);
INSERT INTO product VALUES('cd',6,'074643851428','New','2025-06-28 08:35:13','14x12x1 cm','https://upload.wikimedia.org/wikipedia/en/5/55/Michael_Jackson_-_Thriller.png','2024-01-22','Michael Jackson''s Thriller with Billie Jean and Beat It.',340000,18,0,'Thriller','2025-06-28 08:35:13',0.10000000000000000555);
INSERT INTO product VALUES('dvd',7,'085391163926','New','2025-06-28 08:35:13','19x13x1.5 cm','https://m.media-amazon.com/images/I/519NBNHX5BL._AC_.jpg','2024-01-25','A story of friendship and redemption in prison.',220000,12,0,'The Shawshank Redemption','2025-06-28 08:35:13',0.14999999999999999444);
INSERT INTO product VALUES('dvd',8,'097360719147','New',1751074513000,'19x13x1.5 cm','https://m.media-amazon.com/images/I/41+dkHBquGL._AC_.jpg','2024-01-23','The aging patriarch hands control to his son.',250000,8,1,'The Godfather',1751124774669,0.15000000596046447753);
INSERT INTO product VALUES('dvd',9,'031398187737','New',1751074513000,'19x13x1.5 cm','https://m.media-amazon.com/images/I/71c05lTE03L._AC_SL1024_.jpg','2024-01-24','Tarantino''s cult classic about L.A. criminals.',210000,10,0,'Pulp Fiction',1751124968996,0.15000000596046447753);
CREATE TABLE aims_user (
        user_id integer,
        email varchar(255) not null unique,
        is_active boolean,
        name varchar(255) not null,
        password varchar(255) not null,
        phone varchar(20),
        registration_date timestamp,
        role varchar(255) check (role in ('ADMIN', 'MANAGER')),
        salary float,
        primary key (user_id)
    );
INSERT INTO aims_user VALUES(1,'admin@itss.com',1,'Admin User','hashedpassword123','0123456789','2025-06-28 08:35:13','ADMIN',1000.0);
INSERT INTO aims_user VALUES(2,'manager1@itss.com',1,'Manager User 1','hashedpassword456','0987654321',1751074513000,'MANAGER',0.0);
INSERT INTO aims_user VALUES(3,'manager2@itss.com',1,'Manager User 2','hashedpassword789','0555123456','2025-06-28 08:35:13','MANAGER',800.0);
CREATE TABLE HT_product (
        product_id bigint not null,
        hib_sess_id char(36) not null,
        primary key (product_id, hib_sess_id)
    );
CREATE TABLE payment_transaction (
        transaction_id integer,
        amount integer not null,
        created_at timestamp,
        failure_reason varchar(255),
        payment_method varchar(50),
        processed_at timestamp,
        status varchar(255) check (
            status in (
                'PENDING',
                'SUCCESS',
                'FAILED',
                'CANCELLED',
                'REFUNDED'
            )
        ),
        invoice_id bigint not null,
        primary key (transaction_id)
    );
INSERT INTO payment_transaction VALUES(1,100000,1751099820086,NULL,NULL,NULL,'PENDING',1);
INSERT INTO payment_transaction VALUES(2,200000,1751100722459,NULL,NULL,NULL,'PENDING',2);
INSERT INTO payment_transaction VALUES(3,300000,1751103252708,NULL,NULL,NULL,'PENDING',3);
INSERT INTO payment_transaction VALUES(4,150000,1751103628441,NULL,NULL,NULL,'PENDING',4);
INSERT INTO payment_transaction VALUES(5,170000,1751103729000,NULL,NULL,NULL,'PENDING',5);
INSERT INTO payment_transaction VALUES(6,250000,1751105467139,NULL,NULL,NULL,'PENDING',6);
INSERT INTO payment_transaction VALUES(7,275000,1751105514857,NULL,NULL,NULL,'PENDING',7);
INSERT INTO payment_transaction VALUES(8,225000,1751105595388,NULL,NULL,NULL,'PENDING',8);
INSERT INTO payment_transaction VALUES(9,300000,1751106140194,NULL,NULL,NULL,'PENDING',9);
CREATE TABLE user (
        user_id integer,
        email varchar(255) not null unique,
        is_active boolean,
        name varchar(255) not null,
        password varchar(255) not null,
        phone varchar(20),
        registration_date timestamp,
        role varchar(255) check (role in ('ADMIN','MANAGER')),
        salary float,
        primary key (user_id)
    );
INSERT INTO user VALUES(1,'admin@itss.com',1,'Admin User','hashedpassword123','0123456789','2025-06-28 15:32:00','ADMIN',1000.0);
INSERT INTO user VALUES(2,'manager1@itss.com',1,'Manager User 1','hashedpassword456','0987654321','2025-06-28 15:32:00','MANAGER',0.0);
INSERT INTO user VALUES(3,'manager2@itss.com',1,'Manager User 2','hashedpassword789','0555123456','2025-06-28 15:32:00','MANAGER',800.0);
COMMIT;
