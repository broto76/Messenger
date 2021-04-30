const express = require('express');
const mongoose = require('mongoose');

const authRouter = require('./routes/authRoutes');
const errorRouter = require('./controller/errorController');
const messagesRouter = require('./routes/messagingRoutes');
const creds = require('./creds');

const app = express();

// Parse JSON data in body of request
app.use(express.json());
app.use(express.urlencoded({
    extended: true
}));

app.use((req, res, next) => {
    console.log('Incoming Request: ' + req.url);
    next();
});

app.use('/auth', authRouter);
app.use(messagesRouter);

// This would be default handler for sending 404 response.
app.use(errorRouter.getErrorHandler);

mongoose.connect(creds.MongoDbURI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
    useFindAndModify: false
})
.then(result => {
    const port = (process.env.PORT || 5000);
    const server = app.listen(5000);
    console.log('Server Running on port: ' + 5000);
})
.catch(err => {
    console.log('Unable to connect to MongoDB. Error: ' + err);
})

