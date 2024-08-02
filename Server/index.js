const express = require('express');
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('uuid');

const cors = require('cors');


const app = express();
const PORT = process.env.PORT || 3000;
const SECRET_KEY = 'your_secret_key';

app.use(cors());
app.use(bodyParser.json());

let users = []; // 简单的用户数据存储

// 注册接口
app.post('/api/register', (req, res) => {
  const { username, email, password } = req.body;

  console.log(username, email, password);

  // 校验请求参数
  if (!username || !email || !password) {
    return res.status(400).json({ success: false, message: 'body参数有问题' });
  }

  // 检查用户名或邮箱是否已存在
  const userExists = users.some(user => user.username === username || user.email === email);
  if (userExists) {
    return res.status(409).json({ success: false, message: '用户名或邮箱重复' });
  }

  // 创建新用户
  const newUser = {
    id: uuidv4(),
    username,
    email,
    password,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString()
  };

  users.push(newUser);

  res.status(201).json({ success: true, message: '注册成功' });
});

// 登录接口
app.post('/api/login', (req, res) => {
  const { email, password } = req.body;

  console.log(email, password);
  // 校验请求参数
  if (!email || !password) {
    return res.status(400).json({ success: false, message: 'body参数有问题' , token: null });
  }

  // 查找用户
  const user = users.find(user => user.email === email && user.password === password);
  if (!user) {
    return res.status(401).json({ success: false, message: '邮箱或密码错误', token: null });
  }

  // 生成 JWT Token
  const token = jwt.sign({ id: user.id }, SECRET_KEY, { expiresIn: '1h' });

  res.json({ success: true, message: '登录成功', token });
});


// 获取用户信息接口
app.post('/api/getUserInfo', (req, res) => {
  const { token } = req.body;

  if (!token) {
    return res.status(400).json({ success: false, message: 'Token is required' });
  }

  jwt.verify(token, SECRET_KEY, (err, user) => {
    if (err) {
      return res.status(401).json({ success: false, message: 'Invalid token' });
    }

    const foundUser = users.find(u => u.id === user.id);
    if (!foundUser) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }

    res.json({
      success: true,
      message: 'User information retrieved successfully',
      data: {
        id: foundUser.id,
        username: foundUser.username,
        email: foundUser.email,
        createdAt: foundUser.createdAt,
        updatedAt: foundUser.updatedAt
      }
    });
  });
});


app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
