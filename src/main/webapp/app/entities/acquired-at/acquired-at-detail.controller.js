(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('Acquired_atDetailController', Acquired_atDetailController);

    Acquired_atDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Acquired_at', 'Item', 'Shop'];

    function Acquired_atDetailController($scope, $rootScope, $stateParams, previousState, entity, Acquired_at, Item, Shop) {
        var vm = this;

        vm.acquired_at = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('getReviewsApp:acquired_atUpdate', function(event, result) {
            vm.acquired_at = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
