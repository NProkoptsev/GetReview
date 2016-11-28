(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('ItemController', ItemController);

    ItemController.$inject = ['$scope', '$state', 'Item', 'ParseLinks', 'AlertService', 'pagingParams', 'paginationConstants'];

    function ItemController ($scope, $state, Item, ParseLinks, AlertService, pagingParams, paginationConstants) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.sort = pagingParams.sort;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

        vm.search = "";

        if(pagingParams.search)
            vm.search = pagingParams.search;

        loadAll();

        $scope.search = function(){
            $state.go('item', {search: vm.search});
        };

        function loadAll () {
            Item.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                search: pagingParams.search,
                category: pagingParams.category
            }, onSuccess, onError);
            function sort() {
                var result = [vm.sort];
                if (vm.sort !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.items = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.sort,
                search: vm.search
            });
        }
    }
})();
