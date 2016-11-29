(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('BookmarkController', BookmarkController);

    BookmarkController.$inject = ['$scope', '$state', 'Bookmark'];

    function BookmarkController ($scope, $state, Bookmark) {
        var vm = this;
        
        vm.bookmarks = [];

        loadAll();

        function loadAll() {
            Bookmark.query(function(result) {
                vm.bookmarks = result;
            });
        }
    }
})();
