(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('BookmarkDetailController', BookmarkDetailController);

    BookmarkDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Bookmark', 'Client', 'Item'];

    function BookmarkDetailController($scope, $rootScope, $stateParams, previousState, entity, Bookmark, Client, Item) {
        var vm = this;

        vm.bookmark = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('getReviewsApp:bookmarkUpdate', function(event, result) {
            vm.bookmark = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
