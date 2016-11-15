(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('ImageDetailController', ImageDetailController);

    ImageDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Image', 'Item'];

    function ImageDetailController($scope, $rootScope, $stateParams, previousState, entity, Image, Item) {
        var vm = this;

        vm.image = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('getReviewsApp:imageUpdate', function(event, result) {
            vm.image = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
