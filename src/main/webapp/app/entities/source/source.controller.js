(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('SourceController', SourceController);

    SourceController.$inject = ['$scope', '$state', 'Source'];

    function SourceController ($scope, $state, Source) {
        var vm = this;
        
        vm.sources = [];

        loadAll();

        function loadAll() {
            Source.query(function(result) {
                vm.sources = result;
            });
        }
    }
})();
