(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('SaleDetailController', SaleDetailController);

    SaleDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Sale', 'Item'];

    function SaleDetailController($scope, $rootScope, $stateParams, previousState, entity, Sale, Item) {
        var vm = this;

        vm.sale = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('getReviewsApp:saleUpdate', function(event, result) {
            vm.sale = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
