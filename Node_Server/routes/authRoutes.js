const express = require('express');

const authController = require('../controller/authController');


const authRouter = express.Router();

authRouter.post('/register', authController.postRegister);
authRouter.post('/login', authController.postLogin);
/**
 * return {
 *      token,
 *      user._id
 * }
 * 
 */

authRouter.post('/tokenValidity', authController.postTokenValidity);
/**
 * 
 * return {
 *      tokenStatus: "valid"/"invalid"
 *      userId: user._id/""
 * }
 * 
 */


module.exports = authRouter;
