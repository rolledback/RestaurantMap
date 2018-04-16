const gulp = require("gulp");
const browserify = require("browserify");
const watchify = require("watchify");
const errorify = require("errorify");
const del = require("del");
const tsify = require("tsify");
const source = require("vinyl-source-stream");
const runSequence = require("run-sequence");
const tslint = require("gulp-tslint");

function createBrowserifier(entry) {
    return browserify({
        basedir: ".",
        debug: true,
        entries: [entry],
        cache: {},
        packageCache: {}
    })
        .plugin(tsify)
        .plugin(watchify)
        .plugin(errorify);
}

function bundle(browserifier, bundleName, destination) {
    return browserifier
        .bundle()
        .pipe(source(bundleName))
        .pipe(gulp.dest(destination));
}

gulp.task("tslint", () => {
    return gulp.src("ts/**/*.ts")
        .pipe(tslint({
            formatter: "prose"
        }))
        .pipe(tslint.report());
});

gulp.task("clean", () => {
    return del(".bin")
});

gulp.task("tsc-browserify-src", ["tslint"], () => {
    return bundle(createBrowserifier("./ts/index.ts"), "bundle.js", ".bin/js");
});

gulp.task("copy-css-to-bin", () => {
    return gulp.src("*.css")
        .pipe(gulp.dest(".bin"));
});

gulp.task("copy-html-to-bin", () => {
    return gulp.src("*.html")
        .pipe(gulp.dest(".bin"));
});

gulp.task("copy-to-bin", ["copy-css-to-bin", "copy-html-to-bin"]);

gulp.task("build", ["copy-to-bin", "tsc-browserify-src"]);

gulp.task("default", (done) => {
    runSequence(["copy-to-bin", "tsc-browserify-src"], () => {
        console.log("Watching...");
        gulp.watch(["ts/**/*.ts"], ["build"]);
    });
});