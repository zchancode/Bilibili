const express = require('express');
const app = express();

//body parser
const bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

//static
app.use(express.static('public'));

//cors
const cors = require('cors');
app.use(cors());


class Video{
    constructor(name, url){
        this.name = name;
        this.url = url;
    }
}

//routes
app.get('/video', (req, res) => {
    //video list to json
    let videoList = new Array();
    videoList.push(new Video("Video 1", "https://www.youtube.com/embed/1"));
    videoList.push(new Video("Video 2", "https://www.youtube.com/embed/2"));
    videoList.push(new Video("Video 3", "https://www.youtube.com/embed/3"));
    res.json(videoList);
});



app.listen(3000, () => {
    console.log('Server is running on port 3000');
});