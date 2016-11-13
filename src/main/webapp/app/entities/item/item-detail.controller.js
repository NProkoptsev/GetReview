(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('ItemDetailController', ItemDetailController);

    ItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState',
        'entity', 'Item', 'Review'];

    function ItemDetailController($scope, $rootScope, $stateParams, previousState, entity,
                                  Item, Review) {
        var vm = this;

        vm.item = entity;
        vm.previousState = previousState.name;

        vm.reviews = [];

        Item.reviews({id : $stateParams.id}, function (result) {
           vm.reviews = result;
        });

        var unsubscribe = $rootScope.$on('getReviewsApp:itemUpdate', function(event, result) {
            vm.item = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
