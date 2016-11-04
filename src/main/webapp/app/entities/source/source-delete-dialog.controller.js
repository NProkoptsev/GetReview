(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('SourceDeleteController',SourceDeleteController);

    SourceDeleteController.$inject = ['$uibModalInstance', 'entity', 'Source'];

    function SourceDeleteController($uibModalInstance, entity, Source) {
        var vm = this;

        vm.source = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Source.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
