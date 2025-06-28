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
INSERT INTO product VALUES('book',1,'978-0-7432-7356-5','New',1751074513000,'20x13x2 cm',NULL,NULL,'A classic American novel about the Jazz Age and the American Dream.',120000,23,0,'The Great Gatsby (extended)',1751111692160,0.30000001192092895507);
INSERT INTO product VALUES('book',2,'978-0-06-112008-4','New',1751074513000,'21x14x2.5 cm','https://covers.openlibrary.org/b/id/8226816-L.jpg','2024-01-10','A gripping tale of racial injustice and childhood innocence in the American South.',135000,24,0,'To Kill a Mockingbird',1751111692163,0.34999999403953552246);
INSERT INTO product VALUES('book',3,'978-0-452-28423-4','New',1751074513000,'20x13x2 cm','https://covers.openlibrary.org/b/id/8226112-L.jpg','2024-01-12','A dystopian social science fiction novel about totalitarianism.',145000,35,1,'1984',1751105595392,0.2800000011920928955);
INSERT INTO product VALUES('cd',4,'094638241621','New',1751074513000,'14x12x1 cm','https://upload.wikimedia.org/wikipedia/en/4/42/Beatles_-_Abbey_Road.jpg','2024-01-20','The Beatles final studio album featuring Come Together.',350000,13,0,'Abbey Road',1751105514860,0.10000000149011611938);
INSERT INTO product VALUES('cd',5,'094637420621','New',1751074513000,'14x12x1 cm','https://upload.wikimedia.org/wikipedia/en/3/3b/Dark_Side_of_the_Moon.png','2024-01-18','Pink Floyd masterpiece exploring conflict and madness.',380000,18,1,'Dark Side of the Moon',1751106140216,0.10000000149011611938);
INSERT INTO product VALUES('cd',6,'074643851428','New','2025-06-28 08:35:13','14x12x1 cm','https://upload.wikimedia.org/wikipedia/en/5/55/Michael_Jackson_-_Thriller.png','2024-01-22','Michael Jackson''s Thriller with Billie Jean and Beat It.',340000,18,0,'Thriller','2025-06-28 08:35:13',0.10000000000000000555);
INSERT INTO product VALUES('dvd',7,'085391163926','New','2025-06-28 08:35:13','19x13x1.5 cm','https://m.media-amazon.com/images/I/519NBNHX5BL._AC_.jpg','2024-01-25','A story of friendship and redemption in prison.',220000,12,0,'The Shawshank Redemption','2025-06-28 08:35:13',0.14999999999999999444);
INSERT INTO product VALUES('dvd',8,'097360719147','New','2025-06-28 08:35:13','19x13x1.5 cm','https://m.media-amazon.com/images/I/41+dkHBquGL._AC_.jpg','2024-01-23','The aging patriarch hands control to his son.',250000,10,1,'The Godfather','2025-06-28 08:35:13',0.14999999999999999444);
INSERT INTO product VALUES('dvd',9,'031398187737','New','2025-06-28 08:35:13','19x13x1.5 cm','https://m.media-amazon.com/images/I/71c05lTE03L._AC_SL1024_.jpg','2024-01-24','Tarantino''s cult classic about L.A. criminals.',210000,14,0,'Pulp Fiction','2025-06-28 08:35:13',0.14999999999999999444);
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
INSERT INTO aims_user VALUES(2,'manager1@itss.com',0,'Manager User 1','hashedpassword456','0987654321',1751074513000,'MANAGER',0.0);
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
COMMIT;
