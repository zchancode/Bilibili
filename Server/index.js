const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const mysql = require('mysql');
const cors = require('cors');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));
app.use(express.static('public'));
app.use(cors());


const dbConfig = {
    host: 'localhost',
    user: 'root',
    password: '',
    database: 'video_db'
};

const connection = mysql.createConnection(dbConfig);

connection.connect((err) => {
    if (err) {
        console.log('Error connecting to Db');
        return;
    }
    console.log('Connection established');
});

class Video {
    constructor(name, url) {
        this.name = name;
        this.url = url;
    }
}

let videoList = [];

// 从数据库中获取视频列表
let query = 'SELECT * FROM video';
connection.query(query, (err, rows) => {
    if (err) {
        console.log(err);
        return;
    }
    rows.forEach((row) => {
        videoList.push(new Video(row.name, row.url));
    });
});

// routes
app.get('/video', (req, res) => {
    if (videoList.length > 0) {
        let randomIndex = Math.floor(Math.random() * videoList.length);
        let randomVideo = videoList[randomIndex];
        res.json(randomVideo);
    } else {
        res.status(404).json({ error: 'No videos found' });
    }
});

app.listen(3000, () => {
    console.log('Server is running on port 3000');
});
