module.exports = {
    // Change build paths to make them Maven compatible
    // see https://cli.vuejs.org/config/
    outputDir: process.env.NODE_ENV === 'development' ? '../backend/src/main/resources/public' : 'target/dist',
    assetsDir: 'static',
}
