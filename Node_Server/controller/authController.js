const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');

const User = require('../models/User');
const creds = require('../creds');

exports.postLogin = async (req, res, next) => {
    const phoneNumber = req.body.phoneNumber;
    const password = req.body.password;

    if (!phoneNumber || !password) {
        console.log(' Phone: ' + phoneNumber + '. Invalid Input.');
        return res.status(422).json({
            message: 'Invalid Details'
        });
    }

    let user = await User.findOne({
        phoneNumber: phoneNumber
    });
    if (!user) {
        console.log('User: ' + phoneNumber + ' is not registered.');
        return res.status(404).json({
            message: 'User: ' + phoneNumber + ' is not registered.'
        });
    }

    const isAuthorized = await bcrypt.compare(password, user.password);
    if (!isAuthorized) {
        console.log('User: ' + phoneNumber + ' auth failed.');
        return res.status(401).json({
            message: 'Authentication failed'
        });
    }

    const token = jwt.sign({
        name: user.name,
        phoneNumber: user.phoneNumber
    }, creds.JWT_SECRET_TOKEN);

    if (!token) {
        console.log('Could not generate JWT. Abort');
        return res.status(500).json({
            message: 'Could not generate JWT. Abort'
        });
    }

    // Send this token.
    res.status(200).json({
        message: 'Login Success',
        token: token,
        username: user.name,
        phoneNumber: user.phoneNumber,
        userId: user._id
    });

    /**
     * 
{
    "message": "Login Success",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiQW5pYnJhdGEgU2FoYSIsInBob25lTnVtYmVyIjoiNzk4MDM0NzU1MyIsImlhdCI6MTYxOTQyODMwOH0.3lBxSOWe-RbKFZr63_usNasnRWO7YE2m0z8stSbaMQ8",
    "username": "Anibrata Saha",
    "phoneNumber": "7980347553"
}

{
    "message": "Login Success",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiQnJvdG8gU2FoYSIsInBob25lTnVtYmVyIjoiODkwMjY4NjgxNSIsImlhdCI6MTYxOTQyODI2OX0.QZwNTgA3T_jCmvywY62sk5tfftjV--TejJqzGXIM8SM",
    "username": "Broto Saha",
    "phoneNumber": "8902686815"
}
     * 
     */
}

exports.postRegister = async (req, res, next) => {
    const name = req.body.name;
    const phoneNumber = req.body.phoneNumber;
    const password = req.body.password;

    if (!name || !phoneNumber || !password) {
        console.log('Name: ' + name + ' Phone: ' + phoneNumber + '. Invalid Input.');
        return res.status(422).json({
            message: 'Invalid Details'
        });
    }

    let user;

    try {

        user = await User.findOne({
            phoneNumber: phoneNumber
        });
        if (user) {
            console.log("User already exists");
            return res.status(422).json({
                message: 'User already exists.'
            });
        }

        // TODO: Replace password with OTP
        const hashedPassword = await bcrypt.hash(password, 12);
        if (!hashedPassword) {
            console.log('Unable to hash password. Aborted!');
            return res.status(500).json({
                message: 'Unable to hash password. Aborted!'
            });
        }

        user = new User({
            name: name,
            phoneNumber: phoneNumber,
            password: hashedPassword,
            isVerified: false,
            messageRequestList: []
        });
        await user.save();
    
        // TODO: Send OTP
    
        res.status(200).json({
            message: 'Signup Success',
            data: 'OTP sent to registered number'
        });
    } catch (err) {
        res.status(500).json({
            message: 'Internal Server Error',
            details: err
        });
    }
}