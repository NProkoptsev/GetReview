'use strict';

describe('Controller Tests', function() {

    describe('Acquired_at Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockAcquired_at, MockItem, MockShop;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockAcquired_at = jasmine.createSpy('MockAcquired_at');
            MockItem = jasmine.createSpy('MockItem');
            MockShop = jasmine.createSpy('MockShop');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Acquired_at': MockAcquired_at,
                'Item': MockItem,
                'Shop': MockShop
            };
            createController = function() {
                $injector.get('$controller')("Acquired_atDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'getReviewsApp:acquired_atUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
