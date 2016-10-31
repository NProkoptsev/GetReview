(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('ReviewDetailController', ReviewDetailController);

    ReviewDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Review'];

    function ReviewDetailController($scope, $rootScope, $stateParams, previousState, entity, Review) {
        var vm = this;

        vm.review = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('getReviewsApp:reviewUpdate', function(event, result) {
            vm.review = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
