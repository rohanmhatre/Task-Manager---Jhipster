(function() {
    'use strict';

    angular
        .module('filmSolutionsApp')
        .controller('OwnerDialogController', OwnerDialogController);

    OwnerDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Owner', 'Task'];

    function OwnerDialogController ($scope, $stateParams, $uibModalInstance, entity, Owner, Task) {
        var vm = this;
        vm.owner = entity;
        vm.tasks = Task.query();
        vm.load = function(id) {
            Owner.get({id : id}, function(result) {
                vm.owner = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('filmSolutionsApp:ownerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.owner.id !== null) {
                Owner.update(vm.owner, onSaveSuccess, onSaveError);
            } else {
                Owner.save(vm.owner, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
