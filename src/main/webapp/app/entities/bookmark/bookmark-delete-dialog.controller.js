(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('BookmarkDeleteController',BookmarkDeleteController);

    BookmarkDeleteController.$inject = ['$uibModalInstance', 'entity', 'Bookmark'];

    function BookmarkDeleteController($uibModalInstance, entity, Bookmark) {
        var vm = this;

        vm.bookmark = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Bookmark.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
