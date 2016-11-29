(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('acquired-at', {
            parent: 'entity',
            url: '/acquired-at',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'getReviewsApp.acquired_at.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/acquired-at/acquired-ats.html',
                    controller: 'Acquired_atController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('acquired_at');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('acquired-at-detail', {
            parent: 'entity',
            url: '/acquired-at/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'getReviewsApp.acquired_at.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/acquired-at/acquired-at-detail.html',
                    controller: 'Acquired_atDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('acquired_at');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Acquired_at', function($stateParams, Acquired_at) {
                    return Acquired_at.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'acquired-at',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('acquired-at-detail.edit', {
            parent: 'acquired-at-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/acquired-at/acquired-at-dialog.html',
                    controller: 'Acquired_atDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Acquired_at', function(Acquired_at) {
                            return Acquired_at.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('acquired-at.new', {
            parent: 'acquired-at',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/acquired-at/acquired-at-dialog.html',
                    controller: 'Acquired_atDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                price: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('acquired-at', null, { reload: 'acquired-at' });
                }, function() {
                    $state.go('acquired-at');
                });
            }]
        })
        .state('acquired-at.edit', {
            parent: 'acquired-at',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/acquired-at/acquired-at-dialog.html',
                    controller: 'Acquired_atDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Acquired_at', function(Acquired_at) {
                            return Acquired_at.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('acquired-at', null, { reload: 'acquired-at' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('acquired-at.delete', {
            parent: 'acquired-at',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/acquired-at/acquired-at-delete-dialog.html',
                    controller: 'Acquired_atDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Acquired_at', function(Acquired_at) {
                            return Acquired_at.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('acquired-at', null, { reload: 'acquired-at' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
