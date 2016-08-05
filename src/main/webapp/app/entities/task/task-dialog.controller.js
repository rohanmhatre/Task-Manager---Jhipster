(function() {
    'use strict';

    angular
        .module('filmSolutionsApp')
        .controller('TaskDialogController', TaskDialogController);

    TaskDialogController.$inject = ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Task', 'Owner'];

    function TaskDialogController ($scope, $stateParams, $uibModalInstance, entity, Task, Owner) {
        var vm = this;
        vm.task = entity;
        vm.owners = Owner.query();
        vm.load = function(id) {
            Task.get({id : id}, function(result) {
                vm.task = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('filmSolutionsApp:taskUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.task.id !== null) {
                Task.update(vm.task, onSaveSuccess, onSaveError);
            } else {
                Task.save(vm.task, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.date_assigned = false;
        vm.datePickerOpenStatus.date_completion = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
