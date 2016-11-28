(function() {
    'use strict';
    angular
        .module('getReviewsApp')
        .factory('Sale', Sale);

    Sale.$inject = ['$resource', 'DateUtils'];

    function Sale ($resource, DateUtils) {
        var resourceUrl =  'api/sales/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.start_time = DateUtils.convertLocalDateFromServer(data.start_time);
                        data.end_time = DateUtils.convertLocalDateFromServer(data.end_time);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.start_time = DateUtils.convertLocalDateToServer(copy.start_time);
                    copy.end_time = DateUtils.convertLocalDateToServer(copy.end_time);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.start_time = DateUtils.convertLocalDateToServer(copy.start_time);
                    copy.end_time = DateUtils.convertLocalDateToServer(copy.end_time);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
