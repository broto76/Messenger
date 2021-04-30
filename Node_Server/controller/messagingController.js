

const User = require('../models/User');
const UserStatus = require('../models/UserStatus');

exports.getAllFriends = async(req, res, next) => {
    try {
        let user = await User.findOne({
            phoneNumber: req.user.phoneNumber
        }).populate('messageRequestList.remoteUser');

        const result = [];
        user.messageRequestList.forEach(item => {
            if (item.status == UserStatus.Accepted) {
                result.push({
                    // Refer this for output json format
                    name: item.remoteUser.name,
                    phoneNumber: item.remoteUser.phoneNumber,
                    _id: item.remoteUser._id
                });
            }
        });

        res.status(200).json({
            message: 'List fetched.',
            list: result
        });

    } catch (error) {
        console.log('Internal Server error. Error: ' + error);
        return res.status(500).json({
            message: 'Internal server error'
        });
    }
}

exports.postSendMessageRequest = async (req, res, next) => {
    const remoteId = req.body.id;

    if (!remoteId) {
        console.log('Invalid Remote User Id.')
        return res.status(422).json({
            message: 'Invalid Remote User Data.'
        });
    }

    try {
        let remoteUser = await User.findById(remoteId);

        if (!remoteUser) {
            return res.status(404).json({
                message: 'Remote User not found.'
            });
        }

        let user = req.user;

        const request = {
            remoteUser: remoteUser._id,
            isOwner: true,
            status: UserStatus.Requested
        }
        //console.log('User: ' + user);
        user.messageRequestList.push(request);
        await user.save();

        request.remoteUser = user._id;
        request.isOwner = false;
        request.status = UserStatus.Requested;
        //console.log('Remote: ' + remoteUser);
        remoteUser.messageRequestList.push(request);
        await remoteUser.save();

        // TODO: Send a push notification to remote user

        res.status(200).json({
            message: 'Message Request Sent'
        })

    } catch (error) {
        console.log('Internal Server error. Error: ' + error);
        return res.status(500).json({
            message: 'Internal server error'
        });
    }
}

exports.postAcceptMessageRequest = async (req, res, next) => {
    const remoteId = req.body.id;

    if (!remoteId) {
        console.log('Invalid Remote User Id.')
        return res.status(422).json({
            message: 'Invalid Remote User Data.'
        });
    }

    try {
        const user = req.user;
        const remoteUser = await User.findById(remoteId);

        if (!user || !remoteUser) {
            console.log('Cannot find user/remoteUser from db. User: ' + user + ', RemoteUser: ' + remoteUser);
            return res.status(404).json({
                message: 'User/RemoteUser not found in server'
            });
        }

        const userIndex = user.messageRequestList.findIndex(item => {
            return item.remoteUser.toString() == remoteUser._id.toString()
        });
        const remoteUserIndex = remoteUser.messageRequestList.findIndex(item => {
            return item.remoteUser.toString() == user._id.toString()
        });

        if (userIndex < 0 || userIndex < 0) {
            console.log('Cannot find user/remoteUser Message request');
            return res.status(404).json({
                message: 'Cannot find user/remoteUser Message request'
            });
        }

        if (user.messageRequestList[userIndex].isOwner) {
            console.log('Cannot accept request which is owned');
            return res.status(401).json({
                message: 'Request UnAuthorized',
                remoteUser_id: remoteUser._id,
                user_newStatus: user.messageRequestList[userIndex].status,
                remoteUser_newStatus: remoteUser.messageRequestList[remoteUserIndex].status
            })
        }
        
        // console.log("Test1: " + user.messageRequestList[userIndex].status);
        // console.log("Test2: " + remoteUser.messageRequestList[remoteUserIndex].status);
        // console.log("Test3: " + UserStatus.Requested);
        if (user.messageRequestList[userIndex].status != UserStatus.Requested 
            || remoteUser.messageRequestList[remoteUserIndex].status != UserStatus.Requested) {
            console.log('No Active Request found');
            return res.status(404).json({
                message: 'Request Not found',
                remoteUser_id: remoteUser._id,
                user_newStatus: user.messageRequestList[userIndex].status,
                remoteUser_newStatus: remoteUser.messageRequestList[remoteUserIndex].status
            })
        }

        user.messageRequestList[userIndex].status = UserStatus.Accepted;
        remoteUser.messageRequestList[remoteUserIndex].status = UserStatus.Accepted;

        await user.save();
        await remoteUser.save();

        res.status(200).json({
            message: 'Request Accepted',
            remoteUser_id: remoteUser._id,
            user_newStatus: user.messageRequestList[userIndex].status,
            remoteUser_newStatus: remoteUser.messageRequestList[remoteUserIndex].status
        });
    
    } catch (error) {
        console.log('Internal Server error. Error: ' + error);
        return res.status(500).json({
            message: 'Internal server error'
        });
    }
}

exports.getCurrentUserDetails = (req, res, next) => {
    if (!req.user) {
        console.log('Current user details not found. Abort!');
        return res.status(404).json({
            message: 'Current User details not found'
        });
    }

    let resultUser = req.user._doc
    delete resultUser.password;

    res.status(200).json(resultUser);
}

exports.getUserDetails = async (req, res, next) => {
    const userId = req.params.userId;
    if (!userId) {
        console.log('Invalid user data');
        return res.status(422).json({
            message: 'Invalid user data'
        });
    }

    let resultUser = await User.findById(userId);
    if (!resultUser) {
        console.log('User(' + userId +') details not found. Abort!');
        return res.status(404).json({
            message: 'User details not found'
        });
    }
    resultUser = resultUser._doc;
    delete resultUser.password;

    res.status(200).json(resultUser);
}

exports.getUserDetailsLimited = async (req, res, next) => {
    const phoneNumber = req.params.phoneNumber;
    if (!phoneNumber) {
        console.log('Invalid user data');
        return res.status(422).json({
            message: 'Invalid user data'
        });
    }

    let user = await User.findOne({
        phoneNumber: phoneNumber
    });
    if (!user) {
        return res.status(404).json({
            message: 'User not Registered!'
        });
    }

    user = user._doc;
    delete user.password;

    let status = UserStatus.Unknown;
    let statusOwner = -1;
    user.messageRequestList.forEach(item => {
        if (item.remoteUser.toString() == req.user._id.toString()) {
            status = item.status;
            if (item.isOwner) {
                statusOwner = user._id
            } else {
                statusOwner = req.user._id
            }
        }
    });
    delete user.messageRequestList;

    res.status(200).json({
        message: 'User Found',
        user: user,
        status: status,
        statusOwner: statusOwner
    });
}