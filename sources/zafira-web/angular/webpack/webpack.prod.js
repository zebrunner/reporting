const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = merge(common, {
    // plugins: [
    //     new ExtractTextPlugin({
    //         filename: `[name]-${version}.css`
    //     })
    // ],
    mode: 'production',
    module: {
        rules: [
            // {
            //     test: /\.scss|sass$/,
            //     loader: 'style!css!sass',
            //     // exclude: [/app\/app\.styl$/],
            // },
            // {
            //     test: /\.(sa|sc|c)ss$/,
            //     use: [
            //         MiniCssExtractPlugin.loader,
            //         {loader: 'css', options: { importLoaders: 1 }},
            //         // {loader: `postcss`, options: {options: {}}},
            //         'sass',
            //     ],
            // }
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
