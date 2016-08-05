(function() {
    'use strict';

    angular
        .module('filmSolutionsApp')
        .controller('OwnerDeleteController',OwnerDeleteController);

    OwnerDeleteController.$inject = ['$uibModalInstance', 'entity', 'Owner'];

    function OwnerDeleteController($uibModalInstance, entity, Owner) {
        var vm = this;
        vm.owner = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Owner.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
