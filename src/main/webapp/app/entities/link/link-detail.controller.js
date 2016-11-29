(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('LinkDetailController', LinkDetailController);

    LinkDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Link', 'Item'];

    function LinkDetailController($scope, $rootScope, $stateParams, previousState, entity, Link, Item) {
        var vm = this;

        vm.link = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('getReviewsApp:linkUpdate', function(event, result) {
            vm.link = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
