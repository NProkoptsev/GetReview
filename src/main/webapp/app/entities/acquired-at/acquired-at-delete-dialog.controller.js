(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('Acquired_atDeleteController',Acquired_atDeleteController);

    Acquired_atDeleteController.$inject = ['$uibModalInstance', 'entity', 'Acquired_at'];

    function Acquired_atDeleteController($uibModalInstance, entity, Acquired_at) {
        var vm = this;

        vm.acquired_at = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Acquired_at.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
