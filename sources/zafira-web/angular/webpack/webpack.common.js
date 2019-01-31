const path = require('path');

module.exports = {
    node: {
        fs: 'empty'
    },
    context: path.join(__dirname, '../client/app'),
    entry: {
        app: './app.module.js',
        vendors: './app.vendors.js',
        styles: '../styles/app.scss',
    },
    output: {
        // filename: '[name]-' + version + '.js',
        path: path.join(__dirname, '../dist'),
    },
    resolve: {
        modules: [
            path.join(__dirname, '../client/app'),
            path.join(__dirname, '../client/assets'),
            path.join(__dirname, '../client/bower_components'),
            path.join(__dirname, '../node_modules')
        ],
    },
    // resolve: {
    //     alias: {
    //         config: path.join(__dirname, 'app/app-config.json')
    //     }
    // },
    module: {
        rules: [
            {
                test: /\.m?js$/,
                exclude: [/node_modules|bower_components/],
                loader: 'babel',
                options: {
                    presets: ['@babel/preset-env'],
                    plugins: ['@babel/plugin-proposal-object-rest-spread', '@babel/transform-runtime']
                }
            },
            {
                test: /\.css$/,
                use: [
                    'style',
                    'css'
                ]
            },
            {
                test: /\.(otf|ttf|eot|png|woff|woff2|svg)$/,
                loader: 'file'
            },
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
};
