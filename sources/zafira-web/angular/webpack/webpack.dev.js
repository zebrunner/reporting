'use strict';

const mode = 'development';
const path = require('path');
const webpack = require('webpack');
const merge = require('webpack-merge');
const common = require('./webpack.common.js')(mode);
const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

module.exports = merge(common, {
    mode,
    devtool: 'inline-source-map',
    devServer: {
        contentBase: path.join(__dirname, '../dist'),
        compress: true,
        port: 3000,
        disableHostCheck: true,
        inline: true,
        hot: true,
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin(),
    ],
    stats: {
        // modules: true,
        // reasons: true,
        errorDetails: true
    },
    watch: true,
});
