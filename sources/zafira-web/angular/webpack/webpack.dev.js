const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const CleanWebpackPlugin = require('clean-webpack-plugin');

module.exports = merge(common, {
    devtool: 'inline-source-map',
    // output: {
    //     publicPath: '/v1/',
    // },
    plugins: [
        new CleanWebpackPlugin(['../dist'], {
            allowExternal: true
        })
    ],
    mode: 'development',
    watch: true,
    module: {
        rules: [
            // {
            //     test: /\.scss|sass$/,
            //     use: [
            //         { loader: 'sass'},
            //         {
            //             loader: 'css',
            //             options: {
            //                 sourceMap: true
            //             }
            //         },
            //         { loader: 'stylus' }
            //     ],
            //     exclude: [/app\/app\.styl$/],
            // },
            // {
            //     test: /\.styl$/,
            //     use: [
            //         { loader: 'sass'},
            //         {
            //             loader: 'css',
            //             options: {
            //                 sourceMap: true
            //             }
            //         },
            //         { loader: 'stylus' }
            //     ],
            //     include: [/app\/app\.styl$/]
            // },
            {
                test: /\.(sa|sc|c)ss$/,
                use: [
                    'style',
                    'css',
                    'postcss',
                    'sass',
                ],
            }
        ]
    }
});
