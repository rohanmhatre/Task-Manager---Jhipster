(function() {
    'use strict';

    angular
        .module('filmSolutionsApp')
        .controller('TaskDeleteController',TaskDeleteController);

    TaskDeleteController.$inject = ['$uibModalInstance', 'entity', 'Task'];

    function TaskDeleteController($uibModalInstance, entity, Task) {
        var vm = this;
        vm.task = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Task.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
