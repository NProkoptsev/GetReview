'use strict';

describe('Controller Tests', function() {

    describe('Item Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockItem, MockImage, MockReview;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockItem = jasmine.createSpy('MockItem');
            MockImage = jasmine.createSpy('MockImage');
            MockReview = jasmine.createSpy('MockReview');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Item': MockItem,
                'Image': MockImage,
                'Review': MockReview
            };
            createController = function() {
                $injector.get('$controller')("ItemDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'getReviewsApp:itemUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
