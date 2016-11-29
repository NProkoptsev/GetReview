(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('Acquired_atController', Acquired_atController);

    Acquired_atController.$inject = ['$scope', '$state', 'Acquired_at'];

    function Acquired_atController ($scope, $state, Acquired_at) {
        var vm = this;
        
        vm.acquired_ats = [];

        loadAll();

        function loadAll() {
            Acquired_at.query(function(result) {
                vm.acquired_ats = result;
            });
        }
    }
})();
