(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('BookmarkDialogController', BookmarkDialogController);

    BookmarkDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Bookmark', 'Client', 'Item'];

    function BookmarkDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Bookmark, Client, Item) {
        var vm = this;

        vm.bookmark = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.clients = Client.query();
        vm.items = Item.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.bookmark.id !== null) {
                Bookmark.update(vm.bookmark, onSaveSuccess, onSaveError);
            } else {
                Bookmark.save(vm.bookmark, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('getReviewsApp:bookmarkUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
