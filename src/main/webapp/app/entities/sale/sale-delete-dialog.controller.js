(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('SaleDeleteController',SaleDeleteController);

    SaleDeleteController.$inject = ['$uibModalInstance', 'entity', 'Sale'];

    function SaleDeleteController($uibModalInstance, entity, Sale) {
        var vm = this;

        vm.sale = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Sale.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
