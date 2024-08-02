const mysql = require('mysql');

// MySQL 连接配置
const dbConfig = {
    host: 'localhost',
    user: 'root',
    password: ''
};

// 创建连接
const connection = mysql.createConnection(dbConfig);

connection.connect((err) => {
    if (err) {
        console.error('Error connecting to MySQL:', err);
        return;
    }
    console.log('Connected to MySQL');

    // 创建数据库
    connection.query('CREATE DATABASE IF NOT EXISTS video_db', (err, result) => {
        if (err) {
            console.error('Error creating database:', err);
            return;
        }
        console.log('Database created or already exists');

        // 使用创建的数据库
        connection.query('USE video_db', (err) => {
            if (err) {
                console.error('Error using database:', err);
                return;
            }

            // 创建 video 表
            const createVideoTable = `
                CREATE TABLE IF NOT EXISTS video (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    url VARCHAR(255) NOT NULL
                )
            `;
            connection.query(createVideoTable, (err, result) => {
                if (err) {
                    console.error('Error creating video table:', err);
                    return;
                }
                console.log('Video table created or already exists');

                // 创建 user 表
                const createUserTable = `
                    CREATE TABLE IF NOT EXISTS user (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL
                    )
                `;
                connection.query(createUserTable, (err, result) => {
                    if (err) {
                        console.error('Error creating user table:', err);
                        return;
                    }
                    console.log('User table created or already exists');

                    // 关闭连接
                    connection.end((err) => {
                        if (err) {
                            console.error('Error closing connection:', err);
                            return;
                        }
                        console.log('Connection closed');
                    });
                });
            });
        });
    });
});
