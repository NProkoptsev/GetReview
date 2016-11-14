(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('ItemDetailController', ItemDetailController);

<<<<<<< HEAD
    ItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Item', 'Image', 'Review'];

    function ItemDetailController($scope, $rootScope, $stateParams, previousState, entity, Item, Image, Review) {
=======
    ItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState',
        'entity', 'Item', 'Review'];

    function ItemDetailController($scope, $rootScope, $stateParams, previousState, entity,
                                  Item, Review) {
>>>>>>> e977217172be16aee029014f5add4e4bdbe8234a
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
