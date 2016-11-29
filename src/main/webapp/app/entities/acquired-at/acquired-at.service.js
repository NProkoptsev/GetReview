(function() {
    'use strict';
    angular
        .module('getReviewsApp')
        .factory('Acquired_at', Acquired_at);

    Acquired_at.$inject = ['$resource'];

    function Acquired_at ($resource) {
        var resourceUrl =  'api/acquired-ats/:id';

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
