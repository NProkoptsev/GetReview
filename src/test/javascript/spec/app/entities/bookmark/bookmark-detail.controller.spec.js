'use strict';

describe('Controller Tests', function() {

    describe('Bookmark Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockBookmark, MockClient, MockItem;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockBookmark = jasmine.createSpy('MockBookmark');
            MockClient = jasmine.createSpy('MockClient');
            MockItem = jasmine.createSpy('MockItem');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Bookmark': MockBookmark,
                'Client': MockClient,
                'Item': MockItem
            };
            createController = function() {
                $injector.get('$controller')("BookmarkDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'getReviewsApp:bookmarkUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
