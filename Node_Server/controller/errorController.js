
exports.getErrorHandler = (req, res, next) => {
    console.log('Page not found: ' + req.url);
    res.status(404).json({
        message: 'Invalid URL',
        data: 'Page not found'
    });
}