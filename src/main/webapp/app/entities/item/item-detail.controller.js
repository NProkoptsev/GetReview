(function() {
    'use strict';

    angular
        .module('getReviewsApp')
        .controller('ItemDetailController', ItemDetailController);

    ItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState',
        'entity', 'Item', 'Review'];

    function ItemDetailController($scope, $rootScope, $stateParams, previousState, entity,
                                  Item, Review) {

        $scope.$on('$viewContentLoaded', function(){
            //Here your view content is fully loaded !!
            owl = $("#owl-demo");

            owl.owlCarousel({

                navigation: false, // Show next and prev buttons
                slideSpeed: 300,
                paginationSpeed: 400,
                singleItem: true,
                afterInit: afterOWLinit, // do some work after OWL init
                afterUpdate : afterOWLinit
            });

            function afterOWLinit() {
                // adding A to div.owl-page
                $('.owl-controls .owl-page').append('<a class="item-link" />');
                var pafinatorsLink = $('.owl-controls .item-link');
                /**
                 * this.owl.userItems - it's your HTML <div class="item"><img src="http://www.ow...t of us"></div>
                 */
                $.each(this.owl.userItems, function (i) {

                    $(pafinatorsLink[i])
                    // i - counter
                    // Give some styles and set background image for pagination item
                        .css({
                            'background': 'url(' + $(this).find('img').attr('src') + ') center center no-repeat',
                            '-webkit-background-size': 'cover',
                            '-moz-background-size': 'cover',
                            '-o-background-size': 'cover',
                            'background-size': 'cover'
                        })
                        // set Custom Event for pagination item
                        .click(function () {
                            owl.trigger('owl.goTo', i);
                        });

                });
                // add Custom PREV NEXT controls
                $('.owl-pagination').prepend('<a href="#prev" class="prev-owl"/>');
                $('.owl-pagination').append('<a href="#next" class="next-owl"/>');
                // set Custom event for NEXT custom control
                $(".next-owl").click(function () {
                    owl.trigger('owl.next');
                });
                // set Custom event for PREV custom control
                $(".prev-owl").click(function () {
                    owl.trigger('owl.prev');
                });
            }
        });

        var vm = this;

        vm.item = entity;
        vm.previousState = previousState.name;

        vm.reviews = [];

        Item.reviews({id : $stateParams.id}, function (result) {
            vm.reviews = result;
        });

        var unsubscribe = $rootScope.$on('getReviewsApp:itemUpdate', function(event, result) {
            vm.item = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
