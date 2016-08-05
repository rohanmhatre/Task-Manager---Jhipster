(function() {
    'use strict';

    angular
        .module('filmSolutionsApp')
        .controller('OwnerDetailController', OwnerDetailController);

    OwnerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Owner', 'Task'];

    function OwnerDetailController($scope, $rootScope, $stateParams, entity, Owner, Task) {
        var vm = this;
        vm.owner = entity;
        vm.load = function (id) {
            Owner.get({id: id}, function(result) {
                vm.owner = result;
            });
        };
        var unsubscribe = $rootScope.$on('filmSolutionsApp:ownerUpdate', function(event, result) {
            vm.owner = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
