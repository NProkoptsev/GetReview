(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('LinkController', LinkController);

    LinkController.$inject = ['$scope', '$state', 'Link'];

    function LinkController ($scope, $state, Link) {
        var vm = this;
        
        vm.links = [];

        loadAll();

        function loadAll() {
            Link.query(function(result) {
                vm.links = result;
            });
        }
    }
})();
