(function() {
    'use strict';
    angular
        .module('getReviewsApp')
        .factory('Item', Item);

    Item.$inject = ['$resource'];

    function Item ($resource) {
        var resourceUrl =  'api/items/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
