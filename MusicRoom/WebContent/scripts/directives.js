'use strict';

(function() {
    var musicRoom = angular.module("musicRoom");

    musicRoom.directive('transit', function(){
        var linker = function(scope, element, attrs) {
            element.hover(
                function () {
                    $(this).transition({ scale: 1.1 });
                },
                function () {
                    $(this).transition({ scale: 1 });
                }
            );
        };

        return {
            link: linker
        }
    });
}());