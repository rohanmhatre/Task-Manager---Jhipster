(function() {
    'use strict';
    angular
        .module('filmSolutionsApp')
        .factory('Task', Task);

    Task.$inject = ['$resource', 'DateUtils'];

    function Task ($resource, DateUtils) {
        var resourceUrl =  'api/tasks/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.date_assigned = DateUtils.convertDateTimeFromServer(data.date_assigned);
                    data.date_completion = DateUtils.convertDateTimeFromServer(data.date_completion);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
