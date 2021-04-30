const express = require('express');

const authController = require('../controller/authController');


const authRouter = express.Router();


authRouter.post('/login', authController.postLogin);
/**
 * return {
 *      token,
 *      user._id
 * }
 * 
 */
authRouter.post('/register', authController.postRegister);


module.exports = authRouter;