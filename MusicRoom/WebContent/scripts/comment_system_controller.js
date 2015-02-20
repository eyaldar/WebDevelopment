'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	var commentSystemController = function($scope, Restangular, $stateParams) {
		$scope.items = {};  
			
		Restangular.setBaseUrl("rest/");
		var reviewsService = Restangular.one("reviews", $stateParams.id);

		reviewsService.getList().then(function(reviews) {
			$scope.items = reviews;
		});

		var formMessages = {
			showCommentPrompt : false,
			showSubmittedPrompt : false,
			showSubmittedError : false
		};

		var commentData = {
			comment : '',
			rating : 0
		};

		var newComment = {
			data : commentData,
			messages: formMessages,
			toggleCommentPrompt : function(value) {
				formMessages.showCommentPrompt = value;
			},
			addComment : function() {
				Restangular.all("reviews").post({
					studio_id : $stateParams.id,
					rating : commentData.rating,
					comment : commentData.comment
				}).then(function(response) {
					formMessages.showSubmittedPrompt = true;
					formMessages.showSubmittedError = false;
				}, function(response) {
					$scope.errorText = response.data.error;

					formMessages.showSubmittedPrompt = false;
					formMessages.showSubmittedError = true;
				});
			}
		};

		$scope.newComment = newComment;
	};

	musicRoom.controller('CommentSystemController', commentSystemController);
}());