(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', '$http'];

    function HomeController ($scope, Principal, LoginService, $state, $http) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        $scope.search = function(){
            $state.go('item', {search: $scope.keywords});
        };

        vm.randomItems = [];

        $http.get("/api/randomitems")
            .then(function(response) {
                vm.randomItems = response.data;
            });

        vm.itemsCount = 0;
        vm.reviewsCount = 0;
        vm.membersCount = 0;

        $http.get("/api/itemscount")
            .then(function(response) {
                vm.itemsCount = response.data;
            });

        $http.get("/api/reviewscount")
            .then(function(response) {
                vm.reviewsCount = response.data;
            });

        $http.get("/api/clientscount")
            .then(function(response) {
                vm.membersCount = response.data;
            });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
