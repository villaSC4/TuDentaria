const target = process.env.API_TARGET || 'http://127.0.0.1:8080';
module.exports = { '/api/**': { target, secure: false, changeOrigin: true }, '/uploads/**': { target, secure: false, changeOrigin: true } };
