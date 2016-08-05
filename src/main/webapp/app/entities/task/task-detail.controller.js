(function() {
    'use strict';

    angular
        .module('filmSolutionsApp')
        .controller('TaskDetailController', TaskDetailController);

    TaskDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Task', 'Owner'];

    function TaskDetailController($scope, $rootScope, $stateParams, entity, Task, Owner) {
        var vm = this;
        vm.task = entity;
        vm.load = function (id) {
            Task.get({id: id}, function(result) {
                vm.task = result;
            });
        };
        var unsubscribe = $rootScope.$on('filmSolutionsApp:taskUpdate', function(event, result) {
            vm.task = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
