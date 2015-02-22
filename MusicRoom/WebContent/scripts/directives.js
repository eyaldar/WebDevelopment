'use strict';

(function() {
	var musicRoom = angular.module("musicRoom");

	musicRoom
			.directive(
					"ngScopeElement",
					function() {
						var directiveDefinitionObject = {

							restrict : "A",

							compile : function compile(tElement, tAttrs,
									transclude) {
								return {
									pre : function preLink(scope, iElement,
											iAttrs, controller) {
										scope[iAttrs.ngScopeElement] = iElement;
									}
								};
							}
						};

						return directiveDefinitionObject;
					})
			.directive('dateTimePicker', function() {
				return {
					restrict : 'A',
					scope : {
						minDate : '=',
						date : '=',
						maxDate : '='
					},
					link : function(scope, element, attrs) {

						scope.$watch('minDate', function(minDate) {
							if (scope.minDate && scope.date) {
								element.datetimepicker({
									format : "Y-m-d h:i:s",
									dayOfWeekStart : 1,
									lang : 'en',
									minDate : scope.minDate,
									startDate : scope.date,
									step : 15
								});
							}
						});
					}
				}
			})
			.directive(
					"isValidPeriod",
					function() {
						return {
							restrict : 'A',
							require : "ngModel",
							link : function(scope, element, attributes, ngModel) {
								if (!scope.isValidPeriodValidators) {
									scope.isValidPeriodValidators = [];
								}

								scope.isValidPeriodValidators[attributes.isValidPeriodId] = ngModel.$validate;

								ngModel.$validators.isValidPeriod = function(
										endTime) {
									var dataFieldName = attributes.isValidPeriodData;

									return endTime == ''
											|| scope[dataFieldName].endTime
													- scope[dataFieldName].startTime > 0;
								};
							}
						}
					})
			.directive(
					"isValidPeriodLink",
					function() {
						return {
							restrict : 'A',
							link : function(scope, element, attributes) {
								if (!scope.isValidPeriodValidators) {
									scope.isValidPeriodValidators = [];
								}

								element
										.on(
												"change",
												function(evt) {
													scope.isValidPeriodValidators[attributes.isValidPeriodId]
															();
												})
							}
						}
					}).directive("ngMatch", [ '$parse', function($parse) {
				var directiveId = "ngMatch";

				return {
					restrict : 'A',
					require : '?ngModel',
					link : function(scope, elem, attrs, ctrl) {
						// if ngModel is not defined, we don't
						// need to do anything
						if (!ctrl)
							return;
						if (!attrs[directiveId])
							return;

						var firstPassword = $parse(attrs[directiveId]);

						var validator = function(value) {
							var temp = firstPassword(scope);
							var isValid = value === temp;
							ctrl.$setValidity('match', isValid);
							return value;
						}

						ctrl.$parsers.unshift(validator);
						ctrl.$formatters.push(validator);
						attrs.$observe(directiveId, function() {
							validator(ctrl.$viewValue);
						});

					}
				}
			} ]);
}());