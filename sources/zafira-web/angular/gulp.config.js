module.exports = function() {
    var client = 'client',
        clientApp = './client/app'
        dist = 'dist',
        tmp = '.tmp',
        docs = 'documentation',
        landing = 'landing';
    var config = {
        client: client,
        dist: dist,
        tmp: tmp,
        index: client + "/index.html",
        alljs: [
            client + "/app/**/*.js",
            './*.js'
        ],
        assetsLazyLoad: [
            client + '/bower_components/angular-wizard/dist/angular-wizard.min.js',
            client + '/bower_components/ngmap/build/scripts/ng-map.min.js',
            client + '/bower_components/textAngular/dist/textAngular-sanitize.min.js',
            client + '/bower_components/rangy/rangy-core.min.js',
            client + '/bower_components/rangy/rangy-selectionsaverestore.min.js',
            client + '/bower_components/textAngular/dist/textAngular.js', 
            client + '/bower_components/textAngular/dist/textAngularSetup.js',
        ],
        assetsToCopy: [
            client + '/bower_components/webfontloader/webfontloader.js',
            client + "/app/**/*.html",
            client + '/assets/**/*',
            client + '/data/**/*',
            client + '/vendors/**/*',
            client + "/bower_components/font-awesome/css/*", 
            client + "/bower_components/font-awesome/fonts/*", 
            client + "/styles/loader.css", 
            client + "/styles/ui/images/*", 
            client + "/favicon.ico"
        ],
        less: [],
        sass: [
            client + "/styles/**/*.scss"
        ],
        js: [
            clientApp + "/**/*.module.js",
            clientApp + "/**/*.js",
            '!' + clientApp + "/**/*.spec.js"
        ],
        docs: docs, 
        docsPug: [
            docs + "/pug/index.pug",
            docs + "/pug/faqs.pug",
            docs + "/pug/layout.pug"
        ],
        allToClean: [
            tmp, 
            ".DS_Store",
            ".sass-cache",
            "node_modules",
            ".git",
            client + "/bower_components",
            docs + "/pug",
            docs + "/layout.html",
            landing + "/pug",
            landing + "/bower_components",
            "readme.md"
        ]
    };

    return config;
};