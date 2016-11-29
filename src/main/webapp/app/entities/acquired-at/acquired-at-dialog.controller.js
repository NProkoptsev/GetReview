(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('Acquired_atDialogController', Acquired_atDialogController);

    Acquired_atDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Acquired_at', 'Item', 'Shop'];

    function Acquired_atDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Acquired_at, Item, Shop) {
        var vm = this;

        vm.acquired_at = entity;
        vm.clear = clear;
        vm.save = save;
        vm.items = Item.query();
        vm.shops = Shop.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.acquired_at.id !== null) {
                Acquired_at.update(vm.acquired_at, onSaveSuccess, onSaveError);
            } else {
                Acquired_at.save(vm.acquired_at, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('getReviewsApp:acquired_atUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
