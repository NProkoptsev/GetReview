(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bookmark', {
            parent: 'entity',
            url: '/bookmark',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'getReviewsApp.bookmark.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bookmark/bookmarks.html',
                    controller: 'BookmarkController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bookmark');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('bookmark-detail', {
            parent: 'entity',
            url: '/bookmark/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'getReviewsApp.bookmark.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bookmark/bookmark-detail.html',
                    controller: 'BookmarkDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bookmark');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Bookmark', function($stateParams, Bookmark) {
                    return Bookmark.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'bookmark',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('bookmark-detail.edit', {
            parent: 'bookmark-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark/bookmark-dialog.html',
                    controller: 'BookmarkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Bookmark', function(Bookmark) {
                            return Bookmark.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bookmark.new', {
            parent: 'bookmark',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark/bookmark-dialog.html',
                    controller: 'BookmarkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('bookmark', null, { reload: 'bookmark' });
                }, function() {
                    $state.go('bookmark');
                });
            }]
        })
        .state('bookmark.edit', {
            parent: 'bookmark',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark/bookmark-dialog.html',
                    controller: 'BookmarkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Bookmark', function(Bookmark) {
                            return Bookmark.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bookmark', null, { reload: 'bookmark' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bookmark.delete', {
            parent: 'bookmark',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark/bookmark-delete-dialog.html',
                    controller: 'BookmarkDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Bookmark', function(Bookmark) {
                            return Bookmark.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bookmark', null, { reload: 'bookmark' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
