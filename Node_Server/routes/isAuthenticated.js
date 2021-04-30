const jwt = require('jsonwebtoken');

const creds = require('../creds');
const User = require('../models/User');

module.exports = async (req, res, next) => {

    req.isAuth = false;

    const token = req.get('Authorization');
    if(!token) {
        console.log('Not authorized. Invalid token');
        return res.status(401).json({
            message: 'Unauthorized access'
        });
    }

    let decodedData;

    try {
        decodedData = jwt.verify(token, creds.JWT_SECRET_TOKEN);
    } catch (error) {
        console.log('Error while verifying JWT. Error: ' + error);
        return res.status(401).json({
            message: 'Unauthorized Access'
        });
    }

    if(!decodedData) {
        console.log('Not authorized. Invalid token');
        return res.status(401).json({
            message: 'Unauthorized access'
        });
    }

    req.user = await User.findOne({
        phoneNumber: decodedData.phoneNumber
    });
    if (!req.user) {
        console.log('Current User not in database');
        return res.status(500).json({
            message: 'Internal Server Error.'
        });
    }

    req.isAuth = true;
    next();
}