(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('link', {
            parent: 'entity',
            url: '/link',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'getReviewsApp.link.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/link/links.html',
                    controller: 'LinkController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('link');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('link-detail', {
            parent: 'entity',
            url: '/link/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'getReviewsApp.link.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/link/link-detail.html',
                    controller: 'LinkDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('link');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Link', function($stateParams, Link) {
                    return Link.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'link',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('link-detail.edit', {
            parent: 'link-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/link/link-dialog.html',
                    controller: 'LinkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Link', function(Link) {
                            return Link.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('link.new', {
            parent: 'link',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/link/link-dialog.html',
                    controller: 'LinkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                url: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('link', null, { reload: 'link' });
                }, function() {
                    $state.go('link');
                });
            }]
        })
        .state('link.edit', {
            parent: 'link',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/link/link-dialog.html',
                    controller: 'LinkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Link', function(Link) {
                            return Link.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('link', null, { reload: 'link' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('link.delete', {
            parent: 'link',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/link/link-delete-dialog.html',
                    controller: 'LinkDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Link', function(Link) {
                            return Link.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('link', null, { reload: 'link' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
