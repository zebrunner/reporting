var gulp = require('gulp');
var args = require('yargs').argv;
var browserSync = require('browser-sync');
var config = require('./gulp.config')();
var del = require('del');
var $ = require('gulp-load-plugins')({lazy: true});

gulp.task('help', $.taskListing);
gulp.task('default', ['help']);

gulp.task('vet', function() {
    log('Analyzing source with JSHint and JSCS');

    return gulp
        .src(config.alljs)
        .pipe($.if(args.verbose, $.print()))
        .pipe($.jscs())
        .pipe($.jshint())
        .pipe($.jshint.reporter('jshint-stylish', {verbose: true}))
        .pipe($.jshint.reporter('fail'));
});

gulp.task('clean-tmp', function(done) {
    var files = config.tmp;
    clean(files, done);
});

gulp.task('clean', function(done) {
    var delconfig = [].concat(config.dist, config.tmp);
    log('Cleaning ' + $.util.colors.blue(delconfig));
    del(delconfig, done);
});

gulp.task('clean-all', function(done) {
    var delconfig = config.allToClean;
    log('Cleaning ' + $.util.colors.blue(delconfig));
    clean(delconfig, done);
});

gulp.task('pug-docs', function() {
    log('Compiling docs pug --> html');

    var options = {
        pretty: false
    }

    return gulp
        .src(config.docsPug)
        .pipe($.plumber({errorHandler: swallowError}))
        .pipe($.pug(options))
        .pipe(gulp.dest(config.docs));
});

gulp.task('less', function() {
    log('Compiling Less --> CSS');

    return gulp
        .src(config.less)
        .pipe($.plumber({errorHandler: swallowError}))
        .pipe($.less())
        .pipe($.autoprefixer())
        .pipe(gulp.dest(config.tmp));
});

gulp.task('less-watcher', function() {
    gulp.watch([config.less], ['less']);
});

gulp.task('sass', function() {
    log('Compiling Sass --> CSS');

    var sassOptions = {
        outputStyle: 'nested' // nested, expanded, compact, compressed
    };

    return gulp
        .src(config.sass)
        .pipe($.plumber({errorHandler: swallowError}))
        .pipe($.sourcemaps.init())
        .pipe($.sass(sassOptions))
        .pipe($.autoprefixer())
        .pipe($.sourcemaps.write())
        .pipe(gulp.dest(config.tmp + '/styles'));
});

gulp.task('sass-min', function() {
    log('Compiling Sass --> minified CSS');

    var sassOptions = {
        outputStyle: 'compressed' // nested, expanded, compact, compressed
    };

    return gulp
        .src(config.sass)
        .pipe($.plumber({errorHandler: swallowError}))
        .pipe($.sass(sassOptions))
        .pipe($.autoprefixer())
        .pipe(gulp.dest(config.tmp + '/styles'));
})

gulp.task('sass-watcher', function() {
    gulp.watch([config.sass], ['sass']);
});

gulp.task('inject', function() {
    log('Injecting custom scripts to index.html');

    return gulp
        .src(config.index)
        .pipe( $.inject(gulp.src(config.js), {relative: true}) )
        .pipe(gulp.dest(config.client));
});

gulp.task('copy', ['sass-min'], function() {
    log('Copying assets');

    var assets = [].concat(config.assetsLazyLoad, config.assetsToCopy);

    gulp.src(config.tmp + '/styles/loader.css').pipe(gulp.dest(config.dist + '/styles'));

    return gulp
        .src(assets, {base: config.client})
        .pipe(gulp.dest(config.dist + '/'));
});

gulp.task('optimize', ['inject', 'sass-min'], function() {
    log('Optimizing the js, css, html');

    return gulp
        .src(config.index)
        .pipe($.plumber({errorHandler: swallowError}))
        .pipe($.useref())
        // .pipe($.if('scripts/app.js', $.uglify()))
        .pipe(gulp.dest( config.dist ));

});


gulp.task('serve', ['inject', 'sass'], function() {
    startBrowserSync('serve');
});

gulp.task('build', ['optimize', 'copy'], function() {
    //startBrowserSync('dist');
})

gulp.task('serve-dist', function() {
    gulp.run('build');
})

gulp.task('serve-docs', ['pug-docs'], function() {
    startBrowserSync('docs');
})



function clean(path, done) {
    log('Cleaning: ' + $.util.colors.blue(path));
    del(path, done);
}

function log(msg) {
    if (typeof(msg) === 'object') {
        for (var item in msg) {
            if (msg.hasOwnProperty(item)) {
                $.util.log($.util.colors.green(msg[item]));
            }
        }
    } else {
        $.util.log($.util.colors.green(msg));
    }
}

function swallowError (error) {
    // If you want details of the error in the console
    console.log(error.toString());

    this.emit('end');
}

function startBrowserSync(opt) {
    if (args.nosync || browserSync.active) {
        return;
    }

    var options = {
        port: 3000,
        ghostMode: {
            clicks: false,
            location: false,
            forms: false,
            scroll: true
        },
        injectChanges: true,
        logFileChanges: true,
        logLevel: 'debug',
        logPrefix: 'gulp-patterns',
        notify: true,
        reloadDelay: 0, //1000,
        online: false
    };

    switch(opt) {
        case 'dist':
            log('Serving dist app');
            serveDistApp();
            break;
        case 'docs':
            log('Serving docs');
            serveDocs();
            break;
        default:
            log('Serving app');
            serveApp();
            break;
    }

    function serveApp() {
        gulp.watch([config.sass], ['sass']);

        options.server = {
            baseDir: [
                config.client,
                config.tmp
            ]
        };
        options.files = [
            config.client + '/**/*.*',
            '!' + config.sass,
            config.tmp + '/**/*.css'
        ];

        browserSync(options);
    }

    function serveDistApp() {
        options.server = {
            baseDir: [
                config.dist
            ]
        };
        options.files = [];

        browserSync(options);
    }

    function serveDocs() {
        gulp.watch([config.docsPug], ['pug-docs']);

        options.server = {
            baseDir: [
                config.docs
            ]
        }

        options.files = [
            config.docs + '/index.html',
            '!' + config.pug
        ];

        browserSync(options);
    }

}
