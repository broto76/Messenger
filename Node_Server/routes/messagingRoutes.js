const express = require('express');

const authMiddleware = require('./isAuthenticated');
const messageController = require('../controller/messagingController');

const messagingRouter = express.Router();

messagingRouter.post('/sendMessageRequest', authMiddleware, messageController.postSendMessageRequest);
messagingRouter.post('/acceptMessageRequest', authMiddleware, messageController.postAcceptMessageRequest);

messagingRouter.get('/allFriends', authMiddleware, messageController.getAllFriends);

// getCurrentUserDeatils
messagingRouter.get('/userDetails', authMiddleware, messageController.getCurrentUserDetails);
// getUserDetails (param: remoteuser._id)
messagingRouter.get('/userDetails/:userId', authMiddleware, messageController.getUserDetails);
messagingRouter.get('/userDetailsFromNumber/:phoneNumber', authMiddleware, messageController.getUserDetailsLimited);

messagingRouter.get('/allRequests', authMiddleware, messageController.getAllFriendRequests);


module.exports = messagingRouter;