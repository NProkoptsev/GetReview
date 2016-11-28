(function () {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('ItemNewReviewDialogController', ItemNewReviewDialogController);

    ItemNewReviewDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Item',
        'Image', 'Review', 'Principal'];

    function ItemNewReviewDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Item,
                                           Image, Review, Principal) {
        var vm = this;

        vm.item = entity;
        vm.clear = clear;
        vm.save = save;
        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            Principal.identity().then(function (user) {
                // vm.review.clientLogin = user.login;
                vm.review.item = {}
                vm.review.source = {}
                vm.review.item.id = vm.item.id;
                vm.review.source.id = 3;
                Review.save(vm.review, onSaveSuccess, onSaveError);
            });
        }

        function onSaveSuccess(result) {
            $scope.$emit('getReviewsApp:reviewUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }


    }
})();
