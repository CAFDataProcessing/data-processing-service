var os = require('os');

module.exports = function(grunt) {

    grunt.initConfig({
        jekyll: {
            build: {
                options: {
                    serve: false,
                    incremental: false,
                    watch: false,
                    config: '_config.yml',
                    bundleExec: true
                }
            },
            serve: {
                options: {
                    serve: true,
                    incremental: false,
                    watch: true,
                    baseurl: '/documentation',
                    config: '_config.yml',
                    open_url: true,
                    bundleExec: true
                }
            }
        },
        exec: {
            bower_install: 'bower install',
            bower_uninstall: 'bower uninstall caf-templates',
            bower_clean: 'bower cache clean'
        },
        buildcontrol: {
            options: {
                dir: '.',
                commit: true,
                push: true,
                connectCommits: false,
                message: 'Built %sourceName% from commit %sourceCommit% on branch %sourceBranch%'
            },
            pages: {
                options: {
                    remote: 'git@github.hpe.com:caf-staging/data-processing-service.git',
                    login: '',
                    token: '',
                    branch: 'gh-pages'
                }
            }
        },
        //task to copy contract from API folder where it is used by application to a location it can be used by documentation.
        //Note that if you need to update this file, edits should be made to the api/swagger file as any changes to the documentation version of the contract will be overridden on next build.
        copy: {
          main: {
            files: [{
              expand: true,
              flatten: true,
              cwd: '.',
              src: ['../processing-service-core/api/swagger/swagger.yaml'],
              dest: 'contract/',
              rename: function(dest, src){
                return dest + 'processing.yaml';
              }
            }]
          },
          options: {
                process: function (content, srcpath) {
                  return "#THIS FILE SHOULD NOT BE EDITED. THIS FILE IS COPIED FROM THE ACTUAL CONTRACT LOCATION AND WILL BE OVERRIDEN ON DOCUMENTATION REBUILD. REFER TO GRUNTFILE FOR ORIGINAL CONTRACT LOCATION."+os.EOL+content;
                },
              }
        }
    });

    grunt.loadNpmTasks('grunt-build-control');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-jekyll');
    grunt.loadNpmTasks('grunt-exec');

    grunt.registerTask('default', ['copy:main', 'jekyll:build']);

    grunt.registerTask('build', ['copy:main', 'jekyll:build']);
    grunt.registerTask('serve', ['jekyll:serve']);
    grunt.registerTask('update', ['exec:bower_uninstall', 'exec:bower_clean', 'exec:bower_install']);

    grunt.registerTask('publish', ['exec:bower_uninstall', 'exec:bower_clean', 'exec:bower_install', 'buildcontrol:pages']);
};