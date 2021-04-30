const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const userSchema = new Schema({
    name: {
        type: String,
        required: true
    },
    phoneNumber: {
        type: String,
        required: true
    },
    password: {
        type: String,
        required: true
    },
    isVerified: {
        type: Boolean
    },
    messageRequestList: [{
        remoteUser: {
            type: Schema.Types.ObjectId,
            ref: 'User',
            required: true
        },
        isOwner: {
            type: Boolean,
            required: true
        },
        status: {
            type: String,
            // status can be: Requested, Accepted, Blocked, Denied, Unknown
            required: true
        }
    }]
});

module.exports = mongoose.model('User', userSchema);