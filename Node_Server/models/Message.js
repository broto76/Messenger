const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const messageSchema = new Schema({
    userOne: {
        type: Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    userTwo: {
        type: Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    messageData: [{
        messageOwner: {
            type: String,
            required: true
        },
        message: {
            type: String,
            required: true
        },
        time: {
            type: String,
            required: true
        }
    }]
});

module.exports = mongoose.model('Message', messageSchema);