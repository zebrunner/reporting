const merge = require('webpack-merge');
const common = require('./webpack.common.js');
// const ExtractTextPlugin = require('extract-text-webpack-plugin');

const version = process.env.VERSION || process.env.FRONTEND_VERSION;

module.exports = merge(common, {
    // plugins: [
    //     new ExtractTextPlugin({
    //         filename: `[name]-${version}.css`
    //     })
    // ],
    mode: 'production',
    module: {
        rules: [
            {
                test: /\.scss|sass$/,
                loader: 'style!css!sass',
                // exclude: [/app\/app\.styl$/],
            },
            // {
            //     test: /\.styl$/,
            //     loader: ExtractTextPlugin.extract({
            //         fallback: 'style',
            //         use: ['css', 'stylus']
            //     }),
            //     include: [/app\/app\.styl$/]
            // },
        ]
    }
});
