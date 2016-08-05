'use strict';

describe('Controller Tests', function() {

    describe('Task Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTask, MockOwner;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTask = jasmine.createSpy('MockTask');
            MockOwner = jasmine.createSpy('MockOwner');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Task': MockTask,
                'Owner': MockOwner
            };
            createController = function() {
                $injector.get('$controller')("TaskDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'filmSolutionsApp:taskUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
