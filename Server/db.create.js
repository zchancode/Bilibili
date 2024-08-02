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

});
