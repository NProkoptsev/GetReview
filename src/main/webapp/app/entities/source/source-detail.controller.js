(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('SourceDetailController', SourceDetailController);

    SourceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Source'];

    function SourceDetailController($scope, $rootScope, $stateParams, previousState, entity, Source) {
        var vm = this;

        vm.source = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('getReviewsApp:sourceUpdate', function(event, result) {
            vm.source = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
