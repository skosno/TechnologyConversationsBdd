describe('configModule controllers', function() {

    var route, httpBackend;

    beforeEach(module('ngRoute', 'configModule'));

    beforeEach(
        inject(function($route, $httpBackend) {
            route = $route;
            httpBackend = $httpBackend;
        })
    );

    describe('config controller', function() {

        describe('/page/stories/new/ path', function() {
            var path = '/page/stories/new/';
            it('should use the template /assets/html/story/story.tmpl.html', function() {
                expect(route.routes[path].templateUrl).toEqual('/assets/html/story/story.tmpl.html');
            });
            it('should use the controller storyCtrl', function() {
                expect(route.routes[path].controller).toBe('storyCtrl');
            });
        });

        describe('/page/stories/new/:path* path', function() {
            var path = '/page/stories/new/:path*';
            it('should use the template /assets/html/story/story.tmpl.html', function() {
                expect(route.routes[path].templateUrl).toEqual('/assets/html/story/story.tmpl.html');
            });
            it('should use the controller storyCtrl', function() {
                expect(route.routes[path].controller).toBe('storyCtrl');
            });
        });

        describe('/page/stories/view/:path* path', function() {
            var path = '/page/stories/view/:path*';
            it('should use the template /assets/html/story/story.tmpl.html', function() {
                expect(route.routes[path].templateUrl).toEqual('/assets/html/story/story.tmpl.html');
            });
            it('should use the controller storyCtrl', function() {
                expect(route.routes[path].controller).toBe('storyCtrl');
            });
        });

        describe('/page/composites/:className* path', function() {
            var path = '/page/composites/:className*';
            it('should use the template /assets/html/composites/composites.tmpl.html', function() {
                expect(route.routes[path].templateUrl).toEqual('/assets/html/composites/composites.tmpl.html');
            });
            it('should use the controller storyCtrl', function() {
                expect(route.routes[path].controller).toBe('compositesCtrl');
            });
        });

        describe('/page/runner/ path', function() {
            var path = '/page/runner/';
            it('should use the template /assets/html/runner/runner.tmpl.html', function() {
                expect(route.routes[path].templateUrl).toEqual('/assets/html/runner/runner.tmpl.html');
            });
            it('should use the controller runnerCtrl', function() {
                expect(route.routes[path].controller).toBe('runnerCtrl');
            });
        });

    });

});