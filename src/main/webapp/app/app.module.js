(function() {
    'use strict';

    angular
        .module('getReviewsApp', [
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar'
        ])
        .run(run);

    // angular.module('getReviewsApp')
    //     .directive('onFinishRender', [ "$timeout" ,function ($timeout) {
    //         return {
    //             restrict: 'A',
    //             link: function (scope, element, attr) {
    //                 if (scope.$last === true) {
    //                     $timeout(function () {
    //                         scope.$emit(attr.onFinishRender);
    //                     });
    //                 }
    //             }
    //         }
    //     }]);

    run.$inject = ['stateHandler', 'translationHandler'];

    angular.module('getReviewsApp')
        .run(['$rootScope', function ($rootScope) {
            $rootScope.$on('$stateChangeSuccess', function() {
                document.body.scrollTop = document.documentElement.scrollTop = 0;
            });
        }]);

    function run(stateHandler, translationHandler) {
        stateHandler.initialize();
        translationHandler.initialize();
    }
})();
