const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const WebpackStrip = require('webpack-strip'); // TODO: production only

module.exports = {
    devtool: 'source-map',
    node: {
        fs: 'empty'
    },
    context: path.join(__dirname, '../client/app'),
    entry: {
        vendors: './app.vendors.js',
        app: './app.module.js',
        'vendors-styles': '../styles/vendors.scss',
        'main-styles': '../styles/main.scss',
    },
    output: {
        // filename: '[name]-' + version + '.js',
        filename: '[name].build.js',
        path: path.join(__dirname, '../dist'),
        chunkFilename: '[name].lazy-chunk.js'
    },
    resolve: {
        modules: [
            path.join(__dirname, '../client/app'),
            path.join(__dirname, '../client/assets'),
            // path.join(__dirname, '../client/bower_components'),
            path.join(__dirname, '../node_modules')
        ],
        alias: {
            'jquery-ui': 'jquery-ui/ui',
            'humanizeDuration': 'humanize-duration'
        }
    },
    module: {
        rules: [
            {
                test: /\.m?js$/,
                exclude: [/node_modules/],
                use: [
                    {
                        loader: 'babel',
                        options: {
                            presets: ['@babel/preset-env'],
                            plugins: [
                                '@babel/plugin-proposal-object-rest-spread',
                                '@babel/transform-runtime',
                                ['angularjs-annotate', { 'explicitOnly' : true}],
                                '@babel/plugin-syntax-dynamic-import'
                            ]
                        }
                    },
                    // WebpackStrip.loader('debug', 'debugger', 'console.log') // TODO: production only
                ]

            },
            // {
            //     test: /\.css$/,
            //     use: [
            //         'style',
            //         'css'
            //     ]
            // },
             { //TODO: add hash if production; TODO: compressing
                test: /\.(otf|ttf|eot|png|jpg|woff2?|svg)$/,
                loader: 'file',
                // options: {
                //     name: '[path][name].[ext]',
                //     outputPath: (url, resourcePath, context) => {
                //         // console.log(url, resourcePath, context);
                //
                //         return url[0] === '_' ? url.substring(1) : url;
                //     }
                // },
                //  options: {
                //      name(file) {
                //          if (process.env.NODE_ENV === 'development') {
                //              return '[path][name].[ext]';
                //          }
                //
                //          return '[hash].[ext]';
                //      },
                //  }
            },
            {
                test: /\.html$/,
                loader: 'raw',
            },
            {
                test: /\.(sa|sc|c)ss$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    {loader: 'css', options: { importLoaders: 1 }},
                    // {loader: `postcss`, options: {options: {}}}, //TODO: use this
                    'sass',
                ],
            }
            // {
            //     test: /\.svg$/,
            //     loader: 'file',
            //     include: [
            //         path.resolve(__dirname, 'assets/fonts')
            //     ]
            // },
            // {
            //     test: /\.svg$/,
            //     use: ['svg-sprite'],
            //     exclude: [
            //         path.resolve(__dirname, 'assets/fonts')
            //     ]
            // }
        ]
    },
    resolveLoader: {
        moduleExtensions: ['-loader']
    },
    plugins: [
        new CopyWebpackPlugin(
            [{ from: '../assets', to: 'assets'}]
        ),
        new MiniCssExtractPlugin({
            filename: '[name].css',
            chunkFilename: '[id].css'
        }),
        new CleanWebpackPlugin(['../dist'], {
            allowExternal: true
        }),
        new HtmlWebpackPlugin({
            template: '../index.html',
            favicon: '../favicon.ico',
            chunks: ['vendors-styles', 'main-styles', 'vendors', 'app'],
            chunksSortMode: 'manual',
            showErrors: true
        }),
        new webpack.ProgressPlugin()
    ],
    stats: {
        colors: true,
        // modules: true,
        // reasons: true,
        // errorDetails: true
    }
};
