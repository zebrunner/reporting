'use strict';

const mode = 'production';
const merge = require('webpack-merge');
const common = require('./webpack.common.js')(mode);
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const safePostCssParser = require('postcss-safe-parser');

module.exports = merge(common, {
    mode,
    devtool: false,
    optimization: {
        minimizer: [
            new UglifyJsPlugin({
                // uglifyOptions: {
                //     compress: {
                //         passes: 5 //Gives additional 3-5% of compression
                //     }
                // }
            }),
            new OptimizeCssAssetsPlugin({
                cssProcessorOptions: {
                    parser: safePostCssParser,
                },
            }),
        ]
    },
    plugins: [
        new CleanWebpackPlugin(['../dist'], {
            allowExternal: true
        }),
        // new BundleAnalyzerPlugin(),
    ]
});
